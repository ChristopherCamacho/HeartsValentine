package com.example.heartsvalentine.hearts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.example.heartsvalentine.R;
import com.example.heartsvalentine.frameShapes.ShapeType;
import com.example.heartsvalentine.hearts.mainShapes.MainShape;
import com.example.heartsvalentine.hearts.mainSizes.MainSizes;
import com.example.heartsvalentine.hearts.shapeDetails.ShapeDetails;
import com.example.heartsvalentine.hearts.shapeDetails.SymbolShapeDetails;

// https://stackoverflow.com/questions/27588965/how-to-use-custom-font-in-a-project-written-in-android-studio

public class DrawHeartsValentine {
	private final TextFormattingDetails tfd;
	private Bitmap bitmap;
	private Paint paint;
	private Canvas canvas;
	private final ShapeDetails sd;
	private DrawText dt;
	private float pixelTxtLen;
	private final MainSizes mainSizes;
	private static final int maxIterations = 300;
	private static final int stepIncrement = 4;
	private final int backgroundColor;
	private Context context;
	private final ShapeType mainShapeType;

	public DrawHeartsValentine(TextFormattingDetails tfd, ShapeType mainShapeType, ShapeDetails sd, int backgroundColor, int margin, Context context) {
		this.tfd = tfd;
		mainSizes = ObjectFromShapeType.getMainSizeFromShapeType(mainShapeType, margin);
		this.sd = sd;
		this.backgroundColor = backgroundColor;
		initialize();
		this.context = context;
		this.mainShapeType = mainShapeType;
	}
	
	private void initialize() {
		initializeGraphics();
		setStandardTextFont();
		computePixelTextLength();
	}
	
	// Initialises an unused graphic object so can calculate text size.
	// We don't know the size of the image till we have the text size.
	// We can only have this by creating an unused graphic object.
	private void initializeGraphics() {
		bitmap = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
	    canvas = new Canvas(bitmap);
	}
	
	private void setStandardTextFont() {
		Typeface tf = Typeface.create("TimesRoman",  Typeface.NORMAL);

		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setTypeface(tf);
		paint.setTextSize(150);
	}

	
	// Computes the pixel text length. (Not number of characters)
	private void computePixelTextLength() {
		pixelTxtLen = paint.measureText(tfd.getContentText());
	}
	
	// part of experiment - used for computing gross estimate
	private int textLenFromWidth(int width) {
		//hd = new HeartDetails(/*canvas*/);
		mainSizes.resetSizes(width);
		dt = new DrawText(canvas, mainSizes, sd, tfd, mainShapeType, context);
		return dt.computeTextSpaceAvailable();
	}
  	
	private int computeWidthEstimateFromTextLen(int lowerBound, int upperBound, int step, float txtLengthEstimate) {
		int retVal = -1;
	 		
  		for (int i = lowerBound; i <= upperBound; i+= step) {	
  			int txtLen = textLenFromWidth(i);
  			
  			if (txtLen >= txtLengthEstimate) {
  				if (step == 1) {
  					retVal = i;
  				}
  				else {
  					retVal = computeWidthEstimateFromTextLen(i - step, i, step/4, txtLengthEstimate);
  				}
  				break;
  			}
  		}
  		return retVal;
	}
  	
  	// Retrieves a gross estimate of with from pixel text length
  	private int getWidthEstimateFromTextLen(float txtLengthEstimate) {
  		int step = stepIncrement;
  		
  		while (step < txtLengthEstimate) {
  			step *= stepIncrement;
  		}
  		int lowerBound = 0;
  		int upperBound = step;
  		step /= stepIncrement;
  
  		return computeWidthEstimateFromTextLen(lowerBound, upperBound, step, txtLengthEstimate);
  	}
	
	public void computeTextFit(Context context) throws HeartsValException {
		mainSizes.resetSizes(getWidthEstimateFromTextLen(pixelTxtLen));
		this.context = context;

		if (tfd.getContentText().length() == 0) {
			throw new HeartsValException(context.getResources().getString(R.string.error_no_text));
		}
		else if (mainSizes.getWidth() == 0 || mainSizes.getHeight() == 0) {
			throw new HeartsValException(context.getResources().getString(R.string.error_text_too_short));
		}

		boolean goodSize = false;
		int counter = 0;
		boolean decreased = false;
		boolean increased = false;
		boolean smallDecrease = false;
		boolean smallIncrease = false;
		boolean bestOptimization = false;
		canvas = new Canvas(bitmap);

		do {
			counter++;

			if (mainSizes.getWidth() == 0 || mainSizes.getHeight() == 0)
				throw new HeartsValException(context.getResources().getString(R.string.error_no_width_or_height));

			dt = new DrawText(canvas, mainSizes, sd, tfd, mainShapeType, context);

			if (counter == 1) {
				dt.resetTextInputDetails();
			}
			dt.computeTextPlacementDetails();
			
			if (bestOptimization)
				break;
			
			if (dt.doesAllTextFit()) {
				if (dt.SizeOptimized()) {
					goodSize = true;
				}
				else {
					// Frame is too big. We need to decrease it and try again...
					if (increased) {
						if (smallIncrease) {
							// this is best size we can get. Any smaller, text won't fit...
							goodSize = true;
						}
						else {
							mainSizes.resetSizes(mainSizes.getWidth() - 1);
							smallDecrease = true;
						}				
					}
					else {
						mainSizes.resetSizes(mainSizes.getWidth() - 5);
					}	
					decreased = true;
				}				
			}
			else {
				// Frame is too small. We need to increase it and try again...
				if (decreased) {
					mainSizes.resetSizes(mainSizes.getWidth() + 1);
					smallIncrease = true;
					
					if (smallDecrease) {
						// both small increase and decrease have been call - this is best optimisation we can get.
						// exit loop after this...
						bestOptimization = true;
					}
				}
				else {
					mainSizes.resetSizes(mainSizes.getWidth() + 5);
				}
				increased = true;
			}
		}
		while (!goodSize && counter < maxIterations);

		bitmap.recycle();
		bitmap = Bitmap.createBitmap(mainSizes.getWidth(), mainSizes.getHeight(), Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		dt = new DrawText(canvas, mainSizes, sd, tfd, mainShapeType, context);
		dt.computeTextPlacementDetails();
	}
	
	public void draw() {
		int closestDistance = sd.getClosestDistance(); // cannot get an exact distance between hearts centres. This is lowest value possible for good fit.

		// We work out this distance later, which will be a bit greater.
		paint.setColor(backgroundColor);
		canvas.drawRect(0, 0, mainSizes.getWidth(), mainSizes.getHeight(), paint );

		/* for testing emoji placement
		if (sd instanceof EmojiShapeDetails) {
			EmojiShapeDetails esd = (EmojiShapeDetails)sd;
			esd.drawEmojiWithBoundaries(canvas, paint);
		}
		*/

		// Draw hearts...
		MainShape ms = ObjectFromShapeType.getMainShape(mainShapeType, canvas, mainSizes, closestDistance, sd);

		if (ms != null) {
			ms.draw();
		}

		// for testing so see where bounding rectangles are...
		// dt.drawTextBoundingRectangles();
		dt.draw();
	}

	public Bitmap GetHeartValBitmapImage() {
		return bitmap;
	}
}
