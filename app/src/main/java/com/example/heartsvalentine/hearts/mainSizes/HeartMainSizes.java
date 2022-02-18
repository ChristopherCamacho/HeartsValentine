package com.example.heartsvalentine.hearts.mainSizes;

public final class HeartMainSizes implements MainSizes {
    private int width;
    private int height;
    private int radius;
    final private int margin;

    public HeartMainSizes(int margin) {
        width = 0;
        height = 0;
        radius = 0;
        this.margin = margin;
    }

    public void resetSizes(int newWidth) {
        width = newWidth;
        int widthInsideFrame = width - 2 * margin;
        height = (int) (0.96 * widthInsideFrame) + 2 * margin;
        radius = (int) (widthInsideFrame * 0.28);
    }

    public int getWidth() {
        return width;
    }
    public int getHeight() {
        return height;
    }
    public int getMargin() {
        return margin;
    }
    public int getRadius() {
        return radius;
    }
}
