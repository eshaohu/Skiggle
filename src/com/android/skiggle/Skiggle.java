/*
*   This file is part of Skiggle, an online handwriting recognition
*   Java application.
*   Copyright (C) 2009-2011 Willie Lim <drwillie650@gmail.com>
*
*   Licensed under the Apache License, Version 2.0 (the "License");
*   you may not use this file except in compliance with the License.
*   You may obtain a copy of the License at
*
*       http://www.apache.org/licenses/LICENSE-2.0
*
*   Unless required by applicable law or agreed to in writing, software
*   distributed under the License is distributed on an "AS IS" BASIS,
*   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
*   See the License for the specific language governing permissions and
*   limitations under the License.
*/

package com.android.skiggle;


import com.android.skiggle.chinese.SegmentBitSetCn;
import com.android.skiggle.english.SegmentBitSetEn;
import com.android.skiggle.english.R;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

public class Skiggle extends Activity {
	
	// Global constants
	
	protected static final String APP_TITLE = "Skiggle"; // Title of the app
	
	protected static final String CHINESE_MODE = "Chinese"; // Chinese handwriting mode
	protected static final String ENGLISH_MODE = "English"; // English handwriting mode
	protected static final String DEFAULT_LANGUAGE_MODE = ENGLISH_MODE; // Default language mode is English
	
	// TODO: Use res/values/colors.xml?
	// Color codes
	public static final int AQUA = 0xFF00FFFF;
	public static final int GRAY_26 = 0xFF424242;
	public static final int GRAY_80 = 0xFFCCCCCC;
	public static final int RED = 0xFFFF0000;
	public static final int TRUE_GREEN = 0xFF00AF23;
	public static final int WHITE = 0xFFFFFFFF;
	
	// Global variables
	protected static String sLanguage = ENGLISH_MODE; // Set default language to English

	protected static int sDefaultPenColor = AQUA; // aqua
	protected static int sDefaultCanvasColor = WHITE;  // White color
	protected static float sDefaultStrokeWidth = 12.0F;	
	protected static float sDefaultFontSize = 14.0F; //12.0F;

	protected static int sDefaultWritePadWidth = 320;
	public static int sDefaultWritePadHeight = 480;
	
	protected static int sVirtualKeyboardLeft = 5; // Location of left edge of virtual keyboard (for candidate characters recognized)
	protected static int sVirutalKeyhoardTop = 5; // Location of top edge of virtual keyboard (for candidate characters recognized)
	
	protected static CandidatesKeyBoard sCharactersVirtualKeyBoard = null;
	
	protected static boolean sDebugOn = false; // TEMPORARY: Used for testing the virtual keyboard

	protected static BoxView sBoxView;
	
	protected static Canvas sCanvas;
	private Paint mPaint;
	private Paint mTextPaint;
	protected static Context sContext;

