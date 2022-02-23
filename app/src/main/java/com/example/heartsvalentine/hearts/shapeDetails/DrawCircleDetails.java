package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

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
    public void drawWriting(Canvas canvas, float x, float y, Paint paint) {
        Path line = new Path();
        line.moveTo(x, 0);
        line.lineTo(x + width, 0);
        int vLineShift = 3;

        line.moveTo(x, height/6f + vLineShift);
        line.lineTo(x + width, height/6f + vLineShift);

        line.moveTo(x, 2*height/6f + vLineShift);
        line.lineTo(x + width, 2*height/6f + vLineShift);

        line.moveTo(x, 3*height/6f + vLineShift);
        line.lineTo(x + width, 3*height/6f + vLineShift);

        line.moveTo(x, 4*height/6f + vLineShift);
        line.lineTo(x + width, 4*height/6f + vLineShift);

        line.moveTo(x, 5*height/6f + vLineShift);
        line.lineTo(x + width, 5*height/6f + vLineShift);

        line.moveTo(x, 6*height/6f + vLineShift);
        line.lineTo(x + width, 6*height/6f + vLineShift);

        canvas.save();
        Path path = new Path();
        y += height;
        path.addArc(x, y, x + width, y + height, 0, 360);
        canvas.clipPath(path);
        canvas.drawPath(line, paint);
        canvas.restore();
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

