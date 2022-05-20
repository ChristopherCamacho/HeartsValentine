package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class DrawDiamondDetails implements DrawShapeDetails {
    // SquareSide is the side of the square that entirely contains square.
    // It's the same as height but width is narrower.
    private final float squareSide;
    private final float width;
    private final Path path = new Path();
    private int color;

    public DrawDiamondDetails(int color, int size) {
        this.color = color;
        squareSide = size;
        width = 0.7f * squareSide;
    }

    public void draw(Canvas canvas, float x, float y, Paint paint) {
        y += squareSide;
        path.reset();
        path.moveTo(x + 0.5f * squareSide, y);
        path.lineTo(x + 0.5f * (squareSide - width), y + 0.5f * squareSide);
        path.lineTo(x + 0.5f * squareSide, y + squareSide);
        path.lineTo(x +  0.5f * (squareSide + width), y + 0.5f * squareSide);
        path.lineTo(x + 0.5f * squareSide, y);
        canvas.drawPath(path, paint);
    }

    public float getCenterX() {
        return squareSide/2.0f;
    }

    public float getCenterY() {
        return squareSide/2.0f;
    }

    public float getWidth() {
        return squareSide;
    }

    public float getHeight() {
        return squareSide;
    }

    public float getBottomAdjustment() {
        return -2 * squareSide;
    }

    public int getClosestDistance() {
        return 150;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        this.color = color;
    }

    // Draws lines inside shape so looks like shape has text written inside
    public void drawWriting(Canvas canvas, float x, float y, Paint paint) {
    }
}