	public void setLanguageMode(String language) {
		// Set language specifics globals
		sLanguage = language;
		if (language == CHINESE_MODE) {
			SegmentBitSetCn.initializeSegmentBitSetGlobals();
		}
		else if (language == ENGLISH_MODE) {
			SegmentBitSetEn.initializeSegmentBitSetGlobals();
		}

		this.setTitle(APP_TITLE + "-" + language);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	

		sContext = this.getApplication().getBaseContext();
		sBoxView = new BoxView(this);
		setContentView(sBoxView);
		//setContentView(new BoxView(this));

		mPaint = new Paint();
		mPaint.setAntiAlias(true);
		mPaint.setDither(true);
		mPaint.setStyle(Paint.Style.STROKE);
		mPaint.setStrokeJoin(Paint.Join.ROUND);
		mPaint.setStrokeCap(Paint.Cap.ROUND);
		
		// Set canvas defaults
		mPaint.setColor(sDefaultPenColor);
		mPaint.setStrokeWidth(sDefaultStrokeWidth);

		mTextPaint = new Paint();
		// Set text paint defaults
		mTextPaint.setTextSize(sDefaultFontSize);

		setLanguageMode(sLanguage);
		
		// Restore preferences
		SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
		sLanguage = mPrefs.getString("language", DEFAULT_LANGUAGE_MODE);
		sDebugOn = mPrefs.getBoolean("debugMode", false);
		
		/*
		// Set language specifics globals
		if (sLanguage == CHINESE_MODE) {
			SegmentBitSetCn.initializeSegmentBitSetGlobals();
		}
		else if (sLanguage == ENGLISH_MODE) {
			SegmentBitSetEn.initializeSegmentBitSetGlobals();
		}

		this.setTitle(APP_TITLE + "-" + sLanguage);
		 */
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		
		// Save current settings
		SharedPreferences mPrefs = getPreferences(MODE_PRIVATE);
		SharedPreferences.Editor mPrefsEditor = mPrefs.edit();
		mPrefsEditor.putString("language", sLanguage);
		mPrefsEditor.putBoolean("debugMode", sDebugOn);
		mPrefsEditor.commit();
		/*
		if (sCharactersVirtualKeyBoard != null) {
			sCharactersVirtualKeyBoard.clear(Skiggle.sCanvas);
		}
		*/
		sBoxView.clear(); // Need this here?
		
	}
	
/*
	@Override
	protected void onStop() {
		super.onStop();
		sBoxView.clear();
		
	}
*/

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.skiggle_menu, menu);
	    return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    case R.id.chinese:
	    	setLanguageMode(CHINESE_MODE);
	    	/*
	        sLanguage = CHINESE_MODE;
			SegmentBitSetCn.initializeSegmentBitSetGlobals();
			this.setTitle(APP_TITLE + "-" + sLanguage);
			*/
	        return true;
	    case R.id.english:
	    	setLanguageMode(ENGLISH_MODE);
	    	/*
	        sLanguage = ENGLISH_MODE;
			SegmentBitSetEn.initializeSegmentBitSetGlobals();
			this.setTitle(APP_TITLE + "-" + sLanguage);
			*/
	        return true;
	    case R.id.debug_on:
	    	sDebugOn = true; 
	        return true;
	    case R.id.debug_off:
	    	sDebugOn = false; 
	        return true;
	    default:
	        return super.onOptionsItemSelected(item);
	    }
	}
	
	public class BoxView extends View {

		private Bitmap mBitmap;

//		private Canvas mCanvas;
		private Path mPath;
		private Paint mBitmapPaint;
		private float mX, mY;
		private static final float TOUCH_TOLERANCE = 4;
		private PenStroke mPenStroke;
		//		private PenSegment mPenSegment;
		//		private int mStrokeNumber = 0;
		//		private int mSegmentNumber = 0;
		
		public PenCharacter penCharacter = new PenCharacter();

		public BoxView(Context context) {
			super(context);

			mBitmap = Bitmap.createBitmap(sDefaultWritePadWidth, sDefaultWritePadHeight, Bitmap.Config.ARGB_8888);
			sCanvas = new Canvas(mBitmap);
			mBitmapPaint = new Paint(Paint.DITHER_FLAG);
			mPath = new Path();

			

		}

		@Override
		protected void onDraw(Canvas canvas) {
			canvas.drawColor(sDefaultCanvasColor);

			canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);

			canvas.drawPath(mPath, mPaint);
			
			if (Skiggle.sCharactersVirtualKeyBoard != null) {
				sCharactersVirtualKeyBoard.draw(canvas);			
			}

		}

		private void touchStart(float x, float y) {
			mPath.reset();
			mPath.moveTo(x, y);
			mX = x;
			mY = y;
		}

		private void touchMove(float x, float y) {
			float dx = Math.abs(x - mX);
			float dy = Math.abs(y - mY);
			if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
				mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2);
				mX = x;
				mY = y;
			}
		}

		private void touchUp() {
			mPath.lineTo(mX, mY);

			// commit the path to our off screen
			sCanvas.drawPath(mPath, mPaint);

			// If the stroke is a point of zero length , make it a filled circle of
			// diameter Skiggle.sDefaultStrokeWidth and add it to the path
			PathMeasure pMeasure = new PathMeasure(mPath, false);
			if (pMeasure.getLength() == 0) {
				RectF boundingRectF = new RectF(); 
				mPath.computeBounds(boundingRectF, false);

				// Create a line of 1 pixel length
				mPath.lineTo(boundingRectF.centerX(), boundingRectF.centerY() + 1);

			}

			// Set pen stroke to a copy of the stroke
			mPenStroke = new PenStroke(mPath);
			mPenStroke.addPath(mPath);
			penCharacter.addStroke(mPenStroke);

			// Paint the copy of the stroke with the new pen color
			sCanvas.drawPath( mPenStroke, mPaint);
			
			// Check to see if the stroke is a jagged "clear screen" stroke
			if ((mPenStroke.penStrokeLength/(mPenStroke.boundingRectWidth + mPenStroke.boundingRectHeight)) > 2) {
				this.clear();

			}
			else {

				penCharacter.addSegments(mPenStroke, sCanvas, mTextPaint);			
				penCharacter.findMatchingCharacter(sCanvas, mTextPaint, penCharacter, sLanguage);
			}

			// kill this so we don't double draw
			mPath.reset();
		}


		@Override
		public boolean onTouchEvent(MotionEvent event) {
			float x = event.getX();
			float y = event.getY();

			if (sDebugOn) {
				// Trap touch event that is inside sCharactersVirtualKeyBoard (should be taken care of by Android's nested event handlers)
				if ((sCharactersVirtualKeyBoard != null) && sCharactersVirtualKeyBoard.mRect.contains((int) x, (int) y)) {

					boolean flag =  sCharactersVirtualKeyBoard.onTouchEvent(event);
					invalidate();
					return flag;
				}
			}
			
			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:	
				touchStart(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_MOVE:
				touchMove(x, y);
				invalidate();
				break;
			case MotionEvent.ACTION_UP:
				touchUp();
				invalidate();
				break;
			}
			return true;
		}

		
		public void clear() {
			mBitmap.eraseColor(sDefaultCanvasColor);
			mPath.reset();		
			
			// Create a new PenCharacter object
			penCharacter = new PenCharacter();
			/*
			penCharacter.resetStrokes();
			penCharacter.resetSegments();
			*/
			
			// Clear the virtual keyboard if there is one
			if (sCharactersVirtualKeyBoard != null) {
				sCharactersVirtualKeyBoard = null;
			}
			
			invalidate();


		}

		/*
		@Override
		protected void onLayout(boolean arg0, int arg1, int arg2, int arg3,
				int arg4) {
			// TODO Auto-generated method stub
			
		}
		*/

	}
}
