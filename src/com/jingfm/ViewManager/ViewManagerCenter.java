package com.jingfm.ViewManager;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Stack;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.MainActivity.ChangeDataAnimateCallBack;
import com.jingfm.R;
import com.jingfm.Constants.KTC;
import com.jingfm.ViewManager.ChatViewManager.ChatUserData;
import com.jingfm.ViewManager.ShareViewManager.SharedMusicDTO;
import com.jingfm.adapter.AbstractDragAdapter;
import com.jingfm.adapter.RankListAdapter;
import com.jingfm.api.business.UserFavoritesRequestApi;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.socketmessage.SocketPChatDTO;
import com.jingfm.background_model.PlayerManager;
import com.jingfm.customer_views.BackImageButton;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.customer_views.TouchScrollContainer;
import com.jingfm.customer_views.VisualizerViewMini;
import com.jingfm.tools.JingTools;

public class ViewManagerCenter {

	private static final int VIEW_IN_ANIMATE_DIRECTION_LEFT = 0;
	private static final int VIEW_IN_ANIMATE_DIRECTION_RIGHT = 1;
	private Handler mHandler;
	private MainActivity mContext;
	private TouchScrollContainer mainLayout;
	private ImageButton menu_buton;
	private ImageButton right_buton;
	private TextView center_tilte_text;
	private View mCurrentShowingView;
	private View mBaseView;
	private View mCenterView;
	private ViewGroup mCenterContainerView;
	private BackImageButton back_buton;
	private PlayingViewManager mPlayingViewManager;
	private SettingViewManager mSetttingViewManager;
	private FriendsViewManager mFriendsViewManager;
	private MusicViewManager mMusicViewManager;
	private NewbieManager mNewbieManager;
	private boolean isViewsLock;
	private ChatViewManager mChatViewManager;
	private OnClickListener mBackToMainListListener;
	private OnClickListener mShowIsPlayingListener;
	private Stack<LinkedViewData> mStackLinkedViewData;
	private OnClickListener mPushLinkedViewsBackButtonListener;
	private int mNewMessageNotify;
	private TextView center_view_new_message;
	protected View mTempHideView;
	private View center_view_navigation_bar;
	private View mBackToPlayingViewButton;
	private MusicExploreManager mMusicExploreManager;
	public RankListAdapter mRankListAdapter;
	private View title_text_options_bookmark;
	private View title_text_options_share;
	private View title_text_options_go_back;
	private View mTitleTextOptions;
	private Dialog mTitleTextOptionsDialog;
	private View title_text_options_arrow;
	private View center_view_tilte_text_shell;
	private HashSet<String> cmbtSet = new HashSet<String>();

