package com.example.heartsvalentine.hearts;

import android.graphics.Rect;


public class TextRectDetails {
	TextRectDetails(Rect boundingRect) {
		this.boundingRect = boundingRect;
		endOfLine = false;
	}	
	void setText(String text) {
		this.text = text;
	}	
	void setTextWidth(int textWidth) {
		this.textWidth = textWidth;
	}
	void setEndOfLine() {
		this.endOfLine = true;
	}
	Rect getBoundingRect(){
		return boundingRect;
	}
	String getText() {
		return text;
	}	
	int getTextWidth() {
		return textWidth;
	}
	boolean getEndOfLine() {
		return endOfLine;
	}
	
	private final Rect boundingRect;
	private String text;
	private int textWidth;
	private boolean endOfLine;
}
