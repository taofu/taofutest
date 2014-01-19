package com.jingfm.customer_views;

import java.util.Random;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings.System;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.jingfm.background_model.PlayerManager.VisualizerListener;

public class VisualizerView extends View implements VisualizerListener{
	private int[] mBytes;
	private float[] mPoints;
//	private float[] mClearPoints;
	private Rect mRect = new Rect();

	private Paint mForePaint = new Paint();
	private Paint mCleanPaint = new Paint();
	private final int mSpectrumNum = 24 + 2;
	private int mViewWidth;
	private int mViewHeigh;
	private float baseX;
	protected Handler mUpdateHandler;
	private final int MSG_UPDATE = 121210;
	private final int MSG_UPDATE_FAKE = 121211;
	private byte[] fftNeedUpdate;
	private float valueMax;
	private boolean isNeedupdate;
	private boolean isUpdating;
	private float[] mLastValues = new float[mSpectrumNum - 2];
	private Random random = new Random(java.lang.System.currentTimeMillis());
	private boolean isRunning;

	// private boolean mFirst = true;

	public VisualizerView(Context context) {
		super(context);
		init(context);
	}
	public VisualizerView(Context context,AttributeSet attrs) {
		super(context,attrs);
		init(context);
	}

	private void init(Context context) {
//		mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
		mBytes = null;
		mForePaint.setAntiAlias(true);
		mCleanPaint.setXfermode(new PorterDuffXfermode(Mode.CLEAR));
//		25282a
//		mForePaint.setColor(0xFFFF0000);
		mForePaint.setColor(0x9925282a);
		new Thread(){
			public void run() {
				Looper.prepare();
		        mUpdateHandler = new Handler(){
						@Override
		        		public void handleMessage(Message msg) {
		        			super.handleMessage(msg);
						switch (msg.what) {
						case MSG_UPDATE:
//							int max = mAudioManager.getStreamMaxVolume( AudioManager.STREAM_MUSIC );
//						    int current = mAudioManager.getStreamVolume( AudioManager.STREAM_MUSIC );
//							scale = max*1f/current;
						    byte[] fft = fftNeedUpdate;
							int[] model = new int[mSpectrumNum];
							float[] values = new float[fft.length/2/model.length];
//							model[0] = (byte) Math.abs(fft[0]);
							int k = 2 ;
							for (int i = 1; i < model.length; i++) {
								for (int j = 0; j < values.length;j++) {
									values[j] = (byte) Math.hypot(fft[k], fft[k + 1]);
									k+=2;
								}
								model[i] = (int)average(values);
							}
							mBytes = model;
							postInvalidate();
							removeMessages(MSG_UPDATE);
							isUpdating = false;
							updateFft();
							break;
						case MSG_UPDATE_FAKE:
							postInvalidate();
							removeMessages(MSG_UPDATE_FAKE);
							sendEmptyMessageDelayed(MSG_UPDATE_FAKE, 300);
		        				break;
						}
		        		
		        		}

						private void updateFft() {
//							for (int i = 0; i < array.length; i++) {
//								fftNeedUpdate
//							}
						}
		        };
		        Looper.loop();
			};
		}.start();
	}
	
	@Override
	public void updateVisualizer(byte[] fft) {
		if (isUpdating) {
			return;
		}
		fftNeedUpdate = fft;
		mUpdateHandler.sendEmptyMessageDelayed(MSG_UPDATE,100);
	}
	private float average(float[] values) {
		if (values != null) {
			float total = 0;
			for (int i = 0; i < values.length; i++) {
				total += values[i];
			}
			return total/values.length;
		}
		return 0;
	}
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		if (mViewWidth == 0) {
			mViewWidth = View.MeasureSpec.getSize(widthMeasureSpec);
			mViewHeigh = View.MeasureSpec.getSize(heightMeasureSpec);
			baseX = mViewWidth / (mSpectrumNum - 2);
			baseX *= ((float)mSpectrumNum)/(((float)mSpectrumNum) - 1);
			float strokeWidth = baseX-5f;
			mForePaint.setStrokeWidth(strokeWidth);
		}
	}

//	@Override
//	protected void onDraw(Canvas canvas) {
//		super.onDraw(canvas);
//		if (mBytes == null) {
//			return;
//		}
//		if (mPoints == null || mPoints.length < mBytes.length * 4) {
//			mPoints = new float[mBytes.length * 4];
//		}
//		mRect.set(0, 0, mViewWidth, mViewHeigh);
//		// 绘制波形
//		// for (int i = 0; i < mBytes.length - 1; i++) {
//		// mPoints[i * 4] = mRect.width() * i / (mBytes.length - 1);
//		// mPoints[i * 4 + 1] = mRect.height() / 2
//		// + ((byte) (mBytes[i] + 128)) * (mRect.height() / 2) / 128;
//		// mPoints[i * 4 + 2] = mRect.width() * (i + 1) / (mBytes.length - 1);
//		// mPoints[i * 4 + 3] = mRect.height() / 2
//		// + ((byte) (mBytes[i + 1] + 128)) * (mRect.height() / 2) / 128;
//		// }
//
//		// 绘制频谱
//
//		for (int i = 0; i < mSpectrumNum - 2 ; i++) {
//			float value = mBytes[i+1] + 76;
//			if (value < 0) {
//				value *= -1;
//			}else{
//				value = Math.sqrt(value);
//			}
//			if (value > valueMax) {
//				valueMax = value;
//			}
//			final float xi = baseX * i + baseX / 2;
//			canvas.drawLines(value2points(xi,value,mViewHeigh), mForePaint);
//		}
//	}
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
//		if (mBytes == null) {
//			return;
//		}
//		if (mPoints == null || mPoints.length < mBytes.length * 4) {
//			mPoints = new float[mBytes.length * 4];
//		}
//		mRect.set(0, 0, mViewWidth, mViewHeigh);
			for (int i = 0; i < mLastValues.length ; i++) {
				final float xi = baseX * i + baseX / 2;
				if (isRunning) {
					float value = (random.nextFloat() + mLastValues[i])/2;
					mLastValues[i] = value;
					if (!isRunning) {
						value = 0;
					}
					canvas.drawLines(value2points(xi,value,mViewHeigh), mForePaint);
				}else{
					canvas.drawLines(value2points(xi,0,mViewHeigh), mForePaint);
				}
			}
//			canvas.drawPaint(mCleanPaint);
	}
	
//	private float[] value2points(float xi ,float value,float H) {
//		int key = (int) (value / 1.6f);
//		if(key>11){
//			key = 11;
//		}
//		float[] points = new float[key * 4];
//		for (int i = 0; i < points.length; i+=4) {
//			float startX = H*18f/172f*i/4;
//			points[i] = xi;
//			points[i+1] = H - startX;
//			points[i+2] = xi;
//			points[i+3] = H - startX - (H*10f/172f);
//		}
//		return points;
//	}
	private float[] value2points(float xi ,float value,float H) {
		int key = (int) (11 * value);
//		if(key>11){
//			key = 11;
//		}
		float[] points = new float[key * 4];
		for (int i = 0; i < points.length; i+=4) {
			float startX = H*18f/172f*i/4;
			points[i] = xi;
			points[i+1] = H - startX;
			points[i+2] = xi;
			points[i+3] = H - startX - (H*10f/172f);
		}
		return points;
	}
	
	public void start() {
		isRunning = true;
		mUpdateHandler.sendEmptyMessageDelayed(MSG_UPDATE_FAKE,300);
	}
	
	public void stop() {
		isRunning = false;
		mUpdateHandler.removeMessages(MSG_UPDATE_FAKE);
		invalidate();
	}
}