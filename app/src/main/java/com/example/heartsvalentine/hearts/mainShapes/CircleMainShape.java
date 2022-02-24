package com.example.heartsvalentine.hearts.mainShapes;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.example.heartsvalentine.hearts.mainSizes.SquareMainSizes;
import com.example.heartsvalentine.hearts.shapeDetails.DrawShapeDetails;
import com.example.heartsvalentine.hearts.shapeDetails.EmojiShapeDetails;
import com.example.heartsvalentine.hearts.shapeDetails.ShapeDetails;

public class CircleMainShape implements MainShape {
    SquareMainSizes mainSizes;
    private final int closestDistance;
    private final Canvas canvas;
    ShapeDetails sd;

    public CircleMainShape(Canvas canvas, SquareMainSizes mainSizes, int closestDistance, ShapeDetails sd) {
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
        }
        else if (sd instanceof EmojiShapeDetails) {
            horizontalAdjustment = ((EmojiShapeDetails) sd).getHorizontalAdjustment() - 3;
            verticalAdjustment = ((EmojiShapeDetails) sd).getVerticalAdjustment();
        }

        float ptCentreX = mainSizes.getWidth()/2.0f;
        float ptCentreY = mainSizes.getHeight()/2.0f;

        double startAngle = 0; // Math.PI/2.0;

        // There should be very little difference between these but still visible in case width and height of shape
        float horizontalRadius = mainSizes.getWidth()/2.0f - mainSizes.getMargin() - sd.getWidth()/2.0f;
        float verticalRadius = mainSizes.getHeight()/2.0f - mainSizes.getMargin() - sd.getHeight()/2.0f;

        double alpha = 2*Math.asin(closestDistance/(2.0*horizontalRadius));

        int numShapesPerCircle = (int)Math.ceil(2*Math.PI/alpha);

        double beta = 2*Math.PI/numShapesPerCircle;

        for (int i = 0; i < numShapesPerCircle; i++) {
            float xPt = ptCentreX + horizontalRadius*(float)Math.cos(startAngle + i*beta) - sd.getWidth()/2.0f + horizontalAdjustment;
            float yPt = ptCentreY + verticalRadius*(float)Math.sin(startAngle + i*beta) + sd.getBottomAdjustment() + sd.getHeight()/2.0f  + verticalAdjustment;
            sd.draw(canvas, xPt, yPt, paint);
        }
    }
}
