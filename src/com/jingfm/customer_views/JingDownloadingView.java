package com.jingfm.customer_views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import com.jingfm.R;

public class JingDownloadingView extends ProgressBar{

	private Paint paint;

	private Bitmap mBitDownloadBackground;
	private Bitmap mBitDownloadForeground;
	private Bitmap mBiNoDownload;
	private Bitmap mBiDownloadDelete;
	// 百分比进度
	private int mProgress;

	private Rect mRect;

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	public JingDownloadingView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initView(context);
	}

	public JingDownloadingView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initView(context);
	}

	public JingDownloadingView(Context context) {
		super(context);
		initView(context);
	}
	
	public int getmProgress() {
		return mProgress;
	}

	private void initView(Context context) {
		paint = new Paint();
		// paint.setAlpha(0x80);
		paint.setStyle(Style.FILL);
		mRect = new Rect();
		mBitDownloadBackground = ((BitmapDrawable) this.getResources().getDrawable(
				R.drawable.download)).getBitmap();
		mBitDownloadForeground = ((BitmapDrawable) this.getResources().getDrawable(
				R.drawable.downloading)).getBitmap();
//		mBiNoDownload = ((BitmapDrawable) this.getResources().getDrawable(
//				R.drawable.download_no)).getBitmap();
		mBiNoDownload = ((BitmapDrawable) this.getResources().getDrawable(
				R.drawable.download)).getBitmap();
		mBiDownloadDelete = ((BitmapDrawable) this.getResources().getDrawable(
				R.drawable.download_delete)).getBitmap();
		this.mProgress = -1;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		if (mProgress >= 100 ) {
			canvas.drawBitmap(mBiDownloadDelete, 0, 0, paint);
		}else if (mProgress < 0 ){
			canvas.drawBitmap(mBiNoDownload, 0, 0, paint);
		}else{
			//由于进度条只有中间的50%，所以要增加顶和底
			int by = (mBitDownloadBackground.getHeight() * mProgress / 100)/2;
			canvas.drawBitmap(mBitDownloadBackground, 0, 0, paint);
			mRect.left = 0;
			mRect.top = mBitDownloadBackground.getHeight() - by - mBitDownloadBackground.getHeight()/4;
			mRect.right = mBitDownloadBackground.getWidth();
			mRect.bottom = mBitDownloadBackground.getHeight();
			canvas.drawBitmap(mBitDownloadForeground, mRect, mRect, paint);
		}
	}
	
	@Override
    public synchronized void setProgress(int progress) {
		if (mProgress != progress) {
			mProgress = progress;
			this.postInvalidate();
		}else{
			mProgress = progress;
		}
    }

	@Override
	public synchronized int getProgress() {
		return mProgress;
	}
}
