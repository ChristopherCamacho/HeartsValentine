package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.Canvas;
import android.graphics.Paint;

public class DrawSquareDetails implements DrawShapeDetails {

    private float centerX;
    private float centerY;
    private final float widthHeight;
    private int color;

    public DrawSquareDetails(int color, float widthHeight) {
        this.color = color;
        this.widthHeight = widthHeight;
        initialize();
    }

    private void initialize() {
        centerX = 0.5f * widthHeight;
        centerY = 0.5f * widthHeight;
    }

    @Override
    public void draw(Canvas canvas, float x, float y, Paint paint) {
        y += widthHeight;
        canvas.drawRect(x, y, x + widthHeight, y + widthHeight, paint);
    }

    @Override
    public float getCenterX() {
        return centerX;
    }

    @Override
    public float getCenterY() {
        return centerY;
    }

    @Override
    public float getWidth() {
        return widthHeight;
    }

    @Override
    public float getHeight() {
        return widthHeight;
    }

    @Override
    public float getBottomAdjustment() { return -2 * widthHeight; }

    @Override
    public int getClosestDistance() {
        return 150;
    }

    @Override
    public void setColor(int color) { this.color = color; }

    @Override
    public int getColor() {
        return color;
    }
}


