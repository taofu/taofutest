package com.jingfm.customer_views;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.SlidingDrawer;

public class CustomSlidingDrawer extends SlidingDrawer {

	private int mHandleId = 0; // 抽屉行为控件ID
	private int[] mTouchableIds = null; // Handle 部分其他控件ID

	public int[] getTouchableIds() {
		return mTouchableIds;
	}

	public void setTouchableIds(int[] mTouchableIds) {
		this.mTouchableIds = mTouchableIds;
	}

	public int getHandleId() {
		return mHandleId;
	}

	public void setHandleId(int mHandleId) {
		this.mHandleId = mHandleId;
	}

	public CustomSlidingDrawer(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public CustomSlidingDrawer(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	/* * 获取控件的屏幕区域 */
	public Rect getRectOnScreen(View view) {
		Rect rect = new Rect();
		int[] location = new int[2];
		View parent = view;
		if (view.getParent() instanceof View) {
			parent = (View) view.getParent();
		}
		parent.getLocationOnScreen(location);
		view.getHitRect(rect);
		rect.offset(location[0], location[1]);
		return rect;
	}

	public boolean onInterceptTouchEvent(MotionEvent event) {
		// 触摸位置转换为屏幕坐标
		int[] location = new int[2];
		int x = (int) event.getX();
		int y = (int) event.getY();
		this.getLocationOnScreen(location);
		x += location[0];
		y += location[1];
		// handle部分独立按钮
		if (mTouchableIds != null) {
			for (int id : mTouchableIds) {
				View view = findViewById(id);
				Rect rect = getRectOnScreen(view);
				if (rect.contains(x, y)) {
					// 在这里可以定制自己的消息。
					if (MotionEvent.ACTION_DOWN == event.getAction()) {
						view.dispatchTouchEvent(event);
					}
					return false;
				}
			}
		}
		// 抽屉行为控件
		if (event.getAction() == MotionEvent.ACTION_DOWN && mHandleId != 0) {
			View view = findViewById(mHandleId);
			Log.i("MySlidingDrawer on touch", String.format("%d,%d", x, y));
			Rect rect = getRectOnScreen(view);
			Log.i("MySlidingDrawer handle screen rect", String
					.format("%d,%d %d,%d", rect.left, rect.top, rect.right,
							rect.bottom));
			if (rect.contains(x, y)) {
				// 点击抽屉控件时交由系统处理
				Log.i("MySlidingDrawer", "Hit handle");

			} else {
				return false;

			}
		}
		return super.onInterceptTouchEvent(event);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		Log.i("MySlidingDrawer on touch", "Action=" + event.getAction());
		return super.onTouchEvent(event);
	}
	/*
	 * protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
	 * int widthSpecMode = MeasureSpec.getMode(widthMeasureSpec); int
	 * widthSpecSize = MeasureSpec.getSize(widthMeasureSpec); int heightSpecMode
	 * = MeasureSpec.getMode(heightMeasureSpec); int heightSpecSize =
	 * MeasureSpec.getSize(heightMeasureSpec); if (widthSpecMode ==
	 * MeasureSpec.UNSPECIFIED || heightSpecMode == MeasureSpec.UNSPECIFIED) {
	 * throw new RuntimeException(
	 * "SlidingDrawer cannot have UNSPECIFIED dimensions"); } final View handle
	 * = getHandle(); final View content = getContent(); measureChild(handle,
	 * widthMeasureSpec, heightMeasureSpec); int extra = handle.getHeight() / 6;
	 * System.out.println(handle.getMeasuredHeight() + "        " +
	 * content.getHeight()); if (mVertical) { int height = heightSpecSize -
	 * handle.getMeasuredHeight() - mTopOffset;
	 * content.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(height,
	 * heightSpecMode)); heightSpecSize = handle.getMeasuredHeight() +
	 * mTopOffset + content.getMeasuredHeight(); widthSpecSize =
	 * content.getMeasuredWidth(); if (handle.getMeasuredWidth() >
	 * widthSpecSize) widthSpecSize = handle.getMeasuredWidth(); }
	 * setMeasuredDimension(widthSpecSize, heightSpecSize); } private boolean
	 * mVertical; private int mTopOffset;
	 */
}