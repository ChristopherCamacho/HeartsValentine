package com.example.heartsvalentine.hearts.textBoundaries;

import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;

import com.example.heartsvalentine.hearts.TextFormattingDetails;
import com.example.heartsvalentine.hearts.TextRectDetails;
import com.example.heartsvalentine.hearts.mainSizes.SquareMainSizes;
import com.example.heartsvalentine.hearts.shapeDetails.EmojiShapeDetails;
import com.example.heartsvalentine.hearts.shapeDetails.ShapeDetails;

import java.util.ArrayList;
import java.util.List;

public class SquareTextBoundaries implements TextBoundaries {
    private final TextFormattingDetails tfd;
    private final List<TextRectDetails> rectLst = new ArrayList<>();
    private final SquareMainSizes mainSizes;
    private final ShapeDetails sd;
    private final float textAscent;
    private final float textDescent;

    public SquareTextBoundaries(Paint paint, SquareMainSizes mainSizes, ShapeDetails sd, TextFormattingDetails tfd) {
        this.mainSizes = mainSizes;
        this.sd = sd;
        this.tfd = tfd;
        textAscent = paint.ascent();
        textDescent = paint.descent();
    }

    public List<TextRectDetails> computeTextRectangles() {
        float verticalAdjustment = 0;

        if (sd instanceof EmojiShapeDetails) {
            verticalAdjustment = ((EmojiShapeDetails) sd).getVerticalAdjustment();
        }

        RectF rect = new RectF(mainSizes.getMargin() + sd.getWidth(), mainSizes.getMargin() + sd.getHeight() + verticalAdjustment,
                mainSizes.getWidth() - mainSizes.getMargin() - sd.getWidth(), mainSizes.getHeight() - mainSizes.getMargin() - sd.getHeight());
        //innerRect delimitates the area for drawing text.
        RectF innerRect = new RectF(rect.left + tfd.getTxtHeartsMargin(), rect.top+ tfd.getTxtHeartsMargin(), rect.right - tfd.getTxtHeartsMargin(), rect.bottom - tfd.getTxtHeartsMargin());

        float innerTextTopLine = innerRect.top - textAscent; // Top text line position
        float innerTextBottomLine = innerRect.bottom - textDescent; // Bottom of last line position

        int numLines = (int)Math.floor((innerTextBottomLine - innerTextTopLine)/tfd.getLineHeight());
        float lineHeight = (innerTextBottomLine - innerTextTopLine)/numLines;

        for (int rectIdx = 0; rectIdx <= numLines; rectIdx++) {
            // The top of rectangle is y-coordinate of where text is written. Bottom is lowest point of text
            Rect rc = new Rect((int)innerRect.left, (int)(innerTextTopLine + rectIdx*lineHeight), (int)innerRect.right, (int)(innerTextTopLine + rectIdx*lineHeight + textDescent));
            rectLst.add(new TextRectDetails(rc));
        }
        return rectLst;
    }
}
