package com.jingfm.customer_views;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.FrameLayout;
import android.widget.Scroller;

public class TouchScrollContainer extends FrameLayout {

	private Scroller mScroller;
	private VelocityTracker mVelocityTracker;
	private float mEdgeLimitRate = 0.83f;
	private static final int SNAP_VELOCITY = 1000;
	private static final int ANIMATE_TIME = 500;
	public static final int EVENT_FINAL_IN_PST_OUT_OF_LEFT = -4;
	public static final int EVENT_MOVE_TO_PST_LEFT = -3;
	public static final int EVENT_FINAL_IN_PST_LEFT = -2;
	public static final int EVENT_NOW_IN_PST_LEFT = -1;
	public static final int EVENT_FINAL_IN_PST_CENTER = 0;
	public static final int EVENT_FINAL_IN_PST_RIGHT = 1;
	public static final int EVENT_NOW_IN_PST_RIGHT = 2;
	public static final int EVENT_MOVE_TO_PST_RIGHT = 3;
	public static final int EVENT_FINAL_IN_PST_OUT_OF_RIGHT = 4;
	private int mTouchSlop;
	private int mRightEdgeLimit;
	private int mLeftEdgeLimit;
	private Context mContext;
	private int mMaxWidth;
	private boolean isScrollStart;
	private String mTag;
	private int mOverWidth;
	

	public TouchScrollContainer(Context context, AttributeSet attrs,
			int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public TouchScrollContainer(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public TouchScrollContainer(Context context) {
		super(context);
		this.mContext = context;
		initView(context);
	}

	private void initView(Context context) {
		this.mContext = context;
		mScroller = new Scroller(getContext());
		mTouchSlop = ViewConfiguration.get(getContext()).getScaledTouchSlop();
	}

	public int getmRightEdgeLimit() {
		return mRightEdgeLimit;
	}

	public void setmRightEdgeLimit(int mRightEdgeLimit) {
		this.mRightEdgeLimit = mRightEdgeLimit;
	}

	public int getmLeftEdgeLimit() {
		return mLeftEdgeLimit;
	}

	public void setmLeftEdgeLimit(int mLeftEdgeLimit) {
		this.mLeftEdgeLimit = mLeftEdgeLimit;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mMaxWidth = MeasureSpec.getSize(widthMeasureSpec);
		setEdgeLimitRate(mEdgeLimitRate);
	}

	public void setOverWidth(int overWidth){
		mOverWidth = overWidth;
	}
	
	public void setEdgeLimitRate(float rate){
		mEdgeLimitRate = rate;
		mRightEdgeLimit = (int) (mMaxWidth * mEdgeLimitRate) - mOverWidth;
		mLeftEdgeLimit = (int) (-mMaxWidth * mEdgeLimitRate) + mOverWidth;
	}
	
	public void setTagText(String tag){
		mTag = tag;
	}
	
	// 重写
	@Override
	public void scrollTo(int x, int y) {
		isScrollStart = true;
		super.scrollTo(x, y);
		isScrollStart = false;
		postInvalidate();
	}

	private boolean isScroll = false;

	private void smoothScrollTo(int dx, int time) {
		int duration = time;// 滚动的时间秒数
		int oldScrollX = getScrollX();
		mScroller.startScroll(oldScrollX, getScrollY(), dx, getScrollY(),
				duration);
		if (getScrollX()>mOverWidth) {
			onScorllToEvnet(EVENT_FINAL_IN_PST_LEFT);
		}else if(getScrollX()<-mOverWidth) {
			onScorllToEvnet(EVENT_FINAL_IN_PST_RIGHT);
		}else{
			onScorllToEvnet(EVENT_FINAL_IN_PST_CENTER);
		}
		invalidate();
		isScroll = true;
	}

	public void smoothResetPst(int time) {
		int duration = time;// 滚动的时间秒数
		int oldScrollX = getScrollX();
//		int dx = 8 - oldScrollX;
		int dx = 0 - oldScrollX;
		if (dx != 0) {
			mScroller.startScroll(oldScrollX, getScrollY(), dx, getScrollY(),
					duration);
			invalidate();
			isScroll = true;
		}
	}
	public void smoothToLeft(int time) {
		int duration = time;// 滚动的时间秒数
		int oldScrollX = getScrollX();
		int dx = mRightEdgeLimit - oldScrollX;
		if (dx != 0) {
			mScroller.startScroll(oldScrollX, getScrollY(), dx, getScrollY(),
					duration);
			invalidate();
			isScroll = true;
		}
	}
	public void smoothToLeftOut(int time) {
		int duration = time;// 滚动的时间秒数
		int oldScrollX = getScrollX();
		int dx = mMaxWidth - oldScrollX;
		if (dx != 0) {
			mScroller.startScroll(oldScrollX, getScrollY(), dx, getScrollY(),
					duration);
			invalidate();
			isScroll = true;
		}
		onScorllToEvnet(EVENT_FINAL_IN_PST_OUT_OF_LEFT);
	}
	public void smoothToRight(int time) {
		int duration = time;// 滚动的时间秒数
		int oldScrollX = getScrollX();
		int dx = mLeftEdgeLimit  - oldScrollX;
		if (dx != 0) {
			mScroller.startScroll(oldScrollX, getScrollY(), dx, getScrollY(),
					duration);
			invalidate();
			isScroll = true;
		}
	}
	public void smoothToRightOut(int time) {
		int duration = time;// 滚动的时间秒数
		int oldScrollX = getScrollX();
		int dx = -mMaxWidth + mOverWidth  - oldScrollX;
		if (dx != 0) {
			mScroller.startScroll(oldScrollX, getScrollY(), dx, getScrollY(),
					duration);
			invalidate();
			isScroll = true;
		}
		onScorllToEvnet(EVENT_FINAL_IN_PST_OUT_OF_RIGHT);
	}

	@Override
	public void computeScroll() {
		if (!mScroller.isFinished()) {
			if (mScroller.computeScrollOffset()) {
				int oldX = getScrollX();
				int oldY = getScrollY();
				int x = mScroller.getCurrX();
				int y = mScroller.getCurrY();
				if (oldX != x || oldY != y) {
					scrollTo(x, y);
				}
				invalidate();
			} else {
				clearChildrenCache();
			}
			Intent intent = new Intent("invalidate");
			this.mContext.sendBroadcast(intent);
		} else {
			if (isScroll) {
				isScroll = false;
				postInvalidate();
			}
			clearChildrenCache();
		}
	}

	// ////移动/////
	private boolean mIsBeingDragged = true;
	private float mLastMotionX;
	private float mLastMotionY;

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev) {
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			mLastMotionX = x;
			mLastMotionY = y;
			mIsBeingDragged = false;
			break;
		case MotionEvent.ACTION_MOVE:
			final float dx = x - mLastMotionX;
			final float xDiff = Math.abs(dx);
			final float yDiff = Math.abs(y - mLastMotionY);
			if (xDiff > mTouchSlop && xDiff > yDiff) {
				mIsBeingDragged = true;
				mLastMotionX = x;
			}
			break;
		}
		return mIsBeingDragged;
	}

