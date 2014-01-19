package com.jingfm;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.jingfm.Constants.Constants;
import com.jingfm.Constants.KTC;
import com.jingfm.ViewManager.AnimateManager;
import com.jingfm.ViewManager.GuideManger;
import com.jingfm.ViewManager.LoginStateChangeListener;
import com.jingfm.ViewManager.LoginViewManager;
import com.jingfm.ViewManager.MusicInfoViewManager;
import com.jingfm.ViewManager.ShareViewManager;
import com.jingfm.ViewManager.ShareViewManager.SharedMusicDTO;
import com.jingfm.ViewManager.ViewManagerCenter;
import com.jingfm.ViewManager.ViewManagerLeft;
import com.jingfm.ViewManager.ViewManagerRight;
import com.jingfm.ViewManager.WebViewManager;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.ResponseErrorCode;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.DeviceRequestApi;
import com.jingfm.api.business.UserClickRequestApi;
import com.jingfm.api.business.UserMediaRequestApi;
import com.jingfm.api.business.UserRequestApi;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.LoginDataDTO;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.MusicFollowDTO;
import com.jingfm.api.model.UserPlayingDTO;
import com.jingfm.api.model.socketmessage.SocketPUserSignedoffDTO;
import com.jingfm.api.model.socketmessage.SocketPUserSignedonDTO;
import com.jingfm.background_model.JingService;
import com.jingfm.background_model.JingService.JingIBinder;
import com.jingfm.background_model.JingService.SearchCallBack;
import com.jingfm.background_model.LocalCacheManager;
import com.jingfm.background_model.LoginRegisterManager;
import com.jingfm.background_model.PlayerManager;
import com.jingfm.background_model.PlayerManager.PlayListOverListener;
import com.jingfm.background_model.PlayerManager.PlayerStateChangeListener;
import com.jingfm.background_model.SettingManager;
import com.jingfm.background_model.UpdateManager;
import com.jingfm.customer_views.TouchScrollContainer;
import com.jingfm.customer_views.TouchScrollContainer.OnShowEventListener;
import com.jingfm.third_part_api.VoiceHelper;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.HttpDownloader;
import com.jingfm.tools.JingTools;
import com.tendcloud.tenddata.TCAgent;

