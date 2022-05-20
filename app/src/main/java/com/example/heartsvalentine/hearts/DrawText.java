package com.example.heartsvalentine.hearts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.ConcurrentModificationException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.NoSuchElementException;

import com.example.heartsvalentine.frameShapes.ShapeType;
import com.example.heartsvalentine.hearts.mainSizes.MainSizes;
import com.example.heartsvalentine.hearts.shapeDetails.ShapeDetails;
import com.example.heartsvalentine.hearts.textBoundaries.TextBoundaries;
import com.example.heartsvalentine.hyphens.Hyphenator;

final class DrawText {
	private final Canvas g2d;
	private final Paint paint;
	private final TextFormattingDetails tfd;
	private final TextBoundaries tb;
	private Hyphenator hyphenator;
	private final char hyphen = '-';
	private final float hyphenWidth;
	private List<TextRectDetails> rectLst;
	private String lastWord;
	private final static HashMap<String, Hyphenator> HyphenatorLangMap = new HashMap<>();
	// Below must be reset and reloaded if text content changes, as must exceptions in HyphenatorLangMap above.
	private final static HashMap<Integer, UserExceptionDetails> usrExceptionMap = new HashMap<>();
	private final static HashMap<Character, Float> charWidthMap = new HashMap<>();
	private final static TextInputDetails textInputDetails = new TextInputDetails();
	private final static char nonBreakSpace = 0xA0;
	private final static double maxExcessWidth = 0.5;

	DrawText(Canvas g2d, MainSizes mainSizes, ShapeDetails sd, TextFormattingDetails tfd, ShapeType mainShapeType, Context context) {
		this.g2d = g2d;
		this.tfd = tfd;

		if (tfd.gethyphenPatternLan() != null) {
			if (HyphenatorLangMap.containsKey(tfd.gethyphenPatternLan())) {
				this.hyphenator = HyphenatorLangMap.get(tfd.gethyphenPatternLan());
			} else {
				this.hyphenator = new Hyphenator(tfd.gethyphenPatternLan(), context);
				HyphenatorLangMap.put(tfd.gethyphenPatternLan(), this.hyphenator);
			}
		}

		Typeface tf = Typeface.create("TimesRoman",  Typeface.NORMAL);

		paint = new Paint();
		paint.setTypeface(tf);
		paint.setTextSize(150);

		this.hyphenWidth = getCharWidth(hyphen);

		tb = ObjectFromShapeType.getTextBoundariesFromShapeType(mainShapeType, paint, mainSizes, sd, tfd);
		}

	int computeTextSpaceAvailable() {
		rectLst = tb.computeTextRectangles();
		int retVal = 0;
		
		for (TextRectDetails txtRectDetails : rectLst) {
			retVal += txtRectDetails.getBoundingRect().width();
		}
		
		return retVal;
	 } 
	
	private void HandleWordInProgressNoPunctuation(StringBuilder wordInProgressNoPunctuation, StringBuilder punctuations, List<String> brokenWordsList) {
		if (wordInProgressNoPunctuation.length() > 0) {
			String[] brokenWords = hyphenator.hyphernateWord(wordInProgressNoPunctuation.toString());
		
			if (brokenWordsList.size() == 0) {
				brokenWords[0] = punctuations.toString() + brokenWords[0];
				Collections.addAll(brokenWordsList, brokenWords);
			}
			else {
				brokenWordsList.set(brokenWordsList.size() - 1, brokenWordsList.get(brokenWordsList.size() - 1) + punctuations.toString() + brokenWords[0]);

				brokenWordsList.addAll(Arrays.asList(brokenWords).subList(1, brokenWords.length));
			}
			punctuations.setLength(0);
			wordInProgressNoPunctuation.setLength(0);
		}
	}
	
