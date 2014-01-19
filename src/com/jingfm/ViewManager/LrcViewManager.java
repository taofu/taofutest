package com.jingfm.ViewManager;

import java.util.Arrays;
import java.util.HashMap;

import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.ScaleAnimation;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jingfm.MainActivity;
import com.jingfm.R;
import com.jingfm.Constants.KTC;
import com.jingfm.lyrics.LyricsInfo;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.JingTools;

public class LrcViewManager{
	protected static final int MSG_REFRESH_LYRICS = 0;
	protected static final int ANIMATING_TIME = 200;
	protected static final int LYRICS_COLOR_NORMAL = 0xffa6a6a6;
	protected static final int LYRICS_COLOR_HIGHLIGHT = 0xff497838;
	protected static final int LYRICS_SIZE_NORMAL = 14;
	protected static final int LYRICS_SIZE_HIGHLIGHT = 16;
	protected static final int LYRICS_MIN_HEIGHT = 28;
	
	private MainActivity mContext;
	private LyricsInfo mLyricsInfo;
	private LinearLayout mTextViewContainer;
	private ViewGroup mBaseView;
	private ScaleAnimation mPopInAnimation;
	private ScaleAnimation mPopOutAnimation;
	private Long[] mainArray;
	private HashMap<Long, String> textMap;
	private Handler mHander;
	private ScrollView mScrollView;
	protected Long currentHighlightTag;
	private int mLastViewHeight;
	private long timeOffset;
	private View mLrcViews;
	private boolean isAnimating;
	private TextView mLoadingText;
	protected boolean isScrolling;
	private TextView mNeedScrollHightView;
	private TextView mLastHighlightView;
	private ImageView mCoverView;
	private TextView mTitleText;
	private TextView mBy;
	
	public LrcViewManager(MainActivity context,ViewGroup baseView) {
		mContext = context;
		mBaseView = baseView;
		initHandler();
		initViews();
	}
	
