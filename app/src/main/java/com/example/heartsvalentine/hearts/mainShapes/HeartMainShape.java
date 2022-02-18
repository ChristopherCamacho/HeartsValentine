package com.example.heartsvalentine.hearts.mainShapes;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Typeface;

import com.example.heartsvalentine.hearts.shapeDetails.DrawShapeDetails;
import com.example.heartsvalentine.hearts.mainSizes.HeartMainSizes;
import com.example.heartsvalentine.hearts.shapeDetails.ShapeDetails;

import java.util.ArrayList;
import java.util.List;

public class HeartMainShape implements MainShape {
    HeartMainSizes mainSizes;
    private final int closestDistance;
    private final Canvas canvas;
    ShapeDetails sd;
    private final float bottomAdjust;

    public HeartMainShape(Canvas canvas, HeartMainSizes mainSizes, int closestDistance, ShapeDetails sd) {
        this.canvas = canvas;
        this.mainSizes = mainSizes;
        this.closestDistance = closestDistance;
        this.sd = sd;
        bottomAdjust = sd.getBottomAdjustment();
    }

    private void computeSideHearts(int totalHeartCount, double distance, double startAngle, double beta, List<Point> heartsLst,
                                   Point ptCircleCentre, boolean left) {
        double gamma = 2 * Math.asin(distance / (2 * mainSizes.getRadius())); // seems correct
        int heartCount = 0;
        double angle = left? startAngle : -startAngle + Math.PI;

        if (left) {
            while (angle < beta) {
                heartsLst.add(new Point((int)(ptCircleCentre.x + mainSizes.getRadius() * Math.cos(angle) - sd.getCenterX()), (int)(ptCircleCentre.y - mainSizes.getRadius() * Math.sin(angle) -  sd.getCenterY())));
                angle += gamma;
                heartCount++;
            }
        }
        else {
            while (angle > -beta + Math.PI) {
                heartsLst.add(new Point((int)(ptCircleCentre.x + mainSizes.getRadius() * Math.cos(angle) -  sd.getCenterX()),  (int)(ptCircleCentre.y - mainSizes.getRadius() * Math.sin(angle) -  sd.getCenterY())));
                angle -= gamma;
                heartCount++;
            }
        }

        double a = ptCircleCentre.x + mainSizes.getRadius() * Math.cos( (left)? beta : Math.PI - beta) -  sd.getCenterX();
        double b = ptCircleCentre.y - mainSizes.getRadius() * Math.sin( (left)? beta : Math.PI - beta) -  sd.getCenterY();
        double c = mainSizes.getWidth()/2.0 -  sd.getCenterX();
        double d = mainSizes.getHeight() - mainSizes.getMargin() + bottomAdjust;//hd.getHeartCenterY();

        Point ptLast = heartsLst.get(heartsLst.size() - 1);

        // equation of a line through (a, b) and (c, d) is:
        // x + aa*y + bb = 0 where:
        // aa = (c - a)/(b - d) and bb = (ad - bc)/(b - d)
        double aa = (c - a)/(b - d);
        double bb = (a*d - b*c)/(b - d);
        // see link for calculations below
        // https://en.wikipedia.org/wiki/Distance_from_a_point_to_a_line

        double distanceFromLine = Math.abs(ptLast.x + aa*ptLast.y + bb)/Math.sqrt(1 + aa*aa);

        double junctionX = (aa * (aa * ptLast.x - ptLast.y) - bb)/(1 + aa*aa);
        double junctionY = ( - aa * ptLast.x + ptLast.y - aa * bb)/(1 + aa*aa);

        double shortDist = Math.sqrt(distance*distance - distanceFromLine*distanceFromLine);

        double zeta = Math.atan (-1/aa) + ((left)? 0 : Math.PI);

        double lineX = junctionX + shortDist * Math.cos(zeta);
        double lineY = junctionY + shortDist * Math.sin(zeta);

        heartsLst.add(new Point( (int)lineX,   (int)lineY));

        while (heartCount <= totalHeartCount ) {
            lineX += distance * Math.cos(zeta);
            lineY += distance * Math.sin(zeta);
            heartsLst.add(new Point((int)lineX,  (int)lineY));
            heartCount++;
        }
    }

    public void draw() {
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Typeface tf = Typeface.create("TimesRoman",  Typeface.NORMAL);
        paint.setTypeface(tf);
        paint.setTextSize(150);

        if (sd instanceof DrawShapeDetails) {
            paint.setColor(((DrawShapeDetails)sd).getColor());
        }

        List<Point> heartsLst = new ArrayList<>();

        // left circle
        Point ptLeftTopCircleCentre = new Point((int)(mainSizes.getMargin() + mainSizes.getRadius() +  sd.getCenterX()), (int)(mainSizes.getMargin() + mainSizes.getRadius() -  sd.getCenterY()));

        double startAngle = Math.acos((mainSizes.getWidth()/2.0 - ptLeftTopCircleCentre.x)/mainSizes.getRadius());
        double vertDistBottomPt = mainSizes.getHeight() - 2 * mainSizes.getMargin() - mainSizes.getRadius(); // y-coordinate top circle centres - y coordinate of bottom of heart
        double alpha = Math.atan(vertDistBottomPt/mainSizes.getRadius());
        double phi = Math.atan(vertDistBottomPt/(mainSizes.getRadius()*Math.cos(startAngle)));

        double beta = 2*Math.PI - alpha - phi;
        double circleDist = (beta  - startAngle)*mainSizes.getRadius();
        double totDistance = circleDist + vertDistBottomPt;

        // first approximation of distance using linear length
        int totalHeartCount = 1;
        double distance;
        double oldDistance;

        do
        {
            distance = totDistance/++totalHeartCount;
        }
        while (distance > closestDistance);

        distance = totDistance/--totalHeartCount;

        // distance is approximate - between last heart on curve and first on line, we are assuming distance as section of curve
        // and line when it should be just a line.

        // following approximations using numerical computation of line...
        // prevError is there to prevent hanging in scenario where error margin doesn't reduce
        double error = 100000000, prevError;

        do {
            computeSideHearts(totalHeartCount, distance, startAngle, beta, heartsLst, ptLeftTopCircleCentre, true);
            Point ptLast = heartsLst.get(heartsLst.size() - 1);
            prevError = error;
            oldDistance = distance;
            error = Math.sqrt(Math.pow((mainSizes.getWidth()/2.0 -  sd.getCenterX()) - ptLast.x, 2) + Math.pow(mainSizes.getHeight() - mainSizes.getMargin() + bottomAdjust - ptLast.y, 2));

            if (error > 1 && error < prevError) {
                distance += (mainSizes.getHeight() - mainSizes.getMargin() + bottomAdjust > ptLast.y)? error/totalHeartCount: -error/totalHeartCount;
                heartsLst.clear();
            }
        }  while (error > 1 && error < prevError);

        if (error > prevError) {
            heartsLst.clear();
            computeSideHearts(totalHeartCount, oldDistance, startAngle, beta, heartsLst, ptLeftTopCircleCentre, true);
        }

        // right circle
        Point ptRightTopCircleCentre = new Point((int)(mainSizes.getWidth() - mainSizes.getMargin() - mainSizes.getRadius() -  sd.getCenterX()), (int)(mainSizes.getMargin() + mainSizes.getRadius() - sd.getCenterY()));

        computeSideHearts(totalHeartCount, distance, startAngle, beta, heartsLst, ptRightTopCircleCentre, false);

        for (Point pt2d : heartsLst) {
            sd.draw(canvas, (float)pt2d.x, (float)pt2d.y, paint);
        }
    }
}
