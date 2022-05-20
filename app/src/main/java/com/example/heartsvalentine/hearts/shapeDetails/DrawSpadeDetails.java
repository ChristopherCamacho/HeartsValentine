package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class DrawSpadeDetails implements DrawShapeDetails {

    // SquareSide is the side of the square that entirely contains spade.
    // It's the same as height but width is narrower.
    private final float squareSide;
    private final float width;
    private final float radius;
    private final Path path = new Path();

    private int color;

    static private boolean alphaBetaComputed = false;
    static private float alpha;
    static private float beta;

    public DrawSpadeDetails(int color, int size) {
        this.color = color;
        squareSide = size;
        width = 0.8f * squareSide;
        radius = 0.28f * width;

        initializeAlphaBeta(width, squareSide, radius);
    }

    static void initializeAlphaBeta(float width, float height, float radius) {
        if (!alphaBetaComputed) {
            alphaBetaComputed = true;
            // Start angle of left heart curve is given by:
            alpha = (float) (Math.acos((width / 2.0 - radius) / radius) * 180 / Math.PI);
            double phi = Math.atan((height - 1.5 * radius) / (width / 2.0 - radius));
            // distance l between centre of heart (Cx, Cy) and bottom point of heart (BPx, BPy) is
            // SQRT((PBx - Cx)pow2 + (BPy - Cy)pow2)
            // Cx = x + radius
            // Cy = y + radius
            // BPx = x + width/2.0
            // PBy = y + height
            // this is:
            // l = Math.sqrt(Math.pow(x + width/2.0 - (x + radius), 2) + Math.pow(y + height - (y + radius), 2));
            // Which can be simplified into:
            double l = Math.sqrt(Math.pow(width / 2.0 - radius, 2) + Math.pow(height - 1.5 * radius, 2));
            double zeta = Math.acos(radius / l);
            double betaRadian = Math.max(phi + zeta, phi - zeta);
            beta = (float) (betaRadian * 180.0 / Math.PI) + 90;
        }
    }

    public void draw(Canvas canvas, float x, float y, Paint paint) {
        y += squareSide;

        float leftXCoordinate = x + (squareSide - width)/2.0f;
        float rightXCoordinate = leftXCoordinate + width;
        RectF leftRect = new RectF(leftXCoordinate, y + squareSide - 2.5f * radius, leftXCoordinate + 2 * radius, y + squareSide - 0.5f * radius);
        RectF rightRect = new RectF(rightXCoordinate - 2 * radius,  y + squareSide - 2.5f * radius, rightXCoordinate, y + squareSide - 0.5f * radius);

        RectF leftBottomRect = new RectF(x + squareSide/2.0f - 2 * radius, y + squareSide - 2f * radius, x + squareSide/2.0f, y + squareSide);
        RectF rightBottomRect = new RectF(x + squareSide/2.0f,  y + squareSide - 2f * radius, x + squareSide/2.0f + 2 * radius, y + squareSide);

        path.reset();
        path.moveTo(x + 0.5f * squareSide, y );

        path.arcTo(leftRect, beta, (alpha - beta));
        path.arcTo(rightRect, -alpha + 180, -(-alpha + beta));
        path.lineTo(x + 0.5f * squareSide, y);

        // Draw bottom tongue
        path.moveTo(x + squareSide/2.0f, y + squareSide - radius);
        path.arcTo(leftBottomRect, 0, 90);
        path.lineTo(x + squareSide/2.0f + radius, y+ squareSide);
        path.arcTo(rightBottomRect, 90, 90);
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
