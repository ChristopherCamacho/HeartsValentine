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

public class MainShapeTableCtrl extends View implements View.OnClickListener{
    private static int size;
    private final int columns;
    private final int lastColumn;
    private int borderThickness;
    private int borderMargin;
    private MainShapeCellCtrl selectedMainShapeCtrl = null;
    private final Paint paint = new Paint(/*Paint.ANTI_ALIAS_FLAG*/);
    private RectF boundingRect;
    private final ArrayList<MainShapeCellCtrl> mainShapeCellCtrlList = new ArrayList<>();
    private boolean fillShape = true;
    private Point popUpSize;
    private String selectMainShape;
    private final Rect rcBounds = new Rect();
    private float verticalGapHeight;
    private RectF popUpBound;

    public MainShapeTableCtrl(Context context) {
        super(context);
        columns = 0;
        lastColumn = -1;
    }

    public MainShapeTableCtrl(Context context, AttributeSet attrs) {
        super(context, attrs);
        columns = 0;
        lastColumn = -1;
    }

    public MainShapeTableCtrl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        columns = 0;
        lastColumn = -1;
    }

    public MainShapeTableCtrl(Context context, ShapeType[] shapeTypes, boolean fillShape) {
        super(context);
        columns = 3;
        lastColumn = columns - 1;
        this.fillShape = fillShape;
        init(context, shapeTypes);
    }

    private void init(Context context, ShapeType[] shapeTypes) {

        for (ShapeType shapeType : shapeTypes) {
            MainShapeCellCtrl mSCC = new MainShapeCellCtrl(context, shapeType, fillShape, false);
            mainShapeCellCtrlList.add(mSCC);
        }

        int rows = (shapeTypes.length / columns + ((shapeTypes.length % columns != 0) ? 1 : 0));

        borderThickness = (int) Utilities.convertDpToPixel(2, context);
        borderMargin = (int) Utilities.convertDpToPixel(3, context);
        int halfThickness = borderThickness/2;

        if (mainShapeCellCtrlList.size() > 0) {
            MainShapeCellCtrl mainShapeCellCtrl = mainShapeCellCtrlList.get(0);
            size = mainShapeCellCtrl.getSize();
        }

        boundingRect = new RectF(halfThickness, rows * size - halfThickness + 2 * borderMargin, columns * size - halfThickness + 2 * borderMargin, halfThickness);
        popUpSize = Utilities.getRealScreenSize(context);

        Typeface tf = Typeface.create("TimesRoman",  Typeface.NORMAL);
        paint.setTypeface(tf);
        paint.setTextSize(Utilities.convertDpToPixel(40, getContext()));
        selectMainShape = getResources().getString(R.string.select_main_shape);
        paint.getTextBounds(selectMainShape, 0, selectMainShape.length(), rcBounds);
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
        canvas.drawText(selectMainShape, (popUpSize.x - rcBounds.width())/2.0f, verticalGapHeight + rcBounds.height(), paint);
        canvas.translate((popUpSize.x - boundingRect.width())/2.0f, 2 * verticalGapHeight + rcBounds.height());
        paint.setColor(getContext().getResources().getColor(R.color.midDayFog));
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(borderThickness);
        canvas.drawRect(boundingRect, paint);
        canvas.translate(borderMargin, borderMargin);

        for (int i = 0; i < mainShapeCellCtrlList.size(); i++) {
            mainShapeCellCtrlList.get(i).draw(canvas);

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
        if (pos < mainShapeCellCtrlList.size() && col < columns && pos >= 0) {
            selectedMainShapeCtrl.setShapeType(mainShapeCellCtrlList.get(pos).getShapeType());
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

    public void setSelectedEmojiCtrl(MainShapeCellCtrl selectedMainShapeCtrl) {
        this.selectedMainShapeCtrl = selectedMainShapeCtrl;

        for (int idx = 0; idx < mainShapeCellCtrlList.size(); idx++) {
            MainShapeCellCtrl mSCC = mainShapeCellCtrlList.get(idx);

            if (mSCC.getShapeType()  == selectedMainShapeCtrl.getShapeType()) {
                mSCC.setSelected(true);
            }
        }
    }
}
