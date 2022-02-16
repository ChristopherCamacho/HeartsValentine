package com.example.heartsvalentine.hearts;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
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

import com.example.heartsvalentine.hearts.shapeDetails.EmojiShapeDetails;
import com.example.heartsvalentine.hearts.shapeDetails.ShapeDetails;
import com.example.heartsvalentine.hyphens.Hyphenator;

final class DrawText {
	
	DrawText(Canvas g2d, MainSizes mainSizes, ShapeDetails sd, TextFormattingDetails tfd, Context context) {
		this.g2d = g2d;
		this.mainSizes = mainSizes;
		this.sd = sd;
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
		textAscent = paint.ascent();// - g2d.getFontMetrics().getDescent();//.getAscent();//.getHeight();
		//textDescent = paint.descent();
	}

	private int[] getXCircleIntersectionsFromY(int y, boolean left) {
		// centre of left circle is x_centre = margin + 2*heartCenterX + (radius - heartCenterX) = margin + heartCenterX + radius
		// 					        y_centre = margin - 2*heartCenterY + (radius + heartCenterY) = margin - heartCenterY + radius
		// centre of right circle is x_centre = width - margin - 2 * radius + radius - heartCenterX = width - margin - radius - heartCenterX
		// 					   y_centre = margin - 2*heartCenterY + (radius + heartCenterY) = margin - heartCenterY + radius			
		// equation of top left inner circle is:
		// (margin + heartCenterX + radius) + (radius - heartCenterX)*cos(angle)
		// (margin - heartCenterY + radius) + (radius + heartCenterY)*sin(angle) where angle = 0 to 2*pi. 
		// equation of top right inner circle is:
		// (width - margin - radius - heartCenterX) + (radius - heartCenterX)*cos(angle)
		//  (margin - heartCenterY + radius) + (radius + heartCenterY)*sin(angle) where angle = 0 to 2*pi. 
		// these are not circles but ovals!
		// adjustedHeartCenter is an arbitrary quickfix that gives better results for emoji
		float adjustedHeartCenter = sd.getCenterX() - 10;
		double angle = Math.asin((y - (mainSizes.getMargin() - sd.getCenterY() + mainSizes.getRadius()))/(mainSizes.getRadius() - tfd.getTxtHeartsMargin() + sd.getCenterY()));
		double xCentre = left? (mainSizes.getMargin() + adjustedHeartCenter /*hd.getHeartCenterX()*/ + mainSizes.getRadius()) : (mainSizes.getWidth() - mainSizes.getMargin() - mainSizes.getRadius() - adjustedHeartCenter /*hd.getHeartCenterX()*/);
		int xFirst = (int)(xCentre + (mainSizes.getRadius() - tfd.getTxtHeartsMargin() - sd.getCenterX())*Math.cos(angle));
		int xSecond = (int)(xCentre + (mainSizes.getRadius() - tfd.getTxtHeartsMargin() - sd.getCenterX())*Math.cos(angle + Math.PI));
		int[] retVal = new int[2];
		retVal[0] = Math.min(xFirst, xSecond);
		retVal[1] = Math.max(xFirst, xSecond);
		return retVal;
	}

	private int[] getXEllipseIntersectionsFromY(int y, boolean left) {
		// centre of left circle is x_centre = margin + 2*heartCenterX + (radius - heartCenterX) = margin + heartCenterX + radius
		// 					        y_centre = margin - 2*heartCenterY + (radius + heartCenterY) = margin - heartCenterY + radius
		// centre of right circle is x_centre = width - margin - 2 * radius + radius - heartCenterX = width - margin - radius - heartCenterX
		// 					   y_centre = margin - 2*heartCenterY + (radius + heartCenterY) = margin - heartCenterY + radius
		// equation of an ellipse is (x - Cx)**2/a**2 + (y - Cy)**2/b**2 = 1
		// so x = Cx +- a*sqrt(1 - (y - Cy)**2/b**2)
		double Cx = left? (mainSizes.getMargin() + mainSizes.getRadius() +  sd.getCenterX()) : (mainSizes.getWidth() - mainSizes.getMargin() - mainSizes.getRadius() -  sd.getCenterX());
		double Cy = (mainSizes.getMargin() + mainSizes.getRadius() +  sd.getCenterY());
		double a = mainSizes.getRadius() - sd.getCenterX() - tfd.getTxtHeartsMargin();
		double b = mainSizes.getRadius() - sd.getCenterY() - tfd.getTxtHeartsMargin();

		int[] retVal = new int[2];
		retVal[0] = (int)(Cx - a*Math.sqrt(1 - Math.pow(y - Cy, 2)/(b*b)));
		retVal[1] = (int)(Cx + a*Math.sqrt(1 - Math.pow(y - Cy, 2)/(b*b)));
		return retVal;
	}

