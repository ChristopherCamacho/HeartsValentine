package com.example.heartsvalentine.hearts.textBoundaries;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.heartsvalentine.hearts.TextFormattingDetails;
import com.example.heartsvalentine.hearts.TextRectDetails;
import com.example.heartsvalentine.hearts.mainSizes.SquareMainSizes;
import com.example.heartsvalentine.hearts.shapeDetails.ShapeDetails;

import java.util.ArrayList;
import java.util.List;

public class CircleTextBoundaries implements TextBoundaries {
    private final TextFormattingDetails tfd;
    private final List<TextRectDetails> rectLst = new ArrayList<>();
    private final SquareMainSizes mainSizes;
    private final ShapeDetails sd;
    private final float textAscent;
    private final float textDescent;

    public CircleTextBoundaries(Paint paint, SquareMainSizes mainSizes, ShapeDetails sd, TextFormattingDetails tfd) {
        this.mainSizes = mainSizes;
        this.sd = sd;
        this.tfd = tfd;
        textAscent = paint.ascent();
        textDescent = paint.descent();
    }

    private int[] getXEllipseIntersectionsFromY(float y) {
        // equation of an ellipse is (x - Cx)**2/a**2 + (y - Cy)**2/b**2 = 1
        // so x = Cx +- a*sqrt(1 - (y - Cy)**2/b**2)
        // X and Y Centre of circle
        double Cx = mainSizes.getWidth()/2.0;
        double Cy = mainSizes.getHeight()/2.0;
        // Horizontal and vertical radius
        double a = mainSizes.getWidth()/2.0 - mainSizes.getMargin() - sd.getWidth() - tfd.getTxtHeartsMargin();
        double b = mainSizes.getHeight()/2.0 - mainSizes.getMargin() - sd.getHeight()- tfd.getTxtHeartsMargin();
        int[] retVal = new int[2];
        double otherSide = a*Math.sqrt(1 - Math.pow(y - Cy, 2)/(b*b));
        retVal[0] = (int)(Cx - otherSide);
        retVal[1] = (int)(Cx + otherSide);
        return retVal;
    }

    public List<TextRectDetails> computeTextRectangles() {
        RectF rect = new RectF(mainSizes.getMargin() + sd.getWidth(), mainSizes.getMargin() + sd.getHeight(),
                mainSizes.getWidth() - mainSizes.getMargin() - sd.getWidth(), mainSizes.getHeight() - mainSizes.getMargin() - sd.getHeight());
        // InnerRect delimitates the area for drawing text.
        RectF innerRect = new RectF(rect.left + tfd.getTxtHeartsMargin(), rect.top + tfd.getTxtHeartsMargin(), rect.right - tfd.getTxtHeartsMargin(), rect.bottom - tfd.getTxtHeartsMargin());
        float topBottomMargin = -50;
        float innerTextTopLine = innerRect.top - topBottomMargin;// - tfd.getTopTextMargin(); // Top text line position
        float innerTextBottomLine = innerRect.bottom + textDescent + topBottomMargin;// + tfd.getTopTextMargin(); // Bottom of last line position
        int numLines = (int)Math.floor((innerTextBottomLine - innerTextTopLine)/tfd.getLineHeight());
        float lineHeight = (innerTextBottomLine - innerTextTopLine)/numLines;
        int lineIdx = 0;

        while (lineIdx <= numLines) {
            float yPos = innerTextTopLine + lineIdx*lineHeight;
            int[] xTopPts = getXEllipseIntersectionsFromY(yPos);
            int[] xBottomPts = getXEllipseIntersectionsFromY(yPos - textAscent);

            Rect rc = new Rect(Math.max(xTopPts[0], xBottomPts[0]), (int)(yPos - (textAscent + textDescent)), Math.min(xTopPts[1], xBottomPts[1]), (int)(yPos - textAscent));
            rectLst.add(new TextRectDetails(rc));

            lineIdx++;
        }
        return rectLst;
    }
}
