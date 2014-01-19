package com.jingfm.customer_views;

import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

import com.jingfm.MainActivity.CallBacker;
import com.jingfm.R;

public class JingClearCacheView extends View{

	protected static final int MSG_START_CLEAR_VIEW = 0;

	protected static final int MSG_REFESHING_VIEW = 1;

	protected static final int MSG_STOP_REFRESH_VIEW = 2;

	private Paint paint;

	private Bitmap mBitDestTop;
	// 百分比进度
	private int mProgress;

	private RectF mRectF;

	private int mViewWidth;

	private int mViewHeigh;

	private float mStrokeWidth;

	private Handler mHandler;

	private Context context;

	private Bitmap backgroundImage;

	private Bitmap foregroundImage;

	private float mDigreeProgress;

	private Paint mProgressPaint;

	private CallBacker mCallBacker;

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	public JingClearCacheView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public JingClearCacheView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public JingClearCacheView(Context context) {
		super(context);
		initView(context);
	}

	private void initView(Context context) {
		this.context = context;
		initHandler();
		paint = new Paint();
		// paint.setAlpha(0x80);
		paint.setStyle(Style.FILL);
		mProgressPaint = new Paint();
		mProgressPaint.setStyle(Paint.Style.STROKE);
		mProgressPaint.setColor(Color.rgb(98, 169, 70));
		// mProgressPaint.setColor(Color.TRANSPARENT);
		mProgressPaint.setAntiAlias(true);
		this.mProgress = 0;
	}
	
	private void initHandler() {
		mHandler = new Handler(){
			private boolean isClockWise = true;

			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				switch (msg.what) {
				case MSG_START_CLEAR_VIEW:
					mDigreeProgress = 0;
					sendEmptyMessageDelayed(MSG_REFESHING_VIEW, 15);
					break;
				case MSG_REFESHING_VIEW:
					removeMessages(MSG_REFESHING_VIEW);
					if (isClockWise) {
						mDigreeProgress += 5;
						if (mDigreeProgress > 270) {
							isClockWise = false;
							mDigreeProgress -= 5;
						}
					}else{
						mDigreeProgress -= 5;
						if (mDigreeProgress < 0) {
							isClockWise = true;
							mDigreeProgress += 5;
						}
					}
					invalidate();
					if (mDigreeProgress > 0) {
						sendEmptyMessageDelayed(MSG_REFESHING_VIEW, 15);
					}else{
						sendEmptyMessage(MSG_STOP_REFRESH_VIEW);
					}
					break;
				case MSG_STOP_REFRESH_VIEW:
					mDigreeProgress = 0;
					isClockWise = true;
					removeMessages(MSG_REFESHING_VIEW);
					mCallBacker.onCallBack();
					break;
				}
			}
		};
	}

	@Override
	protected void onDraw(Canvas canvas) {
		canvas.drawBitmap(backgroundImage, 0, 0, paint);
		canvas.drawArc(mRectF, mDigreeProgress, 270 - mDigreeProgress, false, mProgressPaint);
		canvas.drawBitmap(foregroundImage, 0, 0, paint);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mViewWidth = View.MeasureSpec.getSize(widthMeasureSpec);
		mViewHeigh = View.MeasureSpec.getSize(heightMeasureSpec);
		if (mViewWidth > 0) {
			if (backgroundImage == null) {
				InputStream is = context.getResources().openRawResource(
						R.drawable.clear_cache_anim_background);
				backgroundImage = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is), mViewWidth, mViewHeigh, true);
				InputStream is1 = context.getResources().openRawResource(
						R.drawable.clear_cache_anim_foreground);
				foregroundImage = Bitmap.createScaledBitmap(BitmapFactory.decodeStream(is1), mViewWidth, mViewHeigh, true);
				try {
					is.close();
					is1.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				mStrokeWidth = mViewWidth * 0.095f;
				mProgressPaint.setStrokeWidth(mStrokeWidth);
				mRectF = new RectF(0 + (int) (mStrokeWidth), 0
				+ (int) (mStrokeWidth), mViewWidth
				- (int) (mStrokeWidth), mViewHeigh
				- (int) (mStrokeWidth));
			}
		}
		
	}
	
	public void startAmin(CallBacker callBacker){
		mCallBacker = callBacker;
		mHandler.sendEmptyMessage(MSG_START_CLEAR_VIEW);
	}
}