	private boolean mIsAlreadySetViewState = false;

	private boolean isFreeze;
	private OnShowEventListener mOnShowEventListener;
	private boolean isDisableMoveToRight;

	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if (isFreeze) {
			return true;
		}
		if (mVelocityTracker == null) {
			mVelocityTracker = VelocityTracker.obtain();
		}
		mVelocityTracker.addMovement(ev);
		final int action = ev.getAction();
		final float x = ev.getX();
		final float y = ev.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if (mIsAlreadySetViewState) {
				return false;
			}
			if (!mScroller.isFinished()) {
				mScroller.abortAnimation();
			}
			mLastMotionX = x;
			mLastMotionY = y;
			int viewOffset = -getScrollX();
			if (viewOffset < 0
					&& mLastMotionX > viewOffset + mMaxWidth) {
				return false;
			}else if (viewOffset > 0
					&& mLastMotionX < viewOffset)  {
				return false;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			enableChildrenCache();
			float deltaX = mLastMotionX - x;
			mLastMotionX = x;
			float oldScrollX = getScrollX();
			float scrollX = oldScrollX + deltaX;

			if (deltaX < 0 && oldScrollX < 0) { // left view
				final float leftBound = 0;
				final float rightBound = -getmRightEdgeLimit();
				if (scrollX > leftBound) {
					scrollX = leftBound;
				} else if (scrollX < rightBound) {
					scrollX = rightBound;
				}
			} else if (deltaX > 0 && oldScrollX > 0) { // right view
				final float rightBound = -getmLeftEdgeLimit();
				final float leftBound = 0;
				if (scrollX < leftBound) {
					scrollX = leftBound;
				} else if (scrollX > rightBound) {
					scrollX = rightBound;
				}
			}
			if (isDisableMoveToRight 
					&& deltaX > 0
					&& scrollX > 0) { //disable move to right
				break;
			}
			if (deltaX > 0) {
				onScorllToEvnet(EVENT_MOVE_TO_PST_LEFT);
			}else{
				onScorllToEvnet(EVENT_MOVE_TO_PST_RIGHT);
			}
			if (scrollX > 0) {
				onScorllToEvnet(EVENT_NOW_IN_PST_LEFT);
			}else{
				onScorllToEvnet(EVENT_NOW_IN_PST_RIGHT);
			}
			scrollTo((int) scrollX, getScrollY());
			break;
		case MotionEvent.ACTION_CANCEL:
		case MotionEvent.ACTION_UP:
			isScrollStart = false;
//			final VelocityTracker velocityTracker = mVelocityTracker;
//			velocityTracker.computeCurrentVelocity(1000);
//			int velocityX = (int) velocityTracker.getXVelocity();
//			velocityX = 0;
//			int oldScrollX1 = getScrollX();
//			int dx = 0;
//			if (oldScrollX1 < 0) {
//				if (oldScrollX1 < -mRightEdgeLimit / 2
//						|| velocityX > SNAP_VELOCITY) {
//					dx = -mRightEdgeLimit - oldScrollX1;
//					// isDisplayMenu = true;
//					isScroll = true;
//				} else if (oldScrollX1 >= -mRightEdgeLimit / 2
//						|| velocityX < -SNAP_VELOCITY) {
//					dx = -oldScrollX1;
//					// isDisplayMenu = false;
//					isScroll = true;
//				}
//			} else {
//				if (oldScrollX1 > -mLeftEdgeLimit / 2
//						|| velocityX < -SNAP_VELOCITY) {
//					dx = -mLeftEdgeLimit - oldScrollX1;
//				} else if (oldScrollX1 <= -mLeftEdgeLimit / 2
//						|| velocityX > SNAP_VELOCITY) {
//					dx = -oldScrollX1;
//				}
//			}
			int oldScrollX1 = getScrollX();
			int dx = 0;
			if (oldScrollX1 < 0) {
				if (oldScrollX1 < -mRightEdgeLimit / 2) {
					dx = -mRightEdgeLimit - oldScrollX1;
					// isDisplayMenu = true;
					isScroll = true;
				} else if (oldScrollX1 >= -mRightEdgeLimit / 2) {
					dx = -oldScrollX1;
					// isDisplayMenu = false;
					isScroll = true;
				}
			} else {
				if (oldScrollX1 > -mLeftEdgeLimit / 2) {
					dx = -mLeftEdgeLimit - oldScrollX1;
				} else if (oldScrollX1 <= -mLeftEdgeLimit / 2) {
					dx = -oldScrollX1;
				}
			}
			smoothScrollTo(dx, 500);
			clearChildrenCache();
			break;
		}
		if (mVelocityTracker != null) {
			mVelocityTracker.recycle();
			mVelocityTracker = null;
		}
		super.onTouchEvent(ev);
		return true;
	}

	private void onScorllToEvnet(int eventPstCenter) {
		if(mOnShowEventListener != null)
			mOnShowEventListener.onShowEvent(this,eventPstCenter);
	}

	private void clearChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(false);
		}
	}

	private void enableChildrenCache() {
		final int count = getChildCount();
		for (int i = 0; i < count; i++) {
			final View layout = (View) getChildAt(i);
			layout.setDrawingCacheEnabled(true);
		}
	}

	public void freeze() {
		isFreeze = true;
	}

	public void unfreeze() {
		isFreeze = false;
	}
	public boolean isFreeze(){
		return isFreeze;
	}
	
	public int getPst(){
		return getScrollX();
	}
	
	public OnShowEventListener getmOnShowEventListener() {
		return mOnShowEventListener;
	}

	public void setmOnShowEventListener(OnShowEventListener mOnShowEventListener) {
		this.mOnShowEventListener = mOnShowEventListener;
	}

	public interface OnShowEventListener{
		public void onShowEvent(TouchScrollContainer touchScrollContainer, int type);
	}
	public void setDisableMoveToRight(boolean b){
		isDisableMoveToRight = b;
	}
}
