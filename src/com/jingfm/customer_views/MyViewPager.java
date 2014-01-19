package com.jingfm.customer_views;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ViewGroup;

public class MyViewPager extends ViewPager{
	
	private ViewGroup mParentView;

	public MyViewPager(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public MyViewPager(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		postParentMoveViewNotification(true);
		return super.onInterceptTouchEvent(ev);
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
