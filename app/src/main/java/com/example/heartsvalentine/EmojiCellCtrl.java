package com.example.heartsvalentine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

public class EmojiCellCtrl extends View {

    private String emoji = Character.toString((char) 0x2665);
    private final Paint paint = new Paint(/*Paint.ANTI_ALIAS_FLAG*/);
    private static int size;
    private static int txtSize, txtBaseLinePos;
    private static int borderWidth;
    private static RectF boundingRect = null;
    private boolean showBorder = false;
    private boolean isSelected = false;
    private boolean isActive = true;

    public EmojiCellCtrl(Context context) {
        super(context);
        initStandardSizes(context);
    }

    public EmojiCellCtrl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStandardSizes(context);
    }

    public EmojiCellCtrl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStandardSizes(context);
    }

    public EmojiCellCtrl(Context context, String emoji, boolean showBorder) {
        super(context);
        initStandardSizes(context);
        this.emoji = emoji;
        this.showBorder = showBorder;
        this.isSelected = false;
    }

    private static void initStandardSizes(Context context) {
        size = (int) Utilities.convertDpToPixel(28, context);
        txtSize = (int) Utilities.convertDpToPixel(22.5f, context);
        txtBaseLinePos = (int) Utilities.convertDpToPixel(21.5f, context);
        borderWidth = (int) Utilities.convertDpToPixel(2, context);

        if (boundingRect == null) {
            boundingRect = new RectF(0, 0, size, size);
        }
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
        invalidate();
    }

    public String getEmoji() {
        return emoji;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public int getSize() { return size; }

    public void setIsActive(boolean isActive) {
        this.isActive = isActive;
        invalidate();
    }

    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (showBorder) {
            paint.setColor(getContext().getResources().getColor(R.color.highlightBlue));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderWidth);
            canvas.drawRect(boundingRect, paint);
        }

        if (isSelected) {
            paint.setColor(getContext().getResources().getColor(R.color.faintHighlightBlue));

            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(boundingRect, paint);

            paint.setColor(getContext().getResources().getColor(R.color.highlightBlue));
            paint.setStyle(Paint.Style.STROKE);
            paint.setStrokeWidth(borderWidth);
            canvas.drawRect(boundingRect, paint);
        }

        if (!isActive) {
            paint.setColor(getContext().getResources().getColor(R.color.midDayFog));
            paint.setStyle(Paint.Style.FILL);
            canvas.drawRect(boundingRect, paint);
            paint.setColor(getContext().getResources().getColor(R.color.translucide));
        }
        else {
            paint.setColor(getContext().getResources().getColor(R.color.black));
        }

        Typeface tf = Typeface.create("TimesRoman", Typeface.NORMAL);
        paint.setTypeface(tf);
        paint.setTextSize(txtSize);

        canvas.drawText(emoji, 0, txtBaseLinePos, paint);
    }

    @Override public void  onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //
        setMeasuredDimension(size, size);
    }
}
