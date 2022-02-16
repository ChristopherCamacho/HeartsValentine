package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.Canvas;
import android.graphics.Paint;

public class DrawCircleDetails implements DrawShapeDetails {

    private float centerX;
    private float centerY;
    private final float width;
    private float height;
    private int color;

    public DrawCircleDetails(int color, int width) {
        this.color = color;
        this.width = width;
        initialize();
    }

    private void initialize() {
        height = width;
        centerX = 0.5f * width;
        centerY = 0.5f * height;
    }


    @Override
    public void draw(Canvas canvas, float x, float y, Paint paint) {
        y += height;
        canvas.drawArc(x, y, x + width, y + height, 0, 360, true, paint);
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
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getBottomAdjustment() { return -2 * height; }

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