	private void initHandler() {
		mHander = new Handler(){
			private long currentTime;
			private int mLastHeightIndex;

			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				switch (msg.what) {
				case MSG_REFRESH_LYRICS:
					if (mainArray == null || mainArray.length == 0 || mScrollView == null) {
						return;
					}
					removeMessages(MSG_REFRESH_LYRICS);
					int index = 0;
					currentTime = mContext.musicPst() - timeOffset;
					for (int count = 0; count < mainArray.length; count++) {
						int current = (mLastHeightIndex + count)%mainArray.length;
						int next = (mLastHeightIndex + count + 1)%mainArray.length;
						if (currentTime > mainArray[current]
								&& currentTime < mainArray[next]) {
							if (mainArray[next] - currentTime < 200) {
								index = next;
							}else{
								index = current;
							}
							break;
						}
					}
					setHighlight((TextView) mTextViewContainer.findViewWithTag(mainArray[index]),true);
					currentHighlightTag = mainArray[index];
					mLastHeightIndex = index;
					removeMessages(MSG_REFRESH_LYRICS);
					sendEmptyMessageDelayed(MSG_REFRESH_LYRICS, 200);
					break;
				}
			}
		};
		
	}

	private void initViews() {
		mTitleText = new TextView(mContext);
		mTitleText.setTypeface(Typeface.DEFAULT_BOLD);
		mTitleText.setShadowLayer(2, 0, 2, 0xFF000000);
		mTitleText.setTextColor(LYRICS_COLOR_NORMAL);
		mTitleText.setGravity(Gravity.CENTER);
		mTitleText.setTextSize(TypedValue.COMPLEX_UNIT_SP, LYRICS_SIZE_NORMAL + 2);
		mBy = new TextView(mContext);
		mBy.setTypeface(Typeface.DEFAULT, Typeface.ITALIC);
		mBy.setShadowLayer(1, 0, 1, 0xFF000000);
		mBy.setTextColor(LYRICS_COLOR_NORMAL);
		mBy.setGravity(Gravity.CENTER);
		mBy.setTextSize(TypedValue.COMPLEX_UNIT_SP, LYRICS_SIZE_NORMAL - 2);
		mLoadingText = new TextView(mContext);
		mLoadingText.setTypeface(Typeface.DEFAULT_BOLD);
		mLoadingText.setShadowLayer(2, 0, 2, 0xFF000000);
		mLoadingText.setTextColor(LYRICS_COLOR_NORMAL);
		mLoadingText.setGravity(Gravity.CENTER);
		mLoadingText.setTextSize(TypedValue.COMPLEX_UNIT_SP, LYRICS_SIZE_NORMAL);
		mLrcViews = LayoutInflater.from(mContext).inflate(R.layout.lyric_layout, null);
		mScrollView = (ScrollView) mLrcViews.findViewById(R.id.scroll_view);
		mCoverView = (ImageView) mLrcViews.findViewById(R.id.cover_view);
		mBaseView.setScrollBarStyle(0);
		mScrollView.setSmoothScrollingEnabled(true);
		mScrollView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
				case MotionEvent.ACTION_MOVE:
					isScrolling = true;
					break;
				case MotionEvent.ACTION_UP:
				case MotionEvent.ACTION_CANCEL:
				case MotionEvent.ACTION_OUTSIDE:
					if (isScrolling) {
						isScrolling = false;
						if (mNeedScrollHightView != null) {
							scrollToHightLight(mNeedScrollHightView);
						}
					}
					break;
				default:
					break;
				}
				return false;
			}
		});
		mTextViewContainer = (LinearLayout) mLrcViews.findViewById(R.id.text_view_container);
		mTextViewContainer.setFocusable(false);
		mTextViewContainer.setOrientation(LinearLayout.VERTICAL);
		mTextViewContainer.setPadding(20, 20, 20, 20);
		mBaseView.addView(mLrcViews);
	}

	public void setupAnimatePoint(Point point){
		if (mPopInAnimation != null) {
			return;
		}
		mPopInAnimation = new ScaleAnimation(0.0f, 1.0f, 0.0f, 1.0f,
				point.x,point.y);
		mPopInAnimation.setDuration(300);
		mPopOutAnimation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f,
				point.x,point.y);
		mPopOutAnimation.setDuration(300);
		mPopOutAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				if (animation == mPopOutAnimation) {
					mBaseView.setVisibility(View.GONE);
				}
			}
		});
	}
	
	public void setmLyricsInfo(LyricsInfo lyricsInfo) {
		if (mLyricsInfo == lyricsInfo) {
			return;
		}
		this.mLyricsInfo = lyricsInfo;
		if (mBaseView == null) {
			mHander.postDelayed(new Runnable() {
				
				@Override
				public void run() {
					setmLyricsInfo(mLyricsInfo);
				}
			}, 1000);
			return;
		}
		mLastViewHeight = 0;
		mTextViewContainer.removeAllViews();
		if (JingTools.isValidString(lyricsInfo.getTitle())) {
			mTitleText.setText(lyricsInfo.getTitle());
			mTextViewContainer.addView(mTitleText);
		}
		if (JingTools.isValidString(lyricsInfo.getBy())) {
			mBy.setText(lyricsInfo.getBy());
			mTextViewContainer.addView(mBy);
		}
		timeOffset = 0L;
		HashMap<Long, String> map = mLyricsInfo.getLineOfContentMS();
		timeOffset = mLyricsInfo.getOffset();
		final int TotalNumOfLines = map.keySet().size();
		Long[] timeLines = new Long[TotalNumOfLines];
		map.keySet().toArray(timeLines);
		Arrays.sort(timeLines);
		mainArray = timeLines;
		textMap = map;
		for (int i = 0; i < timeLines.length; i++) {
			TextView lineView = new TextView(mContext);
			lineView.setTypeface(Typeface.DEFAULT_BOLD);
			lineView.setShadowLayer(2, 0, 2, 0xFF000000);
			lineView.setMinHeight(JingTools.dip2px(mContext, LYRICS_MIN_HEIGHT));
			lineView.setTextSize(TypedValue.COMPLEX_UNIT_SP, LYRICS_SIZE_NORMAL);
			lineView.setTextColor(LYRICS_COLOR_NORMAL);
			lineView.setGravity(Gravity.CENTER);
			lineView.setTag(mainArray[i]);
			lineView.setText(textMap.get(mainArray[i]));
			mTextViewContainer.addView(lineView);
		}
		mHander.sendEmptyMessage(MSG_REFRESH_LYRICS);
		OnClickListener onClickListener = new  OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mContext.isCenterViewNeedBack()) {
					return;
				}
				switchLrcFullScreen();
			}
		};
		mLrcViews.setOnClickListener(onClickListener);
		mTextViewContainer.setOnClickListener(onClickListener);
	}
	
	public void setBg(Bitmap bitmap) {
		Bitmap tmpLyricsImage = AsyncImageLoader.blurImage(bitmap, bitmap.getWidth(), bitmap.getHeight(), 5);
		mCoverView.setImageBitmap(tmpLyricsImage);
	}
	
	public void clear(){
		mainArray = null;
		mTextViewContainer.setBackgroundColor(0);
	}
	public void hide(boolean isNeedAnimate){
		if (isNeedAnimate) {
			mBaseView.startAnimation(mPopOutAnimation);
		}else{
			mBaseView.setVisibility(View.GONE);
		}
	}
	
	public void showLyricView() {
		if (mLyricsInfo == null) {
			clear();
//			return;
		}
		KTC.rep("Home", "ClickLyricsBtn", "");
		mBaseView.setVisibility(View.VISIBLE);
		mBaseView.startAnimation(mPopInAnimation);
		mHander.sendEmptyMessageDelayed(MSG_REFRESH_LYRICS,500);
	}

	protected void switchLrcFullScreen() {
		if (isAnimating) {
			return;
		}
		isAnimating = true;
//		new Timer().schedule(new TimerTask() {
//			
//			@Override
//			public void run() {
//				isAnimating = false;
//			}
//		}, ANIMATING_TIME*2);
		mContext.startCacheAnimation(ANIMATING_TIME,new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				if (mContext.getmViewManagerCenter().getCenter_view_navigation_bar().getVisibility() == View.VISIBLE) {
					mContext.getmViewManagerCenter().getCenter_view_navigation_bar().setVisibility(View.GONE);
//					mContext.getmViewManagerCenter().getmPlayingViewManager().getCustomSlidingDrawer().setVisibility(View.GONE);
					FrameLayout.LayoutParams lp = (LayoutParams) mBaseView.getLayoutParams();
					lp.setMargins(0, 0, 0, 0);
					mContext.getmViewManagerCenter().getMainLayout().freeze();
					mContext.getmViewManagerCenter().getmPlayingViewManager().refreshLyricsBackButtonView();
				}else{
					mContext.getmViewManagerCenter().getCenter_view_navigation_bar().setVisibility(View.VISIBLE);
//					mContext.getmViewManagerCenter().getmPlayingViewManager().getCustomSlidingDrawer().setVisibility(View.VISIBLE);
					FrameLayout.LayoutParams lp = (LayoutParams) mBaseView.getLayoutParams();
//					lp.setMargins(0, 0, 0, JingTools.dip2px(mContext, 50));
					mContext.getmViewManagerCenter().getMainLayout().unfreeze();
					mContext.getmViewManagerCenter().getmPlayingViewManager().refreshLyricsBackButtonView();
				}
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				isAnimating = false;
			}
		});
	}

	private void setHighlight(TextView view, boolean b) {
		if (view == null || mLastHighlightView == view) {
			return;
		}
		if (b) {
			view.setTextColor(LYRICS_COLOR_HIGHLIGHT);
			view.setTextSize(TypedValue.COMPLEX_UNIT_SP, LYRICS_SIZE_HIGHLIGHT);
			scrollToHightLight(view);
			if (mLastHighlightView != null) {
				mLastHighlightView.setTextColor(LYRICS_COLOR_NORMAL);
				mLastHighlightView.setTextSize(TypedValue.COMPLEX_UNIT_SP, LYRICS_SIZE_NORMAL);
			}
			mLastHighlightView = view;
		}else{
			view.setTextColor(LYRICS_COLOR_NORMAL);
			view.setTextSize(TypedValue.COMPLEX_UNIT_SP, LYRICS_SIZE_NORMAL);
		}
	}

	private void scrollToHightLight(TextView view) {
		if (isScrolling) {
			mNeedScrollHightView = view;
		}else{
			mNeedScrollHightView = null;
			int offset = (int) (view.getTop()+(view.getHeight()) - mScrollView.getHeight()/2f);
			offset  = offset < 0 ? 0 : offset;
			mScrollView.smoothScrollTo(0,offset - mLastViewHeight);
			mScrollView.smoothScrollTo(0, offset);
			mLastViewHeight = view.getHeight();
		}
	}

	public void loading() {
		mHander.removeMessages(MSG_REFRESH_LYRICS);
		mTextViewContainer.removeAllViews();
		mLoadingText.setGravity(Gravity.CENTER);
		mLoadingText.setText("正在加载歌词...");
		mTextViewContainer.addView(mLoadingText);
	}
	public void noLrc() {
		mHander.removeMessages(MSG_REFRESH_LYRICS);
		mTextViewContainer.removeAllViews();
		TextView titleTextView = new TextView(mContext);
		titleTextView.setTextColor(0xFFA9A9A9);
		titleTextView.setTextSize(18f);
		titleTextView.setText("这首歌暂时没有歌词");
		mLoadingText.setGravity(Gravity.LEFT);
		mLoadingText.setText("\n我们会在最快的时间将有歌词的歌曲全部添加上\n感谢你对Jing的支持，我们会一如既往的坚持Jing的原则。通过重新发明搜索音乐的方式，让更多美妙的旋律\n到达大家的身边。");
		mTextViewContainer.addView(titleTextView);
		mTextViewContainer.addView(mLoadingText);
	}

	public boolean sendKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_MENU:
			return isAnimating || mContext.getmViewManagerCenter().getCenter_view_navigation_bar().getVisibility() != View.VISIBLE;
		case KeyEvent.KEYCODE_BACK:
			if (isAnimating) {
				return true;
			}
			if (mContext.getmViewManagerCenter().getCenter_view_navigation_bar().getVisibility() != View.VISIBLE) {
				switchLrcFullScreen();
				return true;
			}else{
				if (mBaseView.getVisibility() == View.VISIBLE) {
					hide(true);
					return true;
				}
			}
			break;
		}
		return false;
	}
	
}
