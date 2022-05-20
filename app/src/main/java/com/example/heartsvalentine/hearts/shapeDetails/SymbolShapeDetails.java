package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class SymbolShapeDetails implements ShapeDetails {

    private final String symbol;
    private float centerX;
    private float centerY;
    private float width;
    private float height;
    private int color;


    public SymbolShapeDetails(String symbol, int color) {
        this.symbol = symbol;
        this.color = color;
        initialize();
    }

    private void initialize() {
        Typeface tf = Typeface.create("TimesRoman", Typeface.NORMAL);

        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setTypeface(tf);
        paint.setTextSize(150);

        Rect rectHeart = new Rect();
        paint.getTextBounds(symbol, 0, symbol.length(), rectHeart);

        centerX = rectHeart.exactCenterX();
        // This looks a quickfix for heart shape. Investigate properly when time.
        centerY = rectHeart.exactCenterY() + 0; // have to add 12 or trespasses

        switch (symbol) {
            case "▲":
                width = (float) (rectHeart.width() * 0.7f);
                height = (float) (rectHeart.height() * 1.0f);

            default:
                width = (float) (rectHeart.width() * 1.0f);
                height = (float) (rectHeart.height() * 1.0f);
        }
    }

    public float getVerticalAdjustment() {

        switch (symbol) {
            case "▲":
                return 0;
        }

        return 0;
    }

    public float getHorizontalAdjustment() {

        switch (symbol) {
            case "▲":
                return 0;
        }

        return 0;
    }

    public void drawEmojiWithBoundaries(Canvas canvas, Paint paint) {
        // This code draws a rectangle and the bounding rect that should touch edges of heart.
        // Adjusted rect so heart reaches its sides
        paint.setColor(Color.BLUE);
 //       canvas.drawText(emoji, 150, 150, paint);
        canvas.drawRect((float)150  + centerX - width/2,
                (float)150 + centerY - height/2,
        		150 + centerX + width/2,
        		150 + centerY + height/2, paint);
        paint.setColor(Color.GREEN);
         canvas.drawText(symbol, 150, 150, paint);
    }

    public int getColor() {
        return color;
    }

    @Override
    public void draw(Canvas canvas, float x, float y, Paint paint) {
        canvas.drawText(symbol, x, y, paint);
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
        return width;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public float getBottomAdjustment() {
        return 0;
    }

    @Override
    public int getClosestDistance() {
        return 150;
    }
}
