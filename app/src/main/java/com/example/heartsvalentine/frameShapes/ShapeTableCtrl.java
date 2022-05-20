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

import java.util.ArrayList;

public class ShapeTableCtrl extends View implements View.OnClickListener{
    private static int size;
    private int columns;
    private int lastColumn;
    private int borderThickness;
    private int borderMargin;
    private ShapeCellCtrl selectedShapeCtrl = null;
    private final Paint paint = new Paint(/*Paint.ANTI_ALIAS_FLAG*/);
    private RectF boundingRect;
    private final ArrayList<ShapeCellCtrl> shapeCellCtrlList = new ArrayList<>();
    private Point popUpSize;
    private String selectSymbol;
    private final Rect rcBounds = new Rect();
    private float verticalGapHeight;
    private RectF popUpBound;

    public ShapeTableCtrl(Context context) {
        super(context);
        init(context);
    }

    public ShapeTableCtrl(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ShapeTableCtrl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        shapeCellCtrlList.add(new ShapeCellCtrl(context, ShapeType.Square, false));
        shapeCellCtrlList.add(new ShapeCellCtrl(context, "▲", false));
        shapeCellCtrlList.add(new ShapeCellCtrl(context, ShapeType.Circle, false));
        shapeCellCtrlList.add(new ShapeCellCtrl(context, "☻", false));

        ShapeType[] suitArray = {ShapeType.Spade, ShapeType.Club, ShapeType.StraightHeart, ShapeType.Diamond, ShapeType.Star}; // star isn't a suit but is next one we want

        for (ShapeType suit : suitArray) {
            ShapeCellCtrl scc = new ShapeCellCtrl(context, suit, false);
            shapeCellCtrlList.add(scc);
        }

        String remainingSymbols = "✪⍟⎈❉❋✺✹✸✶✷✵✲✱✦⊛⁕❃❂✼⨳✚❖✜";

        for (int idx = 0; idx < remainingSymbols.length(); idx++) {
            char chr = remainingSymbols.charAt(idx);
            ShapeCellCtrl scc = new ShapeCellCtrl(context, Character.toString(chr), false);
            shapeCellCtrlList.add(scc);
        }
        columns = 8;
        int rows = (shapeCellCtrlList.size() / columns + ((shapeCellCtrlList.size() % columns != 0) ? 1 : 0));
        lastColumn = columns - 1;

        borderThickness = (int) Utilities.convertDpToPixel(2, context);
        borderMargin = (int) Utilities.convertDpToPixel(3, context);
        int halfThickness = borderThickness/2;

        if (shapeCellCtrlList.size() > 0) {
            ShapeCellCtrl shapeCellCtrl = shapeCellCtrlList.get(0);
            size = shapeCellCtrl.getSize();
        }

        boundingRect = new RectF(halfThickness, rows * size - halfThickness + 2 * borderMargin, columns * size - halfThickness + 2 * borderMargin, halfThickness);
        popUpSize = Utilities.getRealScreenSize(context);

        Typeface tf = Typeface.create("TimesRoman",  Typeface.NORMAL);
        paint.setTypeface(tf);
        paint.setTextSize(Utilities.convertDpToPixel(40, getContext()));
        selectSymbol = getResources().getString(R.string.select_symbol);
        paint.getTextBounds(selectSymbol, 0, selectSymbol.length(), rcBounds);
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
        canvas.drawText(selectSymbol, (popUpSize.x - rcBounds.width())/2.0f, verticalGapHeight + rcBounds.height(), paint);
        canvas.translate((popUpSize.x - boundingRect.width())/2.0f, 2 * verticalGapHeight + rcBounds.height());
        paint.setColor(getContext().getResources().getColor(R.color.midDayFog));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderThickness);
        canvas.drawRect(boundingRect, paint);
        canvas.translate(borderMargin, borderMargin);

        for (int i = 0; i < shapeCellCtrlList.size(); i++) {
            shapeCellCtrlList.get(i).draw(canvas);

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
        if (pos < shapeCellCtrlList.size() && col < columns && pos >= 0) {

            ShapeCellCtrl sCC = shapeCellCtrlList.get(pos);
            if (sCC.getShapeType() != ShapeType.None) {
                selectedShapeCtrl.setShapeType(sCC.getShapeType());
            }
            else {
                selectedShapeCtrl.setSymbol(sCC.getSymbol());
            }

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

    public void setSelectedShapeCtrl(ShapeCellCtrl selectedShapeCtrl) {
        this.selectedShapeCtrl = selectedShapeCtrl;

        for (int idx = 0; idx < shapeCellCtrlList.size(); idx++) {
            ShapeCellCtrl scc = shapeCellCtrlList.get(idx);

            if (selectedShapeCtrl.getShapeType() != ShapeType.None) {
                if (scc.getShapeType() == selectedShapeCtrl.getShapeType()) {
                    scc.setSelected(true);
                }
            }
            else if (selectedShapeCtrl.getSymbol() != null && scc.getSymbol() != null) {
                if (scc.getSymbol().compareTo(selectedShapeCtrl.getSymbol()) == 0) {
                    scc.setSelected(true);
                }
            }
        }
    }
}
