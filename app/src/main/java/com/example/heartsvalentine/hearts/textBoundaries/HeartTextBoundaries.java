package com.example.heartsvalentine.hearts.textBoundaries;

import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;

import com.example.heartsvalentine.hearts.TextFormattingDetails;
import com.example.heartsvalentine.hearts.TextRectDetails;
import com.example.heartsvalentine.hearts.mainSizes.HeartMainSizes;
import com.example.heartsvalentine.hearts.shapeDetails.EmojiShapeDetails;
import com.example.heartsvalentine.hearts.shapeDetails.ShapeDetails;

import java.util.ArrayList;
import java.util.List;

public class HeartTextBoundaries implements TextBoundaries {
    private final TextFormattingDetails tfd;
    private final List<TextRectDetails> rectLst = new ArrayList<>();
    private final HeartMainSizes mainSizes;
    ShapeDetails sd;
    private final float textAscent;

    public HeartTextBoundaries(Paint paint, HeartMainSizes mainSizes, ShapeDetails sd, TextFormattingDetails tfd) {
        this.mainSizes = mainSizes;
        this.sd = sd;
        this.tfd = tfd;
        textAscent = paint.ascent();
    }

    private int[] getXCircleIntersectionsFromY(int y, boolean left) {
        // centre of left circle is x_centre = margin + 2*heartCenterX + (radius - heartCenterX) = margin + heartCenterX + radius
        // 					        y_centre = margin - 2*heartCenterY + (radius + heartCenterY) = margin - heartCenterY + radius
        // centre of right circle is x_centre = width - margin - 2 * radius + radius - heartCenterX = width - margin - radius - heartCenterX
        // 					   y_centre = margin - 2*heartCenterY + (radius + heartCenterY) = margin - heartCenterY + radius
        // equation of top left inner circle is:
        // (margin + heartCenterX + radius) + (radius - heartCenterX)*cos(angle)
        // (margin - heartCenterY + radius) + (radius + heartCenterY)*sin(angle) where angle = 0 to 2*pi.
        // equation of top right inner circle is:
        // (width - margin - radius - heartCenterX) + (radius - heartCenterX)*cos(angle)
        //  (margin - heartCenterY + radius) + (radius + heartCenterY)*sin(angle) where angle = 0 to 2*pi.
        // these are not circles but ovals!
        // adjustedHeartCenter is an arbitrary quickfix that gives better results for emoji
        float adjustedHeartCenter = sd.getCenterX() - 10;
        double angle = Math.asin((y - (mainSizes.getMargin() - sd.getCenterY() + mainSizes.getRadius()))/(mainSizes.getRadius() - tfd.getTxtHeartsMargin() + sd.getCenterY()));
        double xCentre = left? (mainSizes.getMargin() + adjustedHeartCenter /*hd.getHeartCenterX()*/ + mainSizes.getRadius()) : (mainSizes.getWidth() - mainSizes.getMargin() - mainSizes.getRadius() - adjustedHeartCenter /*hd.getHeartCenterX()*/);
        int xFirst = (int)(xCentre + (mainSizes.getRadius() - tfd.getTxtHeartsMargin() - sd.getCenterX())*Math.cos(angle));
        int xSecond = (int)(xCentre + (mainSizes.getRadius() - tfd.getTxtHeartsMargin() - sd.getCenterX())*Math.cos(angle + Math.PI));
        int[] retVal = new int[2];
        retVal[0] = Math.min(xFirst, xSecond);
        retVal[1] = Math.max(xFirst, xSecond);
        return retVal;
    }

    private int[] getXEllipseIntersectionsFromY(int y, boolean left) {
        // centre of left circle is x_centre = margin + 2*heartCenterX + (radius - heartCenterX) = margin + heartCenterX + radius
        // 					        y_centre = margin - 2*heartCenterY + (radius + heartCenterY) = margin - heartCenterY + radius
        // centre of right circle is x_centre = width - margin - 2 * radius + radius - heartCenterX = width - margin - radius - heartCenterX
        // 					   y_centre = margin - 2*heartCenterY + (radius + heartCenterY) = margin - heartCenterY + radius
        // equation of an ellipse is (x - Cx)**2/a**2 + (y - Cy)**2/b**2 = 1
        // so x = Cx +- a*sqrt(1 - (y - Cy)**2/b**2)
        double Cx = left? (mainSizes.getMargin() + mainSizes.getRadius() +  sd.getCenterX()) : (mainSizes.getWidth() - mainSizes.getMargin() - mainSizes.getRadius() -  sd.getCenterX());
        double Cy = (mainSizes.getMargin() + mainSizes.getRadius() +  sd.getCenterY());
        double a = mainSizes.getRadius() - sd.getCenterX() - tfd.getTxtHeartsMargin();
        double b = mainSizes.getRadius() - sd.getCenterY() - tfd.getTxtHeartsMargin();

        int[] retVal = new int[2];
        double otherSide = a*Math.sqrt(1 - Math.pow(y - Cy, 2)/(b*b));
        retVal[0] = (int)(Cx - otherSide);
        retVal[1] = (int)(Cx + otherSide);
        return retVal;
    }