	private float getCharWidth(char chr) {
			
		if (charWidthMap.containsKey(chr)) {
			Float width = charWidthMap.get(chr);
			return width == null? 0.0f : width;
		}
		
		float width = paint.measureText(Character.toString(chr));
		charWidthMap.put(chr, width);

		return width;
	}
	
 
	 // Computes bounding rectangles and text inside each of the rectangles
	void computeTextPlacementDetails() {
		char chr;
		StringBuilder lineInProgress = new StringBuilder();
		StringBuilder wordInProgress = new StringBuilder();
		float lineWidth = 0; // The total width of characters on line inside a bounding rectangle.
		int wordWidth = 0; // The total width of characters of a word.
		boolean noHyphenWord = false; // This is set to true when we are not hyphenating a particular word.

		try {
			rectLst = tb.computeTextRectangles();
			Iterator<TextRectDetails> rectLstIterator = rectLst.iterator();
				
			if (rectLstIterator.hasNext()) {
				TextRectDetails txtRectDetails = rectLstIterator.next();
				Rect boundingRect = txtRectDetails.getBoundingRect();
				
				if (!textInputDetails.isInitialized()) {
					textInputDetails.initialise(tfd.getContentText());
				}
				else {
					textInputDetails.resetAllTextFits();
				}
				
				for (int lineDetIdx = 0; lineDetIdx < textInputDetails.count(); lineDetIdx++) {
					TextLineDetails txtLineDetails = textInputDetails.getLineDetails(lineDetIdx);
					String data = txtLineDetails.getLine();
					
					if (data.length() != 0) {
					// We need the last word to know if optimized size for placing all text.
					// If use just last text in rectangle, this text may already have been hyphenated so no good...		
						String dataTrim = data.trim();
						// Safe if no ' ' in string. lastIndexOf returns -1 so lastWord = substring(0) - same as dataTrim - desired effect
						lastWord = dataTrim.substring(dataTrim.lastIndexOf(" ") + 1);		
						lineWidth = 0;
						txtLineDetails.setAllTextFits(false);
					}
					boolean breakLoop = false;
					int emojiLen = 0;
					int emojiPos = -2;
				
					for (int i = 0; i < data.length(); i++) {
						// Have to skip emoji characters already processed
						if (i == emojiPos + 1) {
							i += emojiLen - 1;

							// Case emoji is last character of line.
							if (i >= data.length()) {
								break;
							}
						}
						chr = data.charAt(i);
						boolean isEmoji = EmojiHelper.isCharEmojiAtPos(data, i);

						float charWidth;
						if (isEmoji) {
							emojiPos = i;
							emojiLen = EmojiHelper.emojiLengthAtPos(data, i);
							charWidth = EmojiHelper.getEmojiWidth(data, i, emojiLen, paint);
						}
						else {
							charWidth = getCharWidth(chr);
						}
						// Handling of the { } and {-} here...
						if (chr == '{' && i < data.length() - 2) {
							int handlerRet = -1;
							
							if (data.charAt(i + 1) == ' ') {
								// Handling of "{ }" below. (non-break space)
								if (data.charAt(i + 2) == '}') {
									wordInProgress.append(nonBreakSpace);
									wordWidth += getCharWidth(nonBreakSpace);
									handlerRet = i + 2;
								}
								// Handling of "{  }" below - so user can enter "{ }".
								else if (data.charAt(i + 2) == ' ') {
									// in order to allow user to enter "{ }", all user has to do is enter "{  }" (2 spaces and one is removed) 
									wordInProgress.append(chr);
									wordWidth += charWidth;
									handlerRet = i + 1;
								}
								// else invalid entry warning?
							}
							if (data.charAt(i + 1) == 'x') {
								// Handling of "{x}" below. (no hyphenation for word following)
								if (data.charAt(i + 2) == '}') {
									noHyphenWord = true;
									handlerRet = i + 2;
								}
								// Handling of "{x}" below - so user can enter "{x}".
								else if (data.charAt(i + 2) == 'x') {
									wordInProgress.append(chr);
									wordWidth += charWidth;
									handlerRet = i + 1;
								}
							}
							else if (data.charAt(i + 1) == '-') {
								if (data.charAt(i + 2) == '}') {
									// We only need to perform computations below once so store result in a map
									if (usrExceptionMap.containsKey(i)) {
										UserExceptionDetails usrExceptionDetails = usrExceptionMap.get(i);
										wordInProgress.setLength(0);
										if (usrExceptionDetails != null) {
											wordInProgress.append(usrExceptionDetails.getWord());
											wordWidth = usrExceptionDetails.getWordWidth();
											handlerRet = usrExceptionDetails.getHandlerRet();
										}
									}
									else {				
										// Normally, the start of new exception we want for hyphenation is wordInProgress
										// Strictly speaking, that is not always correct. User might have added a non space break prefixing word.
										// User might also have entered inverted exclamation mark or inverted question mark (used in Spanish)
										// We will handle those possibilities...
										int nbsPos = -1;
									
										for (int idx = 0;  idx < wordInProgress.length(); idx++) {
											if (!Character.isJavaIdentifierPart(wordInProgress.charAt(idx))) {
												nbsPos = idx;
											}
										}
										
										ArrayList<String> exception = new ArrayList<>();
										 
										if (nbsPos == -1) {
											exception.add(wordInProgress.toString());
										} 
										else {
											exception.add(wordInProgress.substring(nbsPos + 1)); // test case last chr nbs
										}
										int j = i + 3;
										StringBuilder wordFragment = new StringBuilder();
									
										while (j < data.length()) {
											char currChr = data.charAt(j);
											
											if (!Character.isJavaIdentifierPart(currChr) && currChr != '{')
												break;
											
											if (currChr == '{' && j < data.length() - 2 && data.charAt(j+1) == '-' && data.charAt(j+2) == '}') {
												exception.add(wordFragment.toString());
												wordFragment.setLength(0);
												j += 2;
											}
											else {
												wordInProgress.append(currChr);
												wordWidth += getCharWidth(currChr);
												wordFragment.append(currChr);
											}	
											++j;
										}
									
										exception.add(wordFragment.toString());
										handlerRet = j - 1;
										hyphenator.addException(exception.toArray(new String[0]));
										usrExceptionMap.put(i, new UserExceptionDetails(wordInProgress.toString(), wordWidth, handlerRet));					
									}
								} 
								else if (data.charAt(i + 2) == '-') {
									// in order to allow user to enter "{-}", all user has to do is enter "{--}" (2 hyphens and one is removed) 
									wordInProgress.append(chr);
									wordWidth += charWidth;
									handlerRet = i + 1;
								}			
							}
							if (handlerRet != -1) {
								i = handlerRet; // move so many characters ahead
								continue;
							}
						}
						
						if (chr != ' ' && !isEmoji) {
							wordInProgress.append(chr);
							wordWidth += charWidth;
						}
						if (chr == ' ' || i == data.length() - 1 || isEmoji) {
							// In French, non-breaking space before exclamation, question marks or colon
							if (chr == ' ' && i != data.length() - 1 && (data.charAt(i + 1) == '!' || data.charAt(i + 1) == '?' || data.charAt(i + 1) == ':')) {
								wordInProgress.append(nonBreakSpace);
								wordWidth += charWidth;
								continue;
							}
							
							if (lineWidth + wordWidth <= boundingRect.width()) {
								lineInProgress.append(wordInProgress);
								lineWidth += wordWidth;
								wordInProgress.setLength(0);
								wordWidth = 0;
								
								if (chr == ' ' && lineWidth + charWidth <= boundingRect.width()) {
									lineInProgress.append(chr);
									lineWidth += charWidth;
								}
								if (isEmoji) {
									if (lineWidth + charWidth <= boundingRect.width()) {
										lineInProgress.append(chr);

										for (int idx = 1; idx < emojiLen; idx++) {
											lineInProgress.append(data.charAt(i + idx));
										}

										lineWidth += charWidth;
									}
									else {
										wordInProgress.append(chr);

										for (int idx = 1; idx < emojiLen; idx++) {
											wordInProgress.append(data.charAt(i + idx));
										}

										wordWidth += charWidth;


									}
								}

								if (i == data.length() - 1) {
									txtLineDetails.setAllTextFits(true);
								}
							}		
							else {
								if (isEmoji) {
									i--; // We are changing bounding rectangle, so we have to go back so we re-process the emoji we have just
										  // done on next bounding box and we add word that didn't fit into next bounding rectangle that way..

									if (rectLstIterator.hasNext()) {
										txtRectDetails.setText(lineInProgress.toString());
										txtRectDetails.setTextWidth((int)lineWidth);
										lineInProgress.setLength(0);
										lineWidth = 0;

										txtRectDetails = rectLstIterator.next();
										boundingRect = txtRectDetails.getBoundingRect();
									}
									else {
										break;
									}
								}

								if (tfd.getHyphenateText() && !noHyphenWord) {
									// wordInProgress may have punctuation marks.
									// No hyphenation can take place around these punctuation marks.
									// I have made that assumption in calculations below.
									// Finish punctuation marks may be multiple such as ?! or ...
									// I have handled inverted Spanish exclamation and question marks prefixing a word.
									// You can have apostrophe in middle of wordInProgress - example: "isn't"
									StringBuilder wordInProgressNoPunctuation = new StringBuilder();
									StringBuilder punctuations = new StringBuilder();
								    List<String> brokenWordsList = new ArrayList<>();

									for (int idx = 0;  idx < wordInProgress.length(); idx++) {
										if (Character.isJavaIdentifierPart(wordInProgress.charAt(idx))) {
											wordInProgressNoPunctuation.append(wordInProgress.charAt(idx));
										}
										else {
											HandleWordInProgressNoPunctuation(wordInProgressNoPunctuation, punctuations, brokenWordsList);
											punctuations.append(wordInProgress.charAt(idx));	
										}
									}
									// Case space immediately after wordInProgressNoPunctuation
									HandleWordInProgressNoPunctuation(wordInProgressNoPunctuation, punctuations, brokenWordsList);
									
									// Case punctuation marks after wordInProgressNoPunctuation: simply append these
									if (punctuations.length() > 0) {
										if (brokenWordsList.size() > 0) {
											brokenWordsList.set(brokenWordsList.size() - 1, brokenWordsList.get(brokenWordsList.size() - 1) + punctuations);
										}
										else {
											brokenWordsList.add(punctuations.toString());
										}
									}
									
									breakLoop = false;
				
									for (int idx = 0; idx < brokenWordsList.size(); idx++) {
										String str = brokenWordsList.get(idx);
									//	int brokenWordWidth = g2d.getFontMetrics().charsWidth(str.toCharArray(), 0, str.length());
										float brokenWordWidth = paint.measureText(str);

										float requiredWidth = (idx < brokenWordsList.size() - 1)? brokenWordWidth + hyphenWidth: brokenWordWidth;
										
										if (lineWidth + requiredWidth <= boundingRect.width()) {
											lineInProgress.append(str);
											lineWidth += brokenWordWidth;
										}		
										else {
											if (idx > 0) {
												lineInProgress.append(hyphen);
												lineWidth += hyphenWidth;		
											}
											txtRectDetails.setText(lineInProgress.toString());
											txtRectDetails.setTextWidth((int)lineWidth);
											
											// BEWARE: not enough space in previous rectangle does not necessarily mean there will not be enough in any of next rectangles.
											boolean badFit = true;
											
											while (rectLstIterator.hasNext() && badFit) {
												txtRectDetails = rectLstIterator.next();
												boundingRect = txtRectDetails.getBoundingRect();
												
												// make sure not last. Otherwise mustn't affix hyphen width
												if (requiredWidth <= boundingRect.width()) {
													badFit = false;
												}
											}
											// badFit is important. Otherwise we exit loop which results in all text fits = false when all text could fit in.
											if (!rectLstIterator.hasNext() && badFit) {
												breakLoop = true;
												break;	
											}
											lineInProgress.setLength(0);
											lineInProgress.append(str);
											lineWidth = brokenWordWidth;
										}	
									}					
									// We have processed word in progress, so reset this...
									wordInProgress.setLength(0);
									wordWidth = 0;
									
									if (breakLoop) {
										break;
									} 
									else if ( i == data.length() - 1) {
										txtLineDetails.setAllTextFits(true);
									}			
								}	
								else {
									txtRectDetails.setText(lineInProgress.toString());
									txtRectDetails.setTextWidth((int)lineWidth);
									lineInProgress.setLength(0);
									lineWidth = 0;
									
									// BEWARE: not enough space in this rectangle does not necessarily mean there will not be enough in next rectangle.
									// Rectangles start by getting wider
									boolean badFit = true;
								//	int currentWidth = txtRectDetails.getBoundingRect().width;
									
									while (rectLstIterator.hasNext() && badFit) {
										txtRectDetails = rectLstIterator.next();
										boundingRect = txtRectDetails.getBoundingRect();
										
										if (wordWidth <= boundingRect.width()) {
											badFit = false;
										}
										
									//	currentWidth = txtRectDetails.getBoundingRect().width;
									}
									
									if (!badFit && i == data.length() - 1) {
										txtLineDetails.setAllTextFits(true);
									}
							
									if (!rectLstIterator.hasNext()) {
										breakLoop = true;
										break;	
									}
								}
								if (chr == ' ' && i != data.length() - 1) {
									i--; // We are changing bounding rectangle, so we have to go back so we re-process the space we have just
								         // done on next bounding box and we add word that didn't fit into next bounding rectangle that way..	
								}
							}
							noHyphenWord = false;
						}
					}
					// Don't forget to add last line.
					if (lineWidth + wordWidth <= boundingRect.width() && !breakLoop) {
						lineInProgress.append(wordInProgress);
						lineWidth += wordWidth;
						txtRectDetails.setText(lineInProgress.toString());
						txtRectDetails.setTextWidth((int)lineWidth);
						lineInProgress.setLength(0);
						wordInProgress.setLength(0);
						lineWidth = 0;
						// here end of line set to true
						txtRectDetails.setEndOfLine();
						txtLineDetails.setAllTextFits(true);
					}
					else {
						// We couldn't place last piece of text...
						txtLineDetails.setAllTextFits(false);
					}
					
					// have to make sure next rectangle is lower - top 2 rectangles have same y coordinate.
					int boundingRectY = boundingRect.bottom;
					boolean yChanged = false;
					while (rectLstIterator.hasNext() && !yChanged) {	
						txtRectDetails = rectLstIterator.next();
						boundingRect = txtRectDetails.getBoundingRect();
						if (boundingRect.bottom != boundingRectY) {
							yChanged = true;
						}
					}				
				}
		//		scanner.close();
			}
		}
		//catch (FileNotFoundException e) {
		//	System.out.println("An error occurred reading " + tfd.getContentText() + " file.");
		//	e.printStackTrace();
		//}
		catch (NoSuchElementException | ConcurrentModificationException e) {
			System.out.println("An error occurred with call to rectLstIterator.next() file.");
			e.printStackTrace();
		} catch (Exception e) {
			System.out.println("An error occurred in method computeTextPlacementDetails().");
			e.printStackTrace();
		}
	}
		 		
