package com.jingfm.ViewManager;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;

import com.jingfm.MainActivity;
import com.jingfm.R;

public class GuideManger implements OnClickListener {
	
	private MainActivity mContext;
	private Handler mHandler;
	private View[] mGuideArray;
	private View mBaseView;
	private AlphaAnimation mAlphaAnimationIn;
	private AlphaAnimation mAlphaAnimationOut;
	private boolean viewLock;

	public GuideManger(MainActivity context) {
		this.mContext = context;
		initHandler();
		initView();
	}

	private void initHandler() {
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		};
	}

	private void initView(){
		mBaseView = LayoutInflater.from(mContext).inflate(R.layout.guide_layout, mContext.getAnimateContainer());
		mGuideArray = new View[]{
			mBaseView.findViewById(R.id.guide_layout_03),
			mBaseView.findViewById(R.id.guide_layout_02),
			mBaseView.findViewById(R.id.guide_layout_01)
		};
		for (int i = 0; i < mGuideArray.length; i++) {
			mGuideArray[i].setOnClickListener(this);
		}
		mAlphaAnimationIn = new AlphaAnimation(0f,1f);
		mAlphaAnimationOut = new AlphaAnimation(1f,0f);
		mAlphaAnimationIn.setDuration(500);
		mAlphaAnimationOut.setDuration(500);
	}

	public View getView() {
		return mBaseView;
	}

	public void show() {
		if (mContext.getAnimateContainer().getVisibility() == View.VISIBLE) {
			return;
		}
		mContext.getAnimateContainer().setVisibility(View.VISIBLE);
		mGuideArray[0].setVisibility(View.VISIBLE);
	}

	@Override
	public void onClick(View v) {
		if (viewLock) {
			return;
		}
		viewLock = true;
		for (int i = 0; i < mGuideArray.length; i++) {
			if (v.equals(mGuideArray[i])) {
				final int index = i;
				mAlphaAnimationOut.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {
						if (index + 1 != mGuideArray.length) {
							mGuideArray[index + 1].setVisibility(View.VISIBLE);
							mGuideArray[index + 1].startAnimation(mAlphaAnimationIn);
						}
					}
					@Override
					public void onAnimationRepeat(Animation animation) {
					}
					@Override
					public void onAnimationEnd(Animation animation) {
						mGuideArray[index].setVisibility(View.GONE);
						if (index + 1 == mGuideArray.length) {
							mContext.getAnimateContainer().setVisibility(View.GONE);
						}
						viewLock = false;
					}
				});
				mGuideArray[i].startAnimation(mAlphaAnimationOut);
				break;
			}
		}
	}
}
