package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.Canvas;
import android.graphics.Paint;

// This interface is for the shapes in frame
public interface ShapeDetails {
    void draw(Canvas canvas, float x, float y, Paint paint);
    float getCenterX();
    float getCenterY();
    float getWidth();
    float getHeight();
    float getBottomAdjustment();
    int getClosestDistance();
}
