package com.jingfm.ViewManager;

import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.jingfm.MainActivity;

public class AnimateManager {
	private MainActivity mContext;
	private Handler mHandler;
	private ViewGroup mAnimateContainer;

	public AnimateManager(MainActivity context,ViewGroup animateContainer) {
		this.mContext = context;
		this.mAnimateContainer = animateContainer;
		initHandler();
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				default:
					break;
				}
			}
		};
	}
	
	public void doBadgeTransfer(Drawable drawable,int w, int h, Point pointStart,Point pointEnd,final AnimateCallback callback){
		if (mAnimateContainer.getVisibility() == View.VISIBLE) {
			return;
		}
		if (drawable == null) {
			callback.onCallback();
			return;
		}
		mAnimateContainer.setVisibility(View.VISIBLE);
		final ImageView imageView = new ImageView(mContext);
		imageView.setImageDrawable(drawable);
		imageView.setLayoutParams(new FrameLayout.LayoutParams(w,h));
		mAnimateContainer.addView(imageView);
		TranslateAnimation animation = new TranslateAnimation(
//				Animation.RELATIVE_TO_SELF,pointStart.x, 
//				Animation.RELATIVE_TO_SELF,pointEnd.x,
//				Animation.RELATIVE_TO_SELF,pointStart.y,
//				Animation.RELATIVE_TO_SELF,pointEnd.y);
				pointStart.x, 
				pointEnd.x,
				pointStart.y,
				pointEnd.y);
		animation.setDuration(600);
		animation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				imageView.setVisibility(View.GONE);
				imageView.clearAnimation();
				mAnimateContainer.removeAllViews();
				mAnimateContainer.setVisibility(View.GONE);
				callback.onCallback();
			}
		});
		imageView.startAnimation(animation);
	}
	
	public interface AnimateCallback{
		public void onCallback();
	}
}
