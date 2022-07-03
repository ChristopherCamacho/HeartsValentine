package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

public class DrawSmileyDetails implements DrawShapeDetails {

    private float centerX;
    private float centerY;
    private final float side;
    private int color;

    public DrawSmileyDetails(int color, int side) {
        this.color = color;
        this.side = side;
        initialize();
    }

    private void initialize() {
        centerX = 0.5f * side;
        centerY = 0.5f * side;
    }

    @Override
    public void draw(Canvas canvas, float x, float y, Paint paint) {
        y += side;

        Path path = new Path();
        // draw face disc
        path.addCircle(x + centerX, y + centerY, 0.5f * side, Path.Direction.CCW);

        // draw eyes
        float eyeRadius = side / 9.0f;
        float eyeHeight = 0.65f * side;
        float eyeLeftRightDisplacement = 0.19f * side;
        path.addCircle(x + centerX - eyeLeftRightDisplacement, y + side - eyeHeight, eyeRadius, Path.Direction.CW);
        path.addCircle(x + centerX + eyeLeftRightDisplacement, y + side - eyeHeight, eyeRadius, Path.Direction.CW);

        // draw mouth
        float lowerLipCircleCentre = 0.44f * side;
        float lowerLipRadius = 0.255f * side;

       float smileAngle = 10;

        double rightAngleRad = smileAngle*Math.PI/180;
        path.moveTo(x + centerX + (float)(lowerLipRadius*Math.cos(rightAngleRad)), y + side - lowerLipCircleCentre+ (float)(lowerLipRadius*Math.sin(rightAngleRad)));

        path.addArc(x + centerX - lowerLipRadius, y + side - lowerLipCircleCentre - lowerLipRadius,
                   x + centerX + lowerLipRadius, y + side - lowerLipCircleCentre + lowerLipRadius, smileAngle, 180 - 2 * smileAngle);

        double leftAngleRad = (180 - smileAngle)*Math.PI/180;
        path.lineTo(x + centerX + (float)(lowerLipRadius*Math.cos(leftAngleRad)), y + side - lowerLipCircleCentre+ (float)(lowerLipRadius*Math.sin(leftAngleRad)));

        float upperLipCircleCentre = 0.473f * side;
        float upperLipRadius = 0.207f * side;

        path.lineTo(x + centerX + (float)(upperLipRadius*Math.cos(leftAngleRad)), y + side - upperLipCircleCentre+ (float)(upperLipRadius*Math.sin(leftAngleRad)));

        path.addArc(x + centerX - upperLipRadius, y + side - upperLipCircleCentre - upperLipRadius,
                x + centerX + upperLipRadius, y + side - upperLipCircleCentre + upperLipRadius, 180 - smileAngle, -(180 - 2 * smileAngle) );

        path.lineTo(x + centerX + (float)(upperLipRadius*Math.cos(rightAngleRad)), y + side - upperLipCircleCentre+ (float)(upperLipRadius*Math.sin(rightAngleRad)));

        path.lineTo(x + centerX + (float)(lowerLipRadius*Math.cos(rightAngleRad)), y + side - lowerLipCircleCentre+ (float)(lowerLipRadius*Math.sin(rightAngleRad)));

        canvas.drawPath(path, paint);
    }

    @Override
    public void drawWriting(Canvas canvas, float x, float y, Paint paint) {
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
        return side;
    }

    @Override
    public float getHeight() {
        return side;
    }

    @Override
    public float getBottomAdjustment() { return -2 * side; }

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