    private int[][] computeBottomTrianglePts() {
        Point ptLeftTopCircleCentre = new Point((int)(mainSizes.getMargin() + mainSizes.getRadius() + sd.getCenterX()), (int)(mainSizes.getMargin() + mainSizes.getRadius() - sd.getCenterY()));
        Point ptRightTopCircleCentre = new Point((int)(mainSizes.getWidth() - mainSizes.getMargin() - mainSizes.getRadius() - sd.getCenterX()), (int)(mainSizes.getMargin() + mainSizes.getRadius() - sd.getCenterY()));

        double startAngle = Math.acos((mainSizes.getWidth()/2.0 - ptLeftTopCircleCentre.x)/mainSizes.getRadius());
        double vertDistBottomPt = mainSizes.getHeight() - 2 * mainSizes.getMargin() - mainSizes.getRadius(); // y-coordinate top circle centres - y coordinate of bottom of heart
        double alpha = Math.atan(vertDistBottomPt/mainSizes.getRadius());
        double phi = Math.atan(vertDistBottomPt/(mainSizes.getRadius()*Math.cos(startAngle)));

        double beta = 2*Math.PI - alpha - phi;

        double pt1x = ptLeftTopCircleCentre.x + mainSizes.getRadius() * Math.cos(beta)  - tfd.getTxtHeartsMargin() / Math.sin(beta)/*- heartCenterX + heartCenterWidth*/;
        double pt1y = ptLeftTopCircleCentre.y - mainSizes.getRadius() * Math.sin(beta) - sd.getCenterY() /* + heightAdjustment*/;

        double pt2x = ptRightTopCircleCentre.x + mainSizes.getRadius() * Math.cos(Math.PI - beta) + tfd.getTxtHeartsMargin() / Math.sin(beta)/*- heartCenterX + heartCenterWidth*/;
        double pt2y = ptRightTopCircleCentre.y - mainSizes.getRadius() * Math.sin(Math.PI - beta) - sd.getCenterY() /*+ heightAdjustment*/;

        if (!(sd instanceof EmojiShapeDetails)) {
            //	if (!hd.getUseEmoji()) {
            pt1y += 2.0f * sd.getHeight();
            pt2y += 2.0f * sd.getHeight();
            // I've already added for pt3.y elsewhere - tidy when have time. This works safely, but messy.
        }

        double pt3x = mainSizes.getWidth()/2.0;
        double pt3y = mainSizes.getHeight() - mainSizes.getMargin() + tfd.getTxtHeartsMargin() / Math.cos(Math.PI/2.0 - beta);

        double zeta = Math.atan ( (pt1y - pt3y)/(pt1x - pt3x));
        double heightAdjustment = -0.5 * sd.getWidth() * Math.sin(zeta) - sd.getHeight();

        pt1y += heightAdjustment;
        pt2y += heightAdjustment;
        pt3y += heightAdjustment;

        int[] xPoints = {(int)pt1x, (int)pt3x, (int)pt2x};
        int[] yPoints = {(int)pt1y, (int)pt3y, (int)pt2y};

        return new int[][]{xPoints, yPoints};
    }

