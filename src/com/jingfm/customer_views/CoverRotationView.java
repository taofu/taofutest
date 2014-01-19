package com.jingfm.customer_views;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem.ProgressListener;
import android.util.AttributeSet;
import android.view.View;

import com.jingfm.R;
import com.jingfm.tools.AsyncImageLoader;

public class CoverRotationView extends View implements ProgressListener {

	protected static final int MSG_AUTO_REFRESH_VIEW = 0;
	private static final float WIDTH_RATE_OF_MASK = 3f;
	private Bitmap mBitmap;
	private Options options;
	private Paint paint;
	public int mViewWidth;
	public int mViewHeigh;
	private int mProgress = 0;
	private double mRotationSpeed = 0.1; // 圈每秒
	private double fps = 30;
	private float paddingScale = 0.96f;

	private Paint mProgressPaint;
	private boolean isRotateEnable;
	private Handler mHandler;
	private Bitmap unScaledBitmap;
	private float mStrokeWidth = 10f;
	private RectF mRectF;
	private float mRotateDegree;
	private float mDigreeProgress;
	private long mLastEndingTime;
	private Bitmap mCoverMask;
	private Context mContext;
	private float mStepLength;
	private float mCoverScaleRate = 0.1f;
	private Bitmap mDefaultBitmap;
	private Matrix mMatrix;
	private boolean isFreeze;
	private RectF mRectMask;
	private Paint mMaskPaint;
	private Bitmap mRingMask;
	private Paint mRingPaint;

	public CoverRotationView(Context context) {
		super(context);
		init(context);
	}

