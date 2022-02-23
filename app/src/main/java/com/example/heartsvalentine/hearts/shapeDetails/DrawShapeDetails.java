package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.Canvas;
import android.graphics.Paint;

public interface DrawShapeDetails extends ShapeDetails {
    int getColor();
    void setColor(int color);
    // Draws lines inside shape so looks like shape has text written inside
    void drawWriting(Canvas canvas, float x, float y, Paint paint);
}