	private int[][] computeBottomTrianglePts() {
		Point ptLeftTopCircleCentre = new Point((int)(mainSizes.getMargin() + mainSizes.getRadius() + sd.getCenterX()), (int)(mainSizes.getMargin() + mainSizes.getRadius() - sd.getCenterY()));
		Point ptRightTopCircleCentre = new Point((int)(mainSizes.getWidth() - mainSizes.getMargin() - mainSizes.getRadius() - sd.getCenterX()), (int)(mainSizes.getMargin() + mainSizes.getRadius() - sd.getCenterY()));

		double startAngle = Math.acos((mainSizes.getWidth()/2.0 - ptLeftTopCircleCentre.x)/mainSizes.getRadius());
		double vertDistBottomPt = mainSizes.getHeight() - 2 * mainSizes.getMargin() - mainSizes.getRadius(); // y-coord top circle centres - y coord of bottom of heart
		double alpha = Math.atan(vertDistBottomPt/mainSizes.getRadius());
		double phi = Math.atan(vertDistBottomPt/(mainSizes.getRadius()*Math.cos(startAngle)));
	
		double beta = 2*Math.PI - alpha - phi;

		double pt1x = ptLeftTopCircleCentre.x + mainSizes.getRadius() * Math.cos(beta)  - tfd.getTxtHeartsMargin() / Math.sin(beta)/*- heartCenterX + heartCenterWidth*/;
		double pt1y = ptLeftTopCircleCentre.y - mainSizes.getRadius() * Math.sin(beta) - sd.getCenterY() /* + heightAdjustment*/;

		double pt2x = ptRightTopCircleCentre.x + mainSizes.getRadius() * Math.cos(Math.PI - beta) + tfd.getTxtHeartsMargin() / Math.sin(beta)/*- heartCenterX + heartCenterWidth*/;
		double pt2y = ptRightTopCircleCentre.y - mainSizes.getRadius() * Math.sin(Math.PI - beta) - sd.getCenterY() /*+ heightAdjustment*/;

		if (!(sd instanceof EmojiShapeDetails)) {
	//	if (!hd.getUseEmoji()) {
			pt1y += 2.0f * sd.getHeight();
			pt2y += 2.0f * sd.getHeight();
			// I've already added for pt3.y elsewhere - tidy when have time. This works safely, but messy.
		}
		
		double pt3x = mainSizes.getWidth()/2.0;
		double pt3y = mainSizes.getHeight() - mainSizes.getMargin() + tfd.getTxtHeartsMargin() / Math.cos(Math.PI/2.0 - beta);
		
		double zeta = Math.atan ( (pt1y - pt3y)/(pt1x - pt3x));
		double heightAdjustment = -0.5 * sd.getWidth() * Math.sin(zeta) - sd.getHeight();

		pt1y += heightAdjustment;
		pt2y += heightAdjustment;
		pt3y += heightAdjustment;
	
		int[] xpoints = {(int)pt1x, (int)pt3x, (int)pt2x};
		int[] ypoints = {(int)pt1y, (int)pt3y, (int)pt2y};

		return new int[][]{xpoints, ypoints};
		//int[][] retVal = {xpoints, ypoints};
		//return retVal;
	}

