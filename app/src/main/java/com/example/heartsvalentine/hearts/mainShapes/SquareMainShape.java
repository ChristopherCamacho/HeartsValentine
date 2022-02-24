package com.example.heartsvalentine.hearts.mainShapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.example.heartsvalentine.hearts.mainSizes.SquareMainSizes;
import com.example.heartsvalentine.hearts.shapeDetails.DrawShapeDetails;
import com.example.heartsvalentine.hearts.shapeDetails.EmojiShapeDetails;
import com.example.heartsvalentine.hearts.shapeDetails.ShapeDetails;

public class SquareMainShape implements MainShape {
    SquareMainSizes mainSizes;
    private final int closestDistance;
    private final Canvas canvas;
    ShapeDetails sd;

    public SquareMainShape(Canvas canvas, SquareMainSizes mainSizes, int closestDistance, ShapeDetails sd) {
        this.canvas = canvas;
        this.mainSizes = mainSizes;
        this.closestDistance = closestDistance;
        this.sd = sd;
    }

    public void draw() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Typeface tf = Typeface.create("TimesRoman", Typeface.NORMAL);
        paint.setTypeface(tf);
        paint.setTextSize(150);
        float horizontalAdjustment = 0;
        float verticalAdjustment = 0;

        if (sd instanceof DrawShapeDetails) {
            paint.setColor(((DrawShapeDetails) sd).getColor());
        } else if (sd instanceof EmojiShapeDetails) {
            horizontalAdjustment = ((EmojiShapeDetails) sd).getHorizontalAdjustment();
            verticalAdjustment = ((EmojiShapeDetails) sd).getVerticalAdjustment();
        }

        int SquareLen = (int)(mainSizes.getWidth() - 2 * mainSizes.getMargin() - sd.getWidth());
        float topYCoordinate = mainSizes.getHeight() + sd.getBottomAdjustment() - mainSizes.getMargin() + verticalAdjustment;
        float bottomYCoordinate = sd.getBottomAdjustment() + sd.getHeight() + mainSizes.getMargin() + verticalAdjustment;

        float leftXCoordinate = mainSizes.getMargin() + horizontalAdjustment;
        float rightXCoordinate = mainSizes.getWidth() - mainSizes.getMargin() - sd.getWidth() + horizontalAdjustment;

        int shapeSideCount = SquareLen/closestDistance;
        float distShapeLen = (float)SquareLen/shapeSideCount;

        float xPlotPt;
        int shapePos = 0;

        do {
            xPlotPt = leftXCoordinate + shapePos*distShapeLen;
            sd.draw(canvas, xPlotPt, topYCoordinate, paint);
            sd.draw(canvas, xPlotPt, bottomYCoordinate, paint);
        } while (++shapePos <= shapeSideCount);

        shapePos = 1;
        float yPlotPt;

         do {
            yPlotPt = bottomYCoordinate + shapePos*distShapeLen;
            sd.draw(canvas, leftXCoordinate, yPlotPt, paint);
            sd.draw(canvas, rightXCoordinate, yPlotPt, paint);
        } while (++shapePos < shapeSideCount);
    }
}