	public CoverRotationView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init(context);
	}

	private void init(Context context) {
		this.mContext = context;
		options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inSampleSize = 1;
		initHandler();
		initView();
		initRotateMathData();
		mMatrix = new Matrix();
	}

	private void initRotateMathData() {
		mStepLength = 0.48f;
		mDigreeProgress = mProgress / 100f * 360f;
	}

	private void initHandler() {
		mHandler = new Handler() {

			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_AUTO_REFRESH_VIEW:
						if (isFreeze) {
							return;
						}
						mHandler.removeMessages(MSG_AUTO_REFRESH_VIEW);
						invalidate();
						if (isRotateEnable) {
							mRotateDegree += mStepLength;
						}
						mHandler.sendEmptyMessageDelayed(MSG_AUTO_REFRESH_VIEW,
								30);
					break;
				}
			}
		};
	}

	public void setProgress(int progress) {
		if (progress > 100) {
			this.mProgress = 100;
		} else if (progress < 0) {
			this.mProgress = 0;
		} else {
			this.mProgress = progress;
		}
		mDigreeProgress = mProgress / 100f * (360f);
	}

	private void initView() {
		paint = new Paint();
		paint.setAntiAlias(true);
		paint.setFilterBitmap(true);
		mRingPaint = new Paint();
		mRingPaint.setAntiAlias(true);
		mRingPaint.setFilterBitmap(true);
		mProgressPaint = new Paint();
		mProgressPaint.setStyle(Paint.Style.STROKE);
		mProgressPaint.setStrokeWidth(mStrokeWidth);
		mProgressPaint.setColor(Color.rgb(45, 203, 115));
		mProgressPaint.setAntiAlias(true);
		mMaskPaint = new Paint();
		mMaskPaint.setStyle(Paint.Style.STROKE);
		mMaskPaint.setStrokeWidth(mStrokeWidth*WIDTH_RATE_OF_MASK);
		mMaskPaint.setColor(Color.rgb(35, 35, 35));
		mMaskPaint.setAntiAlias(true);
	}

	public void setProgressPaintStrokeWidth(float width) {
		mStrokeWidth = width;
		mProgressPaint.setStrokeWidth(mStrokeWidth);
		mMaskPaint.setStrokeWidth(mStrokeWidth*WIDTH_RATE_OF_MASK);
		float padding = mStrokeWidth*2/3;
		mRectF = new RectF(padding, padding,mViewWidth-padding,mViewHeigh-padding);
		padding = (mStrokeWidth-padding*2)+mMaskPaint.getStrokeWidth();
		mRectMask = new RectF(padding, padding,mViewWidth-padding,mViewHeigh-padding);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mCoverMask == null) {
			try {
				setImage(unScaledBitmap);
			} catch (OutOfMemoryError e) {
				try {
					setImage(null);
				} catch (OutOfMemoryError e1) {
				}
			}
			unScaledBitmap = null;
			mViewWidth = View.MeasureSpec.getSize(widthMeasureSpec);
			mViewHeigh = View.MeasureSpec.getSize(heightMeasureSpec);
			mStrokeWidth = mViewWidth * 0.015f;
			setProgressPaintStrokeWidth(mStrokeWidth);
			mHandler.sendEmptyMessage(MSG_AUTO_REFRESH_VIEW);
		}
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		doDraw(canvas);
	}

	protected void doDraw(Canvas canvas) {
		if (mBitmap == null) {
			return;
		}
		mMatrix.setRotate(mRotateDegree, mViewWidth / 2f, mViewHeigh / 2f);
		canvas.drawBitmap(mBitmap, mMatrix, paint);
//		canvas.drawArc(mRectMask, 0, 360, false, mMaskPaint);
		canvas.drawBitmap(mRingMask,(mViewWidth * (1 - paddingScale)/2f),(mViewWidth * (1 - paddingScale)/2f),mRingPaint);
		canvas.drawArc(mRectF, 0-90, mDigreeProgress, false, mProgressPaint);
	}

	public synchronized void setImage(Bitmap bitmap) {
		if (mViewWidth != 0) {
			if (mCoverMask == null) {
//				InputStream is = getResources().openRawResource(R.drawable.cv_mask);
//				Bitmap res = BitmapFactory.decodeStream(is, null, options);
//				mCoverMask = Bitmap.createScaledBitmap(res, mViewWidth,
//						mViewWidth, true);
//				try {
//					is.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//				res.recycle();
				mCoverMask = Bitmap.createBitmap(mViewWidth, mViewWidth, Bitmap.Config.ALPHA_8);
			}
			if (mDefaultBitmap == null) {
				InputStream is = mContext.getResources().openRawResource(
						R.drawable.player_defaultcover);
				Bitmap tmpBitmap = BitmapFactory.decodeStream(is,null,options);
				int width = mViewWidth - (int) (mViewWidth * mCoverScaleRate);
				Bitmap tmpBitmap2 = Bitmap.createScaledBitmap(tmpBitmap, width,
						width, true);
				if (tmpBitmap2 != tmpBitmap) {
					tmpBitmap.recycle();
				}
				Bitmap tmpBitmap3 = AsyncImageLoader.toRound(tmpBitmap2);
				if (tmpBitmap3 != tmpBitmap2) {
					tmpBitmap2.recycle();
				}
				mDefaultBitmap = AsyncImageLoader.mergeBitmap(tmpBitmap3,
						mCoverMask);
				if (tmpBitmap3 != null) {
					tmpBitmap3.recycle();
				}
			}
			if (mRingMask == null) {
				try {
					InputStream is = mContext.getResources().openRawResource(
							R.drawable.cover_ring);
					mRingMask = BitmapFactory.decodeStream(is,null,options);
					int width = (int) (mViewWidth * paddingScale);
					mRingMask = Bitmap.createScaledBitmap(mRingMask, width, width, true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			if (bitmap != null) {
				try {
					int width = mViewWidth - (int) (mViewWidth * mCoverScaleRate);
					Bitmap cover = Bitmap.createScaledBitmap(bitmap, width, width,
							true);
					Bitmap roundBitmap = AsyncImageLoader.toRound(cover);
					if (roundBitmap != cover) {
						cover.recycle();
					}
					mBitmap = AsyncImageLoader.mergeBitmap(roundBitmap, mCoverMask);
					roundBitmap.recycle();
				} catch (OutOfMemoryError e) {
					mBitmap = mDefaultBitmap;
				}
			} else {
				mBitmap = mDefaultBitmap;
			}
		} else {
			unScaledBitmap = bitmap;
		}
		invalidate();
	}

	public void startRotate() {
		isRotateEnable = true;
		invalidate();
	}
	public boolean isRotateRunning() {
		return isRotateEnable;
	}

	public void pauseRotate() {
		isRotateEnable = false;
	}

	public void resetRotate() {
		mRotateDegree = 0;
		setProgress(0);
		invalidate();
	}

	@Override
	public void onProgress(int progress) {
		setProgress(progress);
	}

	public void freeze() {
		isFreeze = true;
		mHandler.removeMessages(MSG_AUTO_REFRESH_VIEW);
	}

	public void unfreeze() {
		isFreeze = false;
		mHandler.sendEmptyMessage(MSG_AUTO_REFRESH_VIEW);
	}
	
}
