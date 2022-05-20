package com.example.heartsvalentine.frameShapes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.example.heartsvalentine.R;
import com.example.heartsvalentine.Utilities;
import com.example.heartsvalentine.hearts.EmojiHelper;

import java.util.ArrayList;

public class EmojiTableCtrl extends View implements View.OnClickListener {

    private static int size;
    private final static int columns = 6;
    private final static int lastColumn = columns - 1;
    private int borderThickness;
    private int borderMargin;
    private EmojiCellCtrl selectedEmojiCtrl = null;
    private final Paint paint = new Paint(/*Paint.ANTI_ALIAS_FLAG*/);
    private RectF boundingRect;
    private final ArrayList<EmojiCellCtrl> emojiCellCtrlList = new ArrayList<>();
    private Point popUpSize;
    private String selectEmoji;
    private final Rect rcBounds = new Rect();
    private float verticalGapHeight;
    private RectF popUpBound;

    public EmojiTableCtrl(Context context) {
        super(context);
        init(context);
    }

    public EmojiTableCtrl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public EmojiTableCtrl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        int emojiCount = 0;
        int i = 0;
        String emojiList = "❤️\uD83E\uDDE1\uD83D\uDC9B\uD83D\uDC9A\uD83D\uDC99\uD83D\uDC9C\uD83D\uDDA4\uD83E\uDD0D\uD83E\uDD0E\uD83D\uDC94\uD83D\uDC8C️\uD83D\uDC95\uD83D\uDC9E\uD83D\uDC93\uD83D\uDC97\uD83D\uDC96\uD83D\uDC98\uD83D\uDC9D\uD83D\uDC9F\uD83D\uDD34\uD83D\uDFE0\uD83D\uDFE1\uD83D\uDFE2\uD83D\uDD35\uD83D\uDFE3⚪⚫\uD83D\uDFE4\uD83D\uDD3A\uD83D\uDD38\uD83D\uDD39\uD83D\uDD36\uD83D\uDD37\uD83D\uDFE5\uD83D\uDFE7\uD83D\uDFE8\uD83D\uDFE9\uD83D\uDFE6\uD83D\uDFEA⬛⬜\uD83D\uDFEB♠️♣️♥️♦️\uD83D\uDCA7\uD83E\uDE78⭐\uD83C\uDF1F✨\uD83D\uDCA5\uD83D\uDD25☀️";

        while (i < emojiList.length()) {
            char chr = emojiList.charAt(i);
            String singleEmojiString;

            if (EmojiHelper.isCharEmojiAtPos(emojiList, i)) {
                int len = EmojiHelper.emojiLengthAtPos(emojiList, i);
                singleEmojiString = emojiList.substring(i, i + len);
                i += len;
            } else {
                singleEmojiString = Character.toString(chr);
                i++;
            }
            if (chr != 65039) {
                emojiCount++;
                EmojiCellCtrl ecc = new EmojiCellCtrl(context, singleEmojiString, false);
                emojiCellCtrlList.add(ecc);
            }
        }

        EmojiCellCtrl emojiCtrl = emojiCellCtrlList.get(0);
        size = emojiCtrl.getSize();
        int rows = (emojiCount / columns + ((emojiCount % columns != 0) ? 1 : 0));
        borderThickness = (int) Utilities.convertDpToPixel(2, context);
        borderMargin = (int) Utilities.convertDpToPixel(3, context);
        int halfThickness = borderThickness/2;

        boundingRect = new RectF(halfThickness, rows * size - halfThickness + 2 * borderMargin, columns * size - halfThickness + 2 * borderMargin, halfThickness);

        popUpSize = Utilities.getRealScreenSize(context);
     //   public static Point

        Typeface tf = Typeface.create("TimesRoman",  Typeface.NORMAL);
        paint.setTypeface(tf);
        paint.setTextSize(Utilities.convertDpToPixel(40, getContext()));
        selectEmoji = getResources().getString(R.string.select_emoji);
        paint.getTextBounds(selectEmoji, 0, selectEmoji.length(), rcBounds);
        verticalGapHeight = (popUpSize.y - Math.abs(boundingRect.height()) - Math.abs(rcBounds.height()))/3.0f;
        popUpBound  = new RectF(0, 0, popUpSize.x, popUpSize.y);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(getContext().getResources().getColor(R.color.white));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(popUpBound, paint);

        paint.setColor(getContext().getResources().getColor(R.color.black));
        canvas.drawText(selectEmoji, (popUpSize.x - rcBounds.width())/2.0f, verticalGapHeight + rcBounds.height(), paint);
        canvas.translate((popUpSize.x - boundingRect.width())/2.0f, 2 * verticalGapHeight + rcBounds.height());
        paint.setColor(getContext().getResources().getColor(R.color.midDayFog));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderThickness);
        canvas.drawRect(boundingRect, paint);
        canvas.translate(borderMargin, borderMargin);

        for (int i = 0; i < emojiCellCtrlList.size(); i++) {
            emojiCellCtrlList.get(i).draw(canvas);

            if (i % columns == lastColumn) {
                canvas.translate(-lastColumn * size, size);
            } else {
                canvas.translate(size, 0);
            }
        }
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(popUpSize.x, popUpSize.y);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);   // this super call is important !!!
        boolean success = false;

        float x = event.getX() - (popUpSize.x - boundingRect.width())/2.0f;
        float y = event.getY() - (2 * verticalGapHeight + rcBounds.height());

        int col = (int)(x - borderMargin)/ size;
        int row = (int)(y - borderMargin)/ size;
        int pos = columns * row + col;

        // Click on bottom border and get crash without if statement as out of range.
        // Click on right border and leftmost next item selected without columns check.
        if (pos < emojiCellCtrlList.size() && col < columns && pos >= 0) {
            selectedEmojiCtrl.setEmoji(emojiCellCtrlList.get(pos).getEmoji());
            success = performClick();
        }

        return success;
    }

    @Override
    public boolean performClick() {
        super.performClick();
        return true;
    }

    @Override
    public void onClick(View v) {
    }

    public void setSelectedEmojiCtrl(EmojiCellCtrl selectedEmojiCtrl) {
        this.selectedEmojiCtrl = selectedEmojiCtrl;
        String selectedEmoji = "";

        if (selectedEmojiCtrl != null) {
            selectedEmoji = selectedEmojiCtrl.getEmoji();
        }

        for (int idx = 0; idx < emojiCellCtrlList.size(); idx++) {
            EmojiCellCtrl ecc = emojiCellCtrlList.get(idx);

            if (ecc.getEmoji().compareTo(selectedEmoji) == 0) {
                ecc.setSelected(true);
            }
        }
    }
}
