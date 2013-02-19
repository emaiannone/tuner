/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *	http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

// Severely hacked by:
// Bill Farmer	william j farmer [at] yahoo [dot] co [dot] uk.
//
// 16-02-2013
//

package org.billthefarmer.tuner;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.SweepGradient;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

// Colour picker

public class ColourPicker extends View
{
    private Paint mCirclePaint;
    private Paint mCentrePaint;
    private final int[] mColours =
    {Color.RED, Color.MAGENTA, Color.BLUE, Color.CYAN,
     Color.GREEN, Color.YELLOW, Color.RED};

    private boolean mTrackingCentre;
    private int mCircleRadius;
    private int mStrokeWidth;
    private int mCentreRadius;
    private int mOffset;

    private float hsv[] = {0, 1, 1};

    private final static int WIDTH = 160;
    private final static int HEIGHT = 160;
    
    // Colour change listener

    private ColourChangeListener listener;

    protected interface ColourChangeListener
    {
	void onColourChanged(int colour);
    }

    // Constructor

    public ColourPicker(Context context, AttributeSet attrs)
    {
	super(context, attrs);

	Shader shader = new SweepGradient(0, 0, mColours, null);

	mCirclePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mCirclePaint.setShader(shader);
	mCirclePaint.setStyle(Paint.Style.STROKE);

	mCentrePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
	mCentrePaint.setStrokeWidth(8);
    }

    // On measure

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
	setMeasuredDimension(WIDTH, HEIGHT);
    }

    // On size changed

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
	// Calculate the dimensions based on the given values

	mCircleRadius = h / 2;
	mStrokeWidth = w / 5;
	mCentreRadius = w / 6;
	mOffset = w / 2;

	mCirclePaint.setStrokeWidth(mStrokeWidth);
    }

    // On draw

    @Override
    protected void onDraw(Canvas canvas)
    {
	float r = mCircleRadius - mStrokeWidth * 0.5f;

	canvas.translate(mOffset, mCircleRadius);

	canvas.drawCircle(0, 0, r, mCirclePaint);
	canvas.drawCircle(0, 0, mCentreRadius, mCentrePaint);
    }

    // Set listener

    protected void setListener(ColourChangeListener l)
    {
	listener = l;
    }

    // Set colour

    protected void setColour(int c)
    {
	mCentrePaint.setColor(c);
    }

    // Get colour

    protected int getColour()
    {
	return mCentrePaint.getColor();
    }

    // On touch event

    @Override
    public boolean onTouchEvent(MotionEvent event)
    {
	float x = event.getX() - mOffset;
	float y = event.getY() - mCircleRadius;

	boolean inCentre = Math.sqrt(x * x + y * y) <= mCentreRadius;

	switch (event.getAction())
	{
	case MotionEvent.ACTION_DOWN:
	    mTrackingCentre = inCentre;
	    if (inCentre)
	    {
		if (listener != null)
		    listener.onColourChanged(mCentrePaint.getColor());

		break;
	    }

	case MotionEvent.ACTION_MOVE:
	    if (!mTrackingCentre)
	    {
		float angle = (float)Math.toDegrees(Math.atan2(y, -x));

		// need to turn angle +-180 to 0-360

		hsv[0] = angle + 180;

		mCentrePaint.setColor(Color.HSVToColor(hsv));
		invalidate();
	    }
	    break;

	case MotionEvent.ACTION_UP:
	    if (mTrackingCentre)
	    {
		mTrackingCentre = false;
		invalidate();
	    }
	    break;
	}

	return true;
    }
}
