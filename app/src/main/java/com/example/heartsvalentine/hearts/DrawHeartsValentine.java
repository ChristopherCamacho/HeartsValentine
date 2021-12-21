package com.example.heartsvalentine.hearts;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;

import com.example.heartsvalentine.R;

// https://stackoverflow.com/questions/27588965/how-to-use-custom-font-in-a-project-written-in-android-studio

public class DrawHeartsValentine {
	public DrawHeartsValentine(TextFormattingDetails tfd, boolean useEmoji, int heartColor, int backgroundColor, int margin, Context context) {
		this.tfd = tfd;
		mainSizes = new MainSizes(margin);
		hd = new HeartDetails(useEmoji, heartColor);
		this.backgroundColor = backgroundColor;
		initialize();
		this.context = context;
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
	    canvas = new Canvas(bitmap);//  bufferedImage.createGraphics();
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
		mainSizes.resetWidthHeightRadius(width);
		dt = new DrawText(canvas, mainSizes, hd, tfd, context);
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
		mainSizes.resetWidthHeightRadius(getWidthEstimateFromTextLen(pixelTxtLen));
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

			dt = new DrawText(canvas, mainSizes, hd, tfd, context);

			if (counter == 1) {
				dt.resetTextInputDefails();
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
							mainSizes.resetWidthHeightRadius(mainSizes.getWidth() - 1);
							smallDecrease = true;
						}				
					}
					else {
						mainSizes.resetWidthHeightRadius(mainSizes.getWidth() - 5);
					}	
					decreased = true;
				}				
			}
			else {
				// Frame is too small. We need to increase it and try again...
				if (decreased) {
					mainSizes.resetWidthHeightRadius(mainSizes.getWidth() + 1);
					smallIncrease = true;
					
					if (smallDecrease) {
						// both small increase and decrease have been call - this is best optimisation we can get.
						// exit loop after this...
						bestOptimization = true;
					}
				}
				else {
					mainSizes.resetWidthHeightRadius(mainSizes.getWidth() + 5);
				}
				increased = true;
			}
		}
		while (!goodSize && counter < maxIterations);

		bitmap.recycle();
		bitmap = Bitmap.createBitmap(mainSizes.getWidth(), mainSizes.getHeight(), Bitmap.Config.ARGB_8888);
		canvas = new Canvas(bitmap);
		dt = new DrawText(canvas, mainSizes, hd, tfd, context);
		dt.computeTextPlacementDetails();
	}
	
	public void draw() {
	//	try {
			int closestDistance = hd.getUseEmoji()? 250 : 150; // cannot get an exact distance between hearts centres. This is lowest value possible for good fit.
			// We work out this distance later, which will be a bit greater.
			paint.setColor(backgroundColor);
	    	canvas.drawRect(0, 0, mainSizes.getWidth(), mainSizes.getHeight(), paint );

			// Draw hearts...	    
			DrawHearts drawHearts = new DrawHearts(canvas, mainSizes, closestDistance, hd);
			drawHearts.draw();
			//for testing so see where bounding rectangles are...
	//		dt.drawTextBoundingRectangles();
			dt.draw();
	    
		//	canvas.dispose();
		//	https://stackoverflow.com/questions/13533471/how-to-save-view-from-canvas-to-png-file
			//https://www.geeksforgeeks.org/how-to-capture-screenshot-of-a-view-and-save-it-to-gallery-in-android/
			//https://www.geeksforgeeks.org/how-to-capture-screenshot-of-a-view-and-save-it-to-gallery-in-android/
			// Generating a file name
/*			String filename = "${System.currentTimeMillis()}.png";

			// Output stream
			OutputStream fos = null;

			// For devices running android >= Q
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
				// getting the contentResolver
				this.contentResolver?.also { resolver ->

						// Content resolver will process the contentvalues
						val contentValues = ContentValues().apply {

					// putting file information in content values
					put(MediaStore.MediaColumns.DISPLAY_NAME, filename)
					put(MediaStore.MediaColumns.MIME_TYPE, "image/jpg")
					put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
				}

					// Inserting the contentValues to
					// contentResolver and getting the Uri
					val imageUri: Uri? = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)

					// Opening an outputstream with the Uri that we got
					fos = imageUri?.let { resolver.openOutputStream(it) }
				}
			} else {
				// These for devices running on android < Q
				val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
				val image = File(imagesDir, filename)
				fos = FileOutputStream(image)
			}

			fos?.use {
				// Finally writing the bitmap to the output stream that we opened
				bitmap.compress(Bitmap.CompressFormat.JPEG, 100, it)
				Toast.makeText(this , "Captured View and saved to Gallery" , Toast.LENGTH_SHORT).show()
			}
		}



			File file = new File("HeartsValentine.png");
			ImageIO.write(bufferedImage, "png", file);
	
			file = new File("HeartsValentine.jpg");
			ImageIO.write(bufferedImage, "jpg", file); */
	/*	} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  */
	}

	public Bitmap GetHeartValBitmapImage() {
		return bitmap;
	}
		
	private final TextFormattingDetails tfd;
	private Bitmap bitmap;
	private Paint paint;
	//private BufferedImage bufferedImage;
	private Canvas canvas;
	private final HeartDetails hd;
	private DrawText dt;
	private float pixelTxtLen;
	private final MainSizes mainSizes;
	private static final int maxIterations = 300;
	private static final int stepIncrement = 4;
	private final int backgroundColor;
	private Context context;
}