	// drawTopCircles draws the top two bounding disks inside hearts
	void drawShapes(int[][] pts) {
		// top left oval
		paint.setColor(Color.BLUE);
		g2d.drawArc((mainSizes.getMargin()  + sd.getWidth() + tfd.getTxtHeartsMargin()),
				 (mainSizes.getMargin() + sd.getHeight() + tfd.getTxtHeartsMargin()),
				(2*mainSizes.getRadius() /*- hd.getHeartCenterX()*/ - tfd.getTxtHeartsMargin()),
				(2*(mainSizes.getRadius() + sd.getCenterY() - tfd.getTxtHeartsMargin())),
				0, 360, true, paint);
		// Top right oval
		paint.setColor(Color.CYAN);
		g2d.drawArc((mainSizes.getWidth() - mainSizes.getMargin() - 2*mainSizes.getRadius()  + tfd.getTxtHeartsMargin()),
				 (mainSizes.getMargin() + sd.getHeight() + tfd.getTxtHeartsMargin()),
				(mainSizes.getWidth()  - sd.getWidth() - tfd.getTxtHeartsMargin()),
				(2*(mainSizes.getRadius() - tfd.getTxtHeartsMargin() + sd.getCenterY())),
                                              	    0, 360, true, paint);

		Path path = new Path();
		path.moveTo(pts[0][0], pts[1][0]); // used for first point
		path.lineTo(pts[0][1], pts[1][1]);
		path.lineTo(pts[0][2], pts[1][2]);

		paint.setColor(Color.GREEN);
		g2d.drawPath(path, paint);
	}

	void drawTextBoundingRectangles() {
		int counter = 0;
		 
		for (TextRectDetails txtRectDets : rectLst) {
			if (counter%4 == 0) {
				paint.setColor(Color.MAGENTA);
			} else if (counter%4 == 1) {
				paint.setColor(Color.rgb(255, 128,0));
			} else if (counter%4 == 2) {
				paint.setColor(Color.rgb(115, 163,5));
			} else {
				paint.setColor(Color.YELLOW);
			}
			g2d.drawRect(txtRectDets.getBoundingRect().left, txtRectDets.getBoundingRect().top, txtRectDets.getBoundingRect().right,
					txtRectDets.getBoundingRect().bottom, paint);

			counter++;
		}	 
	}
	 
