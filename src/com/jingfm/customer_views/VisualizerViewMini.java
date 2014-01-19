package com.jingfm.customer_views;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.View;

import com.jingfm.R;

public class VisualizerViewMini extends View {

	private Handler mHandler;

	private Runnable mRunnableRefresh;

	private boolean isStarting;

	private float[] data;
	private boolean[] dataIncrease;

	private Random mRandom;

	private Paint paint;

	private float mUnitWidth;

	private RectF mRectR;

	private float mStrokeWidth;

	private Paint mBgPaint;

	private RectF mRectWholeView;

	private RectF mRectRightPartOfView;

	public VisualizerViewMini(Context context) {
		super(context);
		initView(context);
	}

	public VisualizerViewMini(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public VisualizerViewMini(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	private void initView(Context context) {
		if (paint == null) {
			paint = new Paint();
			paint.setAntiAlias(true);
			paint.setColor(0xFFFFFFFF);
		}
		setBackgroundDrawable(context.getResources().getDrawable(R.drawable.visualizer_mini_bg));
		if (mBgPaint == null) {
			mBgPaint = new Paint();
			mBgPaint.setAntiAlias(true);
			mBgPaint.setColor(getContext().getResources().getColor(R.color.jing_green));
			mBgPaint.setAlpha(0x88);
		}
		mRectR=new RectF();                           //RectF对象  
		mRectWholeView=new RectF();                           //RectF对象  
		mRectRightPartOfView=new RectF();                           //RectF对象  
		mRandom = new Random(System.currentTimeMillis());
		resizeLines();
	}

	public void startScroll() {
		isStarting = true;
		invalidate();
	}

	public void pauseScroll() {
		isStarting = false;
		invalidate();
	}
	public void stopScroll() {
		pauseScroll();
		for (int i = 0; i < data.length; i++) {
			data[i] = 0;
		};
		invalidate();
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		resizeLines();
	}
	
	private void resizeLines() {
		if (data == null) {
			data = new float[3];
			dataIncrease = new boolean[data.length];
			for (int i = 0; i < data.length; i++) {
				data[i] = mRandom.nextInt(100);
				dataIncrease[i] = mRandom.nextBoolean();
			}
		}
		mUnitWidth = ((float)getWidth()/2)/data.length;
		mRectWholeView.right =  getWidth();
		mRectWholeView.bottom =  getWidth();
		mRectRightPartOfView.top = mRectWholeView.top; 
		mRectRightPartOfView.bottom = mRectWholeView.bottom; 
		mRectRightPartOfView.right = mRectWholeView.right; 
		mRectRightPartOfView.left = mRectWholeView.right /2; 
		mStrokeWidth = mUnitWidth * 0.4f;
	}

	public void setLineCount(int len){
		if (data.length != len) {
			data = new float[len];
		}
		resizeLines();
	}
	
	@Override
	public void onDraw(Canvas canvas) {
		if (mUnitWidth == 0) {
			resizeLines();
			invalidate();
			return;
		}
//		canvas.drawRoundRect(mRectWholeView, 10, 10, mBgPaint);
//		canvas.drawRect(mRectRightPartOfView, mBgPaint);
		for (int i = 0; i < data.length; i++) {
			float sX = mUnitWidth*(i + 0.5f);
			float sY = (100-data[i])*(getHeight()/2)/100;
			mRectR.left = sX + getWidth()/4;
			mRectR.top = sY + getHeight()/4;
			mRectR.right = sX + mStrokeWidth + +getWidth()/4;
			mRectR.bottom = getHeight()* 3 /4;
//			canvas.drawLine(sX, getHeight(), sX, sY, paint);
			canvas.drawRoundRect(mRectR, mStrokeWidth/3, mStrokeWidth/3, paint);
		}
		isStarting = true;
		refresh();
	}

	private void refresh() {
		if (mHandler == null) {
			mHandler = new Handler();
			if (mRunnableRefresh == null) {
				mRunnableRefresh = new Runnable() {
					@Override
					public void run() {
						if (isStarting) {
							for (int i = 0; i < data.length; i++) {
								if (dataIncrease[i]) {
									if (mRandom.nextBoolean()) {
										data[i]+=3;
									}else{
										data[i]+=2;
									}
								}else{
									if (mRandom.nextBoolean()) {
										data[i]-=3;
									}else{
										data[i]-=2;
									}
								}
								if (data[i] > 100) {
									data[i] = 99;
									dataIncrease[i] = false;
								}
								if (data[i] < 20) {
									data[i] = 21;
									dataIncrease[i] = true;
								}
							}
							invalidate();
							mHandler.removeCallbacks(mRunnableRefresh);
							mHandler.postDelayed(mRunnableRefresh,30);
						}
					}
				};
			}
			mRunnableRefresh.run();
		}
	}

}