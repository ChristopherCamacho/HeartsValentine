package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class DrawClubDetails implements DrawShapeDetails {
    // SquareSide is the side of the square that entirely contains spade.
    // It's the same as height but width is narrower.
    private final float size;
   // private final float width;
    private final float radius;
    // stemLen is the distance of the stem joining the 3 leaves
    private final float stemLen;
    private final float halfStemThickness;
    private final Path path = new Path();

    private int color;

    static private boolean anglesComputed = false;
    static private float topStartAngle;
    static private float topSweepAngle;

    public DrawClubDetails(int color, int size) {
        this.color = color;
        this.size = size;
        radius = 0.24f * size;
        stemLen = 0.3f * size;
        halfStemThickness = 0.02f * size;

        initializeAngles(this);
    }

    static private float getAngle(float radius, float dist) {
        return (float) (Math.acos(radius / dist) * 180 / Math.PI);
    }

    static private void initializeAngles(DrawClubDetails dcd) {
        if (!anglesComputed) {
            anglesComputed = true;
            // top angles
            float dist = dcd.size - dcd.stemLen - dcd.radius;
            float topAngles = getAngle(dcd.radius, dist);
            topStartAngle = topAngles + 90f;
            topSweepAngle = 360 - 2 * topAngles;
        }
    }

    public void draw(Canvas canvas, float x, float y, Paint paint) {
        y += size;

        RectF topRect = new RectF(x + size/2.0f - radius, y, x + size/2.0f + radius, y + 2f * radius);
        RectF leftRect = new RectF(x, y + size - stemLen - radius, x + 2.0f * radius, y + size - stemLen + radius);
        RectF rightRect = new RectF(x + size - 2 * radius,   y + size - stemLen - radius, x + size,  y + size - stemLen + radius);

        RectF leftBottomRect = new RectF(x + size/2.0f - 2 * radius - halfStemThickness, y + size - 2*stemLen, x + size/2.0f - halfStemThickness, y + size);
        RectF rightBottomRect = new RectF(x + size/2.0f + halfStemThickness,  y + size - 2*stemLen, x + size/2.0f + 2 * radius + halfStemThickness, y + size);

        paint.setAntiAlias(true);
        path.reset();

        // Draw top leaf
        path.moveTo(x + size/2.0f, y + size - stemLen);
        path.arcTo(topRect, topStartAngle, topSweepAngle);
        path.lineTo(x + size/2.0f, y + size - stemLen);
        canvas.drawPath(path, paint);
        path.reset();

        // Draw left leaf
        path.addCircle(leftRect.centerX(), leftRect.centerY(), radius, Path.Direction.CW);
        canvas.drawPath(path, paint);
        path.reset();

        // Draw right leaf
        path.addCircle(rightRect.centerX(), rightRect.centerY(), radius, Path.Direction.CW);
        canvas.drawPath(path, paint);
        path.reset();

        // Draw stem
        path.addRect(x + size/2.0f - halfStemThickness, y + size - stemLen - 4*halfStemThickness, x + size/2.0f + halfStemThickness, y + size, Path.Direction.CCW);
        // Draw bottom tongue
        path.moveTo(x + size/2.0f, y + size - stemLen);
        path.arcTo(leftBottomRect, 0, 90);
        path.lineTo(x + size/2.0f + radius, y + size);
        path.arcTo(rightBottomRect, 90, 90);
        canvas.drawPath(path, paint);
    }

    public float getCenterX() {
        return size/2.0f;
    }

    public float getCenterY() {
        return size/2.0f;
    }

    public float getWidth() {
        return size;
    }

    public float getHeight() {
        return size;
    }

    public float getBottomAdjustment() {
        return -2 * size;
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
