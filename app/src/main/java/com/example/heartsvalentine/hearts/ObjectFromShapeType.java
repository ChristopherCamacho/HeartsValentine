package com.example.heartsvalentine.hearts;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.heartsvalentine.ShapeType;
import com.example.heartsvalentine.hearts.mainShapes.HeartMainShape;
import com.example.heartsvalentine.hearts.mainShapes.MainShape;
import com.example.heartsvalentine.hearts.mainSizes.HeartMainSizes;
import com.example.heartsvalentine.hearts.mainSizes.MainSizes;
import com.example.heartsvalentine.hearts.shapeDetails.ShapeDetails;
import com.example.heartsvalentine.hearts.textBoundaries.HeartTextBoundary;
import com.example.heartsvalentine.hearts.textBoundaries.TextBoundaries;

public class ObjectFromShapeType {
    public static MainSizes getMainSizeFromShapeType(ShapeType st, int margin) {
        switch (st) {
            case StraightHeart:
                return new HeartMainSizes(margin);
        }
        return null;
    }

    public static TextBoundaries getTextBoundariesFromShapeType(ShapeType st, Paint paint, MainSizes mainSizes, ShapeDetails sd, TextFormattingDetails tfd) {

        switch (st) {
            case StraightHeart:
                if (mainSizes instanceof HeartMainSizes) {
                    return new HeartTextBoundary(paint, (HeartMainSizes)mainSizes, sd, tfd);
                }
        }
        return null;
    }

    public static MainShape getMainShape(ShapeType st, Canvas canvas, MainSizes mainSizes, int closestDistance, ShapeDetails sd) {

        switch (st) {
            case StraightHeart:
                if (mainSizes instanceof HeartMainSizes) {
                    return new HeartMainShape(canvas, (HeartMainSizes)mainSizes, closestDistance, sd);
                }
        }
        return null;
    }
}
