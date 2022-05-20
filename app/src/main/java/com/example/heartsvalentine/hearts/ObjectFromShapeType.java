package com.example.heartsvalentine.hearts;

import android.graphics.Canvas;
import android.graphics.Paint;

import com.example.heartsvalentine.frameShapes.ShapeType;
import com.example.heartsvalentine.hearts.mainShapes.CircleMainShape;
import com.example.heartsvalentine.hearts.mainShapes.HeartMainShape;
import com.example.heartsvalentine.hearts.mainShapes.MainShape;
import com.example.heartsvalentine.hearts.mainShapes.SquareMainShape;
import com.example.heartsvalentine.hearts.mainSizes.HeartMainSizes;
import com.example.heartsvalentine.hearts.mainSizes.MainSizes;
import com.example.heartsvalentine.hearts.mainSizes.SquareMainSizes;
import com.example.heartsvalentine.hearts.shapeDetails.ShapeDetails;
import com.example.heartsvalentine.hearts.textBoundaries.CircleTextBoundaries;
import com.example.heartsvalentine.hearts.textBoundaries.HeartTextBoundaries;
import com.example.heartsvalentine.hearts.textBoundaries.SquareTextBoundaries;
import com.example.heartsvalentine.hearts.textBoundaries.TextBoundaries;

public class ObjectFromShapeType {
    public static MainSizes getMainSizeFromShapeType(ShapeType st, int margin) {
        switch (st) {
            case StraightHeart:
                return new HeartMainSizes(margin);
            case Square:
            case Circle:
                return new SquareMainSizes(margin);
        }
        return null;
    }

    public static TextBoundaries getTextBoundariesFromShapeType(ShapeType st, Paint paint, MainSizes mainSizes, ShapeDetails sd, TextFormattingDetails tfd) {

        switch (st) {
            case StraightHeart:
                if (mainSizes instanceof HeartMainSizes) {
                    return new HeartTextBoundaries(paint, (HeartMainSizes)mainSizes, sd, tfd);
                }
            case Square:
                if (mainSizes instanceof SquareMainSizes) {
                    return new SquareTextBoundaries(paint, (SquareMainSizes)mainSizes, sd, tfd);
                }
            case Circle:
                if (mainSizes instanceof SquareMainSizes) {
                    return new CircleTextBoundaries(paint, (SquareMainSizes)mainSizes, sd, tfd);
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
            case Square:
                if (mainSizes instanceof SquareMainSizes) {
                    return new SquareMainShape(canvas, (SquareMainSizes)mainSizes, closestDistance, sd);
                }
            case Circle:
                if (mainSizes instanceof SquareMainSizes) {
                    return new CircleMainShape(canvas, (SquareMainSizes)mainSizes, closestDistance, sd);
                }
        }
        return null;
    }
}
