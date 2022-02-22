package com.example.heartsvalentine.hearts.mainSizes;

public class SquareMainSizes implements MainSizes {
    private int widthHeight;
    final private int margin;

    public SquareMainSizes(int margin) {
        widthHeight = 0;
        this.margin = margin;
    }

    public void resetSizes(int newWidthHeight) {
        widthHeight = newWidthHeight;
    }

    public int getWidth() {
        return widthHeight;
    }
    public int getHeight() {
        return widthHeight;
    }
    public int getMargin() {
        return margin;
    }
}
