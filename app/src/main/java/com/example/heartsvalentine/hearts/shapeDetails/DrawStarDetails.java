package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;

import java.util.ArrayList;
import java.util.HashMap;

public class DrawStarDetails implements DrawShapeDetails {

    private final float widthHeight;
    private int color;
    private final StarDetailsGlobalObject starSearchGloObj;
    private final static HashMap<StarSearchDetails, StarDetailsGlobalObject> starDetailsMap = new HashMap<>();

    public DrawStarDetails(int color, float widthHeight) {
        this.color = color;
        int spikes = 5;
        double startAngle = -Math.PI/2.0;
        StarSearchDetails starSearchDetails = new StarSearchDetails(widthHeight, 0.25f * widthHeight, startAngle, spikes);
        this.widthHeight = widthHeight;

        if (starDetailsMap.containsKey(starSearchDetails)) {
            this.starSearchGloObj = starDetailsMap.get(starSearchDetails);
        } else {
            this.starSearchGloObj = new StarDetailsGlobalObject(starSearchDetails);
            starDetailsMap.put(starSearchDetails, this.starSearchGloObj);
        }
    }

    @Override
    public void draw(Canvas canvas, float x, float y, Paint paint) {
        y += widthHeight;
        ArrayList<PointF> pts = starSearchGloObj.getStarPtsList();

        if (pts.size() > 1) {
            Path path = new Path();
            PointF firstPt = pts.get(0);
            path.moveTo(x + firstPt.x, y + firstPt.y);

            for (int i = 1; i < pts.size(); i++) {
                PointF pt = pts.get(i);
                path.lineTo(x + pt.x, y + pt.y);
            }
            canvas.drawPath(path, paint);
        }
    }

    @Override
    public void drawWriting(Canvas canvas, float x, float y, Paint paint) {
    }

    @Override
    public float getCenterX() {
        return starSearchGloObj.getCenterX();
    }

    @Override
    public float getCenterY() {
        return starSearchGloObj.getCenterY();
    }

    @Override
    public float getWidth() {
        return widthHeight;
    }

    @Override
    public float getHeight() {
        return widthHeight;
    }

    @Override
    public float getBottomAdjustment() { return -2 * widthHeight; }

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