    public List<TextRectDetails> computeTextRectangles() {
        int[][] pts = computeBottomTrianglePts();

        // Uncomment line below for testing purposes
        // drawShapes(pts);

        int yTop = (int)(tfd.getTopTextMargin() + mainSizes.getMargin() + 2*sd.getHeight());

        if (sd instanceof EmojiShapeDetails) {
            // if (hd.getUseEmoji()) {
            while (yTop < pts[1][0]) {
                int[] xTopLeft = getXCircleIntersectionsFromY(yTop, true);
                int[] xTopRight = getXCircleIntersectionsFromY(yTop, false);

                int yBottom = yTop + (int) textAscent;
                int[] xBottomLeft = getXCircleIntersectionsFromY(yBottom, true);
                int[] xBottomRight = getXCircleIntersectionsFromY(yBottom, false);

                int xLeftLeftCircle = Math.max(xTopLeft[0], xBottomLeft[0]);
                int xRightLeftCircle = Math.min(xTopLeft[1], xBottomLeft[1]);

                int xLeftRightCircle = Math.max(xTopRight[0], xBottomRight[0]);
                int xRightRightCircle = Math.min(xTopRight[1], xBottomRight[1]);

                // The 2 rectangles can be very close, without intersecting and without these intersecting heart if these were joined. When this happens, funny result with hyphen added in middle of line.
                // see INPUT_PATH_SILLY_MESSAGE with text to heart margin of 20
                // To remedy this, let us also merge if gap is very small - say half of heart width.
                // Hope not introducing bug here.
                int maxGap = (int) (sd.getWidth() / 2.0);

                if (xRightLeftCircle + maxGap < xLeftRightCircle && yTop < (mainSizes.getMargin() - sd.getCenterY() + mainSizes.getRadius()) && yBottom < (mainSizes.getMargin() - sd.getCenterY() + mainSizes.getRadius())) {
                    rectLst.add(new TextRectDetails(new Rect(xLeftLeftCircle, yTop, xRightLeftCircle, yBottom)));
                    rectLst.add(new TextRectDetails(new Rect(xLeftRightCircle, yTop, xRightRightCircle, yBottom)));
                } else {
                    rectLst.add(new TextRectDetails(new Rect(xLeftLeftCircle, yTop, xRightRightCircle, yBottom)));
                }
                yTop += tfd.getLineHeight();
            }
        }
        else {
            yTop -= (int) textAscent + 110 - tfd.getTxtHeartsMargin();
            while (yTop < pts[1][0]) {
                int[] xTopLeft = getXEllipseIntersectionsFromY(yTop, true);
                int[] xTopRight = getXEllipseIntersectionsFromY(yTop, false);

                int yBottom = yTop + (int) textAscent;
                int[] xBottomLeft = getXEllipseIntersectionsFromY(yBottom, true);
                int[] xBottomRight = getXEllipseIntersectionsFromY(yBottom, false);

                int xLeftLeftCircle = Math.max(xTopLeft[0], xBottomLeft[0]);
                int xRightLeftCircle = Math.min(xTopLeft[1], xBottomLeft[1]);

                int xLeftRightCircle = Math.max(xTopRight[0], xBottomRight[0]);
                int xRightRightCircle = Math.min(xTopRight[1], xBottomRight[1]);

                // The 2 rectangles can be very close, without intersecting and without these intersecting heart if these were joined. When this happens, funny result with hyphen added in middle of line.
                // see INPUT_PATH_SILLY_MESSAGE with text to heart margin of 20
                // To remedy this, let us also merge if gap is very small - say half of heart width.
                // Hope not introducing bug here.
                int maxGap = (int) (sd.getWidth() / 2.0);

                if (xRightLeftCircle + maxGap < xLeftRightCircle && yTop < (mainSizes.getMargin() - sd.getCenterY() + mainSizes.getRadius()) && yBottom < (mainSizes.getMargin() - sd.getCenterY() + mainSizes.getRadius())) {
                    rectLst.add(new TextRectDetails(new Rect(xLeftLeftCircle, yTop, xRightLeftCircle, yBottom)));
                    rectLst.add(new TextRectDetails(new Rect(xLeftRightCircle, yTop, xRightRightCircle, yBottom)));
                } else {
                    rectLst.add(new TextRectDetails(new Rect(xLeftLeftCircle, yTop, xRightRightCircle, yBottom)));
                }
                yTop += tfd.getLineHeight();
            }
        }

        while (yTop < pts[1][1]) {
            // equation of a line through (a, b) = pt[0][0], pt[1][0] or pt[0][1], pt[1][1]
            // and (c, d) = pt[0][1], pt[1][1] is:
            // x + aa*y + bb = 0 where:
            // aa = (c - a)/(b - d) and bb = (ad - bc)/(b - d)
            double aa1 = (pts[0][1] - pts[0][0])/(double)(pts[1][0] - pts[1][1]);
            double bb1 = (pts[0][0]*pts[1][1] - pts[1][0]*pts[0][1])/(double)(pts[1][0] - pts[1][1]);

            double aa2 = (pts[0][1] - pts[0][2])/(double)(pts[1][2] - pts[1][1]);
            double bb2 = (pts[0][2]*pts[1][1] - pts[1][2]*pts[0][1])/(double)(pts[1][2] - pts[1][1]);
            // so x = - bb - aa*y

            int yBottom = yTop + (int)textAscent;
            int x1 = (int) (- bb1 - aa1*yTop);
            int x2 = (int) (- bb2 - aa2*yTop);

            rectLst.add(new TextRectDetails(new Rect(x1, yTop, x2, yBottom)));
            yTop += tfd.getLineHeight();
        }

        // Uncomment for testing purpose
        // drawTextBoundingRectangles();
        return rectLst;
    }
}
