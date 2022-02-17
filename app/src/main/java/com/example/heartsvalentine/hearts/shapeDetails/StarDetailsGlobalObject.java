package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.PointF;
import java.util.ArrayList;

public class StarDetailsGlobalObject {

    private final float centerX, centerY;
    private final ArrayList<PointF> starPtsList = new ArrayList<>();

    StarDetailsGlobalObject(StarSearchDetails searchDetails) {
        centerX = 0.5f * searchDetails.getWidthHeight();
        centerY = 0.5f * searchDetails.getWidthHeight();

        float innerRadius = searchDetails.getInnerRadius();
        float outerRadius = 0.5f * searchDetails.getWidthHeight();

        PointF pt = new PointF((float)(centerX + outerRadius*Math.cos(searchDetails.getStartAngle())), (float)(centerY + outerRadius*Math.sin(searchDetails.getStartAngle())));
        starPtsList.add(pt);

        double forwardAngleMove = Math.PI/searchDetails.getSpikes();
        double currentAngle = searchDetails.getStartAngle();

        while (currentAngle < searchDetails.getStartAngle() + 2 * Math.PI) {
            currentAngle += forwardAngleMove;
            starPtsList.add(new PointF((float)(centerX + innerRadius*Math.cos(currentAngle)), (float)(centerY + innerRadius*Math.sin(currentAngle))));
            currentAngle += forwardAngleMove;
            starPtsList.add(new PointF((float)(centerX + outerRadius*Math.cos(currentAngle)), (float)(centerY + outerRadius*Math.sin(currentAngle))));
        }
    }

    public ArrayList<PointF> getStarPtsList() {
        return starPtsList;
    }

    public float getCenterX() {
        return centerX;
    }

    public float getCenterY() {
        return centerY;
    }
}
