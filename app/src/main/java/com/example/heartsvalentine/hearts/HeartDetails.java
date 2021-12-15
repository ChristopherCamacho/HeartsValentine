package com.example.heartsvalentine.hearts;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;

final class HeartDetails {
	
	HeartDetails(boolean useEmoji, /*Canvas canvas*/int heartColor) {
		//this.canvas = canvas;
		this.heartColor = heartColor;
		this.useEmoji = useEmoji;
		initialize();
	}
	
	private void initialize() {
		if (useEmoji) {
			Typeface tf = Typeface.create("TimesRoman", Typeface.NORMAL);

			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setTypeface(tf);
			paint.setTextSize(150);

			Rect rectHeart = new Rect();
			paint.getTextBounds(heart, 0, 1, rectHeart);

			// Check where rect is painted
			//paint.setColor(Color.GREEN);
			//canvas.drawRect(150 + rectHeart.left, 150 + rectHeart.top, 150 + rectHeart.right,
			//		150 + rectHeart.bottom, paint);

			heartCenterX = rectHeart.exactCenterX();// - rectHeart.left;
			heartCenterY = rectHeart.exactCenterY() - 12;// have to add this or trespasses// - rectHeart.top;

			heartWidth = (float) (rectHeart.width() * 0.89);
			heartHeight = (float) (rectHeart.height() * 0.87);

			// This code draws a rectangle and the bounding rect that should touch edges of heart.
			// Adjusted rect so heart reaches its sides
			//paint.setColor(Color.BLUE);
			//canvas.drawRect(150 + rectHeart.left + heartCenterX - heartWidth/2,
			//		150 + rectHeart.top + heartCenterY - heartHeight/2,
			//		150 + rectHeart.left + heartCenterX + heartWidth/2,
			//		150 + rectHeart.top + heartCenterY + heartHeight/2, paint);
			// paint.setColor(Color.RED);
			// canvas.drawText(heart, 150, 150, paint);
		}
		else {
			heartWidth = 92;
			heartHeight = 0.96f * heartWidth;
			radius = 0.28f * heartWidth;
			heartCenterX = 0.5f * heartWidth;
			heartCenterY = 0.5f * heartHeight;
			initializeAlphaBeta(heartWidth, heartHeight, radius);
		}
	}

	public void draw(Canvas canvas, float x, float y, Paint paint) {
		if (useEmoji) {
			canvas.drawText(heart, x, y, paint);
		}
		else {
			y += heartHeight;
			RectF leftRect = new RectF(x, y, x + 2 * radius, y + 2 * radius);
			RectF rigthRect = new RectF(x + heartWidth - 2 * radius, y, x + heartWidth, y + 2 * radius);

			// Scaffolding code below to help with final drawing
			// Let us draw the background that contans heart
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

        canvas.drawArc(rigthRect, beta + 180, -beta + alpha, true, paint);
        paint.setColor(Color.GREEN);
        float startEndPtX = x + 0.5f * width;
        float startEndPtY = y + radius - (float)Math.sqrt(Math.pow(radius, 2) - Math.pow(width/2.0 - radius, 2));
        path.moveTo(startEndPtX, startEndPtY);

        */
			Path path = new Path();
		//	paint.setColor(Color.RED);
			path.arcTo(leftRect, -alpha, -beta + alpha);
			path.lineTo(x + 0.5f * heartWidth, y + heartHeight);
			path.arcTo(rigthRect, beta + 180, -beta + alpha);
			canvas.drawPath(path, paint);

		}

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

/*	void drawHeart(Canvas canvas) {
		// This code draws a rectangle and the bounding rect that should touch edges of heart.
		Paint paint = new Paint();
		paint.setColor(Color.BLUE);


		Typeface tf = Typeface.create("TimesRoman",  Typeface.NORMAL);
		paint.setTypeface(tf);
		paint.setTextSize(150);


	} */

	float getHeartCenterX() { return heartCenterX; }
	float getHeartCenterY() {
		return heartCenterY;
	}
	float getHeartWidth() {
		return heartWidth;
	}
	float getHeartHeight() {
		return heartHeight;
	}

	int getColor() {return heartColor; }
	boolean getUseEmoji(){ return useEmoji; }

	//private final Canvas canvas;
	private final String heart = Character.toString((char)0x2665);//"♥";//""♥";
	private float heartCenterX;
	private float heartCenterY;
	private float heartWidth;
	private float heartHeight;
	private float radius;
	private final int heartColor;
	private final boolean useEmoji;
	static private boolean alphaBetaComputed = false;
	static private float alpha;
	static private float beta;
}
