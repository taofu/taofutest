package com.jingfm.customer_views;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public class AnimationView extends View {

	public AnimationView(Context context) {
		super(context);
		initView();
	}

	private void initView() {
		// Object a = android.graphics.Path.quadTo( 1, 1, 1, 1);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		doDraw(canvas);
	}

	private void doDraw(Canvas canvas) {
		// TODO Auto-generated method stub

	}

	private long calcBezier(float interpolatedTime, float p0, float p1, float p2) {
		return Math.round((Math.pow((1 - interpolatedTime), 2) * p0)
				+ (2 * (1 - interpolatedTime) * interpolatedTime * p1)
				+ (Math.pow(interpolatedTime, 2) * p2));
	}

}
