package com.example.heartsvalentine.hearts;

//import Hyphens.HyphenatePatternsLanguage;

public final class TextFormattingDetails {
	
	public TextFormattingDetails(String contentText, boolean optimizeSpacing, boolean hyphenateText,
								 String hyphenPatternLan, int topTextMargin, int lineHeight, int txtHeartsMargin, int txtColor) {
		this.contentText = contentText;
		this.optimizeSpacing = optimizeSpacing;
		this.hyphenPatternLan = hyphenPatternLan;
		this.topTextMargin = topTextMargin;
		this.lineHeight = lineHeight;
		this.txtHeartsMargin_ = txtHeartsMargin;
		this.txtColor = txtColor;

		if (this.hyphenPatternLan == null) {
			this.hyphenateText = false;
		}
		else {
			this.hyphenateText = hyphenateText;
		}
	}

	String getContentText() {
		return contentText;
	}

	boolean getOptimizeSpacing() {
		return optimizeSpacing;
	}

	boolean getHyphenateText() {
		return hyphenateText;
	}

	String gethyphenPatternLan() {
		return hyphenPatternLan;
	}

	int getTopTextMargin() {
		return topTextMargin;
	}

	int getLineHeight() {
		return lineHeight;
	}

	int getTxtHeartsMargin() {
		return txtHeartsMargin_;
	}

	int getTxtColour() {
		return txtColor;
	}

	private final String contentText;
	private final boolean optimizeSpacing;
	private final boolean hyphenateText;
	private final String hyphenPatternLan;
	private final int topTextMargin;
	private final int lineHeight;
	private final int txtHeartsMargin_; // The margin between heart frame and actual text. Can be 0.
	private final int txtColor;
}
