package com.example.heartsvalentine.frameShapes;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.example.heartsvalentine.R;
import com.example.heartsvalentine.Utilities;
import com.example.heartsvalentine.hearts.shapeDetails.DrawCircleDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawClubDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawDiamondDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawHeartDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawShapeDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawSmileyDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawSpadeDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawSquareDetails;
import com.example.heartsvalentine.hearts.shapeDetails.DrawStarDetails;

public class MainShapeCellCtrl extends View {
    private final Paint paint = new Paint(/*Paint.ANTI_ALIAS_FLAG*/);
    private static int size;
    private static int borderWidth;
    private static RectF boundingRect = null;
    private boolean showBorder = false;
    private boolean isSelected = false;
    private ShapeType shapeType = ShapeType.None;
    private boolean fillShape;
    private DrawShapeDetails drawShapeDetails = null;
    private float innerShapeBorder;

    public MainShapeCellCtrl(Context context) {
        super(context);
        initStandardSizes(context);
    }

    public MainShapeCellCtrl(Context context, AttributeSet attrs) {
        super(context, attrs);
        initStandardSizes(context);
    }

    public MainShapeCellCtrl(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initStandardSizes(context);
    }

    public MainShapeCellCtrl(Context context, ShapeType shapeType, boolean fillShape, boolean showBorder) {
        super(context);
        initStandardSizes(context);
        this.shapeType = shapeType;
        this.fillShape = fillShape;
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

            case Smiley:
                drawShapeDetails = new DrawSmileyDetails(getContext().getResources().getColor(R.color.black), innerShapeWidth);
                break;

            default:
        }
    }

    private static void initStandardSizes(Context context) {
        size = (int) Utilities.convertDpToPixel(28, context);
        borderWidth = (int) Utilities.convertDpToPixel(1, context);

        if (boundingRect == null) {
            boundingRect = new RectF(0, 0, size, size);
        }
    }

    public void setShapeType( ShapeType shapeType) {
        this.shapeType = shapeType;
        createShape(this.getContext());
        invalidate();
    }

    public ShapeType getShapeType() {
        return shapeType;
    }

    public void setFillShape(boolean fillShape) {
        this.fillShape = fillShape;
    }

    public int getSize() {
        return size;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
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

        if (drawShapeDetails != null) {
            int shapeColor = getContext().getResources().getColor(R.color.black);
            drawShapeDetails.setColor(shapeColor);
        }

        drawShapeType(canvas);
    }

    private void drawShapeType(Canvas canvas) {
        if (drawShapeDetails != null) {
            paint.setColor(drawShapeDetails.getColor());

            if (fillShape) {
                paint.setStyle(Paint.Style.FILL);
            }
            else {
                paint.setStyle(Paint.Style.STROKE);
                int shapeWidth = (int) Utilities.convertDpToPixel(3, this.getContext());
                paint.setStrokeWidth(shapeWidth);
            }

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

            if (!fillShape) {
                paint.setColor(getResources().getColor(R.color.highlightBlue));
                int writingWidth = (int) Utilities.convertDpToPixel(1, this.getContext());
                paint.setStrokeWidth(writingWidth);
                drawShapeDetails.drawWriting(canvas, innerShapeBorder, shapeHeight, paint);
                paint.setColor(drawShapeDetails.getColor());
                int shapeWidth = (int) Utilities.convertDpToPixel(3, this.getContext());
                paint.setStrokeWidth(shapeWidth);
                drawShapeDetails.draw(canvas, innerShapeBorder, shapeHeight, paint);
            }
        }
    }

    @Override public void  onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //
        setMeasuredDimension(size, size);
    }
}