	private void computeTextRectangles() {
		int[][] pts = computeBottomTrianglePts();
		 	
		// Uncomment line below for testing purposes
        // drawShapes(pts);
			
		int yTop = (int)(tfd.getTopTextMargin() + mainSizes.getMargin() + 2*sd.getHeight());

		if (sd instanceof EmojiShapeDetails) {
		// if (hd.getUseEmoji()) {
			while (yTop < pts[1][0]) {
				int[] xTopLeft = getXCircleIntersectionsFromY(yTop, true);
				int[] xTopRight = getXCircleIntersectionsFromY(yTop, false);

				int yBottom = yTop + (int) textAscent;
				int[] xBottomLeft = getXCircleIntersectionsFromY(yBottom, true);
				int[] xBottomRight = getXCircleIntersectionsFromY(yBottom, false);

				int xLeftLeftCircle = Math.max(xTopLeft[0], xBottomLeft[0]);
				int xRightLeftCircle = Math.min(xTopLeft[1], xBottomLeft[1]);

				int xLeftRightCircle = Math.max(xTopRight[0], xBottomRight[0]);
				int xRightRightCircle = Math.min(xTopRight[1], xBottomRight[1]);

				// The 2 rectangles can be very close, without intersecting and without these intersecting heart if these were joined. When this happens, funny result with hyphen added in middle of line.
				// see INPUT_PATH_SILLY_MESSAGE with text to heart margin of 20
				// To remedy this, let us also merge if gap is very small - say half of heart width.
				// Hope not introducing bug here.
				int maxGap = (int) (sd.getWidth() / 2.0);

				if (xRightLeftCircle + maxGap < xLeftRightCircle && yTop < (mainSizes.getMargin() - sd.getCenterY() + mainSizes.getRadius()) && yBottom < (mainSizes.getMargin() - sd.getCenterY() + mainSizes.getRadius())) {
					rectLst.add(new TextRectDetails(new Rect(xLeftLeftCircle, yTop, xRightLeftCircle, yBottom)));
					rectLst.add(new TextRectDetails(new Rect(xLeftRightCircle, yTop, xRightRightCircle, yBottom)));
				} else {
					rectLst.add(new TextRectDetails(new Rect(xLeftLeftCircle, yTop, xRightRightCircle, yBottom)));
				}
				yTop += tfd.getLineHeight();
			}
		}
		else {
			yTop -= (int) textAscent + 110 - tfd.getTxtHeartsMargin();
			while (yTop < pts[1][0]) {
				int[] xTopLeft = getXEllipseIntersectionsFromY(yTop, true);
				int[] xTopRight = getXEllipseIntersectionsFromY(yTop, false);

				int yBottom = yTop + (int) textAscent;
				int[] xBottomLeft = getXEllipseIntersectionsFromY(yBottom, true);
				int[] xBottomRight = getXEllipseIntersectionsFromY(yBottom, false);

				int xLeftLeftCircle = Math.max(xTopLeft[0], xBottomLeft[0]);
				int xRightLeftCircle = Math.min(xTopLeft[1], xBottomLeft[1]);

				int xLeftRightCircle = Math.max(xTopRight[0], xBottomRight[0]);
				int xRightRightCircle = Math.min(xTopRight[1], xBottomRight[1]);

				// The 2 rectangles can be very close, without intersecting and without these intersecting heart if these were joined. When this happens, funny result with hyphen added in middle of line.
				// see INPUT_PATH_SILLY_MESSAGE with text to heart margin of 20
				// To remedy this, let us also merge if gap is very small - say half of heart width.
				// Hope not introducing bug here.
				int maxGap = (int) (sd.getWidth() / 2.0);

				if (xRightLeftCircle + maxGap < xLeftRightCircle && yTop < (mainSizes.getMargin() - sd.getCenterY() + mainSizes.getRadius()) && yBottom < (mainSizes.getMargin() - sd.getCenterY() + mainSizes.getRadius())) {
					rectLst.add(new TextRectDetails(new Rect(xLeftLeftCircle, yTop, xRightLeftCircle, yBottom)));
					rectLst.add(new TextRectDetails(new Rect(xLeftRightCircle, yTop, xRightRightCircle, yBottom)));
				} else {
					rectLst.add(new TextRectDetails(new Rect(xLeftLeftCircle, yTop, xRightRightCircle, yBottom)));
				}
				yTop += tfd.getLineHeight();
			}
		}

		while (yTop < pts[1][1]) {
			// equation of a line through (a, b) = pt[0][0], pt[1][0] or pt[0][1], pt[1][1]   
			// and (c, d) = pt[0][1], pt[1][1] is:
			// x + aa*y + bb = 0 where:
			// aa = (c - a)/(b - d) and bb = (ad - bc)/(b - d)
			double aa1 = (pts[0][1] - pts[0][0])/(double)(pts[1][0] - pts[1][1]);			
			double bb1 = (pts[0][0]*pts[1][1] - pts[1][0]*pts[0][1])/(double)(pts[1][0] - pts[1][1]);
				
			double aa2 = (pts[0][1] - pts[0][2])/(double)(pts[1][2] - pts[1][1]);
			double bb2 = (pts[0][2]*pts[1][1] - pts[1][2]*pts[0][1])/(double)(pts[1][2] - pts[1][1]);
			// so x = - bb - aa*y
				
			int yBottom = yTop + (int)textAscent;
			int x1 = (int) (- bb1 - aa1*yTop);
			int x2 = (int) (- bb2 - aa2*yTop);

			rectLst.add(new TextRectDetails(new Rect(x1, yTop, x2, yBottom)));
			yTop += tfd.getLineHeight();		
		}

		// Uncomment for testing purpose
		// drawTextBoundingRectangles();
	}
	
	int computeTextSpaceAvailable() {
		rectLst = new ArrayList<>();
		computeTextRectangles();	
		int retVal = 0;
		
		for (TextRectDetails txtRectDets : rectLst) {
			retVal += txtRectDets.getBoundingRect().width();
		}
		
		return retVal;
	 } 
	
