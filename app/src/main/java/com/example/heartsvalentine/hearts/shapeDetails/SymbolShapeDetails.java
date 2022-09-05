package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;

public class SymbolShapeDetails implements ShapeDetails {

    private final String symbol;
    private float centerX;
    private float centerY;
    private float width;
    private float height;
    private final int color;

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
        centerY = rectHeart.exactCenterY();

        width = (float) (rectHeart.width());
        height = (float) (rectHeart.height());
    }

    public int getColor() {
        return color;
    }

    @Override
    public void draw(Canvas canvas, float x, float y, Paint paint) {
        // draws a blue square behind symbol
    /*    int col = paint.getColor();
        paint.setColor(Color.BLUE);
        //       canvas.drawText(emoji, 150, 150, paint);
        canvas.drawRect(x + centerX - width/2,
                y + centerY - height/2,
                x + centerX + width/2, y + centerY + height/2, paint);
        paint.setColor(col); */
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
        return  -height/2.0f - centerY;
    }

    @Override
    public int getClosestDistance() {
        if (symbol == "█●▬")
            return 200;

        return 150;
    }
}
