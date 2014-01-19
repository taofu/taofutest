package com.jingfm.background_model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnBufferingUpdateListener;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.audiofx.Visualizer;
import android.media.audiofx.Visualizer.OnDataCaptureListener;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.RecoverySystem.ProgressListener;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.jingfm.MainActivity;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.ViewManager.MusicInfoListener;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.SecureCustomerAudioRule;
import com.jingfm.api.SecureCustomerWsAudioRule;
import com.jingfm.api.business.UserClickRequestApi;
import com.jingfm.api.business.UserMusicRequestApi;
import com.jingfm.api.business.UserTickerRequestApi;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.MovinfoDTO;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.MusicInfoDTO;
import com.jingfm.api.model.PlyinfoDTO;
import com.jingfm.api.model.RecommendCmbtDTO;
import com.jingfm.api.model.UserFrdDTO;
import com.jingfm.tools.JingTools;

public class PlayerManager implements OnCompletionListener,
		OnBufferingUpdateListener, OnErrorListener, OnInfoListener,
		OnPreparedListener, OnSeekCompleteListener{
	MediaPlayer mediaPlayer;

	private static final int MSG_NET_MUSIC_CHECK_LOVED 	= 0;
	private static final int MSG_NET_MUSIC_SET_LOVED 	= 1;
	private static final int MSG_NET_MUSIC_SET_HATE 		= 2;
	private static final int MSG_NET_MUSIC_SKIP_NEXT 	= 3;
	private static final int MSG_NET_PROGRESS_OVER_HALF 	= 4;
	private static final int MSG_NET_ASYNC_PLAY_NEXT     = 5;
	private static final int MSG_NET_ASYNC_PLAY_NEXT_UNLOCK     = 6;
	private static final int MSG_NET_GET_MUSIC_INFO     	= 7;
	private static final int MSG_TEST_DOUBLE_CLICK     	= 8;
	private int isMediaCenterButtonPressedCount;
	
	protected static final int DEFAULT_LENGHT_OF_TIPS_ON_FRIENDS = 13;

	protected static final int SLEEP_TIME_REFRESH = 60*1000;

	private static final int CUSTOM_NOTIFICATION_ID = 8820;

	public static final String MUSIC_CONTROLLOR_ACTION = "com.jing.player";
	public static final String MUSIC_CONTROLLOR_ACTION_Key = "MusicAction_Key";
	public static final int MUSIC_CONTROLLOR_ACTION_Play = 12100;
	public static final int MUSIC_CONTROLLOR_ACTION_Pause = 12101;
	public static final int MUSIC_CONTROLLOR_ACTION_Next = 12102;
	public static final int MUSIC_CONTROLLOR_ACTION_Fav = 12103;
	public static final int MUSIC_CONTROLLOR_ACTION_Hate = 12105;
	public static final int MUSIC_CONTROLLOR_ACTION_CenterButton = 12106;
	
	public static final long DEFAULT_NEXT_LOCK_TIME = 1200;

	private boolean isUiLock;
	
	private static PlayerManager instance = new PlayerManager();
	private ArrayList<List<MusicDTO>> mPlayLists = new ArrayList<List<MusicDTO>>();
	private Set<PlayerStateChangeListener> playerStateChangeListenerSet = new HashSet<PlayerStateChangeListener>();
	private Set<ProgressListener> progressListenerSet = new HashSet<ProgressListener>();
	private LinkedList<PlyinfoDTO> tipsList = new LinkedList<PlyinfoDTO>();
	private int mCurrentListIndex;
	private int mCurrentMediaIndex;
	private int[] lastPlayingInMainList = { 0, 0 };
	private Context mContext;
	private PlayListOverListener mPlayListOverListener;
	private PlayListOverListener lastPlayListOverListener;
	private MusicDTO mLastDTO;
	private boolean isHighMode;
	private Timer mProgressTimer;
	protected int mProgress;
	private Visualizer mVisualizer;
	private VisualizerListener mVisualizerListener;
	private String mMainCmbt;
	private String mSubCmbt;
	private Integer mMainM = 0;
	private Integer mSubM = 0;
	private String mLastPostCmbt = "";
	private String mMoodids;
	private int mFavCount;
	private int mHateCount;
	private boolean isLoved;
	private boolean hasSentOverHalf;
	protected Handler mNetworkHandler;
	protected ShowTipsListener mShowTipsListener;
	private boolean isVisualizerDisabled;

	private MusicInfoDTO mMusicInfoDTO;

	private boolean isPrepared;

	private List<UserFrdDTO> mUserFrdDTOList;

	private StartNewListener mStartNewListener;

	protected boolean postOnStart;
	private boolean isHostMode;

	private int isNeedSeekTimeOnPerpared;
	private boolean isNeedPauseOnPerpared;

	private int mSleepTime;

	private Timer mSleepTimer;

	protected Notification mNotification;

	private MusicControllor mMusicControllor;

	public int buttonNum;

	private boolean isDisableNotify;

	private boolean isRelease = true;

	private Options options;

	private boolean isHighModeFromSetting;

	private int onErrorPst;

	private Integer mAlikeTid = 0;

	protected boolean donotPostCmbtOnStart;

	protected boolean playNextLocked;

	private boolean mErrorPlayNext;

	private boolean notificationIsShowing;

	private boolean notificationIsShowingPlay;

	private MusicDTO mLastHateMusicInFollow;

	
	private boolean isFollowingOther;

	private boolean isSecureCustomerWsAudioMode;

	private LrcGetListener mLrcGetListener;

	private MyPhoneStateListener mMyPhoneStateListener;

	private boolean mLastPlayState;

	private boolean mLastLovedState;

	private boolean isCallingPause;

	private Set<String> mVisualizerBlackList = new HashSet<String>();

	private ComponentName mComponentName;

	private MusicDTO mWaitingDTO;

	private int mWaitingPst = -1;

	private boolean isNeedWaiting;
	private boolean isLocalMusicMode;
	private MusicInfoListener mMusicInfoListener;
	
	{
		options = new BitmapFactory.Options();
		options.inPurgeable = true;
		options.inSampleSize = 1;
		mVisualizerBlackList.add("HTC".toLowerCase());
		mVisualizerBlackList.add("ZTE".toLowerCase());
		mVisualizerBlackList.add("HUAWEI".toLowerCase());
	}
	
	public static PlayerManager getInstance() {
		return instance;
	}

	private PlayerManager() {
		mPlayLists.add(new ArrayList<MusicDTO>());
		mMyPhoneStateListener = new MyPhoneStateListener();
		initNetworkHandler();
	}
	
	public MyPhoneStateListener getMyPhoneStateListener() {
		return mMyPhoneStateListener;
	}
	
	public void initNotifyControl(Context context){
		registerMusicControllerReceiver(context);
		mNotification = new Notification(
				R.drawable.ic_notification, "",
				System.currentTimeMillis());
		mNotification.flags = Notification.FLAG_NO_CLEAR;

		Intent mainIntent = new Intent(context, MainActivity.class);
		PendingIntent pendingMainIntent = PendingIntent.getActivity(
				context, 0, mainIntent, 0);
		mNotification.contentIntent = pendingMainIntent;
		
		// Remoteview and intent for my button
		RemoteViews notificationView = new RemoteViews(context
				.getPackageName(), R.layout.notification_view);
		mNotification.contentView = notificationView;
		// =================================================
		Intent intentPlay = new Intent(MUSIC_CONTROLLOR_ACTION);
		Bundle bundle0 = new Bundle();  
	    bundle0.putInt(MUSIC_CONTROLLOR_ACTION_Key,MUSIC_CONTROLLOR_ACTION_Play);  
	    intentPlay.putExtras(bundle0);  
		PendingIntent pendingPlayIntent = PendingIntent.getBroadcast(context, MUSIC_CONTROLLOR_ACTION_Play,
				intentPlay, PendingIntent.FLAG_UPDATE_CURRENT);
		notificationView.setOnClickPendingIntent(R.id.button_play,
				pendingPlayIntent);
		notificationView.setOnClickPendingIntent(R.id.button_pause,
				pendingPlayIntent);
		// =================================================
		Intent intentNext = new Intent(MUSIC_CONTROLLOR_ACTION);
		Bundle bundle1 = new Bundle();  
		bundle1.putInt(MUSIC_CONTROLLOR_ACTION_Key,MUSIC_CONTROLLOR_ACTION_Next);  
		intentNext.putExtras(bundle1);  
		PendingIntent pendingintentNextIntent = PendingIntent.getBroadcast(context, MUSIC_CONTROLLOR_ACTION_Next,
				intentNext, PendingIntent.FLAG_UPDATE_CURRENT);
		notificationView.setOnClickPendingIntent(R.id.button_next,
				pendingintentNextIntent);
		// =================================================
		Intent intentFav = new Intent(MUSIC_CONTROLLOR_ACTION);
		Bundle bundle2 = new Bundle();  
		bundle2.putInt(MUSIC_CONTROLLOR_ACTION_Key,MUSIC_CONTROLLOR_ACTION_Fav);  
		intentFav.putExtras(bundle2);  
		PendingIntent pendingintentFavIntent = PendingIntent.getBroadcast(context, MUSIC_CONTROLLOR_ACTION_Fav,
				intentFav, PendingIntent.FLAG_UPDATE_CURRENT);
		notificationView.setOnClickPendingIntent(R.id.button_fav,
				pendingintentFavIntent);
		notificationView.setOnClickPendingIntent(R.id.button_faved,
				pendingintentFavIntent);
		// =================================================
		Intent intentHate = new Intent(MUSIC_CONTROLLOR_ACTION);
		Bundle bundle3 = new Bundle();  
		bundle3.putInt(MUSIC_CONTROLLOR_ACTION_Key,MUSIC_CONTROLLOR_ACTION_Hate);  
		intentHate.putExtras(bundle3);  
		PendingIntent pendingintentHateIntent = PendingIntent.getBroadcast(context, MUSIC_CONTROLLOR_ACTION_Hate,
				intentHate, PendingIntent.FLAG_UPDATE_CURRENT);
		notificationView.setOnClickPendingIntent(R.id.button_hate,
				pendingintentHateIntent);
		// =================================================
		Intent iconIntent = new Intent(context,
				MainActivity.class);
		PendingIntent pendingIntentIcon = PendingIntent.getActivity(
				context, 0, iconIntent, 0);
		notificationView.setOnClickPendingIntent(R.id.button_icon,
				pendingIntentIcon);
	}
	
	public void hideNotifyView(Context context) {
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.cancel(CUSTOM_NOTIFICATION_ID);
		notificationIsShowing = false;
	}

	public void showNotifyView(Context context) {
		if (isDisableNotify || LoginRegisterManager.getInstance().loginDataDTO == null ||mNotification == null) {
			return;
		}
		refreshNotificationState(context); 
	}
	
	public void enableNotify() {
		isDisableNotify = false;
	}
	public void disableNotify() {
		isDisableNotify = true;
	}
	
	public synchronized void refreshNotificationState(Context context) {
		boolean playState = isPlaying();
		if (notificationIsShowing 
				&& mLastLovedState == isLoved
				&& mLastPlayState == playState) {
			return;
		}
		mLastLovedState = isLoved;
		mLastPlayState = playState;
		mNotification.contentView.setViewVisibility(R.id.button_fav, isLoved?View.GONE:View.VISIBLE);
		mNotification.contentView.setViewVisibility(R.id.button_faved, !isLoved?View.GONE:View.VISIBLE);
		mNotification.contentView.setViewVisibility(R.id.button_play, playState?View.GONE:View.VISIBLE);
		mNotification.contentView.setViewVisibility(R.id.button_pause, !playState?View.GONE:View.VISIBLE);
		NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
		notificationManager.notify(CUSTOM_NOTIFICATION_ID,mNotification);
		notificationIsShowingPlay = isPlaying();
		notificationIsShowing = true;
	}
	
	private void registerMusicControllerReceiver(Context context) {
		mMusicControllor = new MusicControllor();
		IntentFilter filter = new IntentFilter(MUSIC_CONTROLLOR_ACTION);
		filter.addAction(Intent.ACTION_HEADSET_PLUG);
		context.registerReceiver(mMusicControllor, filter);
	}
	
	public void unregisterMusicControllerReceiver(Context context) {
		if (mMusicControllor != null) {
			context.unregisterReceiver(mMusicControllor);
		}
	}
	
	private void initNetworkHandler() {
		new Thread(){
			@Override
			public void run() {
				Looper.prepare();
		        mNetworkHandler = new Handler(){

						@Override
		        		public void handleMessage(Message msg) {
		        			super.handleMessage(msg);
		        			HashMap<Object, Object> params = new HashMap<Object, Object>();
		        		
							switch (msg.what) {
							case MSG_NET_MUSIC_CHECK_LOVED:
								break;
							case MSG_NET_MUSIC_SET_LOVED:
								params.put("uid", ""+LoginRegisterManager.getInstance().getLoginDataDTO().getUsr().getId());
								params.put("tid", ""+((MusicDTO)msg.obj).getTid());
								params.put("c",""+(mFavCount+1));
								if (isMainListPlaying()) {
									params.put("moodTagIds", mMoodids);
									params.put("cmbt", mMainCmbt);
									params.put("M",""+mMainM);
								}else{
									params.put("cmbt", mSubCmbt);
									params.put("M",""+mSubM);
								}
								if (msg.arg1 != 0) {
									params.put("ouid", ""+msg.arg1);
								}
								ResultResponse<ListResult<MusicDTO>> res = UserMusicRequestApi.musicLoveAct(params);
								if (res.isSuccess()) {
									List<MusicDTO> musicDTOList = res.getResult().getItems();
									if (!musicDTOList.isEmpty()) {
										List<MusicDTO> mainList = mPlayLists.get(0);
										mainList.clear();
										mainList.add(mLastDTO);
										mainList.addAll(musicDTOList);
										mFavCount++;
									}
								}
								isUiLock = false;
								notifyAllListener();
								break;
							case MSG_NET_MUSIC_SET_HATE:
								if (msg.obj == null) {
									return;
								}
								params.put("uid", LoginRegisterManager.getInstance().getLoginDataDTO().getUsr().getId());
								params.put("tid", ""+((MusicDTO)msg.obj).getTid());
								params.put("c", ""+(mHateCount + 1));
								if (isMainListPlaying()) {
									params.put("moodTagIds", mMoodids);
									params.put("cmbt", mMainCmbt);
									params.put("M",""+mMainM);
								}else{
									params.put("cmbt", mSubCmbt);
									params.put("M",""+mSubM);
								}
								ResultResponse<ListResult<MusicDTO>> ress = UserMusicRequestApi.musicHateAct(params);
								if (ress.isSuccess()) {
									List<MusicDTO> musicDTOList = ress.getResult().getItems();
									if (!musicDTOList.isEmpty()) {
										mFavCount++;
									}
								}
								isUiLock = false;
								break;
							case MSG_NET_MUSIC_SKIP_NEXT:
								if(msg.obj == null
									||	((Object[])msg.obj)[0] == null
									|| LoginRegisterManager.getInstance().getLoginDataDTO().getUsr().isGuest()){
									break;
								}
								params.put("uid", LoginRegisterManager.getInstance().getLoginDataDTO().getUsr().getId());
								params.put("tid", ((MusicDTO)((Object[])msg.obj)[0]).getTid());
								params.put("half", "" + (((Object[])msg.obj)[1]));
								params.put("next", "" + (msg.arg1 > 10));
								if (!isLocalMusicMode && msg.arg1 > 10) {
									HashMap<Object, Object> tmpParams = new HashMap<Object, Object>();
									tmpParams.put("uid", params.get("uid"));
									tmpParams.put("tid", params.get("tid"));
									tmpParams.put("d", ""+msg.arg1);
									postCurrentMusicDTODuration(tmpParams);
								}
								UserMusicRequestApi.musicSkipNextAct(params);
								break;
							case MSG_NET_PROGRESS_OVER_HALF:
								try {
									params.put("uid", LoginRegisterManager.getInstance().getLoginDataDTO().getUsr().getId());
									params.put("tid", ((MusicDTO)msg.obj).getTid());
									UserMusicRequestApi.musicHalfHeardAct(params);
									UserMusicRequestApi.musicHeardAct(params);
								} catch (Exception e) {
								}
								break;
							case MSG_NET_ASYNC_PLAY_NEXT:
								playNextLocked = true;
								removeMessages(MSG_NET_ASYNC_PLAY_NEXT);
								sendEmptyMessageDelayed(MSG_NET_ASYNC_PLAY_NEXT_UNLOCK, DEFAULT_NEXT_LOCK_TIME);
								doPlayNextMusic();
								break;
							case MSG_NET_ASYNC_PLAY_NEXT_UNLOCK:
								playNextLocked = false;
								removeMessages(MSG_NET_ASYNC_PLAY_NEXT_UNLOCK);
								break;
							case MSG_NET_GET_MUSIC_INFO:
								MusicDTO musicDTO = (MusicDTO) msg.obj;
								getMusicInfo(musicDTO);
								break;
							case MSG_TEST_DOUBLE_CLICK:
								if (isMediaCenterButtonPressedCount == 1) {
									Intent intent = new Intent(
											PlayerManager.MUSIC_CONTROLLOR_ACTION);
									Bundle bundle = new Bundle();
									bundle.putInt(PlayerManager.MUSIC_CONTROLLOR_ACTION_Key,
											PlayerManager.MUSIC_CONTROLLOR_ACTION_Play);
									intent.putExtras(bundle);
									((Context)msg.obj).sendBroadcast(intent);
								}
								isMediaCenterButtonPressedCount = 0;
								break;
							}
		        		}

						private void doPlayNextMusic() {
							/*if (mMusicInfoListener != null) {
								mMusicInfoListener.clearMusicInfoDTO();
							}*/
							onErrorPst = 0;
							try {
								if (mediaPlayer != null && isPrepared() && mediaPlayer.isLooping()) {
									mediaPlayer.seekTo(0);
									mediaPlayer.start();
									notifyAllListener();
									return;
								}else{
									release();
								}
							} catch (Exception e) {
								if (e != null) {
									e.printStackTrace();
								}
							}
							mCurrentMediaIndex += 1;
							final List<MusicDTO> list = mPlayLists.get(mCurrentListIndex);
							if (mCurrentMediaIndex < list.size()) {
								startPlayList();
							} else {
								if (mPlayListOverListener != null) {
									mPlayListOverListener.onPlayListOver(PlayerManager.this);
								} else {
									mCurrentMediaIndex = 0;
									startPlayList();
								}
							}
							

						}
		        };
		        Looper.loop();
			}
		}.start();
	}

	public void setmContext(Context context) {
		this.mContext = context;
	}

	public void setMainList(List<MusicDTO> playList) {
		lastPlayingInMainList[0] = 0;
		mPlayLists.set(0, playList);
	}
	public void addMainList(List<MusicDTO> playList) {
		lastPlayingInMainList[0] = 0;
		if (mPlayLists.get(0) != null) {
			mPlayLists.get(0).clear();
			if (mLastDTO != null) {
				mPlayLists.get(0).add(mLastDTO);
			}
			mPlayLists.get(0).addAll(playList);
		}
	}

	public List<MusicDTO> getMainList() {
		return mPlayLists.get(0);
	}

	public void playUniqueSubList(List<MusicDTO> playList,
			PlayListOverListener playListOverListener) {
		setUniqueSubList(playList);
		startPlayList(0, 1, playListOverListener);
	}

	public void setUniqueSubList(List<MusicDTO> playList) {
		clearSubList();
		addSubList(playList);
	}

	public void clearSubList() {
		List<MusicDTO> playList = mPlayLists.get(0);
		mPlayLists.clear();
		mPlayLists.add(playList);
	}

	public void addSubList(List<MusicDTO> playList) {
		mPlayLists.add(playList);
	}

	public boolean startPlayList() {
		try {
			return startPlay(
					mPlayLists.get(mCurrentListIndex).get(mCurrentMediaIndex),
					isHighModeFromSetting);
		} catch (Exception e) {
			if (e != null) {
				e.printStackTrace();
			}
			return false;
		}
	}
	
	public void setIsHighMode(boolean b) {
		isHighModeFromSetting = b;
		isHighMode = b;
	}

	private synchronized boolean startPlay(final MusicDTO musicDTO, boolean mode) {
		if (musicDTO == null 
				|| LoginRegisterManager.getInstance().getLoginDataDTO() == null) {
			return false;
		}
		if (!musicDTO.equals(mLastDTO)) {
			isLoved = false;
		}
		mLastDTO = musicDTO;
		if (isNeedWaiting) {
			musicWaiting();
			return false;
		}
		if (!LoginRegisterManager.getInstance().getLoginDataDTO().getUsr().isGuest()) {
			mNetworkHandler.removeMessages(MSG_NET_GET_MUSIC_INFO);
			Message msg = new Message(); 
			msg.what = MSG_NET_GET_MUSIC_INFO;
			msg.obj = musicDTO;
			mNetworkHandler.sendMessage(msg);
		}
		hasSentOverHalf = false;
		String mid = musicDTO.getMid();
		isHighMode = mode;
		File file = LocalCacheManager.getInstance().musicFileByTid(musicDTO.getTid());
		if (file != null && file.exists() && file.canRead() ) {
			startPlay(Uri.fromFile(file));
		}else{
			if (isSecureCustomerWsAudioMode) {
				if (mode) {
					startPlay(Uri.parse(SecureCustomerWsAudioRule.ID2URL(mid)));
				} else {
					startPlay(Uri.parse(SecureCustomerWsAudioRule.ID2URL(mid, "MM")));
				}
			}else{
				if (mode) {
					startPlay(Uri.parse(SecureCustomerAudioRule.ID2URL(mid)));
				} else {
					startPlay(Uri.parse(SecureCustomerAudioRule.ID2URL(mid, "MM")));
				}
			}
		}
		notifyAllListener();
		return false;
	}

	public void postCurrentMusicDTODuration(final HashMap<Object, Object> params) {
		new Thread(){
			public void run() {
				UserClickRequestApi.clickPlayduration(params);
			};
		}.start();
	}
	public void postCurrentMusicDTO(final HashMap<Object, Object> params) {
		if (mCurrentListIndex != 0) {
			return;
		}
		ResultResponse<String> rs = UserClickRequestApi.clickPlaydata(params);
	}

	public void addListener(PlayerStateChangeListener listener) {
		playerStateChangeListenerSet.add(listener);
		listener.onPlayerStateChange(this);
	}

	public void removeListener(PlayerStateChangeListener listener) {
		playerStateChangeListenerSet.remove(listener);
	}

	public	void notifyAllListener() {
		if (isPlaying()) {
			startProgressTimer();
		} else {
			stopProgressTimer();
		}
		if (notificationIsShowing && notificationIsShowingPlay != isPlaying()) {
			refreshNotificationState(mContext);
		}
		for (PlayerStateChangeListener listener : playerStateChangeListenerSet) {
			listener.onPlayerStateChange(this);
		}
	}

	private void startProgressTimer() {
		if (mProgressTimer != null) {
			return;
		}
		mProgressTimer = new Timer();
		mProgressTimer.schedule(new TimerTask() {

			@Override
			public void run() {
				if (mediaPlayer != null && isPrepared()) {
					try {
						int currentPst = musicPst();
						int progress = currentPst * 100
								/ mediaPlayer.getDuration();
						if (currentPst > 10 * 1000) {
							if (!postOnStart) {
								HashMap<Object, Object> params = new HashMap<Object, Object>();
								params.put("uid", ""+LoginRegisterManager.getInstance().getLoginDataDTO().getUsr().getId());
								params.put("tid", mLastDTO.getTid());
								params.put("cmbt", mMainCmbt);
								params.put("ct", "" + currentPst/1000);
								postCurrentMusicDTO(params);
								postOnStart = true;
							}
							if (donotPostCmbtOnStart && !mLastPostCmbt.equals(mMainCmbt)) {
								mLastPostCmbt = mMainCmbt;
								if (!mLastPostCmbt.equals(LoginRegisterManager.getInstance().getLoginDataDTO().getPld().getCmbt())) {
									HashMap<Object, Object> params = new HashMap<Object, Object>();
									params.put("uid", ""+LoginRegisterManager.getInstance().getLoginDataDTO().getUsr().getId());
									params.put("content", mLastPostCmbt);
									if (mAlikeTid != 0) {
										params.put("tid", mAlikeTid);
									}
									ResultResponse<RecommendCmbtDTO> rs = UserTickerRequestApi.postCmbt(params);
									System.out.println("rs: " + rs);
								}
							}
							if (!donotPostCmbtOnStart) {
								donotPostCmbtOnStart = true;
							}
							while (SettingManager.getInstance().isNotifyInstruments() && mShowTipsListener != null && !tipsList.isEmpty() && isPlaying()) {
								PlyinfoDTO plyinfoDTO = tipsList.get(0);
								int timing = plyinfoDTO.getTime() - currentPst/1000;
								if (Math.abs(timing) < 1.5) {
									plyinfoDTO = tipsList.poll();
									mShowTipsListener.onShowTips(plyinfoDTO.getAction() + "从这里开始");
								}else{
									if (timing < 0) {
										tipsList.poll();
										continue;
									}
								}
								break;
							}
						}
						
						
						if (mProgress != progress) {
							mProgress = progress;
							if (	!hasSentOverHalf && mProgress > 50) {
								hasSentOverHalf = true;
								Message msg = new Message();
								msg.what = MSG_NET_PROGRESS_OVER_HALF;
								msg.obj = mLastDTO;
								mNetworkHandler.sendMessage(msg);
							}
							for (ProgressListener listener : progressListenerSet) {
								listener.onProgress(mProgress);
							}
						}
					} catch (Exception e) {
						if (e != null) {
							e.printStackTrace();
						}
					}
				}
			}
		}, 0, 1000);
	}

	private void stopProgressTimer() {
		if (mProgressTimer != null) {
			mProgressTimer.cancel();
		}
		mProgressTimer = null;
	}

	public MusicDTO getLastDTO() {
		return mLastDTO;
	}

	public boolean startPlayList(int mediaIndex) {
		mCurrentMediaIndex = mediaIndex;
		startPlayList();
		return true;
	}

	public void backToMainList() {
		startPlayList(lastPlayingInMainList[0],0,lastPlayListOverListener);
	}
	
	public boolean startPlayList(int mediaIndex, int listIndex,
			PlayListOverListener playListOverListener) {
		if (mCurrentListIndex != mediaIndex) {
			mHateCount = 0;
			mFavCount = 0;
		}
		if (isMainListPlaying() && listIndex != 0) {
			lastPlayingInMainList[0] = mCurrentMediaIndex;
			lastPlayingInMainList[1] = musicPst();
			Log.i("kid_debug","startPlayList lastPlayingInMainList[1] : " + lastPlayingInMainList[1]);
			if (mPlayListOverListener != null) {
				lastPlayListOverListener = mPlayListOverListener;
			}
		}
		mPlayListOverListener = playListOverListener;
		mCurrentListIndex = listIndex;
		mCurrentMediaIndex = mediaIndex;
		return startPlayList();
	}

	public synchronized boolean startPlay(Uri uri) {
		if (mStartNewListener != null) {
			mStartNewListener.onStartNewListener(this);
		}
		if (mediaPlayer != null) {
			mediaPlayer.release();
		}
		isRelease =  false;
		isPrepared = false;
		isNeedSeekTimeOnPerpared = 0; 
		isNeedPauseOnPerpared = false;
		postOnStart = false;
		try {
			mediaPlayer = new MediaPlayer();
			mediaPlayer.setDataSource(mContext, uri);
		} catch (Exception e) {
			if (e != null) {
				e.printStackTrace();
			}
		}
		if (mediaPlayer == null) {
			return false;
		}
		try {
			mediaPlayer.prepareAsync();
		} catch (Exception e) {
			if (e != null) {
				e.printStackTrace();
			}
			return false;
		}
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnBufferingUpdateListener(this);
		mediaPlayer.setOnCompletionListener(this);
		mediaPlayer.setOnErrorListener(this);
		mediaPlayer.setOnInfoListener(this);
		mediaPlayer.setOnPreparedListener(this);
		mediaPlayer.setOnSeekCompleteListener(this);
		return true;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		if (!isLocalMusicMode && !LoginRegisterManager.getInstance().getLoginDataDTO().getUsr().isGuest()) {
			HashMap<Object, Object> params = new HashMap<Object, Object>();
			params.put("uid",LoginRegisterManager.getInstance().getLoginDataDTO().getUsr().getId());
			params.put("tid", mLastDTO.getTid());
			params.put("d", ""+mLastDTO.getD());
			postCurrentMusicDTODuration(params);
		}
		if (!isFollowingOther) {
			playNext(true);
		}
	}

	public void playNext(boolean needSendSkip) {
		if (isFollowingOther) {
			return;
		}
		if(playNextLocked){
			return;
		}
		if (needSendSkip) {
			Message msg = new Message();
			msg.what = MSG_NET_MUSIC_SKIP_NEXT;
			int duration = 0;
			try {
				duration = Integer.parseInt(mLastDTO.getD());
			} catch (Exception e) {
				if (e != null) {
					e.printStackTrace();
				}
			}
			if (duration <= 0 && mediaPlayer != null && isPrepared()) {
				duration = mediaPlayer.getDuration()/1000;
			}
			msg.arg1 = musicPst()/1000;
			msg.obj = new Object[]{mLastDTO,new Boolean(hasSentOverHalf)};
			mNetworkHandler.sendEmptyMessage(MSG_NET_ASYNC_PLAY_NEXT);
			mNetworkHandler.sendMessage(msg);
		}else{
			mNetworkHandler.sendEmptyMessage(MSG_NET_ASYNC_PLAY_NEXT);
		}
	}

	public void musicLooping(boolean looping) {
		if (mediaPlayer != null && isPrepared()) {
			if (looping != isLooping()) {
				mediaPlayer.setLooping(looping);
				notifyAllListener();
			}
		}
	}
	
	public boolean isMainListPlaying(){
		return mCurrentListIndex == 0;
	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		isPrepared = true;
		mp.start();
		if (lastPlayingInMainList[1] != 0 && isMainListPlaying()) {
			mp.seekTo(lastPlayingInMainList[1]);
			onErrorPst = lastPlayingInMainList[1];
			lastPlayingInMainList[1] = 0;
		}
		if (isNeedSeekTimeOnPerpared > 0) {
			mp.seekTo(isNeedSeekTimeOnPerpared*1000);
			isNeedSeekTimeOnPerpared = 0;
		}
		if (mWaitingPst > 0) {
			mp.seekTo(mWaitingPst);
			mWaitingPst = -1;
		}
		if (isNeedPauseOnPerpared){
			mp.pause();
		}
		notifyAllListener();
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		if (what == -38 && extra == 0) { //格式可能不兼容，忽略
			return false;
		}
		if (mLastDTO != null) {
			String mid = mLastDTO.getMid();
		}
		if (mp.equals(mediaPlayer)) {
			lastPlayingInMainList[1] = onErrorPst;
			if (donotPostCmbtOnStart) {
				
			}
			if (!isVisualizerDisabled) {						//波表出错
				isVisualizerDisabled = true;
				startPlay(mLastDTO, isHighModeFromSetting);
			}else{
				if (isHighMode) { //高音质出错
					if (isSecureCustomerWsAudioMode) { 		//备用高音质出错
						isSecureCustomerWsAudioMode = false;	//启用首选低音质
						startPlay(mLastDTO, false);		
					}else{									//首选高音质出错
						isSecureCustomerWsAudioMode = true; 	//启用备用高音质
						startPlay(mLastDTO,isHighMode);
					}
				}else{	//低音质出错
					if (isSecureCustomerWsAudioMode) { 		//备用低音质出错
						if (!mErrorPlayNext) {
							mErrorPlayNext = true;
							playNext(false);
						}
					}else{
						isSecureCustomerWsAudioMode = true;	//启用首选低音质
						startPlay(mLastDTO, false);		
					}
				}
			}
		}
		return true;
	}

	@Override
	public void onBufferingUpdate(MediaPlayer mp, int percent) {
	}
	
	public boolean isPrepared(){
		return isPrepared;
	}
	
	public boolean isLooping(){
		if (mediaPlayer != null && isPrepared()) {
			try {
				return mediaPlayer.isLooping();
			} catch (Exception e) {
				if (e != null) {
					e.printStackTrace();
				}
				return false;
			}
		} else {
			return false;
		}
	}
	
	
	public boolean isPlaying() {
		if (mediaPlayer != null && isPrepared()) {
			try {
				return mediaPlayer.isPlaying();
			} catch (Exception e) {
				if (e != null) {
					e.printStackTrace();
				}
				return false;
			}
		} else {
			return false;
		}
	}

	public void pause() {
		if (mediaPlayer == null || !isPrepared()) {
			return;
		}
		try {
			mediaPlayer.pause();
		} catch (Exception e) {
			if (e != null) {
				e.printStackTrace();
			}
		}
		notifyAllListener();
	}

	public void start() {
		if (mediaPlayer == null || !isPrepared()) {
			return;
		}
		try {
			if (!isPlaying()) {
				mediaPlayer.start();
			}
		} catch (Exception e) {
			if (e != null) {
				e.printStackTrace();
			}
		}
		notifyAllListener();
	}

	public int musicPst() {
		if (mediaPlayer != null && isPrepared()) {
			try {
				return mediaPlayer.getCurrentPosition();
			} catch (Exception e) {
				if (e != null) {
					e.printStackTrace();
				}
			}
		}
		return 0;
	}

	public void startMainList(PlayListOverListener playListOverListener) {
		if (playListOverListener != null) {
			mPlayListOverListener = playListOverListener;
		}
		startPlayList(0, 0, mPlayListOverListener);
	}

	public interface PlayListOverListener {
		public void onPlayListOver(PlayerManager pm);
		public void setIndexOfServer(int indexOfServer);
		public int getIndexOfServer();
	}

	public interface PlayerStateChangeListener {
		public void onPlayerStateChange(PlayerManager pm);
	}
	public interface StartNewListener {
		public void onStartNewListener(PlayerManager pm);
	}

	public void addProgressListener(ProgressListener listener) {
		progressListenerSet.add(listener);
	}

	public void removeProgressListener(ProgressListener listener) {
		progressListenerSet.remove(listener);
	}

	public VisualizerListener getmVisualizerListener() {
		return mVisualizerListener;
	}

	public void setmVisualizerListener(VisualizerListener mVisualizerListener) {
		if (mVisualizer != null) {
			mVisualizer.setEnabled(mVisualizerListener != null);
		}
		this.mVisualizerListener = mVisualizerListener;
	}

	public interface VisualizerListener {
		public void updateVisualizer(byte[] data);
	}

	public void release() {
		mLastDTO = null;
		if (mediaPlayer != null) {
			mediaPlayer.release();
			mediaPlayer = null;
		}
		isRelease = true;
		notifyAllListener();
	}

	protected synchronized void getMusicInfo(MusicDTO musicDTO) {
		mMusicInfoDTO = null;
		HashMap<Object, Object> params = new HashMap<Object, Object>();
		try {
			params.put("uid", ""+LoginRegisterManager.getInstance().getLoginDataDTO().getUsr().getId());
		} catch (Exception e) {
			if (e != null) {
				e.printStackTrace();
			}
			return;
		}
		params.put("tid", musicDTO.getTid());
		ResultResponse<MusicInfoDTO> rs = UserMusicRequestApi.fetchTrackInfos(params);
		if (rs != null && rs.isSuccess()) {
			mMusicInfoDTO = rs.getResult();
			if (mLrcGetListener != null) {
				mLrcGetListener.onNewLrc(musicDTO,mMusicInfoDTO.isLrc());
			}
			if ("l".equals(mMusicInfoDTO.getLvd())) {
				isLoved = true;
			} else if ("h".equals(mMusicInfoDTO.getLvd())) {
				isLoved = false;
			} else if ("n".equals(mMusicInfoDTO.getLvd())) {
				isLoved = false;
			}
			notifyAllListener();
		}
		if (mMusicInfoListener != null) {
			mMusicInfoListener.refreshMusicInfoDTO(mLastDTO,mMusicInfoDTO,mUserFrdDTOList);
		}
		mUserFrdDTOList = null;
		ResultResponse<ListResult<UserFrdDTO>> rs1 = UserMusicRequestApi.fetchMusicFrdlvd(params);
		if (rs1.isSuccess()) {
			mUserFrdDTOList = rs1.getResult().getItems();
			notifyAllListener();
		}
		if (mMusicInfoListener != null) {
			mMusicInfoListener.refreshMusicInfoDTO(mLastDTO,mMusicInfoDTO,mUserFrdDTOList);
		}
	}
	
	private void showCmpsInfo(Map<String, String> map) {
		/* composers 作曲 songwriters 作词 arrangers 编曲 producers 制作人 */
		if (!JingTools.isValidString(mMainCmbt)) {
			return;
		}
		HashMap<String, List<String>> keyMap = new HashMap<String,List<String>>();
		String value = map.get("composers");
		if (JingTools.isValidString(value)) {
			if (mMainCmbt.contains(value)) {
				List<String> list = keyMap.get(value);
				if (list == null) {
					list = new ArrayList<String>();
					keyMap.put(value, list);
				}
				list.add("作曲");
			}
		}
		value = map.get("songwriters");
		if (JingTools.isValidString(value)) {
			if (mMainCmbt.contains(value)) {
				List<String> list = keyMap.get(value);
				if (list == null) {
					list = new ArrayList<String>();
					keyMap.put(value, list);
				}
				list.add("作词");
			}
		}
		value = map.get("arrangers");
		if (JingTools.isValidString(value)) {
			if (mMainCmbt.contains(value)) {
				List<String> list = keyMap.get(value);
				if (list == null) {
					list = new ArrayList<String>();
					keyMap.put(value, list);
				}
				list.add("编曲");
			}
		}
		value = map.get("producers");
		if (JingTools.isValidString(value)) {
			if (mMainCmbt.contains(value)) {
				List<String> list = keyMap.get(value);
				if (list == null) {
					list = new ArrayList<String>();
					keyMap.put(value, list);
				}
				list.add("制作人");
			}
		}
		if (keyMap.keySet().isEmpty()) {
			return;
		}
		String toastString = "这首歌是";
		for (String key : keyMap.keySet()) {
			toastString += " " + key +",";
			List<String> list = keyMap.get(key);
			for (int i = 0; i < list.size(); i++) {
				if (i != 0) {
					toastString += "、";
				}
				toastString +=  list.get(i); 
			}
		}
		if (mShowTipsListener != null) {
			mShowTipsListener.onShowTips(toastString);
		}
	}

	public MusicInfoDTO getmMusicInfoDTO() {
		return mMusicInfoDTO;
	}
	
	/**
	 * 喜欢歌曲动作触发
	 * @param params uid tid cmbt moodTagIds c ouid
	 * cmbt 当前播放序列的搜索条件
	 * moodTagIds 当前播放序列的搜索条件包含的心情的tagid
	 * c 本次搜索搜索序列中第几次喜欢歌曲， 参数不传=0
	 * ouid 谁喜欢的此首歌曲 限于ticker中看见别人喜欢的歌曲 点喜欢后 ouid=别人的id
	 * @return 可能会存在播放序列
	 */
	public boolean musicFav(int ouid) {
		if (!isUiLock) {
			isUiLock =true;
			isLoved = !isLoved;
//			if (isLoved) {
				Message msg = new Message();
				msg.what = MSG_NET_MUSIC_SET_LOVED;
				msg.arg1 = ouid;
				msg.obj = mLastDTO;
				mNetworkHandler.removeMessages(msg.what, msg.obj);
				mNetworkHandler.sendMessage(msg);
//			}else{
//				isUiLock = false;
//			}
		}
		return isLoved;
	}

	/**
	 * 喜欢歌曲动作触发
	 * @param params uid tid cmbt c
	 * cmbt 当前播放序列的搜索条件
	 * c 本次搜索搜索序列中第几次讨厌歌曲， 参数不传=0
	 * @return 可能会存在播放序列
	 */
	public void musicHate() {
		if (!isUiLock) {
			isUiLock =true;
			if (isFollowingOther) {
				if (mLastHateMusicInFollow != mLastDTO) {
					mLastHateMusicInFollow = mLastDTO;
					new Thread(){
						@Override
						public void run() {
							HashMap<Object, Object> params = new HashMap<Object, Object>();
							params.put("uid", LoginRegisterManager.getInstance().getLoginDataDTO().getUsr().getId());
							params.put("tid", ""+mLastDTO.getTid());
							params.put("cmbt", Constants.UNKNOWN_CMBT_VALUE);
							params.put("M",""+Constants.UNKNOWN_M_VALUE);
							UserMusicRequestApi.musicHateAct(params);
							isUiLock =false;
						}
					}.start();
				}
			}else{
				isPrepared = false;
				Message msg = new Message();
				msg.what = MSG_NET_MUSIC_SET_HATE;
				msg.obj = mLastDTO;
				mNetworkHandler.sendEmptyMessage(MSG_NET_ASYNC_PLAY_NEXT);
				mNetworkHandler.sendMessage(msg);
			}
		}
	}
	public void setFollowingOther(boolean b){
		isFollowingOther = b;
	}

	public boolean musicIsRelease() {
		return isRelease;
	}

	
	public void setCmbt(String cmbt) {
		if (mMainCmbt == null || !mMainCmbt.equals(cmbt)) {
			mHateCount = 0;
			mFavCount = 0;
		}
		mMainCmbt = cmbt;
	}
	public String getCmbt() {
		return mMainCmbt;
	}

	public void setMoodids(String moodids) {
		mMoodids = moodids;
	}

	public void setLoved(boolean b) {
		isLoved = b;
	}

	public boolean isLoved() {
		return isLoved;
	}
	
	public ShowTipsListener getShowTipsListener() {
		return mShowTipsListener;
	}

	public void setShowTipsListener(ShowTipsListener showTipsListener) {
		this.mShowTipsListener = showTipsListener;
	}

	public interface ShowTipsListener{
		public void onShowTips(String tips);
	}
	
	public LrcGetListener getLrcGetListener() {
		return mLrcGetListener;
	}

	public void setMusicInfoListener(MusicInfoListener musicInfoListener) {
		this.mMusicInfoListener = musicInfoListener;
	}
	
	public void setLrcGetListener(LrcGetListener lrcGetListener) {
		this.mLrcGetListener = lrcGetListener;
		if (mLastDTO != null && mMusicInfoDTO != null && mLrcGetListener != null) {
			mLrcGetListener.onNewLrc(mLastDTO,mMusicInfoDTO.isLrc());
		}
	}
	
	public interface LrcGetListener{
		void onNewLrc(MusicDTO musicDTO, boolean hasLrc);
	}

	public void setStartNewListener(StartNewListener startNewListener) {
		this.mStartNewListener = startNewListener;
	}

	public String getmCurrentCmbt() {
		return mMainCmbt;
	}

	public void setNeedSeekTimeOnPerpared(int pst) {
		isNeedSeekTimeOnPerpared = pst;
	}
	public void setNeedPauseOnPerpared(boolean b) {
		isNeedPauseOnPerpared = b;
	}

	public synchronized void setSleepTime(int time) {
		mSleepTime = time *60*1000;
		cheakSleepTimer();
	}

	private void cheakSleepTimer() {
		if (mSleepTime != 0) {
			mSleepTimer = new Timer();
			if (mSleepTimer != null) {
				mSleepTimer.cancel();
			}
			mSleepTimer = new Timer();
			mSleepTimer.schedule(new TimerTask() {
				
				@Override
				public void run() {
					refresh();
				}
//			}, 0, 5 * 60 * 1000);
			}, 0, SLEEP_TIME_REFRESH);
		}
	}

	private synchronized void refresh() {
		mSleepTime -= SLEEP_TIME_REFRESH;
		if (mSleepTime <= 0) {
			try {
				mediaPlayer.stop();
			} catch (Exception e) {
				if (e != null) {
					e.printStackTrace();
				}
			}
			try {
				mSleepTimer.cancel();
			} catch (Exception e) {
				if (e != null) {
					e.printStackTrace();
				}
			}
		}
//		Log.i("kid_debug","mSleepTimer.schedule: " + mSleepTime/60f/1000);
	}
	
	public int getSleepTime() {
		return mSleepTime;
	}

	public void setMainListMusicPst(String ct) {
//		Log.e("kid_debug","setMainListMusicPst: " + ct);
		try {
			lastPlayingInMainList[1] = Integer.parseInt(ct)*1000;
		} catch (Exception e) {
			if (e != null) {
				e.printStackTrace();
			}
		}
//		Log.e("kid_debug","setMainListMusicPst lastPlayingInMainList[1] : " + lastPlayingInMainList[1]);
	}
	
	public class MusicControllor extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_HEADSET_PLUG.equals(intent.getAction())) {
				  int state = intent.getIntExtra("state", 4);
                  if(state == 0){
                	  	//耳机拔出
  					pause();
                  }else if(state == 1){
                	  	//耳机插入
                  }else {
                	  	//其他情况
                  }
                  return;
			}
			buttonNum = intent.getIntExtra(MUSIC_CONTROLLOR_ACTION_Key, 0);
			switch (buttonNum) {
			case MUSIC_CONTROLLOR_ACTION_Play:
				if (isFollowingOther) {
					return;
				}
				if (isPlaying()) {
					pause();
				}else{
					start();
				}
//				Toast.makeText(context, "MUSIC_CONTROLLOR_ACTION_Play", 1).show();
				break;
			case MUSIC_CONTROLLOR_ACTION_CenterButton:
				if (isMediaCenterButtonPressedCount > 0) {
					playNext(true);
				}else{
					isMediaCenterButtonPressedCount++;
					Message msg = new Message();
					msg.what = MSG_TEST_DOUBLE_CLICK;
					msg.obj = context;
					mNetworkHandler.sendMessageDelayed(msg,300);
				}
				break;
			case MUSIC_CONTROLLOR_ACTION_Pause:
				if (isFollowingOther) {
					return;
				}
				pause();
//				Toast.makeText(context, "MUSIC_CONTROLLOR_ACTION_Play", 1).show();
				break;
			case MUSIC_CONTROLLOR_ACTION_Next:
				if (isFollowingOther) {
					return;
				}
				playNext(true);
//				Toast.makeText(context, "MUSIC_CONTROLLOR_ACTION_Next", 1).show();
				break;
			case MUSIC_CONTROLLOR_ACTION_Fav:
				if (LoginRegisterManager.getInstance().loginDataDTO != null
						&&	!LoginRegisterManager.getInstance().loginDataDTO.getUsr().isGuest()) {
					musicFav(LoginRegisterManager.getInstance().loginDataDTO.getUsr().getId());
				}
//				Toast.makeText(context, "MUSIC_CONTROLLOR_ACTION_Fav", 1).show();
				break;
			case MUSIC_CONTROLLOR_ACTION_Hate:
				if (LoginRegisterManager.getInstance().loginDataDTO != null
						&&	!LoginRegisterManager.getInstance().loginDataDTO.getUsr().isGuest()) {
					musicHate();
				}
//				Toast.makeText(context, "MUSIC_CONTROLLOR_ACTION_Fav", 1).show();
				break;
			default:
//				Toast.makeText(context, "music button " + buttonNum, Toast.LENGTH_SHORT).show();
				break;
			}
			if (notificationIsShowing) {
				refreshNotificationState(context);
			}
		}

	}

	public void setAlikeTid(Integer tid) {
		mAlikeTid = tid;
	}

	public void setSubM_Cmbt(int m, String cmbt) {
		mSubM = m;
		mSubCmbt = cmbt;
	}
	public void setMainM(int m){
		mMainM = m;
	}

	public String getSubCmbt() {
		return mSubCmbt;
	}
	
	public void clearLastMain() {
		lastPlayingInMainList[0] = 0;
		lastPlayingInMainList[1] = 0;
	}

	public class MyPhoneStateListener extends PhoneStateListener{

		@Override
		public void onCallStateChanged(int state,
				String incomingNumber) {
			switch (state) {
			case TelephonyManager.CALL_STATE_OFFHOOK:
			case TelephonyManager.CALL_STATE_RINGING:
				callingPause();
				break;
			case TelephonyManager.CALL_STATE_IDLE:
				callingResume();
				break;
			default:
				break;
			}
			super.onCallStateChanged(state, incomingNumber);
		}
	}

	public void callingPause() {
		if (isPlaying() || isCallingPause) {
			isCallingPause = true;
			pause();
		}
	}

	public void callingResume() {
		if (isCallingPause) {
			isCallingPause = false;
			start();
		}
	}

	public void setVisualizerDisableByModelName(String mModelName) {
		if (JingTools.isValidString(mModelName)) {
			for (String brand : mVisualizerBlackList) {
				if (mModelName.toLowerCase().startsWith(brand)) {
					isVisualizerDisabled = true;
					break;
				}
			}
		}
	}

	public void registMediaButton(Context context) {
		if (mComponentName == null) {
			mComponentName = new ComponentName(context.getPackageName(), AllReceiver.class.getName());
			AudioManager am = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
	        am.registerMediaButtonEventReceiver(mComponentName);
		}
	}

	public void unregistMediaButton(Context context) {
		if (mComponentName == null) {
			return;
		}
		AudioManager am = (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
		am.unregisterMediaButtonEventReceiver(mComponentName);
		mComponentName = null;
	}

	public void musicWaiting() {
		if (mLastDTO == null) {
			isNeedWaiting = true;
			return;
		}
		try {
			mWaitingDTO = mLastDTO;
			int pst = musicPst();
			if (pst > 0) {
				mWaitingPst = pst;
			}
			release();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void musicActive(boolean cancel) {
		isNeedWaiting = false;
		if (cancel || mWaitingDTO == null) {
			mWaitingDTO = null;
			mWaitingPst = -1;
			return;
		}
		startPlay(mWaitingDTO, isHighMode);
		mWaitingDTO = null;
	}

	public void setLocalMusicMode(boolean b) {
		isLocalMusicMode = b;
	}

	public MusicDTO getMainListLastDTO() {
		if (isMainListPlaying()) {
			return mLastDTO;
		}else{
			return mPlayLists.get(0).get(lastPlayingInMainList[0]);
		}
	}

	public int getProgress() {
		try {
			int d = mediaPlayer.getDuration();
			if (d <= 0) {
				return 0;
			}else{
				return mediaPlayer.getCurrentPosition() / d;
			}
		} catch (Exception e) {
			return 0;
		}
	}
}
