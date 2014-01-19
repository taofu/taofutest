package com.jingfm.customer_views;

import com.jingfm.R;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;

public class ResizeLayout extends LinearLayout {
	private ResizeListener mResizeListener;
	public ResizeLayout(Context context) {
		super(context);
	}

	public ResizeLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onSizeChanged(final int w, final int h,final int oldw, final int oldh) {
		ResizeLayout.super.onSizeChanged(w, h, oldw, oldh);
		if (mResizeListener != null) {
			mResizeListener.onResize(w, h, oldw, oldh);
		}
	}

	@Override
	protected void onLayout(boolean changed, int l, int t, int r, int b) {
		super.onLayout(changed, l, t, r, b);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
	}
	
	public interface ResizeListener{
		public void onResize(int w, final int h, int oldw, final int oldh);
	}

	public void setResizeListener(ResizeListener resizeListener) {
		mResizeListener = resizeListener;		
	}
}