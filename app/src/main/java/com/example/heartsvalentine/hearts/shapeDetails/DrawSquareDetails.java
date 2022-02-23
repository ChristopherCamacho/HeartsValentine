package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

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
    public void drawWriting(Canvas canvas, float x, float y, Paint paint) {
        Path line = new Path();
        line.moveTo(x, 0);
        line.lineTo(x + widthHeight, 0);
        int vLineShift = 3;

        line.moveTo(x, widthHeight/6f + vLineShift);
        line.lineTo(x + widthHeight, widthHeight/6f + vLineShift);

        line.moveTo(x, 2*widthHeight/6f + vLineShift);
        line.lineTo(x + widthHeight, 2*widthHeight/6f + vLineShift);

        line.moveTo(x, 3*widthHeight/6f + vLineShift);
        line.lineTo(x + widthHeight, 3*widthHeight/6f + vLineShift);

        line.moveTo(x, 4*widthHeight/6f + vLineShift);
        line.lineTo(x + widthHeight, 4*widthHeight/6f + vLineShift);

        line.moveTo(x, 5*widthHeight/6f + vLineShift);
        line.lineTo(x + widthHeight, 5*widthHeight/6f + vLineShift);

        line.moveTo(x, 6*widthHeight/6f + vLineShift);
        line.lineTo(x + widthHeight, 6*widthHeight/6f + vLineShift);

        canvas.drawPath(line, paint);
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


