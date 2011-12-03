package com.android.skiggle;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.RectShape;
import android.view.View;

public class CandidateCharacterKeysView extends Drawable {
	private ShapeDrawable mDrawable;
	private String mCandidateCharacters;
	private int mX = 5;
	private int mY = 400;
	private int mKeyWidth = 30;
	private int mKeyHeight = 30;
	private int mKeySpacing = 2;

	public CandidateCharacterKeysView(String str) {
		super();

		mCandidateCharacters = str;
		int width = str.length() * (mKeyWidth + mKeySpacing);

		mDrawable = new ShapeDrawable(new RectShape());
		mDrawable.getPaint().setColor(0xffcccccc); // Light Gray
		mDrawable.setBounds(mX, mY, mX + width, mY + mKeyHeight);
	} // End of CandidateCharacterKeysView() constructor

	/**
	 * Draws the keys for the best candidate followed by the other candidates
	 */
	@Override

	public void draw(Canvas canvas) {
		mDrawable.draw(canvas);
		Rect rect = mDrawable.getBounds();
		int baseX = rect.left;
		int y = rect.bottom - 5;
		for (int i = 0; i < mCandidateCharacters.length(); i++) {
			Paint tempPaint = new Paint();
			if (i == 0) { // First character is the best candidate
				tempPaint.setColor(0xffff0000); // Red
			}
			else {
				tempPaint.setColor(0xff444444); // Gray
			}

			tempPaint.setTextSize(20);
			float[] widths = {15};
			//tempPaint.getTextWidths(mCandidateCharacters, widths);

			canvas.drawText(mCandidateCharacters.substring(i, i+1), (float) (baseX + (mKeyWidth * i + mKeySpacing)  + Math.floor((mKeyWidth - widths[0]))/2), y, tempPaint);	
		}
	} // End of draw() method

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
		
	}
} // End of CandidateCharacterKeysView class