	private void HandleWordInProgressNoPunc(StringBuilder wordInProgressNoPunc, StringBuilder punctuations, List<String> brokenwordsList) {
		if (wordInProgressNoPunc.length() > 0) {
			String[] brokenWords = hyphenator.hyphernateWord(wordInProgressNoPunc.toString());
		
			if (brokenwordsList.size() == 0) {
				brokenWords[0] = punctuations.toString() + brokenWords[0];
				Collections.addAll(brokenwordsList, brokenWords);
			}
			else {
				brokenwordsList.set(brokenwordsList.size() - 1, brokenwordsList.get(brokenwordsList.size() - 1) + punctuations.toString() + brokenWords[0]);

				brokenwordsList.addAll(Arrays.asList(brokenWords).subList(1, brokenWords.length));
			}
			punctuations.setLength(0);
			wordInProgressNoPunc.setLength(0);
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
	
 
	 // Computes bounding rectangles and text inside each of ther
	void computeTextPlacementDetails() {
		rectLst = new ArrayList<>();
		char chr;
		StringBuilder lineInProgress = new StringBuilder();
		StringBuilder wordInProgress = new StringBuilder();
		float lineWidth = 0; // The total width of characters on line inside a bounding rectangle.
		int wordWidth = 0; // The total width of characters of a word.
		boolean noHyphenWord = false; // This is set to true when we are not hyphenating a particular word.

		try {
			computeTextRectangles();
			Iterator<TextRectDetails> rectLstIterator = rectLst.iterator();
				
			if (rectLstIterator.hasNext()) {
				TextRectDetails txtRectDets = rectLstIterator.next();
				Rect boundingRect = txtRectDets.getBoundingRect();
				
				if (!textInputDetails.isInitialized()) {
					textInputDetails.initialise(tfd.getContentText());
				}
				else {
					textInputDetails.resetAllTextFits();
				}
				
				for (int lineDetIdx = 0; lineDetIdx < textInputDetails.count(); lineDetIdx++) {
					TextLineDetails txtLineDets = textInputDetails.getLineDetails(lineDetIdx);
					String data = txtLineDets.getLine();
					
					if (data.length() != 0) {
					// We need the last word to know if optimized size for placing all text.
					// If use just last text in rectangle, this text may already have been hyphenated so no good...		
						String dataTrim = data.trim();
						// Safe if no ' ' in string. lastIndexOf returns -1 so lastWord = substring(0) - same as dataTrim - desired effect
						lastWord = dataTrim.substring(dataTrim.lastIndexOf(" ") + 1);		
						lineWidth = 0;
						txtLineDets.setAllTextFits(false);
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
										UserExceptionDetails usrExceptionDets = usrExceptionMap.get(i);
										wordInProgress.setLength(0);
										if (usrExceptionDets != null) {
											wordInProgress.append(usrExceptionDets.getWord());
											wordWidth = usrExceptionDets.getWordWidth();
											handlerRet = usrExceptionDets.getHandlerRet();
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
									txtLineDets.setAllTextFits(true);
								}
							}		
							else {
								if (isEmoji) {
									i--; // We are changing bounding rectangle, so we have to go back so we re-process the emoji we have just
										  // done on next bounding box and we add word that didn't fit into next bounding rectangle that way..

									if (rectLstIterator.hasNext()) {
										txtRectDets.setText(lineInProgress.toString());
										txtRectDets.setTextWidth((int)lineWidth);
										lineInProgress.setLength(0);
										lineWidth = 0;

										txtRectDets = rectLstIterator.next();
										boundingRect = txtRectDets.getBoundingRect();
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
									StringBuilder wordInProgressNoPunc = new StringBuilder();
									StringBuilder punctuations = new StringBuilder();
								    List<String> brokenwordsList = new ArrayList<>();

									for (int idx = 0;  idx < wordInProgress.length(); idx++) {
										if (Character.isJavaIdentifierPart(wordInProgress.charAt(idx))) {
											wordInProgressNoPunc.append(wordInProgress.charAt(idx));
										}
										else {
											HandleWordInProgressNoPunc(wordInProgressNoPunc, punctuations, brokenwordsList);	
											punctuations.append(wordInProgress.charAt(idx));	
										}
									}
									// Case space immediately after wordInProgressNoPunc
									HandleWordInProgressNoPunc(wordInProgressNoPunc, punctuations, brokenwordsList);
									
									// Case punctuation marks after wordInProgressNoPunc: simply append these 
									if (punctuations.length() > 0) {
										if (brokenwordsList.size() > 0) {
											brokenwordsList.set(brokenwordsList.size() - 1, brokenwordsList.get(brokenwordsList.size() - 1) + punctuations.toString());
										}
										else {
											brokenwordsList.add(punctuations.toString());
										}
									}
									
									breakLoop = false;
				
									for (int idx = 0; idx < brokenwordsList.size(); idx++) {
										String str = brokenwordsList.get(idx);
									//	int brokenWordWidth = g2d.getFontMetrics().charsWidth(str.toCharArray(), 0, str.length());
										float brokenWordWidth = paint.measureText(str);

										float requiredWidth = (idx < brokenwordsList.size() - 1)? brokenWordWidth + hyphenWidth: brokenWordWidth;
										
										if (lineWidth + requiredWidth <= boundingRect.width()) {
											lineInProgress.append(str);
											lineWidth += brokenWordWidth;
										}		
										else {
											if (idx > 0) {
												lineInProgress.append(hyphen);
												lineWidth += hyphenWidth;		
											}
											txtRectDets.setText(lineInProgress.toString());	
											txtRectDets.setTextWidth((int)lineWidth);
											
											// BEWARE: not enough space in previous rectangle does not necessarily mean there will not be enough in any of next rectangles.
											boolean badFit = true;
											
											while (rectLstIterator.hasNext() && badFit) {
												txtRectDets = rectLstIterator.next();
												boundingRect = txtRectDets.getBoundingRect();
												
												// make sure not last. Otherwise mustn't affix hyphen width
												if (requiredWidth <= boundingRect.width()) {
													badFit = false;
												}
											}
											
											if (!rectLstIterator.hasNext()) {
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
										txtLineDets.setAllTextFits(true);
									}			
								}	
								else {
									txtRectDets.setText(lineInProgress.toString());	
									txtRectDets.setTextWidth((int)lineWidth);
									lineInProgress.setLength(0);
									lineWidth = 0;
									
									// BEWARE: not enough space in this rectangle does not necessarily mean there will not be enough in next rectangle.
									// Rectangles start by getting wider
									boolean badFit = true;
								//	int currentWidth = txtRectDets.getBoundingRect().width;
									
									while (rectLstIterator.hasNext() && badFit) {
										txtRectDets = rectLstIterator.next();
										boundingRect = txtRectDets.getBoundingRect();
										
										if (wordWidth <= boundingRect.width()) {
											badFit = false;
										}
										
									//	currentWidth = txtRectDets.getBoundingRect().width;
									}
									
									if (!badFit && i == data.length() - 1) {
										txtLineDets.setAllTextFits(true);
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
						txtRectDets.setText(lineInProgress.toString());	
						txtRectDets.setTextWidth((int)lineWidth);
						lineInProgress.setLength(0);
						wordInProgress.setLength(0);
						lineWidth = 0;
						// here end of line set to true
						txtRectDets.setEndOfLine();
						txtLineDets.setAllTextFits(true);
					}
					else {
						// We couln't place last piece of text...
						txtLineDets.setAllTextFits(false);
					}
					
					// have to make sure next rectangle is lower - top 2 rectangles have same y coordinate.
					int boundingRectY = boundingRect.bottom;
					boolean yChanged = false;
					while (rectLstIterator.hasNext() && !yChanged) {	
						txtRectDets = rectLstIterator.next();
						boundingRect = txtRectDets.getBoundingRect();
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

			for (TextRectDetails txtRectDets : rectLst) {
				Rect txtBoundingRect = txtRectDets.getBoundingRect();
				String txt = txtRectDets.getText();

				if (txt != null && txt.length() > 0) {
					if (tfd.getOptimizeSpacing()) {
						int availableWidth = txtRectDets.getBoundingRect().width();
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
						// g2d.drawChars(txt.toCharArray(), 0, txt.length(), txtBoundingRect.x, txtBoundingRect.y + txtBoundingRect.height - textDescent);
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

	void resetTextInputDefails() {
		textInputDetails.reset();
	}
	
	private final Canvas g2d;
	private final Paint paint;
	private final MainSizes mainSizes;
	//private final HeartDetails hd;
	private final ShapeDetails sd;
	private final TextFormattingDetails tfd;
	private final float textAscent;
	//private final float textDescent;
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
}
