package com.example.heartsvalentine;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class ShapeTableCtrl extends View implements View.OnClickListener{
    private static int size;
    private final int columns;
    private final int lastColumn;
    private int rows;
    private int borderThickness;
    private int borderMargin;
    private ShapeCellCtrl selectedShapeCtrl = null;
    private final Paint paint = new Paint(/*Paint.ANTI_ALIAS_FLAG*/);
    private RectF boundingRect;
    private final ArrayList<ShapeCellCtrl> shapeCellCtrlList = new ArrayList<>();

    public ShapeTableCtrl(Context context) {
        super(context);
        columns = 0;
        lastColumn = -1;
    }

    public ShapeTableCtrl(Context context, AttributeSet attrs) {
        super(context, attrs);
        columns = 0;
        lastColumn = -1;
    }

    public ShapeTableCtrl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        columns = 0;
        lastColumn = -1;
    }

    public ShapeTableCtrl(Context context, ShapeType[] shapeTypes) {
        super(context);
        columns = (int)Math.floor(Math.sqrt(shapeTypes.length));
        lastColumn = columns - 1;
        init(context, shapeTypes);
    }

    private void init(Context context, ShapeType[] shapeTypes) {

        for (ShapeType shapeType : shapeTypes) {
            ShapeCellCtrl scc = new ShapeCellCtrl(context, shapeType, true, false);
            shapeCellCtrlList.add(scc);
        }

        rows = (shapeTypes.length / columns + ((shapeTypes.length % columns != 0) ? 1 : 0));

        borderThickness = (int) Utilities.convertDpToPixel(2, context);
        borderMargin = (int) Utilities.convertDpToPixel(3, context);
        int halfThickness = borderThickness/2;

        if (shapeCellCtrlList.size() > 0) {
            ShapeCellCtrl shapeCellCtrl = shapeCellCtrlList.get(0);
            size = shapeCellCtrl.getSize();
        }

        boundingRect = new RectF(halfThickness, rows * size - halfThickness + 2 * borderMargin, columns * size - halfThickness + 2 * borderMargin, halfThickness);
    }

    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        paint.setColor(getContext().getResources().getColor(R.color.white));
        paint.setStyle(Paint.Style.FILL);
        canvas.drawRect(boundingRect, paint);

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

        setMeasuredDimension(columns * size + 2 * borderMargin, rows * size + 2 * borderMargin);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        super.onTouchEvent(event);   // this super call is important !!!
        boolean success = false;

        float x = event.getX();
        float y = event.getY();

        int col = (int)(x - borderMargin)/ size;
        int row = (int)(y - borderMargin)/ size;
        int pos = columns * row + col;

        // Click on bottom border and get crash without if statement as out of range.
        // Click on right border and leftmost next item selected without columns check.
        if (pos < shapeCellCtrlList.size() && col < columns) {
            selectedShapeCtrl.setShapeType(shapeCellCtrlList.get(pos).getShapeType());
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

    public int getComputedWidth() {
        return columns * size + 2 * borderMargin;
    }

    public int getComputedStartDrawHeight() {
        return size;
    }

    public void setSelectedEmojiCtrl(ShapeCellCtrl selectedShapeCtrl) {
        this.selectedShapeCtrl = selectedShapeCtrl;

        for (int idx = 0; idx < shapeCellCtrlList.size(); idx++) {
            ShapeCellCtrl scc = shapeCellCtrlList.get(idx);

            if (scc.getShapeType()  == selectedShapeCtrl.getShapeType()) {
                scc.setSelected(true);
            }
        }
    }
}
