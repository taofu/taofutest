package com.jingfm.ViewManager;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.MainActivity.CallBacker;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.Constants.KTC;
import com.jingfm.adapter.LeftViewAdapter;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.model.LoginDataDTO;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.MusicInfoDTO;
import com.jingfm.background_model.LocalCacheManager;
import com.jingfm.background_model.LocalCacheManager.LrcCallBacker;
import com.jingfm.background_model.PlayerManager;
import com.jingfm.background_model.PlayerManager.LrcGetListener;
import com.jingfm.background_model.PlayerManager.PlayerStateChangeListener;
import com.jingfm.background_model.PlayerManager.ShowTipsListener;
import com.jingfm.background_model.PlayerManager.StartNewListener;
import com.jingfm.background_model.SettingManager;
import com.jingfm.customer_views.CoverRotationView;
import com.jingfm.customer_views.JingPagerAdapter;
import com.jingfm.customer_views.JingViewPager;
import com.jingfm.customer_views.TouchScrollContainer;
import com.jingfm.customer_views.VisualizerViewMini;
import com.jingfm.lyrics.LyricsInfo;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.AsyncImageLoader.ImageCallback;
import com.jingfm.tools.JingTools;

public class PlayingViewManager implements LoginStateChangeListener,
		PlayerStateChangeListener,ShowTipsListener,LrcGetListener,
		StartNewListener, LrcCallBacker{
	protected static final int MSG_REFRESH_VIEW = 0;
	protected static final int MSG_REFRESH_COVER = 1;
	protected static final int MSG_RESET_VIEW = 2;
	protected static final int MSG_TOAST_PREPARING = 3;
	protected static final int MSG_TOAST_TIPS = 5;
	protected static final int MSG_CLEAR_ANIMATE_LOCK = 6;

	private static final float BUTTONS_GROUP_MARGIN_RATE = 11f/100;
	private static final int BUTTONS_GROUP_HEIGHT = 30;
	
	private static final float MIN_ROTATE_ANGLE = -25f;
	private static final float MAX_ROTATE_ANGLE = 0f;
//	private static final float STYLUS_RELAYTIVE_SELF_X = 0.6f;
//	private static final float STYLUS_RELAYTIVE_SELF_Y = 0.25f;
	private static final float STYLUS_RELAYTIVE_SELF_X = 0.746f;
	private static final float STYLUS_RELAYTIVE_SELF_Y = 0.196f;
	private static final float Stylus_View_Rate = 2.876f;
	
	private MainActivity mContext;
	private View mBaseView;
	private View mPlayView;
	private TextView center_tilte_text;
	private Handler mHandler;
	private String mArtistName;
	private String mMusicName;
	private TextView music_name;
	private TextView artist_name;
	private CoverRotationView mCoverRotationView;
	private MusicDTO mCurrentMusicDTO;
//	private View cover_bg;
	private boolean isAlreadyAddProgressListener;
	private ImageView mStylusView;
	private RotateAnimation mStylusViewOffAnimation;
	private RotateAnimation mStylusViewOnAnimation;
	private boolean isRotateEnable = true;
	private boolean isNeedSwitch;
	private MusicInfoDTO mMusicInfoDTO;
	private View mFollowerView;
	private Animation mFollowerViewAnimIn;
	private Animation mFollowerViewAnimOut;
	private View follower_need_go_away;
	private ImageButton playing_buttom_button_download;
	private ImageButton playing_buttom_button_loop;
	private ImageButton controller_button_fav;
	private ImageButton show_follower_list;
	private ImageButton show_lyric;
	protected boolean mIsLooping;
	private boolean isAnimating;

	private SharedPreferences mSharedPreferences;
	protected boolean isNeedResume;
	private boolean isCurrentShowLoved;
	private ViewGroup cd_layout;
	protected MusicDTO mLastHateMusicInFollow;
	private LrcViewManager mLrcViewManager;
	private ViewGroup mLrcViews;
	private boolean isNeedShowLyric;
	private JingViewPager mJingViewPager;
	private OnClickListener mLrcHideListener;
	private OnClickListener mLrcSwitchFullScreenListener;
	private GridView follower_grid_view;
	private int mLastVolumeProgress;
	private SeekBar volume_seek_bar;
	private TouchScrollContainer mainLayout;
	private VisualizerViewMini mVisualizerViewMini;
	private ImageView cover_loading;
	private RotateAnimation mCoverLoadingAnimation;
	private ImageView mCoverForOther;
	private TextView mMainTextForOther;
	private TextView mSubTextForOther;
	private View playing_info_button;
	
	private static final double  when_p_1_5_scale = 0.68;

	public PlayingViewManager(MainActivity context, View baseView,
			TouchScrollContainer mainLayout) {
		this.mContext = context;
		this.mBaseView = baseView;
		this.mainLayout = mainLayout;
		initHandler();
		initPlayView();
		initCoverView();
		initVoice();
		resizeMusicInfoText();
		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				switchPlayingState();
			}
		};
		mCoverRotationView.setOnClickListener(onClickListener);
		mStylusView.setOnTouchListener(new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switchPlayingState();
				return true;
			}
		});
		mContext.addLoginListener(this);
	}

	private void initVoice() {
		mSharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(),
				Context.MODE_PRIVATE);
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean("com.iflytek.isr.showhelp",false);
		editor.commit();
	}
	
	private void initCoverView() {
		mCoverRotationView = new CoverRotationView(mContext);
		int with = getCoverWithByScreen();
		if (with == 408) {
			with =  with * 9/10;
		}
		FrameLayout.LayoutParams viewLayoutParams = new FrameLayout.LayoutParams(
				with, with);
		viewLayoutParams.gravity = Gravity.CENTER;
		mHandler.sendEmptyMessage(MSG_RESET_VIEW);
		int stylusViewWidth = with*25/100;
		FrameLayout.LayoutParams viewLayoutParams1 = new FrameLayout.LayoutParams(
				stylusViewWidth, (int)(stylusViewWidth*Stylus_View_Rate));
		viewLayoutParams1.gravity = Gravity.CENTER;
		double hightModify = 0;
		if (JingTools.getDeviceAspectRatio(mContext) == 1.5) {
			viewLayoutParams1.setMargins(with*36/100, 0, 0, with*18/100);
			View music_info_layout = mPlayView
					.findViewById(R.id.music_info_layout);
			FrameLayout.LayoutParams viewLayoutParams_music_info_layout = (LayoutParams) music_info_layout.getLayoutParams();
			hightModify = viewLayoutParams_music_info_layout.height * (1-when_p_1_5_scale);
			viewLayoutParams_music_info_layout.height -= hightModify;
		}else{
			viewLayoutParams1.setMargins(with*34/100, 0, 0, with*20/100);
		}
		
		cd_layout = (ViewGroup) mPlayView
				.findViewById(R.id.cd_layout);
		cover_loading = (ImageView) mPlayView
				.findViewById(R.id.cover_loading);
		cd_layout.addView(mCoverRotationView);
		mCoverRotationView.setLayoutParams(viewLayoutParams);
		View cover_handle = mPlayView
				.findViewById(R.id.cover_handle);
		FrameLayout.LayoutParams viewLayoutParams2 = new FrameLayout.LayoutParams(
				(int)(viewLayoutParams.width*0.25),
				(int)(viewLayoutParams.width*0.25));
		viewLayoutParams2.gravity = Gravity.CENTER;
		viewLayoutParams2.setMargins(
				viewLayoutParams.width*41/100,
				0,
				0,
				viewLayoutParams.width*41/100);
		if (hightModify != 0) {
			View viewP = ((View)cd_layout.getParent());
			FrameLayout.LayoutParams params = (LayoutParams) viewP.getLayoutParams();
			params.setMargins(0, 0, 0, (int) (JingTools.dip2px(mContext, 160) - hightModify));
			viewP.setLayoutParams(params);
			
		}
		mStylusView.setLayoutParams(viewLayoutParams1);
		cover_handle.setLayoutParams(viewLayoutParams2);
		FrameLayout.LayoutParams viewLayoutParamsCover_loading = new FrameLayout.LayoutParams(
				(int)(viewLayoutParams.width*0.25),
				(int)(viewLayoutParams.width*0.25));
		viewLayoutParamsCover_loading.gravity = Gravity.CENTER;
		cover_loading.setLayoutParams(viewLayoutParamsCover_loading);
		cover_loading.bringToFront();
		mCoverLoadingAnimation = new RotateAnimation(0f,360f,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,0.5f);
		mCoverLoadingAnimation.setDuration(1000);
		mCoverLoadingAnimation.setRepeatCount(RotateAnimation.INFINITE);
		mCoverLoadingAnimation.setInterpolator(new LinearInterpolator());
		mCoverLoadingAnimation.setFillAfter(false);
		mCoverLoadingAnimation.setFillBefore(false);
	}

	private int getCoverWithByScreen() {
		Display display = mContext.getWindowManager().getDefaultDisplay();
		float proportion = JingTools.getDeviceAspectRatio(mContext);
		int with;
		if (proportion > 1.6) {
			with = (int) (display.getWidth() * 0.85f);
		} else if (proportion == 1.5) {
			with = (int) (display.getWidth() * 0.80f);
		}else{
			with = (int) (display.getWidth() * 0.75f);
		}
		return with;
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_REFRESH_VIEW:
					if (mMusicName != null && mMusicName.length() < 24 && mCurrentMusicDTO != null) {
						music_name.setText(mMusicName+" - "+mCurrentMusicDTO.getAn());
					}else{
						music_name.setText(""+mMusicName);
					}
					if (PlayerManager.getInstance().isMainListPlaying()) {
						mMainTextForOther.setText(PlayerManager.getInstance().getmCurrentCmbt());
					}else{
						mMainTextForOther.setText(PlayerManager.getInstance().getSubCmbt());
					}
					mContext.getmViewManagerCenter().refreshRadioImage();
					artist_name.setText(""+mArtistName);
					break;
				case MSG_REFRESH_COVER:
					Bitmap bitmap = (Bitmap) msg.obj;
					try {
						mCoverRotationView.setImage(bitmap);
						int dstWidth = JingTools.dip2px(mContext, 65);
						mCoverForOther.setImageBitmap(Bitmap.createScaledBitmap(bitmap, dstWidth, dstWidth, false));
					} catch (OutOfMemoryError e) {
						try {
							mCoverRotationView.setImage(null);
						} catch (OutOfMemoryError e1) {
						}
					} catch (Exception e) {
					}
//					if (isNeedShowLyric) {
						((View) show_lyric.getParent()).setVisibility(View.VISIBLE);
//					}else{
//						((View) show_lyric.getParent()).setVisibility(View.GONE);
//					}
					isNeedShowLyric = false;
					break;
				case MSG_RESET_VIEW:
					getmLrcViewManager().clear();
					music_name.setText(mContext
							.getString(R.string.music_text_deault));
					artist_name.setText("");
//					mMainTextForOther.setText("");
					mCoverRotationView.resetRotate();
					try {
						mCoverRotationView.setImage(null);
					} catch (OutOfMemoryError e) {
					}
					mCoverForOther.setImageBitmap(null);
					playing_buttom_button_download.setImageResource(R.drawable.mini_download);
					isCurrentShowLoved = false;
					refreshLoveButton();
					playing_info_button.setVisibility(View.GONE);
//					((View) show_lyric.getParent()).setVisibility(View.GONE);
					break;
				case MSG_TOAST_PREPARING:
					mHandler.removeMessages(MSG_TOAST_PREPARING);
					Toast.makeText(mContext, R.string.tips_text_is_buffering, 0)
							.show();
					break;
				case MSG_TOAST_TIPS:
					Toast.makeText(mContext, "" + msg.obj, 1).show();
					break;
				case MSG_CLEAR_ANIMATE_LOCK:
					isAnimating = false;
					break;
				default:
					break;
				}
			}
		};
	}

	private void initPlayView() {
		center_tilte_text = (TextView) mBaseView
				.findViewById(R.id.center_view_tilte_text);
		mPlayView = (View) mBaseView
				.findViewById(R.id.center_view_layout_playing);
		playing_info_button = (View) mBaseView
				.findViewById(R.id.playing_info_button);
		playing_info_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mContext.getmMusicInfoViewManager().showView();
			}
		});
		AlphaAnimation twinkleAnimation = new AlphaAnimation(1f, 0);
		twinkleAnimation.setDuration(1200);
		twinkleAnimation.setRepeatMode(Animation.REVERSE);
		twinkleAnimation.setRepeatCount(Animation.INFINITE);
		twinkleAnimation.setInterpolator(new LinearInterpolator());
		twinkleAnimation.setAnimationListener(new AnimationListener() {
			int count;
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
				count = (count+1)%2;
				if (count == 0) {
					return;
				}
				if (playing_info_button.getTag() != null
						&& (Integer)playing_info_button.getTag() == R.drawable.playing_info_button_dark) {
					playing_info_button.setBackgroundResource(R.drawable.playing_info_button_light);
					playing_info_button.setTag(R.drawable.playing_info_button_light);
				}else{
					playing_info_button.setBackgroundResource(R.drawable.playing_info_button_dark);
					playing_info_button.setTag(R.drawable.playing_info_button_dark);
				}
			}
			@Override
			public void onAnimationEnd(Animation animation) {
			}
		});
		playing_info_button.startAnimation(twinkleAnimation);
		mLrcViews = ((ViewGroup) mBaseView.findViewById(R.id.player_vision_container));
		mJingViewPager = (JingViewPager) mPlayView
				.findViewById(R.id.jing_view_pager);
		mJingViewPager.setOverScrollMode(View.OVER_SCROLL_NEVER);
		music_name = (TextView) mPlayView.findViewById(R.id.music_name);
		artist_name = (TextView) mPlayView.findViewById(R.id.artist_name);
		resizeMusicInfoText();
		mStylusView = (ImageView) mPlayView.findViewById(R.id.stylusView);
		initStylusView(mStylusView);
		show_follower_list = (ImageButton) mPlayView
				.findViewById(R.id.show_follower_list);
		volume_seek_bar = (SeekBar)mPlayView.findViewById(R.id.volume_seek_bar);
		volume_seek_bar.getLayoutParams().width = JingTools.windowWidth*7/10;
		volume_seek_bar.setProgress(mContext.getVolumePercent());
		volume_seek_bar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if (mLastVolumeProgress == progress) {
					return;
				}
				mLastVolumeProgress = progress;
				mContext.setVolumePercent(progress);
			}
		});
		initButtons(mJingViewPager);
		show_lyric.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (isAnimating || mContext.isCenterViewNeedBack()) {
						return;
					}
					isAnimating = true;
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							isAnimating = false;
						}
					}, 500);
					int[] rect = new int[2];
					show_lyric.getLocationInWindow(rect);
					int x = (int) (rect[0]+show_lyric.getWidth()/2f);
					int y = (int) (rect[1]+show_lyric.getHeight()/2f 
							- JingTools.dip2px(mContext,
							mContext.getmViewManagerCenter().
							getCenter_view_navigation_bar().getHeight()/2f));
					Point point = new Point(x,y);
					getmLrcViewManager().setupAnimatePoint(point);
					getmLrcViewManager().showLyricView();
					refreshLyricsBackButtonView();
				}
			});
	}

	private void initButtons(final JingViewPager buttons_container) {
		buttons_container.setDisallowInterceptView(mainLayout);
		LayoutParams buttons_container_Lp = (FrameLayout.LayoutParams) buttons_container.getLayoutParams();
		buttons_container_Lp.setMargins(0, 0, 0,(int) (JingTools.screenHeight*BUTTONS_GROUP_MARGIN_RATE));
		buttons_container_Lp.height = JingTools.dip2px(mContext, BUTTONS_GROUP_HEIGHT);
		buttons_container_Lp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
		View buttonsPage1 = LayoutInflater.from(mContext).inflate(R.layout.play_ctrl_button_page_1, null);
		View group = buttonsPage1.findViewById(R.id.button_container);
		LayoutParams layoutParams = (FrameLayout.LayoutParams) group.getLayoutParams();
		layoutParams.width = JingTools.windowWidth*6/10;
//		buttons_container.setPadding(0, BUTTONS_GROUP_PADDING, 0, BUTTONS_GROUP_PADDING);
		controller_button_fav = (ImageButton) buttonsPage1.findViewById(R.id.button_1);
		final ImageButton controller_button_hate = (ImageButton) buttonsPage1.findViewById(R.id.button_2);
		final ImageButton controller_button_next = (ImageButton) buttonsPage1.findViewById(R.id.button_3);
		LayoutParams layoutParamsButton = (LayoutParams) controller_button_fav.getLayoutParams();
		layoutParamsButton.width = layoutParamsButton.height = buttons_container_Lp.height*8/10;
		layoutParamsButton.setMargins(buttons_container_Lp.height*2/10, 0, 0, 0);
		layoutParamsButton = (LayoutParams) controller_button_hate.getLayoutParams();
		layoutParamsButton.width = layoutParamsButton.height = buttons_container_Lp.height*8/10;
		layoutParamsButton = (LayoutParams) controller_button_next.getLayoutParams();
		layoutParamsButton.width = layoutParamsButton.height = buttons_container_Lp.height*8/10;
		layoutParamsButton.setMargins(0, 0, buttons_container_Lp.height*2/10,0);
		controller_button_fav.setImageResource(R.drawable.button_fav);
		controller_button_hate.setImageResource(R.drawable.button_hate);
		controller_button_next.setImageResource(R.drawable.button_next);
		View buttonsPage2 = LayoutInflater.from(mContext).inflate(R.layout.play_ctrl_button_page_2, null);
		View group2 = buttonsPage2.findViewById(R.id.button_container);
		LayoutParams layoutParams2 = (FrameLayout.LayoutParams) group2.getLayoutParams();
		layoutParams2.width = JingTools.windowWidth*8/10;
		playing_buttom_button_loop = (ImageButton) buttonsPage2.findViewById(R.id.button_1);
		show_lyric = (ImageButton) buttonsPage2.findViewById(R.id.button_4);
		final ImageView playing_buttom_button_share = (ImageButton) buttonsPage2.findViewById(R.id.button_2);
		playing_buttom_button_download = (ImageButton) buttonsPage2.findViewById(R.id.button_3);
		show_lyric.setImageResource(R.drawable.lyric_button);
		playing_buttom_button_loop.setImageResource(R.drawable.mini_loop);
		playing_buttom_button_share.setImageResource(R.drawable.mini_share);
		playing_buttom_button_download.setImageResource(R.drawable.mini_download);
		final View[] buttonsPageArray = new View[2];
		buttonsPageArray[0] = buttonsPage1;
		buttonsPageArray[1] = buttonsPage2;
		buttons_container.setAdapter(new JingPagerAdapter() {
			// 销毁position位置的界面
			public void destroyItem(View arg0, int arg1, Object arg2) {
				((ViewGroup) arg0).removeView(buttonsPageArray[arg1]);
			}
			@Override
			public void finishUpdate(View arg0) {
			}
			// 初始化position位置的界面
			@Override
			public Object instantiateItem(View arg0, int arg1) {
				ViewGroup parentView = ((ViewGroup) arg0);
				buttonsPageArray[arg1].setLayoutParams(new ViewGroup.LayoutParams(LayoutParams.MATCH_PARENT, JingTools.dip2px(mContext, 200)));
				parentView.addView(buttonsPageArray[arg1]);
				return buttonsPageArray[arg1];
			}
			// 判断是否由对象生成界面
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == (arg1);
			}
			@Override
			public int getCount() {
				return 2;
			}
			@Override
			public void startUpdate(View container) {
			}
			@Override
			public Parcelable saveState() {
				return null;
			}
			@Override
			public void restoreState(Parcelable state, ClassLoader loader) {
			}
		});
		OnClickListener mainControllerListenter = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.equals(controller_button_fav)) {
					KTC.rep("Home", "ClickLikeBtn", KTC.getMusicProgress());
					if (mContext.getmLoginData().getUsr().isGuest()) {
						mContext.logout();
						return;
					}
					isCurrentShowLoved = mContext.musicFav(mContext
							.getUserId());
					refreshLoveButton();
				} else if (v.equals(controller_button_hate)) {
					KTC.rep("Home", "ClickHateBtn", KTC.getMusicProgress());
					if (mContext.getmLoginData().getUsr().isGuest()) {
						mContext.logout();
						return;
					}
					mContext.musicHate();
				} else if (v.equals(controller_button_next)) {
					KTC.rep("Home", "ClickNextBtn", KTC.getMusicProgress());
					if (mContext.isFollowingOther()) {
						mContext.toastYouAreInFollow();
						return;
					}
					mContext.musicNext(true);
				} else if (v.equals(playing_buttom_button_download)) {
					KTC.rep("Home", "ClickDonwloadBtn", "");
					if (mContext.getmLoginData().getUsr().isGuest()) {
						mContext.logout();
						return;
					}
					if (mContext.getmViewManagerCenter()
							.getmMusicViewManager()
							.downloadMusicContains(mCurrentMusicDTO)) {
						mContext.getmViewManagerLeft().getLeftViewAdapter().onItemClick(null, null, LeftViewAdapter.INDEX_ITEM_LOCAL_CACHE, 0);
					}else{
						if (mContext.getmViewManagerCenter().getmMusicViewManager().getLocalMusicCanDownload()) {
							playing_buttom_button_download.setImageResource(R.drawable.mini_download);
							mContext.downloadMusic(mContext.getCurrentMusicDTO());
						}else{
							playing_buttom_button_download.setImageResource(R.drawable.mini_download);
							Toast.makeText(mContext, "最多只能缓存" + mContext.getmLoginData().getCm() + "分钟", 1).show();
						}
					}
				} else if (v.equals(playing_buttom_button_loop)) {
					KTC.rep("Home", "ClickRepeatBtn", KTC.getMusicProgress());
					if (mContext.isFollowingOther()) {
						mContext.toastYouAreInFollow();
						return;
					}
					mIsLooping = !mIsLooping;
					mContext.musicLooping(mIsLooping);
					mContext.setPlayingTitleText();
					mContext.getmViewManagerCenter().refreshRightButtonState();
					playing_buttom_button_loop.setImageResource(mIsLooping?
							R.drawable.mini_loop_down
							:R.drawable.mini_loop);
				} else if (v.equals(playing_buttom_button_share)) {
					if (mContext.getmLoginData().getUsr().isGuest()) {
						mContext.logout();
						return;
					}
					if (mCurrentMusicDTO != null) {
						mContext.showShareView(mCurrentMusicDTO);
					}
				}
			}
		};
		show_follower_list.setOnClickListener(mainControllerListenter);
		controller_button_fav.setOnClickListener(mainControllerListenter);
		controller_button_hate.setOnClickListener(mainControllerListenter);
		controller_button_next.setOnClickListener(mainControllerListenter);
		playing_buttom_button_download
				.setOnClickListener(mainControllerListenter);
		playing_buttom_button_loop.setOnClickListener(mainControllerListenter);
		playing_buttom_button_share.setOnClickListener(mainControllerListenter);
		center_tilte_text.setText("Jing");
	}
	
	public void refreshLyricsBackButtonView() {
		if (mLrcViews.getVisibility() == View.GONE) {
			mContext.getmViewManagerCenter().setBackButtonFunction(null);
		}else{
			if (mLrcHideListener == null) {
				mLrcHideListener = new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						getmLrcViewManager().hide(true);
						mContext.getmViewManagerCenter().setBackButtonFunction(null);
					}
				};
			}
			if (mLrcSwitchFullScreenListener == null) {
				mLrcSwitchFullScreenListener = new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						getmLrcViewManager().switchLrcFullScreen();
					}
				};
			}
		}
	}

	protected void quitVoiceUI() {
		if (isNeedResume && !mContext.isPlaying()) {
			isNeedResume = false;
			isNeedSwitch = true;
			mStylusView.startAnimation(mStylusViewOnAnimation);	
		}
	}

	private void initStylusView(ImageView stylusView) {
		mStylusViewOffAnimation = new RotateAnimation(MAX_ROTATE_ANGLE, MIN_ROTATE_ANGLE,
				Animation.RELATIVE_TO_SELF, STYLUS_RELAYTIVE_SELF_X, Animation.RELATIVE_TO_SELF,STYLUS_RELAYTIVE_SELF_Y);
		mStylusViewOnAnimation = new RotateAnimation(MIN_ROTATE_ANGLE, MAX_ROTATE_ANGLE,
				Animation.RELATIVE_TO_SELF, STYLUS_RELAYTIVE_SELF_X, Animation.RELATIVE_TO_SELF, STYLUS_RELAYTIVE_SELF_Y);
		mStylusViewOffAnimation.setFillAfter(true);
		mStylusViewOffAnimation.setDuration(300);
		mStylusViewOnAnimation.setDuration(300);
		AnimationListener animListener = new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				isAnimating = true;
				mHandler.sendEmptyMessageDelayed(MSG_CLEAR_ANIMATE_LOCK,500);
				if (animation.equals(mStylusViewOffAnimation)) {
					if (isNeedSwitch) {
						pauseMusic();
						isNeedSwitch = false;
					}
				}
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isAnimating = false;
				if (animation.equals(mStylusViewOnAnimation)) {
					if (isNeedSwitch) {
						startMusic();
						isNeedSwitch = false;
					}
					mStylusView.clearAnimation();
				}
			}
		};
		mStylusViewOnAnimation.setAnimationListener(animListener);
		mStylusViewOffAnimation.setAnimationListener(animListener);
	}

	protected void switchPlayingState() {
		if (isAnimating) {
			return;
		}
		if (mContext.isFollowingOther()) {
			mContext.toastYouAreInFollow();
			return;
		}
		if (!mContext.musicIsPrepared()) {
			mHandler.removeMessages(MSG_TOAST_PREPARING);
			mHandler.sendEmptyMessage(MSG_TOAST_PREPARING);
			return;
		}
		isNeedSwitch = true;
		if (mContext.isPlaying()) {
			mStylusView.startAnimation(mStylusViewOffAnimation);
		} else {
			mStylusView.startAnimation(mStylusViewOnAnimation);	
		}
	}

	private void startRotateCover(){
		if (mVisualizerViewMini != null) {
			mVisualizerViewMini.startScroll();
		}
		if (SettingManager.getInstance().isCoverRoation()) {
			mCoverRotationView.startRotate();
		}else{
			mCoverRotationView.pauseRotate();
		}
	}
	
	protected void startMusic() {
		startRotateCover();
		mContext.musicPlay();
	}

	protected void pauseMusic() {
		if (mVisualizerViewMini != null) {
			mVisualizerViewMini.pauseScroll();
		}
		mCoverRotationView.pauseRotate();
		mContext.musicPause();
	}

	public View getPlayingView() {
		return mPlayView;
	}
	
	public void invalidateCoverView() {
		if (mCoverRotationView != null) {
			mCoverRotationView.invalidate();
		}
	}
	
	public MusicDTO getmCurrentMusicDTO() {
		return mCurrentMusicDTO;
	}

	@Override
	public void onPlayerStateChange(final PlayerManager pm) {
		if (!isAlreadyAddProgressListener) {
			pm.addProgressListener(mCoverRotationView);
			pm.setShowTipsListener(this);
			pm.setStartNewListener(this);
			pm.setLrcGetListener(this);
			pm.setMusicInfoListener(mContext.getmMusicInfoViewManager());
			isAlreadyAddProgressListener = true;
		}
		if (pm.isPlaying() || pm.isPrepared()) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {	
					mCoverLoadingAnimation.cancel();
					cover_loading.clearAnimation();
					cover_loading.setVisibility(View.GONE);
				}
			});
		}else{
			mHandler.post(new Runnable() {
				@Override
				public void run() {		
					cover_loading.setVisibility(View.VISIBLE);
					cover_loading.startAnimation(mCoverLoadingAnimation);
				}
			});
		}
		if (mIsLooping != pm.isLooping()) {
			mIsLooping = pm.isLooping();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					mContext.getmViewManagerCenter().refreshRightButtonState();
					playing_buttom_button_loop.setImageResource(mIsLooping?
							R.drawable.mini_loop
							:R.drawable.mini_loop);
				}
			});
		}
		if (mCurrentMusicDTO == null) {
			onStartNewListener(pm);
		}
		if (mMusicInfoDTO != pm.getmMusicInfoDTO()) {
			mMusicInfoDTO = pm.getmMusicInfoDTO();
		}
		if (isCurrentShowLoved != pm.isLoved()) {
			isCurrentShowLoved = pm.isLoved();
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					refreshLoveButton();
				}
			});
		}
		if (mCoverRotationView.isRotateRunning() != pm.isPlaying()) {
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					refreshRotateState(pm.isPlaying());
				}
			});
		}
	}

	protected void refreshLoveButton() {
		controller_button_fav.setImageResource(isCurrentShowLoved ? R.drawable.liked
				: R.drawable.like);
	}

	public void refreshRotateState(final boolean isPlaying) {
		if (isRotateEnable && isPlaying) {
			startRotateCover();
			if (mStylusView.getAnimation() == mStylusViewOffAnimation) {
				mStylusView.startAnimation(mStylusViewOnAnimation);
			}
			mPlayView.invalidate();
			Log.i("kid_debug", "startRotate");
		} else {
			mCoverRotationView.pauseRotate();
			if (mStylusView.getAnimation() != mStylusViewOffAnimation) {
				mStylusView.startAnimation(mStylusViewOffAnimation);
			}
			Log.i("kid_debug", "pauseRotate");
		}
	}

	public void switchFreezeCoverRotationView(boolean freeze){
		if (freeze) {
			mCoverRotationView.freeze();
		}else{
			mCoverRotationView.unfreeze();
		}
	}
	
	public void enableRotate(boolean isPlayingViewVisable) {
		isRotateEnable = isPlayingViewVisable;
		if (mContext != null) {
			refreshRotateState(mContext.isPlaying());
		}
	}

	@Override
	public void onLogin(LoginDataDTO data) {
		if(mContext.musicIsRelease()){
			mContext.startMyMusicList();
		}
	}

	public void showGuideFirstPlay() {
		if (SettingManager.getInstance().isNoNeedShowPlayingGuide()) {
			return;
		}
		SettingManager.getInstance().setNoNeedShowPlayingGuide(true);
		mContext.showGuide();
	}
	
	public void showGuideFirstPlaySubList() {
		if (SettingManager.getInstance().isNoNeedShowPlayingSubListGuide()) {
			return;
		}
		SettingManager.getInstance().setNoNeedShowPlayingSubListGuide(true);
		final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.TOP | Gravity.RIGHT;
		params.setMargins(0, 0, JingTools.dip2px(mContext, 6), 0);
	}
	
	public void showGuideBackPlayingViewGuide() {
		if (SettingManager.getInstance().isNoNeedShowBackPlayingViewGuide()) {
			return;
		}
		SettingManager.getInstance().setNoNeedShowBackPlayingViewGuide(true);
		final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.TOP | Gravity.RIGHT;
		params.setMargins(0, 0, JingTools.dip2px(mContext, 6), 0);
	}

	@Override
	public void onLogout() {
		mHandler.removeMessages(MSG_REFRESH_VIEW);
		mHandler.removeMessages(MSG_REFRESH_COVER);
		mHandler.sendEmptyMessage(MSG_RESET_VIEW);
		mContext.musicRelease();
	}

	@Override
	public void onShowTips(String tips) {
		Message msg = new Message();
		msg.what = MSG_TOAST_TIPS;
		msg.obj = tips;
		mHandler.sendMessage(msg);
	}

	@Override
	public void onStartNewListener(final PlayerManager pm) {
		if (mCurrentMusicDTO == pm.getLastDTO()) {
			return;
		}
		mHandler.sendEmptyMessage(MSG_RESET_VIEW);
		mCurrentMusicDTO = pm.getLastDTO();
		LocalCacheManager.getInstance().getLocalLrcByMid(mCurrentMusicDTO.getMid(),this);
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				if (mCurrentMusicDTO != null) {
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							try {
								playing_buttom_button_download.setImageResource(
										mContext.getmViewManagerCenter()
											.getmMusicViewManager()
											.downloadMusicContains(mCurrentMusicDTO)?
										R.drawable.mini_download
										:R.drawable.mini_download);
							} catch (Exception e) {
							}
						}
					},2000);
					mArtistName = mCurrentMusicDTO.getAtn();
					mMusicName = mCurrentMusicDTO.getN();
					mHandler.sendEmptyMessage(MSG_REFRESH_VIEW);
					final MusicDTO musicDtoTag = mCurrentMusicDTO;
					AsyncImageLoader.getInstance().loadBitmapByUrl(
							CustomerImageRule.ID2URL(
									Constants.ID2URL_KEY_WORD_ALBUM,
									mCurrentMusicDTO.getFid(),
									Constants.ID2URL_DEFAULT_BITRATE_ALBUM),
							AsyncImageLoader.IMAGE_TYPE_ORIGINAL,
							new ImageCallback() {
								@Override
								public void imageLoaded(Bitmap mBitmap,
										String imageUrl) {
									if (!musicDtoTag.equals(mCurrentMusicDTO)) {
										return;
									}
									Message msg = new Message();
									mHandler.removeMessages(MSG_RESET_VIEW);
									mHandler.removeMessages(MSG_REFRESH_COVER);
									msg.what = MSG_REFRESH_COVER;
									msg.obj = mBitmap;
									mHandler.sendMessage(msg);
								}
							});
				}
				refreshRotateState(pm.isPlaying());
			}
		});
	}
	
	public boolean isLooping(){
		return mIsLooping;
	}
	
	public LrcViewManager getmLrcViewManager() {
		if (mLrcViewManager == null) {
			mLrcViewManager = new LrcViewManager(mContext,mLrcViews);
		}
		return mLrcViewManager;
	}

	public void resizeMusicInfoText() {
		artist_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingManager.getInstance().isBigFont()?16:14);
		artist_name.invalidate();
		music_name.setTextSize(TypedValue.COMPLEX_UNIT_SP, SettingManager.getInstance().isBigFont()?14:12);
		music_name.invalidate();
		artist_name.setTypeface(Typeface.create("approachable", Typeface.NORMAL));
		music_name.setTypeface(Typeface.create("approachable", Typeface.NORMAL));
	}

	@Override
	public void onNewLrc(MusicDTO musicDTO,boolean hasLrc) {
		if (!hasLrc) {
			isNeedShowLyric = false;
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					getmLrcViewManager().noLrc();
//					((View) show_lyric.getParent()).setVisibility(View.GONE);
				}
			});
			return;
		}else{
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					getmLrcViewManager().loading();
					((View) show_lyric.getParent()).setVisibility(View.VISIBLE);
				}
			});
		}
		LocalCacheManager.getInstance().downLyricsFile(musicDTO.getMid(),this);
	}

	public boolean sendKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_VOLUME_UP :
			volumeChange(true);
			return true;
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			volumeChange(false);
			return true;
		}
		return getmLrcViewManager().sendKeyEvent(event);
	}

	private void volumeChange(boolean b) {
		mContext.volumeChange(b);
		mLastVolumeProgress = mContext.getVolumePercent();
		volume_seek_bar.setProgress(mLastVolumeProgress);
	}
	
	@Override
	public void onCallBack(final String mid, final LyricsInfo lyricsInfo) {
		mHandler.post(new Runnable() {

			@Override
			public void run() {
				if (mCurrentMusicDTO != null 
						&& lyricsInfo!= null
						&& mCurrentMusicDTO.getMid().equals(mid)) {
					((View) show_lyric.getParent()).setVisibility(View.VISIBLE);
					isNeedShowLyric = true;
					getmLrcViewManager().setmLyricsInfo(lyricsInfo);
				}
			}
		});
	}

	public void setVisualView(VisualizerViewMini view) {
		mVisualizerViewMini = view;
	}

	public void setMusicInfoItemViews(ImageView imageView, TextView main_text,
			TextView sub_text) {
		mCoverForOther = imageView;
		mMainTextForOther = main_text;
		mSubTextForOther = sub_text;
	}

	public void hideInfoButton(boolean hide) {
		if (hide) {
			playing_info_button.setVisibility(View.GONE);
		}else{
			playing_info_button.setVisibility(View.VISIBLE);
		}
	}
}
