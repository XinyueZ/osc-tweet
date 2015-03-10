/**
 * Copyright (C) 2010 The Android Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations under the License.
 */
package com.osc.tweet.views.touch;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ImageView;

/**
 * <a href="https://code.google.com/p/android-touchexample/">Author</a>
 */
public final class ScalableView extends ImageView {
	private VersionedGestureDetector mDetector;
	private float mScaleFactor = 1.f;

	private Bitmap mOriginalBmp;

	public ScalableView(Context context) {
		this(context, null, 0);
	}

	public ScalableView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public ScalableView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	public void setImageBitmap(Bitmap bm) {
		super.setImageBitmap(bm);
		if (mDetector == null) {
			mDetector = VersionedGestureDetector.newInstance(getContext(), new GestureCallback(bm));
		}
	}

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		mDetector.onTouchEvent(ev);
		return true;
	}


	private class GestureCallback implements VersionedGestureDetector.OnGestureListener {
		Bitmap mOriginalBmp;


		public GestureCallback(Bitmap originalBmp) {
			mOriginalBmp = originalBmp;
		}

		public void onDrag(float dx, float dy) {
			invalidate();
		}

		public void onScale(float scaleFactor) {
			mScaleFactor *= scaleFactor;
			// Don't let the object get too small or too large.
			//mScaleFactor = Math.max(0.1f, Math.min(mScaleFactor, 5.0f));
			Bitmap bitmap = Bitmap.createScaledBitmap(mOriginalBmp, mOriginalBmp.getWidth()  + (int) (mOriginalBmp.getWidth() * scaleFactor),
					mOriginalBmp.getHeight() + (int) (mOriginalBmp.getHeight() * scaleFactor), true);
			setImageBitmap(bitmap);
			invalidate();
		}
	}
}
