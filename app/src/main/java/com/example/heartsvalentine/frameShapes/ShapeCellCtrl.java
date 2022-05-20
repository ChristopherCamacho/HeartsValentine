package com.example.heartsvalentine.frameShapes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.example.heartsvalentine.R;
import com.example.heartsvalentine.Utilities;
import com.example.heartsvalentine.hearts.shapeDetails.DrawCircleDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawClubDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawDiamondDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawHeartDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawShapeDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawSpadeDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawSquareDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawStarDetails;

public class ShapeCellCtrl extends View {
    private final Paint paint = new Paint(/*Paint.ANTI_ALIAS_FLAG*/);
    private static int size;
    private static int borderWidth;
    private static int txtSize, txtBaseLinePos, txtStartPos;
    private static RectF boundingRect = null;
    private boolean showBorder = false;
    private boolean isSelected = false;
    private ShapeType shapeType = ShapeType.None;
    private String symbol;
    private DrawShapeDetails drawShapeDetails = null;
    private float innerShapeBorder;
    private boolean isActive = true;

    public ShapeCellCtrl(Context context) {
        super(context);
        initStandardSizes(context);
    }

    public ShapeCellCtrl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStandardSizes(context);
    }

    public ShapeCellCtrl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStandardSizes(context);
    }

    public ShapeCellCtrl(Context context, String symbol, boolean showBorder) {
        super(context);
        initStandardSizes(context);
        this.shapeType = ShapeType.None;
        this.symbol = symbol;
        this.showBorder = showBorder;
        this.isSelected = false;
    }

    public ShapeCellCtrl(Context context, ShapeType shapeType, boolean showBorder) {
        super(context);
        initStandardSizes(context);
        this.shapeType = shapeType;
        this.symbol = null;
        this.showBorder = showBorder;
        this.isSelected = false;
        createShape(context);
    }

    void createShape(Context context) {

        int innerShapeWidthDp = 24;
        int innerShapeBorderDp = 2;

        if (shapeType == ShapeType.Square) {
            innerShapeWidthDp = 20;
            innerShapeBorderDp = 4;
        }

        int innerShapeWidth = (int) Utilities.convertDpToPixel(innerShapeWidthDp, context);
        innerShapeBorder = Utilities.convertDpToPixel(innerShapeBorderDp, context);

        switch (shapeType) {
            case StraightHeart:
                drawShapeDetails = new DrawHeartDetails(getContext().getResources().getColor(R.color.black), innerShapeWidth);
                break;

            case Circle:
                drawShapeDetails = new DrawCircleDetails(getContext().getResources().getColor(R.color.black), innerShapeWidth);
                break;

            case Square:
                drawShapeDetails = new DrawSquareDetails(getContext().getResources().getColor(R.color.black), innerShapeWidth);
                break;

            case Star:
                drawShapeDetails = new DrawStarDetails(getContext().getResources().getColor(R.color.black), innerShapeWidth);
                break;

            case Spade:
                drawShapeDetails = new DrawSpadeDetails(getContext().getResources().getColor(R.color.black), innerShapeWidth);
                break;

            case Club:
                drawShapeDetails = new DrawClubDetails(getContext().getResources().getColor(R.color.black), innerShapeWidth);
                break;

            case Diamond:
                drawShapeDetails = new DrawDiamondDetails(getContext().getResources().getColor(R.color.black), innerShapeWidth);
                break;

            default:
        }
    }

    private static void initStandardSizes(Context context) {
        size = (int) Utilities.convertDpToPixel(28, context);
        borderWidth = (int) Utilities.convertDpToPixel(1, context);
        txtSize = (int) Utilities.convertDpToPixel(22.5f, context);
        txtBaseLinePos = (int) Utilities.convertDpToPixel(21.5f, context);
        txtStartPos = (int) Utilities.convertDpToPixel(14f, context);

        if (boundingRect == null) {
            boundingRect = new RectF(0, 0, size, size);
        }
    }

    public void setShapeType(ShapeType shapeType) {
        this.shapeType = shapeType;
        this.symbol = null;
        createShape(this.getContext());
        invalidate();
    }

    public void setSymbol(String symbol) {
        this.shapeType = ShapeType.None;
        drawShapeDetails = null;
        this.symbol = symbol;
       // createShape(this.getContext());
        invalidate();
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getSize() {
        return size;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

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
        }
        int shapeColor = getContext().getResources().getColor(isActive? R.color.black : R.color.fog);

        if (drawShapeDetails != null) {
            drawShapeDetails.setColor(shapeColor);
            drawShapeType(canvas);
        }
        else if (symbol != null) {
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(shapeColor);
            Typeface tf = Typeface.create("TimesRoman", Typeface.NORMAL);
            paint.setTypeface(tf);
            paint.setTextSize(txtSize);
            paint.setTextAlign(Paint.Align.CENTER);
            canvas.drawText(symbol, txtStartPos, txtBaseLinePos, paint);
        }
    }

    private void drawShapeType(Canvas canvas) {
        if (drawShapeDetails != null) {
            paint.setColor(drawShapeDetails.getColor());

            paint.setStyle(Paint.Style.FILL);


            float shapeHeight;

            if (shapeType == ShapeType.StraightHeart) {
                shapeHeight = -drawShapeDetails.getHeight() + 1.5f * innerShapeBorder;
            }
            else if (shapeType == ShapeType.Star) {
                shapeHeight = -drawShapeDetails.getHeight() + 1.5f * innerShapeBorder;
            }
            else {
                shapeHeight = -drawShapeDetails.getHeight() + innerShapeBorder;
            }

            drawShapeDetails.draw(canvas, innerShapeBorder, shapeHeight, paint);
        }
    }

    @Override public void  onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //
        setMeasuredDimension(size, size);
    }
}