	public ViewManagerCenter(MainActivity context,View baseView) {
		this.mContext = context;
		this.mBaseView = baseView;
		initHandler();
		initView();
		mFriendsViewManager = new FriendsViewManager(mContext);
		mShowIsPlayingListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mContext.getmViewManagerRight().isShowing()) {
					removeAllViewsAddNew(null);
					mContext.getmViewManagerRight().sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_BACK));
				}else{
					mContext.changeData(mainLayout.getPst() != mainLayout.getmRightEdgeLimit(), new ChangeDataAnimateCallBack() {
						
						@Override
						public void doChangeData() {
							removeAllViewsAddNew(null);
							refreshRightButtonState();
						}
					});
				}
			}
		};
		mBackToMainListListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mPlayingViewManager.isLooping()) {
					mContext.musicLooping(false);
					mContext.setPlayingTitleText();
				}else{
					mContext.backToMainList();
				}
				refreshRightButtonState();
			}
		};
		mStackLinkedViewData = new Stack<LinkedViewData>();
		mPushLinkedViewsBackButtonListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isViewsLock) {
					return;
				}
				isViewsLock = true;
				if (mStackLinkedViewData.isEmpty()) {
					return;
				}
				viewSwitchAnimate(VIEW_IN_ANIMATE_DIRECTION_RIGHT,mCurrentShowingView,mStackLinkedViewData.pop().makeView());
				if (mStackLinkedViewData.isEmpty()) {
					back_buton.setVisibility(View.GONE);
					back_buton.setOnClickListener(null);
					menu_buton.setVisibility(View.VISIBLE);
				}
			}
		};
	}

	private void initHandler() {
		this.mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		};
	}

	public TouchScrollContainer getMainLayout() {
		return mainLayout;
	}

	public PlayingViewManager getmPlayingViewManager() {
		return mPlayingViewManager;
	}
	public MusicViewManager getmMusicViewManager() {
		return mMusicViewManager;
	}

	public void setmPlayingViewManager(PlayingViewManager mPlayingViewManager) {
		this.mPlayingViewManager = mPlayingViewManager;
	}

	private void initView() {
		mainLayout = new TouchScrollContainer(mContext);
		initContainerView();
		initTitleTextOptions();
		initPlayingView();
		mainLayout.setDisableMoveToRight(true);
		mainLayout.setEdgeLimitRate(0.8f);
		mainLayout.addView(mCenterView);
		mainLayout.setTagText("layout1");
		((ViewGroup) mBaseView.findViewById(R.id.main_view_container_01))
				.addView(mainLayout);
		center_view_tilte_text_shell.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (title_text_options_arrow.getVisibility() != View.GONE) {
					showTitleTextOptionsDialog();
				}
			}
		});
	}

	private void initTitleTextOptions(){
		mTitleTextOptions = LayoutInflater.from(mContext).inflate(R.layout.title_text_options_layout, null);
		title_text_options_share = mTitleTextOptions.findViewById(R.id.title_text_options_share);
		title_text_options_bookmark = mTitleTextOptions.findViewById(R.id.title_text_options_bookmark);
		title_text_options_go_back = mTitleTextOptions.findViewById(R.id.title_text_options_go_back);
		OnClickListener onClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mTitleTextOptionsDialog != null) {
					mTitleTextOptionsDialog.cancel();
				}
				if (v == title_text_options_go_back) {
					if (mPlayingViewManager.isLooping()) {
						mContext.musicLooping(false);
						mContext.setPlayingTitleText();
					}else{
						mContext.backToMainList();
					}
					refreshRightButtonState();
				}else if(v == title_text_options_bookmark) {
					KTC.rep("Home", "CollectSearch", "{}");
					final HashMap<Object, Object> params = new HashMap<Object, Object>();
//					t传C(大写)，fid不传,ouid传自己ID
//					params.put("t", "C");
//					params.put("uid", ""+mContext.getUserId());
//					params.put("ouid", ""+mContext.getUserId());
//					if (PlayerManager.getInstance().isMainListPlaying()) {
//						params.put("tit", PlayerManager.getInstance().getmCurrentCmbt());
//					}else{
//						params.put("tit", PlayerManager.getInstance().getSubCmbt());
//					}
					params.put("uid", ""+mContext.getUserId());
					if (PlayerManager.getInstance().isMainListPlaying()) {
						params.put("q", PlayerManager.getInstance().getmCurrentCmbt());
					}else{
						params.put("q", PlayerManager.getInstance().getSubCmbt());
					}
					if (cmbtSet.contains(params.get("q"))) {
						return;
					}else{
						cmbtSet.add(""+params.get("q"));
						Toast.makeText(mContext, "收藏成功", 1).show();
					}
					new Thread(){
						public void run() {
							UserFavoritesRequestApi.postFavoritesCmbt(params);
						};
					}.start();
					refreshRadioImage();
				}else if(v == title_text_options_share) {
					mContext.showShareView(new SharedMusicDTO(mPlayingViewManager.getmCurrentMusicDTO(),PlayerManager.getInstance().getCmbt()));
				}
			}
		};
		title_text_options_bookmark.setOnClickListener(onClickListener);
		title_text_options_share.setOnClickListener(onClickListener);
		title_text_options_go_back.setOnClickListener(onClickListener);
	}
	
	private void showTitleTextOptionsDialog(){
		mTitleTextOptionsDialog = new Dialog(mContext,R.style.back_title_dialog);
		mTitleTextOptionsDialog.setCancelable(true);
		mTitleTextOptionsDialog.setContentView(mTitleTextOptions);
		Window dialogWindow = mTitleTextOptionsDialog.getWindow();
		WindowManager.LayoutParams lp = dialogWindow.getAttributes();
		dialogWindow.setGravity(Gravity.CENTER_HORIZONTAL | Gravity.TOP);
		lp.y = JingTools.dip2px(mContext, 45); // 新位置Y坐标
		lp.width = WindowManager.LayoutParams.MATCH_PARENT; // 宽度
		lp.height = WindowManager.LayoutParams.WRAP_CONTENT; // 高度
		dialogWindow.setAttributes(lp);
		mTitleTextOptionsDialog.show();
		mTitleTextOptionsDialog.setOnCancelListener(new OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				ViewParent vp = mTitleTextOptions.getParent();
				if (vp != null && vp instanceof ViewGroup) {
					((ViewGroup)vp).removeAllViews();
				}
			}
		});
	}
	
	private void initPlayingView() {
		mPlayingViewManager = new PlayingViewManager(mContext, mCenterView,
				mainLayout);
		mContext.addLoginListener(mPlayingViewManager);
		ImageView imageView = (ImageView) title_text_options_bookmark.findViewById(R.id.icon);
		imageView.setBackgroundResource(R.drawable.title_text_options_share_icon_bg);
		int padding = JingTools.dip2px(mContext, 4);
		imageView.setPadding(padding, 0, 0, padding);
		TextView main_text = (TextView) title_text_options_bookmark.findViewById(R.id.main_text);
		TextView sub_text = (TextView) title_text_options_bookmark.findViewById(R.id.sub_text);
		mPlayingViewManager.setMusicInfoItemViews(imageView,main_text,sub_text);
	}

	private void initContainerView() {
		mCenterView = LayoutInflater.from(mContext).inflate(
				R.layout.center_view, null);
		center_view_navigation_bar = mCenterView
				.findViewById(R.id.center_view_navigation_bar);
		menu_buton = (ImageButton) mCenterView
				.findViewById(R.id.center_view_menu_buton);
		menu_buton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_MENU));
			}
		});
		back_buton = (BackImageButton) mCenterView
				.findViewById(R.id.center_view_back_buton);
		back_buton.setVisibility(View.GONE);
		right_buton = (ImageButton) mCenterView
				.findViewById(R.id.center_view_right_buton);
		right_buton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				mContext.showSearchView();
			}
		});
		center_tilte_text = (TextView) mCenterView
				.findViewById(R.id.center_view_tilte_text);
		center_view_tilte_text_shell = mCenterView
				.findViewById(R.id.center_view_tilte_text_shell);
		title_text_options_arrow = mCenterView
				.findViewById(R.id.tilte_text_options_arrow);
		center_view_new_message = (TextView) mCenterView
				.findViewById(R.id.center_view_new_message);
		mCenterContainerView = (ViewGroup) mCenterView
				.findViewById(R.id.list_view_container);
		mCenterContainerView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mainLayout.getPst() == mainLayout.getmLeftEdgeLimit()
						|| mainLayout.getPst() == mainLayout.getmRightEdgeLimit()) {
					mContext.centerViewBack();
				}
			}
		});
	}
	
	public void setBackButtonFunction(OnClickListener onClickListener){
		back_buton.setOnClickListener(onClickListener);
		if (onClickListener == null) {
			back_buton.setVisibility(View.GONE);
			menu_buton.setVisibility(View.VISIBLE);
		}else{
			back_buton.setVisibility(View.VISIBLE);
			menu_buton.setVisibility(View.GONE);
		}
	}

	public void removeAllViewsAddNew(View view) {
		if (mCurrentShowingView == view) {
			return;
		}else{
			mCurrentShowingView = view;
		}
		mCenterContainerView.removeAllViews();
		mStackLinkedViewData.clear();
		if (view == null) {
			mContext.setPlayingTitleText();
			mTempHideView = null;
			back_buton.setOnClickListener(null);
			back_buton.setVisibility(View.GONE);
			menu_buton.setVisibility(View.VISIBLE);
			mPlayingViewManager.getPlayingView().setVisibility(View.VISIBLE);
			mPlayingViewManager.switchFreezeCoverRotationView(false);
			mPlayingViewManager.showGuideFirstPlay();
			mPlayingViewManager.refreshLyricsBackButtonView();
		}else{
			view.setVisibility(View.VISIBLE);
			mPlayingViewManager.getPlayingView().setVisibility(View.GONE);
			mPlayingViewManager.switchFreezeCoverRotationView(true);
			mCenterContainerView.addView(view);
		}
		refreshRightButtonState();
	}
	
	public void refreshRightButtonState(){
		if ((mPlayingViewManager.getPlayingView().getVisibility() == View.GONE 
				&&!isChatShowing())
				|| mContext.getmViewManagerRight().isShowing()){
			title_text_options_arrow.setVisibility(View.GONE);
			Animation animationIn = AnimationUtils.loadAnimation(mContext,
					R.anim.silde_in_from_right);
			mBackToPlayingViewButton.setVisibility(View.VISIBLE);
			mBackToPlayingViewButton.setOnClickListener(mShowIsPlayingListener);
			mBackToPlayingViewButton.startAnimation(animationIn);
			mPlayingViewManager.showGuideBackPlayingViewGuide();
		}else{
			if (mContext.isOfflineMode()) {
				return;
			}
			title_text_options_arrow.setVisibility(View.VISIBLE);
			title_text_options_bookmark.setVisibility(View.VISIBLE);
			refreshRadioImage();
			hideBackToPlayingView();
			if (mContext.isPlayingSubList()) {
				mPlayingViewManager.showGuideFirstPlaySubList();
				if (mContext.isPlayingPersonalRadio(mContext.getmLoginData().getUsr().getNick())
						|| "收听动态".equals(center_tilte_text.getText().toString())
						|| mContext.isAiRadioPlaying()
						|| mContext.isFollowingOther()
						|| mContext.isLocalMusicMode()) {
					title_text_options_bookmark.setVisibility(View.GONE);
				}
				title_text_options_go_back.setVisibility(View.VISIBLE);
			}else{
				title_text_options_go_back.setVisibility(View.GONE);
			}
		}
		title_text_options_share.setVisibility(
				mContext.isPlayingSubList()?View.GONE:View.VISIBLE);
		title_text_options_bookmark.setVisibility(
				mContext.isPlayingSubList()?View.GONE:View.VISIBLE);
		title_text_options_arrow.setVisibility(mPlayingViewManager.getPlayingView().getVisibility() == View.GONE?View.GONE:View.VISIBLE);
	}
	
	public void hideBackToPlayingView(){
		if (mBackToPlayingViewButton.getVisibility() == View.GONE) {
			return;
		}
		Animation animationOut = AnimationUtils.loadAnimation(mContext,
				R.anim.silde_away_to_right);
		mBackToPlayingViewButton.startAnimation(animationOut);
		animationOut.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				mBackToPlayingViewButton.setVisibility(View.GONE);
			}
		});
	}
	
	public void refreshRadioImage() {
		ImageView imageView = (ImageView) title_text_options_bookmark.findViewById(R.id.title_text_options_bookmark_button);
		TextView textView = (TextView) title_text_options_bookmark.findViewById(R.id.main_text);
		TextView textViewSub = (TextView) title_text_options_bookmark.findViewById(R.id.sub_text);
		if (cmbtSet.contains(PlayerManager.getInstance().getmCurrentCmbt())) {
			imageView.setImageResource(R.drawable.title_text_options_bookmark_had);
			String word = ""+textView.getText();
			if (!word.startsWith("已经收藏电台")) {
				textView.setText("已经收藏电台" + word);
			}
			textViewSub.setText("可以去个人主页查看自己收藏的电台");
		}else{
			imageView.setImageResource(R.drawable.title_text_options_bookmark_add);
			textViewSub.setText("收藏这个电台方便以后再次使用");
		}
	}

	private boolean isChatShowing() {
		return mChatViewManager != null && mChatViewManager.getmChatView().getParent() == mCenterContainerView;
	}

	public void setBackToPlayingViewButton(View view){
		mBackToPlayingViewButton = view;
		if (view instanceof VisualizerViewMini) {
			mPlayingViewManager.setVisualView((VisualizerViewMini) view);
		}
	}
	
	public void startMusicExplore() {
		if (mMusicExploreManager == null) {
			mMusicExploreManager = new MusicExploreManager(mContext);
		}
		setChangeToListView(mMusicExploreManager);
	}

	public void setChangeToListView(AbstractDragAdapter adapter) {
		DragRefreshListView listView = new DragRefreshListView(mContext);
		String title = adapter.getTitleText();
		if (JingTools.isValidString(title)) {
			mContext.setJingTitleText(title);
		}
		mCenterContainerView.addView(listView);
		adapter.setListView(listView);
		removeAllViewsAddNew(listView);
	}
	
	public void setChangeToView(View view, String titleText) {
		if (mCurrentShowingView == view) {
			return;
		}
		if (JingTools.isValidString(titleText)) {
			mContext.setJingTitleText(titleText);
		}
		mCenterContainerView.addView(view);
		removeAllViewsAddNew(view);
	}


	public void logout() {
		removeAllViewsAddNew(null);
		refreshRightButtonState();
	}

	public void showLoginView() {
		removeAllViewsAddNew(null);
		mainLayout.smoothResetPst(1000);
	}

	public void initMusicViewManager() {
		mMusicViewManager = new MusicViewManager(mContext);
	}

	public boolean sendKeyEvent(KeyEvent event) {
		if (mPlayingViewManager.sendKeyEvent(event)) {
			return true;
		}
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_SEARCH:
			if (mainLayout.isFreeze()) {
				return false;
			}
			if (mainLayout.getScrollX() != 0) {
				mContext.centerViewBack();
			} else {
				mContext.centerViewToLeft();
			}
			return true;
		case KeyEvent.KEYCODE_MENU:
			if (mainLayout.isFreeze()) {
				return true;
			}
			if (mainLayout.getScrollX() != 0) {
				mContext.centerViewBack();
			} else {
				mContext.centerViewToRight();
			}
			return true;
		case KeyEvent.KEYCODE_BACK:
			if (mainLayout.getPst() != 0) {
				mainLayout.smoothResetPst(300);
				return true;
			}
			if (back_buton.getVisibility() == View.VISIBLE) {
				if (back_buton.getOnClickListener() != null) {
					back_buton.getOnClickListener().onClick(back_buton);
					return true;
				}
			}
			if (mPlayingViewManager.getPlayingView().getVisibility() != View.VISIBLE) {
				if (mShowIsPlayingListener != null) {
					mShowIsPlayingListener.onClick(right_buton);
					return true;
				}
			}
		}
		return false;
	}

	public void ShowSettingView() {
		if (mSetttingViewManager == null) {
			mSetttingViewManager = new SettingViewManager(mContext,mainLayout);
		}
		removeAllViewsAddNew(mSetttingViewManager.getSettingView());
	}
	
	public SettingViewManager getmSetttingViewManager() {
		return mSetttingViewManager;
	}

	public void showFriendView() {
		mFriendsViewManager.onShowing(null,""+mContext.getUserId());
		removeAllViewsAddNew(mFriendsViewManager.getView());
	}
	
	public FriendsViewManager getFriendsViewManager(){
		return mFriendsViewManager;
	}
	
	public void showMusicViewFav() {
		mMusicViewManager.onShowingFavMusic();
		removeAllViewsAddNew(mMusicViewManager.getView());
	}
	public void showMusicViewLocal() {
		mMusicViewManager.onShowingLocalMusic();
		removeAllViewsAddNew(mMusicViewManager.getView());
	}
	public void startDownloadMusic(MusicDTO mucisDTO) {
		mMusicViewManager.startDownloadMusic(mucisDTO);
	}
	
	public ChatViewManager getmChatViewManager(){
		if (mChatViewManager == null) {
			mChatViewManager = new ChatViewManager(mContext);
		}
		return mChatViewManager;
	}
	
	public void pushLinkedViews(final LinkedViewData oldLinkedViewData,final LinkedViewData newLinkedViewData){
		if (isViewsLock) {
			return;
		}
		isViewsLock = true;
		menu_buton.setVisibility(View.GONE);
		back_buton.setVisibility(View.VISIBLE);
		back_buton.setOnClickListener(mPushLinkedViewsBackButtonListener);
		mStackLinkedViewData.push(oldLinkedViewData);
		final View newView = newLinkedViewData.makeView();
		viewSwitchAnimate(VIEW_IN_ANIMATE_DIRECTION_LEFT,mCurrentShowingView,newView);
	}

	private synchronized void viewSwitchAnimate(int direction, final View oldView, final View newView) {
		if (oldView == null || newView == null) {
			return;
		}
		newView.setVisibility(View.VISIBLE);
		mCurrentShowingView = newView;
		Animation animationOut = null;
		Animation animationIn = null;
		switch (direction) {
		case VIEW_IN_ANIMATE_DIRECTION_LEFT:
			animationOut = AnimationUtils.loadAnimation(mContext,
					R.anim.silde_away_to_left);
			animationIn = AnimationUtils.loadAnimation(mContext,
					R.anim.silde_in_from_right);
			break;
		case VIEW_IN_ANIMATE_DIRECTION_RIGHT:
			animationOut = AnimationUtils.loadAnimation(mContext,
					R.anim.silde_away_to_right);
			animationIn = AnimationUtils.loadAnimation(mContext,
					R.anim.silde_in_from_left);
			break;
		}
		final Animation tmpAnimationOut = animationOut;
		animationOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(back_buton.getWindowToken(), 0);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (animation.equals(tmpAnimationOut)) {
					oldView.setVisibility(View.GONE);
					mHandler.post(new Runnable() {
						public void run() {
							mCenterContainerView.removeView(oldView);
							isViewsLock = false;
							refreshRightButtonState();
						}
					});
				}
			}
		});
		mCenterContainerView.removeView(newView);
		mCenterContainerView.addView(newView);
		newView.setVisibility(View.VISIBLE);
		newView.startAnimation(animationIn);
		oldView.startAnimation(animationOut);
		Object title = newView.getTag();
		if (JingTools.isValidString(title)) {
			mContext.setJingTitleText(title.toString());
		}
	}
	
	public void pushChatView(ChatUserData chatUserData) {
		if (mChatViewManager == null) {
			mChatViewManager = new ChatViewManager(mContext);
		}
		pushNewView(mChatViewManager.getView(chatUserData),null);
	}
	
	public void pushNewView(final View newView,final Runnable recyleRunnable){
		if (isViewsLock || newView == mCurrentShowingView) {
			return;
		}
		isViewsLock = true;
		if (newView != null) {
			newView.setVisibility(View.VISIBLE);
		}
		final Animation animationOut = AnimationUtils.loadAnimation(mContext,
				R.anim.silde_away_to_left);
		final Animation animationIn = AnimationUtils.loadAnimation(mContext,
				R.anim.silde_in_from_right);
		final Animation backAnimationOut = AnimationUtils.loadAnimation(mContext,
				R.anim.silde_away_to_right);
		final Animation backAnimationIn = AnimationUtils.loadAnimation(mContext,
				R.anim.silde_in_from_left);
		back_buton.setVisibility(View.VISIBLE);
		menu_buton.setVisibility(View.GONE);
		final AnimationListener animationListener = new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isViewsLock = false;
				if (backAnimationOut == animation) {
					mCenterContainerView.removeView(mCurrentShowingView);
					mCurrentShowingView = mTempHideView;
					mTempHideView = null;
					mHandler.post(recyleRunnable);
					if (mCurrentShowingView != null) {
						mCurrentShowingView.setVisibility(View.VISIBLE);
					}
				}else if (animationOut == animation) {
					if (mCurrentShowingView != null) {
						mTempHideView.setVisibility(View.GONE);
					}
				}
				refreshRightButtonState();
			}
		};
		backAnimationOut.setAnimationListener(animationListener);
		animationOut.setAnimationListener(animationListener);
		back_buton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (	isViewsLock) {
					return;
				}
				((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(back_buton.getWindowToken(), 0);
				back_buton.setVisibility(View.GONE);
				menu_buton.setVisibility(View.VISIBLE);
				mCurrentShowingView.startAnimation(backAnimationOut);
				mTempHideView.startAnimation(backAnimationIn);
			}
		});
		mCenterContainerView.addView(newView);
		mTempHideView = mCurrentShowingView;
		mCurrentShowingView = newView;
		mCurrentShowingView.startAnimation(animationIn);
		mTempHideView.startAnimation(animationOut);
	}

	public TextView getTitleText() {
		return center_tilte_text;
	}
	public View getCenter_view_navigation_bar() {
		return center_view_navigation_bar;
	}

	public LinkedViewData createLinkedViewData(int type,String ouid,List dataList){
		return new LinkedViewData(type, ouid, dataList);
	}
	public LinkedViewData createLinkedViewData(int type,String ouid,ChatUserData chatUserData,List dataList){
		return new LinkedViewData(type,ouid,chatUserData, dataList);
	}
	public class LinkedViewData{
		public static final int USER_HOME_PAGE = 0;
		public static final int USER_TICKER_PAGE = 1;
		public static final int TOP_USERS = 2;
		public static final int FRIEND_LIST = 3;
		public static final int FRIEND_LIST_WITH_TITLE = 5;
		public static final int FRIEND_LIST_SEARCH = 6;
		public static final int MUSIC_LIST = 7;
		public static final int CHAT_VIEW = 8;
		public static final int USER_HOME_PAGE_TICKER = 9;
		public static final int CONVERSIATION = 10;
		public static final int FRIEND_LIST_EXTEND = 11;
		public static final int FRIEND_LIST_SNS = 12;
		public static final int RANK_LIST = 13;
		public static final int RANK_LIST_DETAILS = 15;
		public static final int MUSIC_EXPLORER = 16;
		public static final int RADIO_LIST = 17;

		private int type;
		private String ouid;
		private List dataList;
		private ChatUserData chatUserData;
		
		public LinkedViewData(int type,String ouid,List dataList) {
			this.type = type;
			this.ouid = ouid;
			this.dataList = dataList;
		}
		
		public LinkedViewData(int type,String ouid,ChatUserData chatUserData,List dataList) {
			this.type = type;
			this.ouid = ouid;
			this.dataList = dataList;
			this.chatUserData = chatUserData;
		}
		
		protected View makeView() {
			switch (type) {
			case LinkedViewData.USER_HOME_PAGE:
				return mContext.getmViewManagerLeft()
						.getLeftViewAdapter()
						.getmUserHomePageAdapter()
						.makeLinkedView(dataList, ouid);
			case LinkedViewData.USER_HOME_PAGE_TICKER:
				return mContext.getmViewManagerLeft()
						.getLeftViewAdapter()
						.getmUserHomePageAdapter()
						.makeLinkedViewTicker(dataList, ouid);
			case LinkedViewData.USER_TICKER_PAGE:
				return mContext.getmViewManagerLeft()
						.getLeftViewAdapter()
						.getmTickerAdapter()
						.initData(dataList, ouid,new DragRefreshListView(mContext));
			case LinkedViewData.TOP_USERS:
				return mContext.getmViewManagerLeft()
						.getLeftViewAdapter()
						.getmTopUserAdapter()
						.initData(dataList,new DragRefreshListView(mContext));
			case LinkedViewData.FRIEND_LIST:
				if (mFriendsViewManager == null) {
					mFriendsViewManager = new FriendsViewManager(mContext);
				}
				FrameLayout contain = new FrameLayout(mContext);
				contain.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
				contain.setBackgroundColor(0xFF343536);
				View view = mFriendsViewManager.makeLinkedView(ouid, dataList);
				try {
					contain.addView(view);
					FrameLayout.LayoutParams lp = (android.widget.FrameLayout.LayoutParams) view.getLayoutParams();
					lp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
				} catch (Exception e) {
					return view;
				}
				return contain;
			case LinkedViewData.FRIEND_LIST_WITH_TITLE:
				if (mFriendsViewManager == null) {
					mFriendsViewManager = new FriendsViewManager(mContext);
				}
				return mFriendsViewManager.getViewWithTitle();
			case LinkedViewData.FRIEND_LIST_SEARCH:
				if (mFriendsViewManager == null) {
					mFriendsViewManager = new FriendsViewManager(mContext);
				}
				return mFriendsViewManager.getFriendSearchView();
			case LinkedViewData.MUSIC_LIST:
				return mMusicViewManager.makeLinkedView(ouid, dataList);
			case LinkedViewData.CHAT_VIEW:
				if (mChatViewManager == null) {
					mChatViewManager = new ChatViewManager(mContext);
				}
				return mChatViewManager.getView(chatUserData);
			case LinkedViewData.CONVERSIATION:
				if (mChatViewManager == null) {
					mChatViewManager = new ChatViewManager(mContext);
				}
				DragRefreshListView listView = new DragRefreshListView(mContext);
				listView.setAdapter(mChatViewManager.getCoversationAdapter());
				mChatViewManager.getCoversationAdapter().setListView(listView);
				return listView;
			case LinkedViewData.FRIEND_LIST_EXTEND:
				if (mFriendsViewManager == null) {
					mFriendsViewManager = new FriendsViewManager(mContext);
				}
				return mFriendsViewManager.getExtendListView();
			case LinkedViewData.FRIEND_LIST_SNS:
				if (mFriendsViewManager == null) {
					mFriendsViewManager = new FriendsViewManager(mContext);
				}
				return mFriendsViewManager.getSnsListView();
			case LinkedViewData.RANK_LIST:
				if (mRankListAdapter == null) {
					mRankListAdapter = new RankListAdapter(mContext);
				}
				DragRefreshListView listViewRank = new DragRefreshListView(mContext);
				listViewRank.setAdapter(mRankListAdapter);
				mRankListAdapter.setListView(listViewRank);
				return listViewRank;
			case LinkedViewData.RANK_LIST_DETAILS:
				if (mFriendsViewManager == null) {
					mFriendsViewManager = new FriendsViewManager(mContext);
				}
				return mFriendsViewManager.getSnsListView();
			case LinkedViewData.MUSIC_EXPLORER:
				DragRefreshListView listViewExplorer = new DragRefreshListView(mContext);
				listViewExplorer.setAdapter(mMusicExploreManager);
				mMusicExploreManager.setListView(listViewExplorer);
				return listViewExplorer;
			case LinkedViewData.RADIO_LIST:
				return mContext.getmViewManagerLeft().getLeftViewAdapter().getmUserHomePageAdapter().getRadioListView();
			}
			return null;
		}
	}
	public void hasNewMessage(SocketPChatDTO socketPChatDTO) {
		if (mFriendsViewManager == null) {
			mFriendsViewManager = new FriendsViewManager(mContext);
		}
		if (isTalkingToThisUser(socketPChatDTO.getFuid())) {
			//正在跟此人聊天 不提示数字
			mContext.getmViewManagerCenter().getmChatViewManager().hasNewMessage(socketPChatDTO);
		}else{
			mFriendsViewManager.hasNewMessage(socketPChatDTO);
			mContext.getmViewManagerLeft().friendsNotifyChange(1);
			mContext.playNewsSound();
		}
	}
	
	public boolean isTalkingToThisUser(String ouid) {
		return mCurrentShowingView == getmChatViewManager().getmChatView()
				&& ouid.equals(mChatViewManager.getmCurrentFuid());
	}

	public void hasNewAttend2u() {
		if (mFriendsViewManager == null) {
			mFriendsViewManager = new FriendsViewManager(mContext);
		}
//		mFriendsViewManager.hasNewAttend2u();
//		mContext.getmViewManagerLeft().friendsNotifyChange(1);
	}
	
	public void clearNewChatMessage(String fuid) {
		if (mFriendsViewManager == null) {
			mFriendsViewManager = new FriendsViewManager(mContext);
		}
		Integer count = mFriendsViewManager.clearNewChatMessage(fuid);
		if (count != null && count != 0) {
			mContext.getmViewManagerLeft().friendsNotifyChange(-count);
		}
	}
	
	public View getmCurrentShowingView() {
		return mCurrentShowingView;
	}

	public void newMessageNotify(int count) {
		mNewMessageNotify += count;
		if (mNewMessageNotify > 0) {
			center_view_new_message.setVisibility(View.VISIBLE);
			center_view_new_message.setText(""+mNewMessageNotify);
		}else{
			center_view_new_message.setVisibility(View.GONE);
		}
	}

	public void showLoginGuide() {
		
	}

}
