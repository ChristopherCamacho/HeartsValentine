package com.example.heartsvalentine.hearts;

import java.util.ArrayList;
import java.util.List;

public final class TextInputDetails {
	
	TextInputDetails() {
		initialized = false;
	}
	
	void initialise(String contentText) {

		String[] lineDetailsArray = contentText.split( "\\r?\\n");

		for (String str : lineDetailsArray) {
			lineDetailsLst.add(new TextLineDetails(str));
		}

		initialized = true;
	}
	
	boolean isInitialized() {
		return initialized;
	}

	void reset() {
		lineDetailsLst.clear();
		initialized = false;
	}
	
	void resetAllTextFits() {
		for(TextLineDetails lineDetails : lineDetailsLst) {  
			lineDetails.setAllTextFits(true);
		}
	}
	
	boolean allTextFits() {
		for (int i = 0; i < lineDetailsLst.size(); i++) {
			if (!lineDetailsLst.get(i).getAllTextFits()) {  
				return false;
			}
		}
		return true;
	}
	
	TextLineDetails getLineDetails(int idx) {
		return lineDetailsLst.get(idx);
	}

	int count() {
		return lineDetailsLst.size();
	}

	private final List<TextLineDetails> lineDetailsLst = new ArrayList<>();
	private boolean initialized;
}
