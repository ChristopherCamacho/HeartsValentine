package com.example.heartsvalentine.hearts;

class MainSizes {

	MainSizes(int margin) {
		width = 0;
		height = 0;
		radius = 0;	
		this.margin = margin;
	}
	
	void resetWidthHeightRadius(int newWidth) {
		width = newWidth;
		int widthInsideFrame = width - 2 * margin;
		height = (int) (0.96 * widthInsideFrame) + 2 * margin;
		radius = (int) (widthInsideFrame * 0.28);	
	}
	
	int getWidth() {
		return width;
	}
	
	int getHeight() {
		return height;
	}
	
	int getRadius() {
		return radius;
	}
	
	int getMargin() {
		return margin;
	}
	
	private int width;
	private int height;
	private int radius;	
	final private int margin;
}
