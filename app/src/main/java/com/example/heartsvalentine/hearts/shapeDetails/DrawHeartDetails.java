package com.example.heartsvalentine.hearts.shapeDetails;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;

public class DrawHeartDetails implements DrawShapeDetails {

    private float heartCenterX;
    private float heartCenterY;
    private final float heartWidth;
    private float heartHeight;
    private float radius;
    private int heartColor;
    static private boolean alphaBetaComputed = false;
    static private float alpha;
    static private float beta;

    public DrawHeartDetails(int heartColor, int heartWidth) {
        this.heartColor = heartColor;
        this.heartWidth = heartWidth;
        initialize();
    }

    private void initialize() {
       // heartWidth = 92;
        heartHeight = 0.96f * heartWidth;
        radius = 0.28f * heartWidth;
        heartCenterX = 0.5f * heartWidth;
        heartCenterY = 0.5f * heartHeight;
        initializeAlphaBeta(heartWidth, heartHeight, radius);
    }

    static void initializeAlphaBeta(float width, float height, float radius) {
        if (!alphaBetaComputed) {
            alphaBetaComputed = true;
            // Start angle of left heart curve is given by:
            alpha = (float) (Math.acos((width / 2.0 - radius) / radius) * 180 / Math.PI);
            double phi = Math.atan((height - radius) / (width / 2.0 - radius));
            // distance l between centre of heart (Cx, Cy) and bottom point of heart (BPx, BPy) is
            // SQRT((PBx - Cx)pow2 + (BPy - Cy)pow2)
            // Cx = x + radius
            // Cy = y + radius
            // BPx = x + width/2.0
            // PBy = y + height
            // this is:
            // l = Math.sqrt(Math.pow(x + width/2.0 - (x + radius), 2) + Math.pow(y + height - (y + radius), 2));
            // Which can be simplified into:
            double l = Math.sqrt(Math.pow(width / 2.0 - radius, 2) + Math.pow(height - radius, 2));
            double zeta = Math.acos(radius / l);
            double betaRadian = Math.max(phi + zeta, phi - zeta);
            beta = (float) (betaRadian * 180.0 / Math.PI) + 90;
        }
    }

    @Override
    public void draw(Canvas canvas, float x, float y, Paint paint) {
        y += heartHeight;
        RectF leftRect = new RectF(x, y, x + 2 * radius, y + 2 * radius);
        RectF rightRect = new RectF(x + heartWidth - 2 * radius, y, x + heartWidth, y + 2 * radius);

        // Scaffolding code below to help with final drawing
        // Let us draw the background that contains heart
        /* paint.setColor(Color.GRAY);
        RectF boundaryRect = new RectF(x, y, x + width, y + height);
        canvas.drawRect(boundaryRect, paint);

        paint.setColor(Color.BLUE);

        canvas.drawRect(leftRect, paint);

        paint.setColor(Color.MAGENTA);
        RectF leftSideRect = new RectF(x, y, x + width/2f, y + height);
        canvas.drawRect(leftSideRect, paint);

        paint.setColor(Color.CYAN);

        canvas.drawArc(leftRect, -alpha, -beta + alpha, true, paint);


        paint.setColor(Color.YELLOW);

        canvas.drawArc(rightRect, beta + 180, -beta + alpha, true, paint);
        paint.setColor(Color.GREEN);
        float startEndPtX = x + 0.5f * width;
        float startEndPtY = y + radius - (float)Math.sqrt(Math.pow(radius, 2) - Math.pow(width/2.0 - radius, 2));
        path.moveTo(startEndPtX, startEndPtY);
        */
        Path path = new Path();
        //	paint.setColor(Color.RED);
        path.arcTo(leftRect, -alpha, -beta + alpha);
        path.lineTo(x + 0.5f * heartWidth, y + heartHeight);
        path.arcTo(rightRect, beta + 180, -beta + alpha);
        canvas.drawPath(path, paint);
    }

    @Override
    public float getCenterX() {
        return heartCenterX;
    }

    @Override
    public float getCenterY() {
        return heartCenterY;
    }

    @Override
    public float getWidth() {
        return heartWidth;
    }

    @Override
    public float getHeight() {
        return heartHeight;
    }

    @Override
    public float getBottomAdjustment() { return -2 * heartHeight; }

    @Override
    public int getClosestDistance() {
        return 150;
    }

    @Override
    public void setColor(int color) { heartColor = color; }

    @Override
    public int getColor() {
        return heartColor;
    }


/*	void drawHeart(Canvas canvas) {
		// This code draws a rectangle and the bounding rect that should touch edges of heart.
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);


		Typeface tf = Typeface.create("TimesRoman",  Typeface.NORMAL);
		paint.setTypeface(tf);
		paint.setTextSize(150);


	} */
}