	void draw() {
		try {
			paint.setColor(tfd.getTxtColour());

			for (TextRectDetails txtRectDetails : rectLst) {
				Rect txtBoundingRect = txtRectDetails.getBoundingRect();
				String txt = txtRectDetails.getText();

				if (txt != null && txt.length() > 0) {
					if (tfd.getOptimizeSpacing()) {
						int availableWidth = txtRectDetails.getBoundingRect().width();
						txt = txt.trim();
						// count characters with emoji not length anymore!
						int usedWidth = 0;
						int interCharRatio = 0; // sum of all chars x2 except 1st and last x1
						// getLineMetrics(str, beginIndex, limit, context).charWidth(txt.charAt(idx));
						// With emoji, the number of characters is different from txt.length as an emoji
						// can be a multiple combination of characters.
						int numCharacters = 0;

						for (int idx = 0; idx < txt.length(); idx++) {
							if (EmojiHelper.isCharEmojiAtPos(txt, idx)) {
								idx += EmojiHelper.emojiLengthAtPos(txt, idx) - 1;
							}
							numCharacters++;
						}

						float[] charsWidths = new float[numCharacters];
						int widthIdx = 0;

						for (int idx = 0; idx < txt.length(); idx++) {
							if (EmojiHelper.isCharEmojiAtPos(txt, idx)) {
								int emojiLen = EmojiHelper.emojiLengthAtPos(txt, idx);
								charsWidths[widthIdx] = EmojiHelper.getEmojiWidth(txt, idx, emojiLen, paint);
								idx += emojiLen - 1;
							} else {
								charsWidths[widthIdx] = getCharWidth(txt.charAt(idx));
							}

							usedWidth += charsWidths[widthIdx];

							if (idx == 0 || idx == txt.length() - 1) {
								interCharRatio += charsWidths[widthIdx];
							} else {
								interCharRatio += 2 * charsWidths[widthIdx];
							}
							widthIdx++;
						}

						int excessWidth = availableWidth - usedWidth;
						double excessUsedWidthRatio = excessWidth / (double)usedWidth;
						double offset = 0;

						if (excessUsedWidthRatio > maxExcessWidth) {
							excessWidth = (int) (usedWidth * maxExcessWidth);
						}

						char[] chr = new char[1];

						//	 for (int idx = 0; idx < txt.length(); idx++) {
						int idxInTxt = 0;
						for (int characterIdx = 0; characterIdx < numCharacters; characterIdx++) {

							if (characterIdx > 0) {
								offset += (charsWidths[characterIdx] * excessWidth) / (double) interCharRatio;
							}

							if (EmojiHelper.isCharEmojiAtPos(txt, idxInTxt)) {
								int emojiLen = EmojiHelper.emojiLengthAtPos(txt, idxInTxt);
								String str = txt.substring(idxInTxt, idxInTxt + emojiLen);
								g2d.drawText(str, (int) (txtBoundingRect.left + Math.round(offset)), txtBoundingRect.top, paint);
								idxInTxt += emojiLen;
							} else {
								chr[0] = txt.charAt(idxInTxt);
								g2d.drawText(Character.toString(chr[0]), (int) (txtBoundingRect.left + Math.round(offset)), txtBoundingRect.top, paint);
								idxInTxt++;
							}

							if (characterIdx < txt.length() - 1) {
								offset += charsWidths[characterIdx];
								offset += (charsWidths[characterIdx] * excessWidth) / (double) interCharRatio;
							}
						}
					} else {
						g2d.drawText(txt, txtBoundingRect.left, txtBoundingRect.top, paint);
					}
				}
			}	
		} catch (Exception e) {
			System.out.println("An error occurred in method draw().");
			e.printStackTrace();
		}
	}
	// Returns true if:
	// - All rectangles optimised.
	// - Last rectangles are empty but last word (no hyphenation) or last hyphenated word fragment are wider than these rectangle.
	// Otherwise returns false.	
	boolean SizeOptimized() {
		boolean optimized = false;
		ListIterator<TextRectDetails> li = rectLst.listIterator(rectLst.size());
		int lastRectWidth = 0;
		int previousRectWidth;
		float lastWordSegmentWidth = 0;

		if (tfd.getHyphenateText()) {
			String[] brokenWords = hyphenator.hyphernateWord(lastWord);
			String lastWordSegment = "-" + brokenWords[brokenWords.length - 1];
			//lastWordSegmentWidth = g2d.getFontMetrics().charsWidth(lastWordSegment.toCharArray(), 0, lastWordSegment.length());
			lastWordSegmentWidth = paint.measureText(lastWordSegment);
		}
		
		if (li.hasPrevious()) {
			TextRectDetails trd = li.previous();
			
			if (trd.getTextWidth() > 0) {
				// All rectangles used...
				optimized = true;
			}
			else {
				while(li.hasPrevious()) {
					previousRectWidth = lastRectWidth;
					lastRectWidth = trd.getBoundingRect().width();
					
					if (previousRectWidth > lastRectWidth) {
						// Heart is getting narrower here - useless processing any further.
						break;
					}

					trd = li.previous();
					
					if (trd.getTextWidth() > 0) {
						
						if (tfd.getHyphenateText()) {
							if (lastWordSegmentWidth < previousRectWidth) {
						//		optimized = false;
								break;
							}			
						}
						else {
							// If the last word in rectangle is wider than rectangle below, then it is optimized.	
							String lastWordInRect = trd.getText().substring(trd.getText().lastIndexOf(" ") + 1);
						//	int lastWordInRectWidth = g2d.getFontMetrics().charsWidth(lastWordInRect.toCharArray(), 0, lastWordInRect.length());
							float lastWordInRectWidth = paint.measureText(lastWordInRect);
							if (lastWordInRectWidth > lastRectWidth) {
								optimized = true;
								break;
							}
						}		
					}
				}
			}				
		}			
		return optimized;
	}
	
	boolean doesAllTextFit() {
		return textInputDetails.allTextFits();
	}

	void resetTextInputDetails() {
		textInputDetails.reset();
	}
}
