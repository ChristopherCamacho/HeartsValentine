package com.example.heartsvalentine.hearts;

public final class UserExceptionDetails {
	UserExceptionDetails(String word, int wordWidth, int handlerRet) {
		this.word = word;
		this.wordWidth = wordWidth;
		this.handlerRet = handlerRet;
	}
	
	String getWord() {
		return word;
	}
	
	int getWordWidth() {
		return wordWidth;	
	}

	int getHandlerRet() {
		return handlerRet;
	}
	
	private final String word;
	private final int wordWidth;
	private final int handlerRet;
}
