package com.jingfm.customer_views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.widget.SeekBar;

public class JingSeekBar extends SeekBar{

	private ViewGroup mParentView;

	public JingSeekBar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public JingSeekBar(Context context) {
		super(context);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		postParentMoveViewNotification(true);
		super.onTouchEvent(event);
		return true;
	}
	
	public void setDisallowInterceptView(ViewGroup parentView){
		mParentView = parentView;
	}
	
	private void postParentMoveViewNotification(boolean flage) {
		if (mParentView != null) {
			mParentView.requestDisallowInterceptTouchEvent(flage);
		}
	}
	
}