public class MainActivity extends Activity implements OnShowEventListener,
		PlayerStateChangeListener {

	public static final int MSG_UI_LOGIN = 100;
	public static final int MSG_SERIVCE_BIND = 101;
	public static final int MSG_UI_CLOSE_LOADING_MASK = 102;
	public static final int MSG_CENTER_OUT_OF_RIGHT = 103;
	public static final int MSG_MUSIC_IS_BUFFERING = 105;
	public static final int MSG_UPDATE_SEARCH_TEXT = 106;
	public static final int MSG_TOAST_TEXT = 107;
	protected static final int SERVICE_FORGROUND_ID = 107008;
	
	public static final int CONNECT_STATE_NOTHING = 80;
	public static final int CONNECT_STATE_WIFI = 81;
	public static final int CONNECT_STATE_GPRS = 82;
	public static final int CONNECT_STATE_2G = 83;
	public static final int CONNECT_STATE_3G = 85;
	public static final int CONNECT_STATE_BLUE_TOOTH = 86;

	public static final int ASYNC_TASK_TYPE_USER_LOGIN = 300;
	public static final int ASYNC_TASK_TYPE_USER_REGISTER = 301;
	public static final int ASYNC_TASK_TYPE_GUEST_LOGIN = 302;
	public static final int ASYNC_TASK_TYPE_USER_LOGIN_BY_TOKEN = 303;

	public static final int LOGIN_BY_3RD_PART = 505;
	public static final int START_ACTIVIY_REQUEST_CODE_3rd_part_login = 2000;
	public static final int START_ACTIVIY_REQUEST_CODE_PICK_AVATER = 2001;
	public static final int START_ACTIVIY_REQUEST_CODE_AVATER_CUT = 2002;
	public static final int START_ACTIVIY_REQUEST_CODE_PICK_COVER = 2003;
	public static final int START_ACTIVIY_REQUEST_CODE_COVER_CUT = 2005;
	public static final int START_ACTIVIY_REQUEST_CODE_3rd_part_bind = 2006;
	public static final int START_ACTIVIY_REQUEST_CODE_3rd_part_bind_in_share = 2007;
	public static final int REQUEST_CODE_CANCEL_UPDATE = 3000;
	private static final int DEFAULT_FREEZE_TIME = 1000;

	private static final String CONNECTIVITY_CHANGE_ACTION = "android.net.conn.CONNECTIVITY_CHANGE";

	private View mAllBaseView;
	private Handler mHandler;

	protected JingService mJingService;

	private LoginRegisterManager mLoginRegisterManager;
	private LoginDataDTO mLoginData;
	private ProgressDialog mWaitingDialog;
	private ServiceConnection mServiceConnection;
	private ViewManagerCenter mViewManagerCenter;
	private ViewManagerRight mViewManagerRight;
	private ViewManagerLeft mViewManagerLeft;
	private boolean isActivityShowing;
	private com.jingfm.MainActivity.ActivityCallback mActivityCallback;
	private HashSet<ImageView> mSetAvatarView = new HashSet<ImageView>();
	private String mTempAvatar;
	protected boolean isSearching;
	private WebViewManager mWebViewManager;
	private MusicDTO mCurrentMusicDTO;
	private boolean isBeFollowedMode;
	private boolean isBeFollowedPlaying;
	private AlertDialog mSelectDialogHasCountDown;
	private boolean isPlayingSubList;
	private LinkedList<ChangeDataAnimateCallBack> changerDataQ = new LinkedList<ChangeDataAnimateCallBack>();
	private boolean isChangingdata;
	private TextView title_text;
	private Intent mIntent;
	private ShareViewManager mShareViewManager;
	private int mFollowListenRequestUid;
	private String mFollowListenRequestNick;
	private boolean isFollowingOther;
	protected boolean isNeedReconnect;
	private boolean isAnimateContainerInterrupterAll;
	private ViewGroup mGuideContainer;
	private NetworkStateReceiver networkReceiver;
	private String mLastJingTitleText = "";
	protected boolean isOfflineMode;
	private String mPersonalRadio = "";
	private boolean isFirstSearchDone;
	private boolean isLocalMusicMode;
	private String mSubListTille;
	private AlertDialog mOfflineAlertDialog;
	private AlertDialog mUpateInterrupt;
	private Timer mGuestHeartBeatTimer;
	private AnimateManager mAnimateManager;
	protected boolean isReconnecting;
	private View welcome_bg_view;
	private boolean isAiRadioPlaying;
	private SoundPool mSoundPool;
	private int mNewsSourceId;
	private LoginViewManager mLoginViewManager;
	private AudioManager mAudioManager;
	private int mSystemMaxVolume;
	private MusicInfoViewManager mMusicInfoViewManager;
	private GuideManger mGuideManger;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		KTC.activity = this;
		initEnvironment();
		if (!JingTools.isValidString(Constants.CHANNELID)) {
			try {
				ApplicationInfo ai = getApplicationContext().getPackageManager().getApplicationInfo(  
						getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
				Object value = ai.metaData.get("CHANNEL");  
				if (value != null) {
					Constants.CHANNELID= value.toString();
					Log.e("CHANNELID",Constants.CHANNELID);
				}
			} catch (Exception e) {
			}
		}
		showLoadMask();
		initViews();
		initWaitingDialog();
		initHandler();
		conectJingService();
		mIntent = getIntent();
		if (DEFAULT_FREEZE_TIME > 0) {
			mHandler.sendEmptyMessageDelayed(MSG_UI_CLOSE_LOADING_MASK,
					DEFAULT_FREEZE_TIME);
		}
		mViewManagerCenter.getMainLayout().setmOnShowEventListener(this);
		registerNetworkStateReceiver();
		initSound();
	}
	
	private void initEnvironment() {
		JingTools.init(this);
		mAudioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
		mSystemMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
	}

	private void initSound() {
		mSoundPool = new SoundPool(10, AudioManager.STREAM_SYSTEM, 5);  
        //载入音频流，返回在池中的id  
		mNewsSourceId = mSoundPool.load(this, R.raw.news, 0);  
	}
	
	public void playNewsSound(){
    		mSoundPool.play(mNewsSourceId, 1, 1, 0, 0, 1);  
	}

	private void showLoadMask() {
		welcome_bg_view = LayoutInflater.from(getApplicationContext()).inflate(R.layout.welcome_view, null);
		setContentView(welcome_bg_view);
	}

	public void setJingTitleText(String str) {
		if (JingTools.isValidString(str) && title_text != null) {
			title_text.setText(str);
			title_text.invalidate();
		}
	}

	private void initWaitingDialog() {
		mWaitingDialog = new ProgressDialog(this);
		mWaitingDialog.setCancelable(false);
	}

	protected void conectJingService() {
		mServiceConnection = new ServiceConnection() {
			@Override
			public void onServiceDisconnected(ComponentName name) {
//				throw new RuntimeException("onServiceDisconnected");
				if (isActivityShowing) {
					finish();
				}
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service) {
				JingIBinder binder = (JingIBinder) service;
				mJingService = (JingService) binder.getService();
				mHandler.sendEmptyMessage(MSG_SERIVCE_BIND);
//				Toast.makeText(MainActivity.this, "Constants.CHANNELID: " + Constants.CHANNELID, 1).show();
			}
		};
		final Intent intent = new Intent(MainActivity.this, JingService.class);
		new Thread() {
			public void run() {
				startService(intent);
				SystemClock.sleep(800);
				bindService(intent, mServiceConnection,
						Context.BIND_AUTO_CREATE);
			};
		}.start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch (requestCode) {
		case START_ACTIVIY_REQUEST_CODE_3rd_part_login:
			thirdPartLoginBack(resultCode, data);
			break;
		case START_ACTIVIY_REQUEST_CODE_3rd_part_bind:
			mViewManagerCenter.getmSetttingViewManager().bindBack(resultCode,
					data);
			break;
		case START_ACTIVIY_REQUEST_CODE_3rd_part_bind_in_share:
			if (mShareViewManager != null) {
				mShareViewManager.bindBack(resultCode, data);
			}
			break;
		case START_ACTIVIY_REQUEST_CODE_PICK_AVATER:
			if (resultCode == RESULT_OK) {
				startPhotoZoom(data.getData(),
						START_ACTIVIY_REQUEST_CODE_AVATER_CUT);
			}
			break;
		case START_ACTIVIY_REQUEST_CODE_PICK_COVER:
			if (resultCode == RESULT_OK) {
				startPhotoZoom(data.getData(),
						START_ACTIVIY_REQUEST_CODE_COVER_CUT);
			}
			break;
		case START_ACTIVIY_REQUEST_CODE_AVATER_CUT:
			if (resultCode == RESULT_OK) {
				if (mActivityCallback != null) {
					mActivityCallback
					.onActivityCallback(new String[] {AsyncImageLoader.getInstance().getTempPath() + Constants.TEMP_AVTAR_FILE_NAME});
					mActivityCallback = null;
				}
			}
			break;
		case START_ACTIVIY_REQUEST_CODE_COVER_CUT:
			if (resultCode == RESULT_OK) {
				if (mActivityCallback != null) {
					mActivityCallback
					.onActivityCallback(new String[] {AsyncImageLoader.getInstance().getTempPath() + Constants.TEMP_COVER_FILE_NAME});
					mActivityCallback = null;
				}
			}
			break;
		default:
			break;
		}
	}

	private void thirdPartLoginBack(int resultCode, Intent data) {
		switch (resultCode) {
		case RESULT_OK:
			KTC.rep("Account", "Register", "weibo");
			mLoginData = LoginRegisterManager.getInstance().getLoginDataDTO();
			mLoginViewManager.loginOk();
			break;
		case Jing3rdPartLoginActivity.RESULT_FAILED:
			Toast.makeText(getApplicationContext(), "登录失败请重试", 0).show();
		case RESULT_CANCELED:
			mLoginViewManager.setLoginViewsLock(false);
			break;
		}
	}

	public void startPhotoZoom(Uri uri, int code) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		// 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
		intent.putExtra("crop", "true"); 
		// aspectX aspectY 是宽高的比例
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());//返回格式  
		intent.putExtra("return-data", false);
		// outputX outputY 是裁剪图片宽高
		File file = null ;
		if (code == START_ACTIVIY_REQUEST_CODE_COVER_CUT) {
			intent.putExtra("outputX", 800);
			intent.putExtra("outputY", 800);
			file = new File(AsyncImageLoader.getInstance().getTempPath(), Constants.TEMP_COVER_FILE_NAME);
		}else{
			intent.putExtra("outputX", 400);
			intent.putExtra("outputY", 400);
			file = new File(AsyncImageLoader.getInstance().getTempPath(), Constants.TEMP_AVTAR_FILE_NAME);
		}
		File parent = file.getParentFile();
		if (!parent.exists()) {
			parent.mkdirs();
		}
		if (file.exists()) {
			file.delete();
		}
		intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(file));
		startActivityForResult(intent, code);
	}

	private void initHandler() {
		mHandler = new Handler() {
			private boolean timeOver;

			@Override
			public void dispatchMessage(Message msg) {
				super.dispatchMessage(msg);
				switch (msg.what) {
				case MSG_UI_LOGIN:
					mLoginData = mLoginRegisterManager.getLoginDataDTO();
					if (mLoginData != null) {
						mLoginViewManager.loginDone(mLoginData);
						onNetworkStateChange(false);
					} else {
						String at = SettingManager.getInstance().getmAtoken();
						String rt = SettingManager.getInstance().getmAtoken();
						if (JingTools.isValidString(at)
								&& JingTools.isValidString(rt)) {
							loginByToken(at,rt);
						}else{
//							showLoginView();
							showLoginGudieView();
						}
					}
					break;
				case MSG_SERIVCE_BIND:
					mLoginRegisterManager = mJingService.getmLoginRegisterManager();
					mLoginViewManager.addLoginListener(mLoginRegisterManager);
					mWebViewManager = new WebViewManager(MainActivity.this);
					closeLoadingMask();
					UpdateManager.startCheckUpdate(MainActivity.this);
					mJingService.startForeground(SERVICE_FORGROUND_ID, new Notification());
					mJingService.enableNotify();
					VoiceHelper.init(MainActivity.this);
					HttpDownloader.getInstance().setAllDownloadNeedStop(false);
					mJingService.setHandler(mHandler);
					mHandler.sendEmptyMessage(MSG_UI_LOGIN);
					mJingService.stopPushModel();
					refreshPlayHighMode();
					mViewManagerCenter.getmPlayingViewManager().showGuideFirstPlay();
					// mJingService.notifyPlayingState();
					break;
				case MSG_UI_CLOSE_LOADING_MASK:
					timeOver = true;
					closeLoadingMask();
					break;
				case MSG_CENTER_OUT_OF_RIGHT:
					while (!changerDataQ.isEmpty()) {
						changerDataQ.poll().doChangeData();
					}
					centerViewBack();
					break;
				case MSG_MUSIC_IS_BUFFERING:
					if (!isLocalMusicMode) {
						Toast.makeText(getApplicationContext(),
								R.string.tips_text_is_buffering, 1).show();
					}
					break;
				case MSG_UPDATE_SEARCH_TEXT:
					mViewManagerRight.setSearchText("" + msg.obj);
					if (!isOfflineMode) {
						setJingTitleText("" + msg.obj);
					}
					break;
				case MSG_TOAST_TEXT:
					Toast.makeText(getApplicationContext(), "" + msg.obj, msg.arg1).show();
					break;
				}
			}

			private void closeLoadingMask() {
				if (mJingService == null || !timeOver) {
					return;
				}
				onNetworkStateChange(false);
				if ("andrdwdjinvt".equals(Constants.CHANNELID)
						|| "andrdwdjinvt115".equals(Constants.CHANNELID)) {
					//豌豆荚需要显示首发界面
					FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
					Bitmap welcome_wandoujia = null;
					try {
						InputStream is = getResources().openRawResource(R.drawable.welcome_wandoujia);
						welcome_wandoujia = BitmapFactory.decodeStream(is);
						ImageView welcome_bg_imageView = new ImageView(MainActivity.this);
						welcome_bg_imageView.setScaleType(ScaleType.FIT_XY);
						welcome_bg_imageView.setImageBitmap(welcome_wandoujia);
						setContentView(welcome_bg_imageView);
						welcome_bg_imageView.setScaleType(ScaleType.FIT_CENTER);
						welcome_bg_imageView.setBackgroundColor(0xFFf9ffe5);
						welcome_bg_imageView.setImageBitmap(welcome_wandoujia);
						welcome_bg_imageView.invalidate();
						is.close();
					} catch (OutOfMemoryError e) {
					} catch (Exception e) {
					}
					final Bitmap welcome_360_wandoujiaTemp = welcome_wandoujia;
					mHandler.postDelayed(new Runnable() {
						@Override
						public void run() {
							doCloseMask();
							try {
								if (welcome_360_wandoujiaTemp != null) {
									welcome_360_wandoujiaTemp.recycle();
								}
							} catch (Exception e) {
							}
						}
					},2500);
				}else{
					doCloseMask();
				}
				mJingService.addPlayerStateChangeListener(mViewManagerCenter
						.getmPlayingViewManager());
				mJingService.bindMediaButton();
//				mJingService.addPlayerStateChangeListener(MainActivity.this);
				title_text.requestFocus();
			}

			private void doCloseMask() {
				getWindow().setFlags(
						~WindowManager.LayoutParams.FLAG_FULLSCREEN,
						WindowManager.LayoutParams.FLAG_FULLSCREEN);
				setContentView(mAllBaseView);
			}
		};
	}

	public void refreshUserCm() {
		new Thread(){
			public void run() {
				SystemClock.sleep(30*1000);
				HashMap<Object, Object> params = new HashMap<Object, Object>();
				int uid = getUserId();
				params.put("u", ""+uid);
				ResultResponse<Map<String, Object>> rs = UserMediaRequestApi.offline_Music(params);
				if (rs != null && rs.isSuccess()) {
					List<MusicDTO> serverList = ((ListResult<MusicDTO>)rs.getResult().get("items")).getItems();
					Integer cm = (Integer) rs.getResult().get("cm");
					if (cm != null) {
						getmLoginData().setCm(cm);
					}
				}
			};
		}.start();
	}

	@Override
	protected void onStart() {
		super.onStart();
		isActivityShowing = true;
		if (mJingService != null) {
			mJingService.stopPushModel();
			hideNotifyView();
			mJingService.enableNotify();
			try {
				mJingService.addPlayerStateChangeListener(
						mViewManagerCenter.getmPlayingViewManager());
			} catch (Exception e) {
				if (e != null) {
					e.printStackTrace();
				}
			}
		}
		mViewManagerCenter.getmPlayingViewManager().invalidateCoverView();
	}
	
	private void hideNotifyView() {
		if (mJingService != null) {
			mJingService.hideNotifyView();
		}
	}

	private void showNotifyView() {
		if (mJingService != null) {
			mJingService.showNotifyView();
		}
	}

	public void pollingReconnect(final boolean isImmediately) {
		if (isReconnecting || !isNeedReconnect || getConnectivityState() == CONNECT_STATE_NOTHING) {
			return;
		}
		if (mLoginData == null 
				|| mLoginData.getUsr().isGuest()) {
			return;
		}
		Log.e("kid_debug","pollingReconnect");
		isReconnecting = true;
		new Thread(){
			public void run() {
				boolean isTmpImmediately = isImmediately;
				while (getConnectivityState() != CONNECT_STATE_NOTHING) {
					if (isTmpImmediately) {
						if (!isNeedReconnect) {
							break;
						}
						if (JingTools.getActiveNetwork(MainActivity.this) != null) {
							destoryWebView();
							setupWebView();
						}
					}
					SystemClock.sleep(5*30*1000);
					isTmpImmediately = true;
				}
				isReconnecting = false;
			};
		}.start();
	}

	public ViewGroup getWebviewContainer(){
		 return (ViewGroup) mAllBaseView.findViewById(R.id.main_view_container_web);
	}
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mIntent = intent;
	}

	@Override
	protected void onPause() {
		super.onPause();
		TCAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		TCAgent.onResume(this);
		if (mIntent != null && UpdateManager.UPDATE_INTENT_KEYWORD.equals(mIntent.getAction())) {
			if (mUpateInterrupt == null) {
				mUpateInterrupt = new AlertDialog.Builder(this).setTitle("程序更新")
						.setMessage("是否要终止下载").create();
				mUpateInterrupt.setButton(AlertDialog.BUTTON_POSITIVE, "终止",
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								UpdateManager.cancel();
							}
						});
				mUpateInterrupt.setButton(AlertDialog.BUTTON_NEGATIVE, "继续下载",
						new OnClickListener() {

							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.dismiss();
							}
						});
			}
			if (!mUpateInterrupt.isShowing()) {
				mUpateInterrupt.show();
			}
			mIntent = null;
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		try {
			mJingService.removePlayerStateChangeListener(
					mViewManagerCenter.getmPlayingViewManager());
		} catch (Exception e) {
			if (e != null) {
				e.printStackTrace();
			}
		}
		showNotifyView();
		isActivityShowing = false;
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		Log.e("kid_debug","onDestroy");
		try {
			UpdateManager.cancel();
		} catch (Exception e) {
		}
		try {
			unregisterNetworkStateReceiver();
		} catch (Exception e) {
		}
		try {
			if (!isOfflineMode) {
				LocalCacheManager.getInstance().clearTempFiles();
			}
		} catch (Exception e) {
		}
		try {
			if (mLoginData != null 
					&& !mLoginData.getUsr().isGuest()
					&& !isOfflineMode) {
				LoginRegisterManager.getInstance().cacheLoginData(mLoginData);
			}
		} catch (Exception e) {
		}
		isNeedReconnect = false;
		try {
			if (isFollowingOther) {
				backToMainList();
			}
		} catch (Exception e) {
		}
		if (mJingService != null) {
			mJingService.removePlayerStateChangeListener(this);
		}
		try {
			if (mWebViewManager != null) {
				destoryWebView();
			}
		} catch (Exception e) {
		}
		try {
			if (mLoginData != null && mLoginData.getUsr().isGuest()) {
				stopGuestHeartBeat();
			}else{
				mJingService.startPushModel();
			}
		} catch (Exception e) {
		}
		try {
			mJingService.setHandler(null);
		} catch (Exception e) {
		}
		try {
			mJingService.unbindMediaButton();
		} catch (Exception e) {
		}
		try {
			unbindService(mServiceConnection);
		} catch (Exception e) {
		}
		
	}

	private void initViews() {
		mAllBaseView = LayoutInflater.from(getApplicationContext()).inflate(
				R.layout.activity_main, null);
		initLoginView();
		initLeftView();
		initCenterView();
		initRightView();
		initMusicInfoView();
		mViewManagerLeft.getLeftViewAdapter().setmViewManagerCenter(
				mViewManagerCenter);
		mGuideContainer = (ViewGroup) mAllBaseView.findViewById(R.id.guide_layout);
		mGuideContainer.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return isAnimateContainerInterrupterAll;
			}
		});
		mViewManagerCenter.setBackToPlayingViewButton(mAllBaseView.findViewById(R.id.visualizer_view_mini));
	}

	private void initMusicInfoView() {
		mMusicInfoViewManager = new MusicInfoViewManager(this,(ViewGroup)mAllBaseView.findViewById(R.id.main_view_container_music_info));
	}

	public MusicInfoViewManager getmMusicInfoViewManager() {
		return mMusicInfoViewManager;
	}
	
	private void initLoginView() {
		mLoginViewManager = new LoginViewManager(this, (ViewGroup)mAllBaseView.findViewById(R.id.main_view_container_login));
	}

	private void initCenterView() {
		mViewManagerCenter = new ViewManagerCenter(this, mAllBaseView);
		title_text = mViewManagerCenter.getTitleText();
	}

	public AnimateManager getmAnimateManager() {
		if (mAnimateManager == null) {
			mAnimateManager = new AnimateManager(this,(ViewGroup) mAllBaseView.findViewById(R.id.animation_layout));
		}
		return mAnimateManager;
	}
	
	private void initLeftView() {
		mViewManagerLeft = new ViewManagerLeft(this, mAllBaseView);
	}

	private void initRightView() {
		mViewManagerRight = new ViewManagerRight(this, mAllBaseView);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (event.getKeyCode() ==  KeyEvent.KEYCODE_MENU
//				|| event.getKeyCode() ==  KeyEvent.KEYCODE_BACK) {
//			if (isKeyLocked) {
//				return true;
//			}else{
//				isKeyLocked = true;
//				new Thread(){
//					public void run() {
//						SystemClock.sleep(200);
//						isKeyLocked = false;
//					};
//				}.start();
//			}
//		}
		
		if (!isActivityShowing || mMusicInfoViewManager.sendKeyEvent(event) || animationSendKeyEvent(event)|| mViewManagerRight.sendKeyEvent(event)
				|| mViewManagerCenter.sendKeyEvent(event)) {
			return true;
		}
		
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_MENU:
			break;
		case KeyEvent.KEYCODE_BACK:
			Intent intent = new Intent(Intent.ACTION_MAIN);
			intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			intent.addCategory(Intent.CATEGORY_HOME);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	
	public void showFinishDialog(){
		AlertDialog alertDialog = new AlertDialog.Builder(this)
		.create();
		alertDialog.setCancelable(true);
		alertDialog.setMessage("是否要退出Jing");
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which == AlertDialog.BUTTON_POSITIVE){
					mJingService.stopForeground(true);
					mJingService.disableNotify();
					try {
						postQuitInfo();
					} catch (Exception e) {
					}
					try {
						mJingService.musicRelease();
					} catch (Exception e) {
					}
					mJingService.musicActive(true);
					HttpDownloader.getInstance().setAllDownloadNeedStop(true);
					finish();
				}
			}
		};
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "退出",
				listener);
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
				listener);
		alertDialog.show();
	}

	private boolean animationSendKeyEvent(KeyEvent event) {
		if (event.getKeyCode() == KeyEvent.KEYCODE_MENU
				|| event.getKeyCode() == KeyEvent.KEYCODE_BACK ) {
			if (mGuideContainer.getVisibility() == View.VISIBLE) {
				if (mShareViewManager != null && mShareViewManager.isShowing()) {
					mShareViewManager.hideAllView();
				}
				return true;
			}
		}
		return false;
	}

	public MainViewAsyncTask getMainViewAsyncTaskInstance(int type) {
		if (mWaitingDialog.isShowing()) {
			return null;
		}
		return new MainViewAsyncTask(type);
	}

	public class MainViewAsyncTask extends AsyncTask<String, Integer, Boolean>
			implements OnCancelListener {

		private String mWaitingDialogTitle = "";
		private String mWaitingDialogMsg = "";
		private int mType;
		private ResultResponse resultResponse;

		public MainViewAsyncTask(int type) {
			this.mType = type;
			switch (mType) {
			case ASYNC_TASK_TYPE_USER_LOGIN:
				mWaitingDialogTitle = "登录";
				mWaitingDialogMsg = "正在登录请稍后...";
				break;
			case ASYNC_TASK_TYPE_USER_LOGIN_BY_TOKEN:
				mWaitingDialogTitle = "登录";
				mWaitingDialogMsg = "正在重新登录请稍后...";
				break;
			case ASYNC_TASK_TYPE_USER_REGISTER:
				mWaitingDialogTitle = "注册";
				mWaitingDialogMsg = "正在注册请稍后...";
				
				break;
			case ASYNC_TASK_TYPE_GUEST_LOGIN:
				mWaitingDialogTitle = "登录";
				mWaitingDialogMsg = "正在登录请稍后...";
				break;
			}
			mWaitingDialog.setTitle(mWaitingDialogTitle);
			mWaitingDialog.setMessage(mWaitingDialogMsg);
			mWaitingDialog.setOnCancelListener(this);
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (mWaitingDialog != null && !mWaitingDialog.isShowing()) {
				mWaitingDialog.show();
			}
		}

		@Override
		protected void onProgressUpdate(Integer... values) {
			super.onProgressUpdate(values);
		}

		@Override
		protected Boolean doInBackground(String... params) {
			boolean rs = false;
			switch (mType) {
			case ASYNC_TASK_TYPE_USER_LOGIN:
				KTC.rep("Account", "Login", "email");
				if (JingTools.isValidString(params[0])
						&& JingTools.isValidString(params[1])) {
					resultResponse = mLoginRegisterManager.loginByUserName(
							params[0], params[1]);
				} else {
//					resultResponse = mLoginRegisterManager.loginByUserName(
//							"android@jing.fm", "123456");
//					 resultResponse = mLoginRegisterManager.loginByUserName(
//					 "lawliet_tang@163.com", "123456");
//					 resultResponse = mLoginRegisterManager.loginByUserName(
//					 "k@koocu.com", "123456");
				}
				break;
			case ASYNC_TASK_TYPE_USER_LOGIN_BY_TOKEN:
				try {
					if (JingTools.isValidString(params[0])
							&& JingTools.isValidString(params[1])) {
						resultResponse = mJingService.loginByToken(params[0], params[1]);
					}
				} catch (Exception e) {
				}
				break;
			case ASYNC_TASK_TYPE_USER_REGISTER:
				KTC.rep("Account", "Register", "email");
				HashMap<Object, Object> map = new HashMap<Object, Object>();
				map.put("email", params[0]);
				map.put("pwd", params[1]);
				map.put("nick", params[2]);
				map.put("sex", params[3]);
				map.put("i", Constants.CHANNELID);
				resultResponse = UserRequestApi.userCreate(map);
				if (resultResponse.isSuccess()) {
					mLoginData = ((LoginDataDTO) resultResponse
							.getResult());
					mLoginRegisterManager.setLoginDataDTO(mLoginData);
					if (params[4] != null) {
						File file = new File(params[4]);
						if (file.exists()) {
							Integer uid = mLoginData.getUsr().getId();
							map.clear();
							map.put("uid", "" + uid);
							map.put("file", file);
							resultResponse = UserRequestApi
									.userAvatarUpload(map);
							if (resultResponse.isSuccess()) {
								setNewAvatar(file.toString());
							}else{
								mTempAvatar = null;
							}
						}
					}
				}
				break;
			case ASYNC_TASK_TYPE_GUEST_LOGIN:
				resultResponse = mLoginRegisterManager.loginByGuest();
				break;
			}
			return rs;
		}

		@Override
		protected void onPostExecute(Boolean result) {
			super.onPostExecute(result);
			if (mWaitingDialog != null && mWaitingDialog.isShowing()) {
				mWaitingDialog.dismiss();
			}
			if (resultResponse != null && resultResponse.isSuccess()) {
				if (mLoginData != null && mLoginData.getUsr().isGuest()) {
					musicRelease();
				}
				mLoginData = mLoginRegisterManager.getLoginDataDTO();
				isOfflineMode = false;
				mLoginViewManager.loginOk();
			} else {
				if (mType == ASYNC_TASK_TYPE_USER_LOGIN_BY_TOKEN) {
					showLoginView();
				}
				if (resultResponse != null) {
//					Toast.makeText(
//							getApplicationContext(),
//							"code: " + resultResponse.getCode() + "\n"
//									+ resultResponse.getCodemsg(), 1).show();
					if (ResponseErrorCode.REQUEST_UNKNOW_ERROR.code().equals(resultResponse.getCode())) {
						Toast.makeText(
								getApplicationContext(),"网络连接错误", 1).show();
					}else{
						Toast.makeText(
								getApplicationContext(),""+resultResponse.getCodemsg(), 1).show();
					}
					
				}
			}
		}

		@Override
		public void onCancel(DialogInterface dialog) {
			this.cancel(true);
		}
	}

	public LoginDataDTO getmLoginData() {
		return this.mLoginData;
	}

	public void setNewAvatar(String string) {
		mTempAvatar = string;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				for (ImageView imageView : mSetAvatarView) {
					FileInputStream fileInputStream = null;
					try {
						fileInputStream = new FileInputStream(mTempAvatar);
						Bitmap bp = BitmapFactory
								.decodeStream(fileInputStream);
						imageView.setImageBitmap(AsyncImageLoader.toRound(bp));
						if (imageView.equals(mViewManagerLeft.getLeftViewAdapter().getAvatarView())) {
							imageView.setImageBitmap(bp);
						}
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}finally{
						if (fileInputStream != null) {
							try {
								fileInputStream.close();
							} catch (IOException e) {
								e.printStackTrace();
							}
						}
					}
				}
			}
		});
	}

	public void searchAndPlay(final HashMap<String, String> map,
			boolean isMainList, PlayerManager.PlayListOverListener listener) {

	}

	public void logout() {
		if (mLoginData == null) {
			return;
		}
		KTC.rep("Account", "Logout", KTC.getTimeKeyString());
		if (mLoginData.getUsr().isGuest()) {
			showLoginView();
		} else {
			isPlayingSubList = false;
			new Thread(){
				public void run() {
					HashMap<Object, Object> params = new HashMap<Object, Object>();
					params.put("uid", "" + mLoginData.getUsr().getId());
					DeviceRequestApi.destory(params);
				};
			}.start();
			showLogoutView();
		}

	}

	private void showLoginGudieView() {
		mViewManagerCenter.showLoginView();
		mLoginViewManager.showLoginGudieView();
	}
	private void showLoginView() {
		mViewManagerCenter.showLoginView();
		mLoginViewManager.showLoginView();
	}
	public void showLogoutView() {
		mViewManagerCenter.logout();
		mLoginViewManager.logout();
	}

	public void clearLoginDataDTO() {
		this.mLoginData = null;
	}


	public void playAiRadio(final String itemMainText) {
		if (isAiRadioPlaying) {
			return;
		}
		isAiRadioPlaying = true;
		mViewManagerCenter.removeAllViewsAddNew(null);
		final PlayListOverListener playListOverListener = new PlayListOverListener() {
			int indexOfServer = 0;
			@Override
			public void onPlayListOver(PlayerManager pm) {
				final PlayListOverListener tmp = this;
				doSearchKeyWord(Constants.MUSIC_SEARCH_AI_KEYWORD, false,indexOfServer, new SearchCallBack() {
					@Override
					public void callBack(final ResultResponse<ListResult<MusicDTO>> resultResponse) {
						isAiRadioPlaying = true;
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								if (resultResponse != null
										&& resultResponse.isSuccess()) {
									List<MusicDTO> list = resultResponse.getResult()
											.getItems();
									int total = resultResponse.getResult().getTotal();
									if (total <= 0) {
										mViewManagerCenter.removeAllViewsAddNew(null);
										isAiRadioPlaying = false;
										Toast.makeText(MainActivity.this, resultResponse.getResult().getHint(), 1).show();
										return;
									}else{
										indexOfServer = (resultResponse.getResult().getSt() + resultResponse.getResult().getPs()) % total;
									}
									startNewSubList(list,resultResponse.getResult().getM(),Constants.MUSIC_SEARCH_AI_KEYWORD,tmp);
									isAiRadioPlaying = true;
									mViewManagerCenter.refreshRightButtonState();
									setSubListTitle(itemMainText);
									setJingTitleText(itemMainText);
								} else {
									isAiRadioPlaying = false;
								}
							}
						});
					}
				});
			}
			@Override
			public void setIndexOfServer(int indexOfServer) {
				this.indexOfServer = indexOfServer;
			}
			@Override
			public int getIndexOfServer() {
				return indexOfServer;
			}
		};
		playListOverListener.onPlayListOver(null);
	}

	public void changeData(final boolean b,
			final ChangeDataAnimateCallBack changeDataAnimateCallBack) {
		if (mLoginData == null) {
			return;
		}
		if (mLoginData.getUsr().isGuest()) {
			centerViewBack();
			logout();
			return;
		}
		if (isChangingdata) {
			return;
		}
		isChangingdata = true;
		if (b) {
			centerViewOutOfRight();
			changerDataQ.push(changeDataAnimateCallBack);
		} else {
			changeDataAnimateCallBack.doChangeData();
			centerViewBack();
		}
	}

	public void centerViewToRight() {
		hideSoftKeyboard();
		mViewManagerLeft.getMainLayout().setVisibility(View.VISIBLE);
		mViewManagerLeft.getMainLayout().setVisibility(View.VISIBLE);
		mViewManagerCenter.getMainLayout().smoothToRight(300);
		KTC.rep("Home", "OpenCabinetMenu", "");
	}

	public void centerViewToLeft() {
		mViewManagerLeft.getMainLayout().setVisibility(View.GONE);
		mViewManagerCenter.getMainLayout().smoothToLeft(300);
		title_text.requestFocus();
		// mViewManagerRight.enableSearchText(true);
	}

	public void centerViewOutOfLeft() {
		hideSoftKeyboard();
		mViewManagerLeft.getMainLayout().setVisibility(View.GONE);
		mViewManagerCenter.getMainLayout().smoothToLeftOut(300);
	}

	public void centerViewOutOfRight() {
		hideSoftKeyboard();
		mViewManagerLeft.getMainLayout().setVisibility(View.VISIBLE);
		mViewManagerCenter.getMainLayout().smoothToRightOut(500);
	}

	public void centerViewBack() {
		hideSoftKeyboard();
		mViewManagerLeft.getMainLayout().setVisibility(View.VISIBLE);
		mViewManagerCenter.getMainLayout().smoothResetPst(300);
		isChangingdata = false;
		title_text.requestFocus();
		mViewManagerCenter.refreshRightButtonState();
	}

	public void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mAllBaseView.getWindowToken(), 0);
	}

	@Override
	public void onShowEvent(TouchScrollContainer touchScrollContainer, int type) {
		if (touchScrollContainer.equals(mViewManagerCenter.getMainLayout())) {
			switch (type) {
			case TouchScrollContainer.EVENT_FINAL_IN_PST_CENTER:
				title_text.requestFocus();
				break;
			case TouchScrollContainer.EVENT_FINAL_IN_PST_LEFT:
				break;
			case TouchScrollContainer.EVENT_FINAL_IN_PST_RIGHT:
				break;
			case TouchScrollContainer.EVENT_FINAL_IN_PST_OUT_OF_LEFT:
				break;
			case TouchScrollContainer.EVENT_FINAL_IN_PST_OUT_OF_RIGHT:
				mHandler.sendEmptyMessageDelayed(MSG_CENTER_OUT_OF_RIGHT,
						500);
				break;
			case TouchScrollContainer.EVENT_NOW_IN_PST_LEFT:
				mViewManagerLeft.getMainLayout().setVisibility(View.GONE);
				break;
			case TouchScrollContainer.EVENT_NOW_IN_PST_RIGHT:
				mViewManagerLeft.getMainLayout().setVisibility(View.VISIBLE);
				break;
			case TouchScrollContainer.EVENT_MOVE_TO_PST_LEFT:
				break;
			case TouchScrollContainer.EVENT_MOVE_TO_PST_RIGHT:
				break;
			}
		}
	}

	public ViewManagerCenter getmViewManagerCenter() {
		return mViewManagerCenter;
	}

	public ViewManagerRight getmViewManagerRight() {
		return mViewManagerRight;
	}

	public ViewManagerLeft getmViewManagerLeft() {
		return mViewManagerLeft;
	}

	public void downloadMusic(MusicDTO musicDTO) {
		mViewManagerCenter.startDownloadMusic(musicDTO);
	}

	public MusicDTO getCurrentMusicDTO() {
		return mJingService.getMucisDTO();
	}

	public void musicPlay() {
		mJingService.musicPlay();
	}

	public void musicPause() {
		mJingService.musicPause();
	}

	public void musicNext(boolean needSendSkip) {
		mJingService.musicNext(needSendSkip);
	}
	
	public void musicLooping(boolean looping) {
		mJingService.musicLooping(looping);
	}

	public boolean musicFav(int ouid) {
		return mJingService.musicFav(ouid);
	}

	public void musicHate() {
		mJingService.musicHate();
	}

	public boolean isPlaying() {
		return mJingService != null && mJingService.isPlaying();
	}

	public boolean musicIsPrepared() {
		return mJingService != null && mJingService.musicIsPrepared();
	}

	public void musicRelease() {
		mJingService.musicRelease();
	}

	public boolean musicIsRelease() {
		if (mJingService == null) {
			return true;
		}
		return mJingService.musicIsRelease();
	}
	
	public long musicPst() {
		return mJingService.musicPst();
	}

	public void backToMainList() {
		setLocalMusicMode(false);
		mPersonalRadio = "";
		if (!isPlayingSubList || isOfflineMode) {
			return;
		}
		if (isFollowingOther) {
			quitFollow();
		}
		musicLooping(false);
		mJingService.backToMainList();
		isPlayingSubList = false;
		mViewManagerCenter.refreshRightButtonState();
		setJingTitleText(mJingService.getcmbt());
	}

	public void startMyMusicList() {
		if (isOfflineMode) {
			return;
		}
		setLocalMusicMode(false);
		mPersonalRadio = "";
		mJingService.startMyMusicList();
		mHandler.sendEmptyMessage(MSG_MUSIC_IS_BUFFERING);
	}

	public void startNewMainList(List<MusicDTO> list) {
		if (isFollowingOther) {
			quitFollow();
		}
		mHandler.sendEmptyMessage(MSG_MUSIC_IS_BUFFERING);
		isPlayingSubList = false;
		setLocalMusicMode(false);
		mPersonalRadio = "";
		isAiRadioPlaying = false;
		mViewManagerCenter.refreshRightButtonState();
		mJingService.startNewMainList(list);
	}

	private void quitFollow() {
		if (isOfflineMode
				|| 	isBeFollowedMode
				||  mLoginData == null 
				||  mLoginData.getUsr().isGuest()) {
			return;
		}
		isFollowingOther = false;
		PlayerManager.getInstance().setFollowingOther(isFollowingOther);
		if(mWebViewManager != null){
			mWebViewManager.notifyFollowListenLeave(""+mLoginData.getUsr().getId());
		}
	}

	public void startNewSubList(List<MusicDTO> list,int M,String cmbt,
			PlayListOverListener playListOverListener) {
		musicLooping(false);
		isPlayingSubList = true;
		setLocalMusicMode(false);
		mPersonalRadio = "";;
		isAiRadioPlaying = false;
		mHandler.sendEmptyMessage(MSG_MUSIC_IS_BUFFERING);
		mJingService.startNewSubList(list,M,cmbt,playListOverListener);
		mViewManagerCenter.refreshRightButtonState();
	}

	public boolean isPlayingSubList() {
		return isPlayingSubList;
	}

	public SettingManager getSettingManager() {
		return mJingService.getSettingManager();
	}

	public String onlineTimeToText(int mUserOnlineTotalTime) {
		if (mUserOnlineTotalTime > 3600) {
			return String
					.format(this.getString(R.string.user_online_total_time),
							mUserOnlineTotalTime / 3600,
							mUserOnlineTotalTime % 3600 / 60,
							mUserOnlineTotalTime % 60);
		} else {
			StringBuffer timeStringBuffer = new StringBuffer(
					this.getString(R.string.already_listen));
			if (mUserOnlineTotalTime > 60) {
				timeStringBuffer.append(" "
						+ (mUserOnlineTotalTime % 3600 / 60)
						+ this.getString(R.string.minute));
			}
			timeStringBuffer.append(" " + (mUserOnlineTotalTime % 60)
					+ this.getString(R.string.second));
			return timeStringBuffer.toString();
		}
	}
	
	public interface ActivityCallback {
		public void onActivityCallback(String[] params);
	}

	public void setActivityCallback(ActivityCallback activityCallback) {
		this.mActivityCallback = activityCallback;
	}

	public void addAvatarImageView(ImageView avatarView) {
		mSetAvatarView.add(avatarView);
	}

	public void deleteAvatarImageView(ImageView avatarView) {
		mSetAvatarView.remove(avatarView);
	}

	public void postQuitInfo() {
		if (mLoginData == null || mLoginData.getUsr().isGuest()) {
			return;
		}
		try {
			mLoginRegisterManager.getLoginDataDTO()
			.getPld().setCmbt(PlayerManager.getInstance().getmCurrentCmbt());
			mLoginRegisterManager.getLoginDataDTO().setPld(UserPlayingDTO.fromMusicDTO(mCurrentMusicDTO));
		} catch (Exception e) {
			e.printStackTrace();
		}
		final String uid = "" + getUserId();
		final HashMap<Object, Object> params = new HashMap<Object, Object>();
		final int time = PlayerManager.getInstance().musicPst() / 1000;
		params.put("uid", "" + uid);
		final MusicDTO mCurrentMusicDTO = PlayerManager.getInstance().getMainListLastDTO();
		if (mCurrentMusicDTO != null) {
			params.put("tid",mCurrentMusicDTO.getTid());
		}
		params.put("cmbt", PlayerManager.getInstance().getmCurrentCmbt());
		params.put("ct", "" + time);
		Log.e("kid_debug", "ct: " + time);
		final LoginDataDTO user = mLoginData;
		new Thread() {
			public void run() {
				if (mCurrentMusicDTO != null && time > 10) {
					user.setPld(JingTools.musicDto2UserPlayingDTO(mCurrentMusicDTO,PlayerManager.getInstance().getCmbt(),time,mLoginData.getUsr().getId()));
					PlayerManager.getInstance().postCurrentMusicDTO(params);
				}
			};
		}.start();
	}
	
	public void searchMainListByCmbt(final String cmbt,int indexOfServer,final boolean needStart,final SearchCallBack searchCallBack) {
		mViewManagerRight.setSearchText(cmbt);
		SearchCallBack tmpSearchCallBack = new SearchCallBack() {
			
			@Override
			public void callBack(ResultResponse<ListResult<MusicDTO>> resultResponse) {
				if (isActivityShowing) {
					if (resultResponse != null 
							&&  resultResponse.getResult() != null) {
						final String hint = resultResponse.getResult().getHint();
						if (JingTools.isValidString(hint)) {
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									Toast.makeText(MainActivity.this, hint, 1).show();
								}
							});
						}
						if (!resultResponse.getResult().getItems().isEmpty()) {
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									if (cmbt.lastIndexOf('+') == cmbt.length() - 1) {
										setJingTitleText(cmbt.substring(0, cmbt.length() - 1));
									}else{
										setJingTitleText(cmbt);
									}
									mViewManagerCenter.refreshRadioImage();
									backToMainList();
								}
							});
						}
					}
				}
				if (searchCallBack != null) {
					searchCallBack.callBack(resultResponse);
				}
			}
		};
		if (cmbt.lastIndexOf('+') == cmbt.length() - 1) {
			mJingService.searchMainListByCmbt(cmbt.substring(0, cmbt.length() - 1), indexOfServer, needStart,tmpSearchCallBack);
		}else{
			mJingService.searchMainListByCmbt(cmbt, indexOfServer, needStart,tmpSearchCallBack);
		}
		
	}
	
	public void doSearchKeyWord(String keyword,final boolean needChangeCmbt,int indexOfserver,
			final SearchCallBack callBack) {
		if (isSearching) {
			return;
		}
		isSearching = true;
		Log.e("kid_debug","doSearchKeyWord");
		SearchCallBack lockSearchCallBack = new SearchCallBack() {
			@Override
			public void callBack(ResultResponse<ListResult<MusicDTO>> resultResponse) {
				Log.e("kid_debug","doSearchKeyWord callBack");
				if (callBack != null) {
					callBack.callBack(resultResponse);
				}
				isSearching = false;
			}
		};
		mJingService.doSearchKeyWord(keyword,needChangeCmbt,indexOfserver,lockSearchCallBack);
	}

	public String getLocalMacAddress() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}
	
	public int getConnectivityState() {
		ConnectivityManager connManager = (ConnectivityManager) this
				.getSystemService(CONNECTIVITY_SERVICE);
		// 获取代表联网状态的NetWorkInfo对象
		NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
		if (networkInfo == null) {
			return CONNECT_STATE_NOTHING; 
		}
		
		// 获取当前的网络连接是否可用
		boolean available = networkInfo.isAvailable();
		if (!available) {
			return CONNECT_STATE_NOTHING;
		}
		State state;
		networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		if (networkInfo != null) {
			state = networkInfo.getState();
			if (State.CONNECTED == state) {
				return CONNECT_STATE_WIFI;
			}
		}
		networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		if (networkInfo != null) {
			state = networkInfo.getState();
			if (State.CONNECTED == state) {
				int netSubType = networkInfo.getSubtype();
				switch (netSubType) {
				case TelephonyManager.NETWORK_TYPE_GPRS:
					return CONNECT_STATE_GPRS;
				case TelephonyManager.NETWORK_TYPE_EDGE:
				case TelephonyManager.NETWORK_TYPE_CDMA:
					return CONNECT_STATE_2G;
				default:
					return CONNECT_STATE_3G;
				}
			}
		}
		networkInfo = connManager.getNetworkInfo(ConnectivityManager.TYPE_BLUETOOTH);
		if (networkInfo != null) {
			state = networkInfo.getState();
			if (State.CONNECTED == state) {
				return CONNECT_STATE_BLUE_TOOTH;
			}
		}
		return CONNECT_STATE_NOTHING;
	}

	public void setupWebView() {
		mWebViewManager.setupWebView();
	}

	public Integer getUserId(){
		try {
			return getmLoginData().getUsr().getId();
		} catch (Exception e) {
			return 0;
		}
	}
	
	public void destoryWebView() {
		mWebViewManager.destory();
	}

	public void sendMessage(String id, String uid, String message) {
		if (mWebViewManager != null) {
			mWebViewManager.sendMessage(id, uid, message);
		}
	}

	public void tickerNotifyChange(int count) {
		mViewManagerLeft.tickerNotifyChange(count);
	}

	public void followListenRequest(final String[] strs) {
		try {
			if (mWebViewManager != null) {
				OnClickListener selected = new OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case AlertDialog.BUTTON_POSITIVE:
							KTC.rep("FollowListening", "RecievedRequest", "Agree");
							isBeFollowedMode = true;
							mJingService
									.addPlayerStateChangeListener(MainActivity.this);
							String act = isPlaying() ? MusicFollowDTO.PlayAction
									: MusicFollowDTO.PauseAction;
							String message = musicDTOtoMessage(act,
									mJingService.getMucisDTO(),
									(mJingService.musicPst() / 1000));
							if (JingTools.isValidString(message)) {
								mWebViewManager.followListenResponseAuthorize(true,
										Integer.parseInt(strs[0]), mLoginData
												.getUsr().getId(), message);
							}
							break;
						case AlertDialog.BUTTON_NEGATIVE:
							KTC.rep("FollowListening", "RecievedRequest", "Disagree");
							if (mSelectDialogHasCountDown != null
									&& mSelectDialogHasCountDown.isShowing()) {
								mWebViewManager.followListenResponseAuthorize(
										false, Integer.parseInt(strs[0]),
										mLoginData.getUsr().getId(), null);
							}
							break;
						}

					}
				};
				showSelectDialog(strs[1] + " 想让你带着Ta听音乐", 15,"同意", "取消", selected,false);
				mHandler.postDelayed(new Runnable() {

					@Override
					public void run() {
						if (mSelectDialogHasCountDown != null && mSelectDialogHasCountDown.isShowing()) {
							mSelectDialogHasCountDown.cancel();
						}
					}
				}, 15000);
			}
		} catch (Exception e) {
		}
	}

	public void showSelectDialog(final String message, final int countDown,String positiveText,
			String negativeText, final OnClickListener listener,boolean cancelable) {
		if (mSelectDialogHasCountDown != null)  {
			mSelectDialogHasCountDown.dismiss();
		}
		mSelectDialogHasCountDown = new AlertDialog.Builder(this)
		.create();
		mSelectDialogHasCountDown.setCancelable(cancelable);
		mSelectDialogHasCountDown.setMessage(message);
		mSelectDialogHasCountDown.setButton(AlertDialog.BUTTON_POSITIVE, positiveText,
				listener);
		mSelectDialogHasCountDown.setButton(AlertDialog.BUTTON_NEGATIVE, negativeText,
				listener);
		if (listener != null) {
			mSelectDialogHasCountDown.setOnCancelListener(new OnCancelListener() {

				@Override
				public void onCancel(DialogInterface dialog) {
					listener.onClick(mSelectDialogHasCountDown, AlertDialog.BUTTON_NEGATIVE);
				}
			});
		} else {
			mSelectDialogHasCountDown.setOnCancelListener(null);
		}
		if (countDown > 0) {
			mSelectDialogHasCountDown.setMessage(""+ message + ",剩余"+ countDown +"秒" );
			final AlertDialog dialog = mSelectDialogHasCountDown;
			mHandler.post(new Runnable() {
				private int time = countDown;
				@Override
				public void run() {
					if (dialog != mSelectDialogHasCountDown) {
						return;
					}
					time--;
					mSelectDialogHasCountDown.setMessage(""+message+",剩余"+ time +"秒" );
					if (time <= 0 || !mSelectDialogHasCountDown.isShowing()) {
						mSelectDialogHasCountDown.dismiss();
						if (listener != null) {
							listener.onClick(mSelectDialogHasCountDown, AlertDialog.BUTTON_NEGATIVE);
						}
						return;
					}
					mHandler.postDelayed(this,1000);
				}
			});
		}else{
			mSelectDialogHasCountDown.setMessage(""+ message);
		}
		mSelectDialogHasCountDown.show();
	}

	public void followListenRequestOther(final String id, final String nick) {
		// Toast.makeText(getApplicationContext(), "跟听" + nick, 1).show();
		if (mSelectDialogHasCountDown != null) {
			mSelectDialogHasCountDown.dismiss();
		}
		OnClickListener selected = new OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case AlertDialog.BUTTON_POSITIVE:
					mWebViewManager.followListenRequest(mLoginData.getUsr()
							.getId(), id);
					try {
						mFollowListenRequestUid = Integer.parseInt(id);
					} catch (Exception e) {
					}
					mFollowListenRequestNick = nick;
					break;
				}

			}
		};
		showSelectDialog("你是否要跟听 " + nick, 15,"跟听", "取消", selected,false);
	}

	private String musicDTOtoMessage(String action, MusicDTO m, int pst) {
		return MusicFollowDTO.dto2Message(action, m, pst);
	}

	private MusicFollowDTO messageToMusicDTO(String message) {
		return new MusicFollowDTO(message);
	}

	public void sendCurrentMusicInfoForFollower() {
		if (mWebViewManager != null) {
//			String act = isPlaying() ? MusicFollowDTO.PlayAction
//					: MusicFollowDTO.PauseAction;
//			String message = musicDTOtoMessage(act, mJingService.getMucisDTO(),0);
			String message = musicDTOtoMessage(MusicFollowDTO.PlayAction, mJingService.getMucisDTO(),0);
			if (JingTools.isValidString(message)) {
				mWebViewManager.sendMessageToFollower(""
						+ mLoginData.getUsr().getId(), message);
			}
		}
	}

	public boolean isFollowingOther(){
		return isFollowingOther;
	}
	public boolean isBeFollowedMode(){
		return isBeFollowedMode;
	}
	
	public void followListenRequest(int uid, int ouid, String nick) {
		if (isFollowingOther) {
			return;
		}
		if (mSelectDialogHasCountDown != null) {
			mSelectDialogHasCountDown.dismiss();
		}
		mSelectDialogHasCountDown = new AlertDialog.Builder(this)
				.create();
		mSelectDialogHasCountDown.setCancelable(false);
		mSelectDialogHasCountDown.setMessage("正在发送请求,剩余"+ 15 +"秒" );
		mSelectDialogHasCountDown.show();
		final AlertDialog dialog = mSelectDialogHasCountDown;
		mHandler.post(new Runnable() {
			private int time = 15;
			@Override
			public void run() {
				if (dialog != mSelectDialogHasCountDown) {
					return;
				}
				time--;
				mSelectDialogHasCountDown.setMessage("正在发送请求,剩余"+ time +"秒" );
				if (time <= 0 || !mSelectDialogHasCountDown.isShowing()) {
					mSelectDialogHasCountDown.dismiss();
					return;
				}
				mHandler.postDelayed(this,1000);
			}
		});
		mFollowListenRequestUid = ouid;
		mFollowListenRequestNick = nick;
		if (mWebViewManager != null) {
			mWebViewManager.followListenRequest(uid, ""+ouid);
		}
	}

	public void followListenResponseAuthorizeRefuse() {
		if(mSelectDialogHasCountDown != null){
			mSelectDialogHasCountDown.cancel();
		}
		Toast.makeText(this, "" + mFollowListenRequestNick +"暂时不想让你跟听", 1).show();
		mFollowListenRequestUid = 0;
		mFollowListenRequestNick = null;
	}
	
	public void followListenKickLeave(String hostid) {
		if (!(""+mFollowListenRequestUid).equals(hostid)) {
			return;
		}
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				Toast.makeText(MainActivity.this, "" + mFollowListenRequestNick + "停止让你跟听Ta", 1).show();
				leaveFollowMode();
			}
		});
	}

	public void doPlayFollowerMusic(boolean startFollow,String message) {
		if (startFollow) {
			isFollowingOther = true;
			PlayerManager.getInstance().setFollowingOther(isFollowingOther);
			if (mSelectDialogHasCountDown != null) {
				mSelectDialogHasCountDown.dismiss();
			}
			changeData(true,
					new ChangeDataAnimateCallBack() {

						@Override
						public void doChangeData() {
							mViewManagerCenter.removeAllViewsAddNew(null);
						}
					});
			setSubListTitle("正在跟听"+mFollowListenRequestNick);
			setJingTitleText(mSubListTille);
			Toast.makeText(this, "正在跟听"+mFollowListenRequestNick, 1).show();
		}
		if (!isFollowingOther) {
			return;
		}
		MusicFollowDTO musicFollowDTO = messageToMusicDTO(message);
		MusicDTO musicDTO = musicFollowDTO.getMusicDto();
		if (musicDTO != null) {
			ArrayList<MusicDTO> musicList = new ArrayList<MusicDTO>();
			musicList.add(musicDTO);
			startNewSubList(musicList,Constants.UNKNOWN_M_VALUE,Constants.UNKNOWN_CMBT_VALUE, null);
			mJingService.setNeedSeekTimeOnPerpared(musicDTO.getPst());
			mJingService.setNeedPauseOnPerpared(musicFollowDTO.PauseAction
					.equals(musicFollowDTO.getAction()));
		} else {
			if (musicFollowDTO.PauseAction.equals(musicFollowDTO.getAction())) {
				musicPause();
			} else if (musicFollowDTO.PlayAction.equals(musicFollowDTO
					.getAction())) {
				musicPlay();
			}
		}
	}

	@Override
	public void onPlayerStateChange(PlayerManager pm) {
		if (pm == null) {
			return;
		}
		pm.setFollowingOther(isFollowingOther);
		if (isBeFollowedMode) {
			if (mCurrentMusicDTO != pm.getLastDTO()) {
				mCurrentMusicDTO = pm.getLastDTO();
				sendCurrentMusicInfoForFollower();
				isBeFollowedPlaying = true;
			} else {
				if (isBeFollowedPlaying != pm.isPlaying()) {
					isBeFollowedPlaying = pm.isPlaying();
					if (isBeFollowedPlaying) {
						mWebViewManager.sendMessageToFollower(""
								+ mLoginData.getUsr().getId(), "playing");
					} else {
						mWebViewManager.sendMessageToFollower(""
								+ mLoginData.getUsr().getId(), "pause");
					}
				}
			}
		}
	}

	public interface ChangeDataAnimateCallBack {
		public void doChangeData();
	}

	public ViewGroup getAnimateContainer() {
		return mGuideContainer;
	}

	public void showShareView(MusicDTO musicDTO) {
		showShareView(new SharedMusicDTO(musicDTO),0);
	}
	public void showShareView(SharedMusicDTO musicDTO) {
		showShareView(musicDTO,0);
	}
	
	public void showShareView(SharedMusicDTO musicDTO,int paddingButtom) {
		if (mShareViewManager == null) {
			mShareViewManager = new ShareViewManager(this);
		}
		mShareViewManager.showShareView(musicDTO,paddingButtom);
	}

	public void leaveFollowMode() {
		backToMainList();
	}

	public void addFollower(String id, String nick, String avatar) {
		if (isBeFollowedMode) {
//			mViewManagerCenter.getmPlayingViewManager().addFollower(id, nick,
//					avatar);
			mMusicInfoViewManager.addFollower(id, nick,
					avatar);
		}
	}
	
	public void setBeFollowedMode(boolean b){
		isBeFollowedMode = b;
		if (!isBeFollowedMode) {
			mJingService.removePlayerStateChangeListener(this);
		}
	}

	public void removeFollower(String id,String nick) {
		if (isFollowingOther && (""+mFollowListenRequestUid).equals(id)) {
			Toast.makeText(this, "主机已经离开跟听", 1).show();
			leaveFollowMode();
			return;
		}
		Toast.makeText(this, ""+ nick +"已经离开跟听", 1).show();
		mMusicInfoViewManager.removeFollower(id);
	}
	
	public void kickFollower(int uid,String touniques){
		if (mWebViewManager != null) {
			mWebViewManager.notifyFollowListenKickLeave(uid,touniques);
		}
	}

	public void disconnect() {
		showSelectDialog("您已在别处登录",5, null, null,null,false);
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				logout();
			}
		}, 5*1000);
	}

	public void connectedSuccess() {
		isNeedReconnect = false;
		isReconnecting  = false;
	}
	
	public void socketOnDisconnect() {
		if (mLoginData != null && !mLoginData.getUsr().isGuest()) {
			isNeedReconnect = true;
			pollingReconnect(false);
		}
	}

	public void frdOnline(SocketPUserSignedonDTO sDTO) {
		Message msg = new Message();
		msg.what = MSG_TOAST_TEXT;
		msg.obj = "" + sDTO.getNick() +" 上线了";
		mHandler.sendMessage(msg);
		if (mViewManagerCenter.getFriendsViewManager() != null) {
			mViewManagerCenter.getFriendsViewManager().frdOnline(sDTO);
		}
		mViewManagerLeft.getLeftViewAdapter().getmUserHomePageAdapter().updatePersonalDataOLState(sDTO.getUid(), true);
	}

	public void frdOff(SocketPUserSignedoffDTO sDTO) {
		Message msg = new Message();
		msg.what = MSG_TOAST_TEXT;
		msg.obj = "" + sDTO.getNick() +" 离线了";
		mHandler.sendMessage(msg);
		if (mViewManagerCenter.getFriendsViewManager() != null) {
			mViewManagerCenter.getFriendsViewManager().frdOff(sDTO);
		}
		mViewManagerLeft.getLeftViewAdapter().getmUserHomePageAdapter().updatePersonalDataOLState(sDTO.getUid(), false);
	}

	public void setSleepTime(int progress) {
		if (mJingService == null) {
			return;
		}
		mJingService.setSleepTime(progress);
	}

	public int getSleepTime() {
		return mJingService == null ? 0 : mJingService.getSleepTime();
	}
	
	public void refreshPlayHighMode(){
		if (mJingService == null) {
			return;
		}
		switch (SettingManager.getInstance().getmMusicQualityMode()) {
		case SettingManager.MUSIC_QUALITY_MODE_LOW:
			mJingService.setIsHighMode(false);
			break;
		case SettingManager.MUSIC_QUALITY_MODE_HIGH:
			mJingService.setIsHighMode(true);
			break;
		case SettingManager.MUSIC_QUALITY_MODE_AUTO:
			mJingService.setIsHighMode(getConnectivityState() == CONNECT_STATE_WIFI);
			break;
		}
	}
	
	public void clearCacheFiles(){
		try {
			LocalCacheManager.getInstance().clearCacheFiles();
			SettingManager.getInstance().clearAllGuide();
		} catch (Exception e) {
		}
	}

	public void setAnimateContainerInterrupter(boolean b) {
		isAnimateContainerInterrupterAll = b;
	}

	public void loginByToken(String at, String rt) {
		new MainViewAsyncTask(ASYNC_TASK_TYPE_USER_LOGIN_BY_TOKEN).execute(at,rt);
	}

	public void onNetworkStateChange(boolean needChoice) {
		if (mLoginData != null && mLoginData.getUsr().isGuest()) {
			return;
		}
		final int rs = getConnectivityState();
		if (rs != CONNECT_STATE_WIFI && rs != CONNECT_STATE_BLUE_TOOTH && !isOfflineMode) { //无网提示进入离线
			if (isLocalMusicMode || (!needChoice && rs == CONNECT_STATE_NOTHING)) {
				startOfflineMode();
				return;
			}
			if (mOfflineAlertDialog == null) {
				mOfflineAlertDialog = new AlertDialog.Builder(this)
				.create();
				mOfflineAlertDialog.setCancelable(false);
				mOfflineAlertDialog.setMessage("是否进入离线模式");
				DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						switch (which) {
						case DialogInterface.BUTTON_POSITIVE:
							startOfflineMode();
							break;
						case DialogInterface.BUTTON_NEGATIVE:
							if (getConnectivityState() == CONNECT_STATE_NOTHING) {
								logout();
							}else{
								mJingService.musicActive(false);
							}
							break;

						}
					}
				};
				mOfflineAlertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "进入离线模式",
						listener);
				mOfflineAlertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
						listener);
			}
			if (!mOfflineAlertDialog.isShowing()) {
				mOfflineAlertDialog.show();
				mJingService.musicWaiting();
			}
		}else{	//有网检查是否处于离线
			if(rs != CONNECT_STATE_NOTHING && rs != CONNECT_STATE_BLUE_TOOTH){
				refreshPlayHighMode();
				pollingReconnect(true);
				if (isOfflineMode) { //离线模式自动登录
					final String a = SettingManager.getInstance().getmAtoken();
					final String r = SettingManager.getInstance().getmRtoken();
					if (JingTools.isValidString(a)
							&& JingTools.isValidString(r)) {
						new Thread() {
							@Override
							public void run() {
								ResultResponse loginRs = mLoginRegisterManager.loginByToken(a, r);
								if (loginRs != null 
										&& loginRs.isSuccess()) {
									mLoginData = mLoginRegisterManager.getLoginDataDTO();
									isOfflineMode = false;
									mHandler.post(new Runnable() {
										
										@Override
										public void run() {
											mViewManagerRight.setOffLine(isOfflineMode);
											mViewManagerCenter.refreshRightButtonState();
											Toast.makeText(MainActivity.this, "重新上线了", 1).show();
											if (isActivityShowing && mViewManagerCenter.getmMusicViewManager().getLocalMusicList().isEmpty()) {
												if (PlayerManager.getInstance().getMainList().isEmpty()) {
													startMyMusicList();
												}else{
													backToMainList();
												}
											}
										}
									});
								}
							}
						}.start();
					}
				}
			}
		}
	}
	
	protected void startOfflineMode() {
		if (mLoginData == null || mLoginData.getUsr().isGuest()) {
			ResultResponse loginRs = mLoginRegisterManager.loginByCache();
			if (loginRs.isSuccess()) {
				isOfflineMode = true;
				mViewManagerRight.setOffLine(isOfflineMode);
				mLoginData = mLoginRegisterManager.getLoginDataDTO();
				mLoginViewManager.loginOk();
				Toast.makeText(MainActivity.this, "开始离线模式", 1).show();
			}else{
				Toast.makeText(MainActivity.this, "无可用账户", 1).show();
				logout();
				return;
			}
		}else{
			isOfflineMode = true;
			mViewManagerRight.setOffLine(isOfflineMode);
			if (isLocalMusicMode) {
				mViewManagerCenter.refreshRightButtonState();
			}else{
				mJingService.musicActive(true);
				mViewManagerCenter.getmMusicViewManager().playLocalMusic();
			}
		}
	}

	public boolean isOfflineMode(){
		return isOfflineMode;
	}
	
	private void registerNetworkStateReceiver() {
		networkReceiver = new NetworkStateReceiver();
		IntentFilter filter = new IntentFilter();
		filter.addAction(CONNECTIVITY_CHANGE_ACTION);
		filter.setPriority(IntentFilter.SYSTEM_HIGH_PRIORITY);
		registerReceiver(networkReceiver, filter);
	}
	
	private void unregisterNetworkStateReceiver() {
		unregisterReceiver(networkReceiver);
	}
	
	public class NetworkStateReceiver extends BroadcastReceiver{
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if(TextUtils.equals(action, CONNECTIVITY_CHANGE_ACTION)){//网络变化的时候会发送通知
				if (mJingService != null) {
					onNetworkStateChange(true);
				}
				return;
			}
		}
	}

	public void changeFontSize() {
		mViewManagerCenter.getmPlayingViewManager().resizeMusicInfoText();
		mViewManagerLeft.getLeftViewAdapter().refreshTextSize();
	}
	
	public boolean isCurrentDtoPlaying(String tid) {
		if (mJingService == null) {
			return false;
		}
		MusicDTO currentMusicDTO = mJingService.getMucisDTO();
		if (currentMusicDTO != null) {
			boolean rs = tid.equals(""+currentMusicDTO.getTid());
			return rs;
		}
		return false;
	}

	public void toastOffLine() {
		Message msg = new Message();
		msg.what = MSG_TOAST_TEXT;
		msg.obj = "当前为离线模式";
		mHandler.sendMessage(msg);
	}

	public void playPersonalRadio(final String name, final ImageView imageButton, final int ridPlay, final int ridPause) {
		if (isOfflineMode()) {
			toastOffLine();
			if (imageButton != null) {
				imageButton.setImageResource(ridPlay);
			}
			return;
		}
		if (JingTools.isValidString(name)) {
			if (mLoginData.getUsr().getNick().equals(name)) {
				setSubListTitle("红心电台");
				Toast.makeText(this, "正在准备红心电台...", 1).show();
				setJingTitleText(mSubListTille);
			}else{
				setSubListTitle("正在收听" + name + "喜欢的歌");
				setJingTitleText(mSubListTille);
			}
			PlayListOverListener playListOverListener = new PlayListOverListener() {
				private int index;
				@Override
				public void setIndexOfServer(int indexOfServer) {
					index = indexOfServer;
				}
				@Override
				public void onPlayListOver(PlayerManager pm) {
					final PlayListOverListener listener = this;
					doSearchKeyWord("@"+name, false, index, new SearchCallBack() {
						
						@Override
						public void callBack(final ResultResponse<ListResult<MusicDTO>> resultResponse) {
							if (resultResponse != null && resultResponse.isSuccess()) {
								final List<MusicDTO> list = resultResponse.getResult().getItems();
								int total = resultResponse.getResult().getTotal();
								if (total > 0) {
									index = (resultResponse.getResult().getSt() + resultResponse.getResult().getPs())%total;
								}
								if (list != null && !list.isEmpty()) {
									mHandler.post(new Runnable() {
										@Override
										public void run() {
											Collections.shuffle(list);
											startNewSubList(list,resultResponse.getResult().getM(),"", listener);
											mPersonalRadio = name;
											if (imageButton != null) {
												imageButton.setImageResource(ridPause);
											}
										}
									});
								}
							}
						}
					});
				}
				@Override
				public int getIndexOfServer() {
					return index;
				}
			};
			playListOverListener.onPlayListOver(null);
		}
	}

	public boolean isPlayingPersonalRadio(String name) {
		return mPersonalRadio.equals(name);
	}

	public void firstSearchDone() {
		isFirstSearchDone = true;
		mViewManagerCenter.setBackButtonFunction(null);
		mLoginData.getUsr().setNewbie(0);
		mLoginViewManager.loginOk();
		if (!mLoginData.getUsr().isGuest()) {
			new Thread(){
				public void run() {
					HashMap<Object, Object> params = new HashMap<Object, Object>();
					params.put("uid", "" + mLoginData.getUsr().getId());
					ResultResponse<String> rs = UserRequestApi.registerCompleted(params);
					KTC.rep("Account", "Activation", "{}");
//					if (rs != null && rs.isSuccess()) {
//						 mLoginData.getUsr().setNewbie(0);
//					}
				};
			}.start();
		}
	}
	public boolean isFirstSearchDone() {
		return isFirstSearchDone;
	}

	public boolean isLocalMusicMode() {
		return isLocalMusicMode;
	}
	public void setLocalMusicMode(boolean b) {
		isLocalMusicMode = b;
		mJingService.setLocalMusicMode(b);
	}
	
	public String getSubListTitle() {
		return mSubListTille;
	}
	public void setSubListTitle(String subListTille) {
		if(!((""+subListTille)).startsWith("正在跟听")) {
			quitFollow();
		}
		mSubListTille = subListTille;
	}

	public void changeAvatar(final ImageView imageView) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		setActivityCallback(new ActivityCallback() {

			@Override
			public void onActivityCallback(String[] params) {
				if (params.length <= 0) {
					return;
				}
				final String file = params[0];
				FileInputStream fileInputStream = null;
				try {
					if (imageView != null) {
						fileInputStream = new FileInputStream(file);
						if (imageView.equals(mViewManagerLeft.getLeftViewAdapter().getAvatarView())) {
							imageView.setScaleType(ScaleType.FIT_XY);
							imageView.setImageBitmap(BitmapFactory
									.decodeStream(fileInputStream));
						}else{
							imageView.setImageBitmap(AsyncImageLoader.toRound(BitmapFactory
									.decodeStream(fileInputStream)));
						}
					}
					Toast.makeText(getApplicationContext(), "正在上传头像...", 1).show();
					new Thread(){
						public void run() {
							HashMap<Object, Object> 	map = new HashMap<Object, Object>();
							map.put("uid", "" + mLoginData.getUsr().getId());
							map.put("file", new File(file));
							ResultResponse<String> resultResponse = UserRequestApi.userAvatarUpload(map);
							Message msg = new Message();
							msg.what = MSG_TOAST_TEXT;
							KTC.rep("Setting", "UpdateAvatar", "");
							if (resultResponse.isSuccess()) {
								msg.obj = "上传头像成功";
								setNewAvatar(file.toString());
								mLoginData.getUsr().setAvatar(resultResponse.getResult());
								//mViewManagerLeft.getLeftViewAdapter().notifyDataSetChanged();
							}else{
								msg.obj = "上传头像失败,请重试";
								mTempAvatar = null;
							}
							mHandler.sendMessage(msg);
						};
					}.start();
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}finally{
					if (fileInputStream != null) {
						try {
							fileInputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		startActivityForResult(intent,
				MainActivity.START_ACTIVIY_REQUEST_CODE_PICK_AVATER);
	}

	public void changeCover(final ImageView imageView) {
		Intent intent = new Intent();
		intent.setType("image/*");
		intent.setAction(Intent.ACTION_GET_CONTENT);
		setActivityCallback(new ActivityCallback() {

			@Override
			public void onActivityCallback(String[] params) {
				if (params.length <= 0) {
					return;
				}
				
				final String file = params[0];
				FileInputStream fileInputStream = null;
				try {
					if (imageView != null) {
						fileInputStream = new FileInputStream(file);
						int width = imageView.getWidth();
						imageView.setImageBitmap(Bitmap.createScaledBitmap(BitmapFactory
								.decodeStream(fileInputStream), width, width, false));
					}
					Toast.makeText(getApplicationContext(), "正在上传用户封面...", 1).show();
					new Thread(){
						public void run() {
							HashMap<Object, Object> 	map = new HashMap<Object, Object>();
							map.put("uid", "" + mLoginData.getUsr().getId());
							map.put("file", new File(file));
							ResultResponse<String> resultResponse = UserRequestApi.userCoverUpload(map);
							Message msg = new Message();
							msg.what = MSG_TOAST_TEXT;
							KTC.rep("Setting", "UpdateCover", "");
							if (resultResponse.isSuccess()) {
								msg.obj = "上传用户封面成功";
							}else{
								msg.obj = "上传用户封面失败,请重试";
							}
							mHandler.sendMessage(msg);
						};
					}.start();
				} catch (OutOfMemoryError e) {
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}finally{
					if (fileInputStream != null) {
						try {
							fileInputStream.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		startActivityForResult(intent,MainActivity.START_ACTIVIY_REQUEST_CODE_PICK_COVER);
	}

	public boolean isMyUrlUpdata(String url) {
		return mTempAvatar != null && url != null && url.equals(CustomerImageRule.ID2URL(
				Constants.ID2URL_KEY_WORD_AVATAR,
				mLoginData.getUsr().getAvatar(),
				Constants.ID2URL_DEFAULT_BITRATE_AVATAR));
	}

	public Bitmap getAvatarAfterChange() {
		Bitmap rsBitmap = null;
		InputStream is = null;
		try {
			is = new FileInputStream(new File(mTempAvatar));
			rsBitmap = BitmapFactory.decodeStream(is);
			is.close();
		} catch (Exception e) {
		} catch (OutOfMemoryError e) {
		}finally{
			if (is != null) {
				try {
					is.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return rsBitmap;
	}
	
	public synchronized void showGuide(int rid,FrameLayout.LayoutParams params,final CallBacker callBacker){
		mGuideContainer.removeAllViews();
		mGuideContainer.setVisibility(View.VISIBLE);
		mViewManagerCenter.getMainLayout().freeze();
		mGuideContainer.setBackgroundColor(0x88000000);
		ImageView guideView = new ImageView(this);
		guideView.setImageResource(rid);
		if (params == null) {
			params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
		}
		guideView.setLayoutParams(params);
		mGuideContainer.addView(guideView);
		mGuideContainer.setOnClickListener(new ViewGroup.OnClickListener() {
			@Override
			public void onClick(View v) {
				mGuideContainer.removeAllViews();
				mGuideContainer.setBackgroundColor(0);
				mGuideContainer.setVisibility(View.GONE);
				mViewManagerCenter.getMainLayout().unfreeze();
				if (callBacker != null) {
					callBacker.onCallBack();
				}
			}
		});
	}
	
	public interface CallBacker{
		public void onCallBack();
	}

	public void toastYouAreInFollow() {
		Toast.makeText(this, "正在跟听别人", 0).show();
	}
	
	public void startGuestHeartBeat() {
		stopGuestHeartBeat();
		mGuestHeartBeatTimer = new Timer();
		mGuestHeartBeatTimer.schedule(new TimerTask() {
			@Override
			public void run() {
				UserClickRequestApi.clickHeartbeat();
			}
		}, 0,Constants.DEFAULT_TIME_OF_GUEST_HEART_BEAT);
	}
	public void stopGuestHeartBeat() {
		if (mGuestHeartBeatTimer != null) {
			mGuestHeartBeatTimer.cancel();
		}
	}

	public void setPlayingTitleText() {
		if (	mJingService.isLooping()) {
			setJingTitleText("单曲循环");
		}else{
			if (isPlayingSubList()) {
				setJingTitleText(getSubListTitle());
			}else{
				setJingTitleText(PlayerManager.getInstance().getCmbt());
			}
		}
	}

	public void startCacheAnimation(long duration,final AnimationListener animationListener) {
		try {
			final View view = new View(this);
//			final Bitmap bitmap = AsyncImageLoader.convertViewToBitmap(mAllBaseView);
			mAllBaseView.setDrawingCacheBackgroundColor(0xff000000);
			mAllBaseView.setDrawingCacheEnabled(true);
			mAllBaseView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_LOW);
			mAllBaseView.buildDrawingCache(true);
			final Bitmap bitmap = mAllBaseView.getDrawingCache();
			view.setBackgroundDrawable(new BitmapDrawable(bitmap));
			((ViewGroup)mAllBaseView).addView(view);
			view.bringToFront();
			AlphaAnimation alphaAnimation = new AlphaAnimation(1,0);
			alphaAnimation.setDuration(duration);
			view.startAnimation(alphaAnimation);
			alphaAnimation.setAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationStart(Animation animation) {
					if (animationListener != null) {
						animationListener.onAnimationStart(animation);
					}
				}
				@Override
				public void onAnimationRepeat(Animation animation) {
					if (animationListener != null) {
						animationListener.onAnimationRepeat(animation);
					}
				}
				@Override
				public void onAnimationEnd(Animation animation) {
					if (animationListener != null) {
						animationListener.onAnimationEnd(animation);
					}
					((ViewGroup)mAllBaseView).removeView(view);
					try {
						mAllBaseView.setDrawingCacheEnabled(false);
						mAllBaseView.destroyDrawingCache();
					} catch (Exception e) {
					}
				}
			});
			return;
		} catch (Exception e) {
		} catch (OutOfMemoryError e) {
		}
		if (animationListener != null) {
			animationListener.onAnimationStart(null);
			animationListener.onAnimationRepeat(null);
			animationListener.onAnimationEnd(null);
		}
	}

	public boolean isCenterViewNeedBack() {
		if (mViewManagerCenter.getMainLayout().getPst() != 0) {
			centerViewBack();
			return true;
		}
		return false;
	}

	public long getAppStartTime() {
		if (mJingService == null) {
			return System.currentTimeMillis();
		}
		return mJingService.getAppStartTime();
	}

	public void addLoginListener(LoginStateChangeListener listener) {
		mLoginViewManager.addLoginListener(listener);
	}
	
	public void setVolumePercent(int progress) {
		mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress*mSystemMaxVolume/100,   AudioManager.FLAG_PLAY_SOUND);
	}
	public int getVolumePercent() {
		int currentVolume=mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
		KTC.rep("Home", "AdjustVolumeSldr", KTC.getProgress(currentVolume));
		return currentVolume*100/mSystemMaxVolume;
	}

	public void volumeChange(boolean b) {
		if (b) {
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE,AudioManager.FLAG_VIBRATE);
		}else{
			mAudioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER,AudioManager.FLAG_VIBRATE);
		}
	}
	public void showSearchView(){
		KTC.rep("Home", "OpenSearchMenu", "");
		final View oldView = mViewManagerCenter.getMainLayout();
		final View newView = mViewManagerRight.getMainLayout();
		Animation animationOut = null;
		Animation animationIn = null;
		animationOut = AnimationUtils.loadAnimation(this,
				R.anim.silde_away_to_left);
		animationIn = AnimationUtils.loadAnimation(this,
					R.anim.silde_in_from_right);
		final Animation tmpAnimationOut = animationOut;
		animationOut.setAnimationListener(new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mAllBaseView.getWindowToken(), 0);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (animation.equals(tmpAnimationOut)) {
					oldView.setVisibility(View.GONE);
				}
				mViewManagerRight.onShow();
			}
		});
		newView.setVisibility(View.VISIBLE);
		newView.startAnimation(animationIn);
		oldView.startAnimation(animationOut);
		mViewManagerCenter.refreshRightButtonState();
	}
	public void hideSearchView(){
		final View newView = mViewManagerCenter.getMainLayout();
		final View oldView = mViewManagerRight.getMainLayout();
		Animation animationOut = null;
		Animation animationIn = null;
		animationOut = AnimationUtils.loadAnimation(this,
				R.anim.silde_away_to_right);
		animationIn = AnimationUtils.loadAnimation(this,
				R.anim.silde_in_from_left);
		final Animation tmpAnimationOut = animationOut;
		animationOut.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mAllBaseView.getWindowToken(), 0);
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				if (animation.equals(tmpAnimationOut)) {
					oldView.setVisibility(View.GONE);
					mViewManagerCenter.refreshRightButtonState();
				}
			}
		});
		newView.setVisibility(View.VISIBLE);
		newView.startAnimation(animationIn);
		oldView.startAnimation(animationOut);
	}

	public boolean isAiRadioPlaying() {
		return isAiRadioPlaying;
	}

	public void showGuide() {
		if (mGuideManger == null) {
			mGuideManger = new GuideManger(this);
		}
		mGuideManger.show();
	}

}
