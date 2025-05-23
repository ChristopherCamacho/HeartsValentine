package com.example.heartsvalentine.hyphens;

import android.content.Context;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Hyphenator {

	public Hyphenator(String language, Context context) {
		loadPatterns(language, context);
		minLength = 5;
		minLeading = 2;
		minTrailing = 3;
		endsMarker = '.';
	}

	private void loadPatterns(String language, Context context) {
		ResourceHyphenatePatternsLoader rhl = new ResourceHyphenatePatternsLoader(language, context);
		rhl.load();

		populatePatternsMapFromList(rhl.getPatterns());
		populateExceptionsMapFromList(rhl.getExceptions());
	}

	private void populateExceptionsMapFromList(List<String> exceptions) {
		for (String str : exceptions ) {
			String[] lst = str.split("-");
			StringBuilder stringBuilderNoHyphen = new StringBuilder();

			for (String s : lst) {
				stringBuilderNoHyphen.append(s);
			}

			this.exceptions.put(stringBuilderNoHyphen.toString(), lst);
		}
	}
	 
	 public void addException(String[] lst) {
		 StringBuilder stringBuilderNoHyphen = new StringBuilder();

		 for (String s : lst) {
			 stringBuilderNoHyphen.append(s);
		 }

		 this.exceptions.put(stringBuilderNoHyphen.toString(), lst);
	 }
	 
	 private void populatePatternsMapFromList(List<String> patterns) {
		 StringBuilder sb = new StringBuilder();
		
		 for (String str : patterns ) {
			 
			 int idx = 0;
			 int[] ints = new int[str.length()];
			 
			 for (int i = 0; i < str.length(); i++) {
				 if (Character.isDigit(str.charAt(i))) {
					 ints[idx] = Character.getNumericValue(str.charAt(i));
				 }
				 else {
					 sb.append(str.charAt(i));
					 idx++;
				 }
			 }
			 
			 String syllable = sb.toString();
			 sb.setLength(0);
			 
			 int[] ints2 = new int[syllable.length() + 1];
			 int intsLength = Math.min(ints.length, ints2.length);
			 System.arraycopy(ints, 0, ints2, 0, intsLength);
			 
			 this.patterns.put(syllable, ints2);
		 }	
	 }
	 
	 public String[] hyphernateWord(String word) {
		 if (exceptions.containsKey(word)) {
			 String[] strArr = exceptions.get(word);
			 return (strArr == null)? null : strArr.clone(); // Need to make clone otherwise you can tamper with an exception stored here.
		 }
		 if (word.length() < minLength) {
			 String[] retVal = new String[1];
			 retVal[0] = word;
			 return retVal;
		 }

		 int[] hyphenIntArr = GetHyphernateIntArrayFromPatterns(word);
		 int startWordIdx = 0;
		 ArrayList<String> hyphens = new ArrayList<>();
		 
		 for (int idx = 0; idx < hyphenIntArr.length; idx++) {
			 if (hyphenIntArr[idx]%2 != 0) {
				 hyphens.add(word.substring(startWordIdx, idx - 1));
				 startWordIdx = idx - 1;
			 }
		 }
		 hyphens.add(word.substring(startWordIdx));
		 
		 return hyphens.toArray(new String[0]); 
	 }
	 // Takes a word.
	 // Using Knuth Liang algorithm, returns a boolean array of the hyphen points.
	 private int[] GetHyphernateIntArrayFromPatterns(String word) {
		 String hyphenableWord = new StringBuilder().append(endsMarker).append(word).append(endsMarker).toString();
		 int[] levels = new int[hyphenableWord.length()];
		 
		 for (int i = 0; i < hyphenableWord.length() - 2; i++) {
			 for (int j = i + 1; j < hyphenableWord.length(); j++) {
				 String subWord = hyphenableWord.substring(i, j);
				 
				 if (patterns.containsKey(subWord)) {
					 int[] subLevels = patterns.get(subWord);

					 if (subLevels != null) {
						 for (int k = 0; k < subLevels.length; k++) {
							 if (subLevels[k] > levels[i + k]) {
								 levels[i + k] = subLevels[k];
							 }
						 }
					 }
				 } 
			 }
		 }

		 // I have assumed here that for all languages 1st and last character cannot be hyphenated.
		 // This may be language dependent?
		 for (int i = 0; i <= minLeading; i++) {
			 levels[i] = 0;
		 }
		 for (int i = levels.length - minTrailing + 1; i < levels.length; i++) {
			 levels[i] = 0;
		 }
	 
		 return levels;
	 }

	private final int minLength;
	private final int minLeading;
	private final int minTrailing;
	private final char endsMarker;
	// internal hyphen - separate frm external, display hyphen
    private final Map<String, String[]> exceptions = new HashMap<>();
    private final Map<String, int[]> patterns = new HashMap<>();
    
}
