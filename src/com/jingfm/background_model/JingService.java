package com.jingfm.background_model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Binder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RecoverySystem.ProgressListener;
import android.os.SystemClock;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.UserAppRequestApi;
import com.jingfm.api.business.UserClickRequestApi;
import com.jingfm.api.business.UserPushRequestApi;
import com.jingfm.api.business.UserSearchApi;
import com.jingfm.api.helper.JsonHelper;
import com.jingfm.api.model.ChooseAlikeDTO;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.UserPlayingDTO;
import com.jingfm.api.model.push.PushMessageDTO;
import com.jingfm.background_model.PlayerManager.PlayListOverListener;
import com.jingfm.background_model.PlayerManager.PlayerStateChangeListener;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.JingTools;
import com.xiaomi.mipush.sdk.MiPushClient;
import com.xiaomi.mipush.sdk.MiPushClient.MiPushClientCallback;

public class JingService extends Service {
	public static final long DEAFULT_PUSH_SLEEP_TIME = 20 * 60 *1000;
	public static final int DEAFULT_NM_ID = 34560;
	public int mCurrentId;
	private final JingIBinder jingIBinder = new JingIBinder();
    private String TAG = "kid_debug";
    private static final String APP_ID = "1002787";
    private static final String APP_KEY = "460100281787";

	@Override
	public IBinder onBind(Intent intent) {
		return jingIBinder;
	}

	@Override
	public boolean onUnbind(Intent intent) {
		return super.onUnbind(intent);
	}

	public class JingIBinder extends Binder {
		public Service getService() {
			return JingService.this;
		}
	}

	// ========
	private Timer timer;
	private LoginRegisterManager mLoginRegisterManager;
	private PlayerManager mPlayerManager;
	private SharedPreferences mSharedPreferences;
	private String mSettingLocalCachePath;
	private PlayListOverListener mainListOverListener;
	private SettingManager mSettingManager;
	private Handler mHandler;
	private String mMacAddress;
	private String mAppVersionName;
	private PushRunnable mPushRunnable;
	private boolean isHostMode;
	private long mAppStartTime;
	private String mModelName;
	public static String sXiaomiPushRegID;

	@Override
	public void onCreate() {
		super.onCreate();
		if (!JingTools.isValidString(Constants.CHANNELID)) {
		ApplicationInfo ai;
			try {
				ai = getApplicationContext().getPackageManager().getApplicationInfo(  
						getApplicationContext().getPackageName(), PackageManager.GET_META_DATA);
				Object value = ai.metaData.get("TD_CHANNEL_ID");  
				if (value != null) {
					Constants.CHANNELID= value.toString();
					Log.e("CHANNELID",Constants.CHANNELID);
				}
			} catch (NameNotFoundException e) {
				e.printStackTrace();
			}  
		}
		try {
			mAppVersionName = getVersionName();
		} catch (Exception e) {
			e.printStackTrace();
		}
		mAppStartTime = System.currentTimeMillis();
		mMacAddress = getLocalMacAddress();
		mModelName = getModelName();
		Log.e("kid_debug","mMacAddress: " + mMacAddress + "\t" + "mModelName: " + mModelName);
		initSetting();
		initLogin();
		mainListOverListener = new PlayListOverListener() {
			public int mMainListIndexInServer;
			@Override
			public void setIndexOfServer(int indexOfServer) {
				mMainListIndexInServer = indexOfServer;
			}
			@Override
			public void onPlayListOver(final PlayerManager pm) {
				searchMainListByCmbt(mPlayerManager.getCmbt(),mMainListIndexInServer,true,null);
			}
			@Override
			public int getIndexOfServer() {
				return mMainListIndexInServer;
			}
		};
		mPlayerManager = PlayerManager.getInstance();
		mPlayerManager.initNotifyControl(this);
		mSettingManager = SettingManager.getInstance();
		mSettingManager.init(getApplicationContext());
		mPlayerManager.setmContext(getApplicationContext());
		mPlayerManager.setVisualizerDisableByModelName(mModelName);
		if (JingTools.isValidString(mSettingManager.getmLastUserId())) {
			stopPushModel();
		}
		
		LocalCacheManager.getInstance().initPath(
				Environment.getExternalStorageDirectory()
						+ mSettingLocalCachePath);
		AsyncImageLoader.getInstance().setLocalCacheManager(
				LocalCacheManager.getInstance());
		if (!SettingManager.getInstance().isNoNeedSendFirstRun()) {
			new Thread(){
				public void run() {
//					mac channel v
					HashMap<Object, Object> params = new HashMap<Object, Object>();
					if (mMacAddress != null) {
						params.put("mac", mMacAddress);
					}else{
						params.put("mac", "UNKNOWN");
					}
					params.put("channel", Constants.CHANNELID);
					try {
						params.put("v", getPackageManager().getPackageInfo("com.jingfm", 0).versionName);
					} catch (Exception e) {
						e.printStackTrace();
					}
					ResultResponse<String> rs = UserClickRequestApi.clickChanneldata(params);
					if (rs != null && rs.isSuccess()) {
						SettingManager.getInstance().setNoNeedSendFirstRun(true);
					}
				};
			}.start();
		}
		hideNotifyView();
		setupTelephonyListen();
		new Thread(){
			public void run() {
				init();
			};
		}.start();
	}

	private void setupTelephonyListen() {
		TelephonyManager telephonyManager =  (TelephonyManager)getApplicationContext().getSystemService(Service.TELEPHONY_SERVICE);
		//设置一个监听器
		telephonyManager.listen(mPlayerManager.getMyPhoneStateListener(), PhoneStateListener.LISTEN_CALL_STATE);
	}

	private String getModelName() {
		try {
			return new Build().MODEL;  
		} catch (Exception e) {
		}
		return "";
	}

	public String getLocalMacAddress() {
		WifiManager wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);
		WifiInfo info = wifi.getConnectionInfo();
		return info.getMacAddress();
	}

	private String getVersionName() throws Exception {
		// 获取packagemanager的实例
		PackageManager packageManager = getPackageManager();
		// getPackageName()是你当前类的包名，0代表是获取版本信息
		PackageInfo packInfo = packageManager.getPackageInfo(getPackageName(),
				0);
		String version = packInfo.versionName;
		return version;
	}

	public void stopPushModel() {
		if (mPushRunnable != null) {
			mPushRunnable.stopLoop();
		}
		try {
			MiPushClient.unsetAlias(getApplicationContext(), ""+mSettingManager.getmLastUserId(), null);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void startPushModel() {
		if (!JingTools.isValidString(mSettingManager.getmLastUserId())) {
			return;
		}
		try {
			MiPushClient.setAlias(getApplicationContext(), ""+mSettingManager.getmLastUserId(), null);
			MiPushClient.subscribe(getApplicationContext(), "User", null);
		} catch (Exception e) {
			e.printStackTrace();
		}
//		stopPushModel();
//		mPushRunnable = new PushRunnable();
//		mPushRunnable.startPush();
	}

	private void initSetting() {
		Context context = getApplicationContext();
		mSharedPreferences = context.getSharedPreferences("JingSetting",
				Context.MODE_PRIVATE);
		mSettingLocalCachePath = mSharedPreferences.getString(
				Constants.APP_SETTING_KEY_WEB_CACHE_PATH,
				Constants.APP_SETTING_VAL_WEB_CACHE_PATH);
	}

	private void initLogin() {
		mLoginRegisterManager = LoginRegisterManager.getInstance();
		mLoginRegisterManager.setPhoneInfo(mMacAddress, mAppVersionName);
	}

	public LoginRegisterManager getmLoginRegisterManager() {
		return mLoginRegisterManager;
	}

	public void setmLoginRegisterManager(
			LoginRegisterManager mLoginRegisterManager) {
		this.mLoginRegisterManager = mLoginRegisterManager;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		// Toast.makeText(getApplicationContext(), "JingService onStart", 0)
		// .show();
		super.onStart(intent, startId);
	}

	private void sendNotification(final String msg) {
//		timer = new Timer(true);
//		timer.schedule(new TimerTask() {
//		new Thread(){
//			public void run() {
				String tickerText = msg;
				// ResultResponse resultResponse =
				// mLoginRegisterManager.loginByUserName("android@jing.fm",
				// "123456");
				// ResultResponse resultResponse =
				// mLoginRegisterManager.loginByGuest();
				// if (resultResponse.isSuccess()) {
				// tickerText = "Guest: " +
				// mLoginRegisterManager.getLoginDataDTO().getUsr().getId();
				// }
				Notification notification = new Notification();
				// img
				notification.icon = R.drawable.ic_notification;
				// text
				notification.tickerText = tickerText;
				// audio
				notification.defaults = Notification.DEFAULT_SOUND;
				// click and auto_cancel
				notification.flags = Notification.FLAG_AUTO_CANCEL;
				long[] vibrate = { 0, 100, 200, 300 };
				notification.vibrate = vibrate;
				NotificationManager mNm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				// 点击顶部状态栏的消息后，MainActivity为需要跳转的页面
				Intent intent = new Intent(getApplicationContext(),
						MainActivity.class);
				PendingIntent pendingIntent = PendingIntent.getActivity(
						getApplicationContext(), 0, intent, 0);
				// 开启新的线程从服务器获取数据
				notification
						.setLatestEventInfo(JingService.this,
								getString(R.string.app_name), tickerText,
								pendingIntent);
				mCurrentId++;
				mNm.notify(mCurrentId%3 + DEAFULT_NM_ID, notification);
//			}
//		}.start();
//		}, 0);
	}

	@Override
	public void onDestroy() {
		try {
			LocalCacheManager.getInstance().clearTempFiles();
		} catch (Exception e) {
		}
		try {
			mPlayerManager.hideNotifyView(getApplicationContext());
		} catch (Exception e) {
		}
		try {
			mPlayerManager.unregisterMusicControllerReceiver(getApplicationContext());
		} catch (Exception e) {
		}
		super.onDestroy();
	}
	
	public void startMyMusicList() {
		UserPlayingDTO userPlayingDTO = mLoginRegisterManager.getLoginDataDTO()
				.getPld();
		if (userPlayingDTO != null) {
			if (mHandler != null) {
				Message msg = new Message();
				msg.what = MainActivity.MSG_UPDATE_SEARCH_TEXT;
				msg.obj = userPlayingDTO.getCmbt();
				mHandler.sendMessage(msg);
			}
			ArrayList<MusicDTO> list = new ArrayList<MusicDTO>();
			list.add(userPlayingDTO.toMusicDTO());
			mPlayerManager.setMainList(list);
			final UserPlayingDTO tmpUserPlayingDTO = userPlayingDTO;
			mPlayerManager.setCmbt(tmpUserPlayingDTO.getCmbt());
			searchMainListByCmbt(mPlayerManager.getCmbt(),0,false,null);
			mPlayerManager.startMainList(mainListOverListener);
			mPlayerManager.setMainListMusicPst(userPlayingDTO.getCt());
		}
	}
	
	public void doSearchKeyWord(final String cmbt,final boolean needChangeCmbt,final int indexOfserver,final SearchCallBack callBack) {
		new Thread() {
			String keyword;
			Integer tid = 0;
			public void run() {
				if (keyword == null) {
					keyword = JingTools.trimSign(cmbt);
				}
				if (!JingTools.isValidString(keyword)) {
					return;
				}
				if (needChangeCmbt && mainListOverListener != null) {
					mainListOverListener.setIndexOfServer(0);
				}
				HashMap<Object, Object> params = new HashMap<Object, Object>();
				params.put("q", keyword);
				params.put("ps",""+ Constants.DEFAULT_MAIN_MUSIC_LIST_NUM_OF_LOAD);
				params.put("st", ""+indexOfserver);
				params.put("mc", Boolean.valueOf(needChangeCmbt)); //是否为从列表
				if (tid != 0) {
					params.put("tid", tid);
				}
				ResultResponse<ListResult<MusicDTO>> resultResponse = null;
				if (mLoginRegisterManager.getLoginDataDTO().getUsr().isGuest()) {
					params.put("mt", "");
					resultResponse = UserAppRequestApi.fetchPls(params);
				}else{
					params.put("u", ""+mLoginRegisterManager.getLoginDataDTO().getUsr().getId());
					resultResponse = UserSearchApi.fetchPls(params);
				}
				if (resultResponse != null
						&& resultResponse.isSuccess()) {
					PlayerManager.getInstance()
							.setMoodids(
									resultResponse.getResult()
											.getMoodids());
					if (resultResponse.getResult().isChoose()) {
						List<ChooseAlikeDTO> chooseitems = resultResponse.getResult().getChooseitems();
						if (chooseitems != null && !chooseitems.isEmpty()) {
							ChooseAlikeDTO chooseAlikeDTO = chooseitems.get(0);
							tid = chooseAlikeDTO.getTid();
							run();
							return;
						}
					}
					String backCmbt = resultResponse.getResult().getCmbt();
					if (JingTools.isValidString(backCmbt)) {
						keyword = backCmbt;
					}
					List<MusicDTO> playList = resultResponse.getResult()
							.getItems();
					if (playList != null && !playList.isEmpty()) {
						if (needChangeCmbt && JingTools.isValidString(keyword)) {
							if (mHandler != null) {
								Message msg = new Message();
								msg.what = MainActivity.MSG_UPDATE_SEARCH_TEXT;
								msg.obj = keyword;
								mHandler.sendMessage(msg);
							}
							PlayerManager.getInstance().setCmbt(keyword);
						}
						if (tid != 0) {
							PlayerManager.getInstance().setAlikeTid(tid);
						}
					}
				}
				callBack.callBack(resultResponse);
			};
		}.start();
	}

	public void searchMainListByCmbt(final String cmbt,int indexOfServer,final boolean needStart,final SearchCallBack searchCallBack) {
		SearchCallBack shellSearchCallBack = new SearchCallBack() {
			@Override
			public void callBack(final ResultResponse<ListResult<MusicDTO>> resultResponse) {
				if (searchCallBack != null) {
					searchCallBack.callBack(resultResponse);
				}
				if (mHandler == null) {
					if (resultResponse != null && resultResponse.isSuccess()) {
						List<MusicDTO> playList = resultResponse.getResult().getItems();
						if (playList != null && !playList.isEmpty() && mainListOverListener != null) {
							mainListOverListener.setIndexOfServer(mainListOverListener.getIndexOfServer() + Constants.DEFAULT_MAIN_MUSIC_LIST_NUM_OF_LOAD);
							if (needStart) {
								mPlayerManager.setMainList(playList);
								mPlayerManager.startMainList(mainListOverListener);
							}else{
								mPlayerManager.addMainList(playList);
							}
						}
					}
					return;
				}
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (resultResponse != null && resultResponse.isSuccess()) {
							List<MusicDTO> playList = resultResponse.getResult().getItems();
							if (playList != null && !playList.isEmpty() && mainListOverListener != null) {
								int total = resultResponse.getResult().getTotal();
								if (total > 0) {
									mainListOverListener.setIndexOfServer((resultResponse.getResult().getSt()+resultResponse.getResult().getPs())%total);
								}else{
									mainListOverListener.setIndexOfServer(0);
								}
								if (needStart) {
									mPlayerManager.setMainList(playList);
									mPlayerManager.startMainList(mainListOverListener);
								}else{
									mPlayerManager.addMainList(playList);
								}
							}
						}else{
							Toast.makeText(getApplicationContext(), "获取主列表数据失败", 1).show();
						}
					}
				});
			}
		};
		doSearchKeyWord(cmbt,true,indexOfServer,shellSearchCallBack);
	}

	public void startSubMusicList(List<MusicDTO> list,
			PlayListOverListener playListOverListener) {
		mPlayerManager.playUniqueSubList(list, playListOverListener);
	}

	public void addPlayerStateChangeListener(PlayerStateChangeListener listener) {
		mPlayerManager.addListener(listener);
	}

	public void removePlayerStateChangeListener(
			PlayerStateChangeListener listener) {
		mPlayerManager.removeListener(listener);
	}

	public void addProgressListener(ProgressListener listener) {
		mPlayerManager.addProgressListener(listener);
	}

	public void removeProgressListener(ProgressListener listener) {
		mPlayerManager.removeProgressListener(listener);
	}

	public boolean isPlaying() {
		return mPlayerManager.isPlaying();
	}

	public void musicPlay() {
		mPlayerManager.start();
	}
	public boolean musicIsPrepared() {
		return mPlayerManager.isPrepared();
	}

	public int musicPst() {
		return mPlayerManager.musicPst();
	}

	public void musicPause() {
		mPlayerManager.pause();
	}

	public void musicNext(boolean needSendSkip) {
		mPlayerManager.playNext(needSendSkip);
	}
	
	public void musicLooping(boolean looping) {
		mPlayerManager.musicLooping(looping);
	}
	public boolean isLooping() {
		return mPlayerManager.isLooping();
	}

	public boolean musicFav(int ouid) {
		return mPlayerManager.musicFav(ouid);
	}

	public void musicHate() {
		if (!mPlayerManager.isLooping()) {
			mPlayerManager.musicHate();
		}
	}
	
	public boolean musicIsRelease() {
		return mPlayerManager.musicIsRelease();
	}
	
	public void musicWaiting() {
		mPlayerManager.musicWaiting();
	}

	public void musicActive(boolean cancel) {
		mPlayerManager.musicActive(cancel);
	}

	public void backToMainList() {
		mPlayerManager.backToMainList();
	}

	public void startNewMainList(List<MusicDTO> list) {
		mPlayerManager.pause();
		mPlayerManager.clearLastMain();
		mPlayerManager.setMainList(list);
		mPlayerManager.startMainList(null);
	}

	public void startNewSubList(List<MusicDTO> list,int M,String cmbt,
			PlayListOverListener playListOverListener) {
		mPlayerManager.pause();
		mPlayerManager.setSubM_Cmbt(M,cmbt);
		mPlayerManager.playUniqueSubList(list, playListOverListener);
	}

	public SettingManager getSettingManager() {
		return mSettingManager;
	}

	public MusicDTO getMucisDTO() {
		return mPlayerManager.getLastDTO();
	}

	public void notifyPlayingState() {
		mPlayerManager.notifyAllListener();
	}

	public void musicRelease() {
		mPlayerManager.release();
	}
	
	public void enableNotify() {
		mPlayerManager.enableNotify();
	}
	public void disableNotify() {
		mPlayerManager.disableNotify();
		mPlayerManager.hideNotifyView(getApplicationContext());
	}

	public void setHandler(Handler mainUiHandler) {
		mHandler = mainUiHandler;
	}

	private class PushRunnable implements Runnable {
		private boolean isLoop = true;

		public void stopLoop() {
			isLoop = false;
		}

		public void startPush() {
			new Thread(this).start();
		}

		@Override
		public void run() {
			while (isLoop) {
				if (!JingTools.isValidString(mSettingManager.getmLastUserId())) {
					return;
				}
				Log.e("kid_debug","PushRunnable isLoop: " + isLoop);
				HashMap<Object, Object> params = new HashMap<Object, Object>();
				params.put("dt", mMacAddress + "," + mSettingManager.getmLastUserId());
				Log.e("kid_debug","dt: " +params.get("dt"));
				ResultResponse<List<PushMessageDTO>> rs = UserPushRequestApi.fetchPushData(params);
				if (rs.isSuccess()) {
					List<PushMessageDTO> list = rs.getResult();
					for (PushMessageDTO pushMessageDTO : list) {
						Log.e("kid_debug","pushMessageDTO: " + pushMessageDTO.getPayload());
						sendNotification(pushMessageDTO.getPayload());
					}
				}
				SystemClock.sleep(DEAFULT_PUSH_SLEEP_TIME);
			}
		}

	}

	public void setNeedPauseOnPerpared(boolean b) {
		mPlayerManager.setNeedPauseOnPerpared(b); 
	}
	public void setNeedSeekTimeOnPerpared(int pst) {
		mPlayerManager.setNeedSeekTimeOnPerpared(pst); 
	}

	public void setSleepTime(int time) {
		mPlayerManager.setSleepTime(time);
	}

	public int getSleepTime() {
		return mPlayerManager == null ? 0 : mPlayerManager.getSleepTime();
	}
	
	public void setIsHighMode(boolean b) {
		if (mPlayerManager == null) {
			return;
		}
		mPlayerManager.setIsHighMode(b);
	}

	public String getcmbt() {
		return mPlayerManager.getCmbt();
	}

	public ResultResponse loginByToken(String at, String rt) {
		return mLoginRegisterManager.loginByToken(at, rt);
	}

	public interface SearchCallBack {
		public void callBack(ResultResponse<ListResult<MusicDTO>> resultResponse);
	}

	public void hideNotifyView() {
		mPlayerManager.hideNotifyView(getApplicationContext());
	}

	public void showNotifyView() {
		mPlayerManager.showNotifyView(getApplicationContext());
	}

	public long getAppStartTime() {
		return mAppStartTime;
	}

	public void bindMediaButton() {
		mPlayerManager.registMediaButton(getApplicationContext());
	}
	public void unbindMediaButton() {
		mPlayerManager.unregistMediaButton(getApplicationContext());
	}

	public void setLocalMusicMode(boolean b) {
		mPlayerManager.setLocalMusicMode(b);
	}

	// === xiaomi push ===
	
	 public class CallbackImpl extends MiPushClientCallback {

	        public String getCategory() {
	            return super.getCategory();
	        }

	        public void setCategory(String category) {
	            super.setCategory(category);
	        }

	        @Override
	        public void onUnsubscribeResult(long resultCode, String reason,
	                String topic) {
	            Log.d(TAG, "onUnsubscribeResult is called.");
	        }

	        @Override
	        public void onSubscribeResult(long resultCode, String reason,
	                String topic) {
	            Log.d(TAG, "onSubscribeResult is called.");
	        }

	        @Override
	        public void onReceiveMessage(String content, String alias, String topic) {
	            Log.d(TAG, "onReceiveMessage is called. " + content);
	            //FunctionResultBuilder.buildSimpleListResult(content,PushMessageDTO.class);
	            try {
	            	  PushMessageDTO dto = JsonHelper.parseObject(content, PushMessageDTO.class);
	  	            sendNotification(dto.getPayload());
				} catch (Exception e) {
					e.printStackTrace();
				}
	        }

	        @Override
	        public void onInitializeResult(long resultCode, String reason,
	                final String regID) {
	        		sXiaomiPushRegID = regID;
	            LoginRegisterManager.getInstance().registerDevice(mSettingManager.getLastUserName(),regID);
	            Log.e("kid_debug","onInitializeResult regID: " + regID);
	        }

	        @Override
	        public void onCommandResult(String command, long resultCode,
	                String reason, List<String> params) {
	            Log.d(TAG, "onCommandResult is called. " + command + ": " + params);
	            Log.e("kid_debug","onCommandResult: " + command + ": " + params);
	        }
	    }
	
	public boolean init() {
        CallbackImpl callback = new CallbackImpl();
        String category = callback.getCategory();
        callback.setCategory(null);
        // !!!!!!!!!!!!!!!!!!!!!!!!!!
        // 注意，在正式使用时，请在Application的onCreate中调用init方法，保证即使进程被杀死也可以收到push并且被唤醒。
        // !!!!!!!!!!!!!!!!!!!!!!!!!!!
        MiPushClient.initialize(this, APP_ID, APP_KEY, callback);
//        MiPushClient.subscribe(getApplicationContext(), editText
//                .getText().toString(), null);
        return true;
    }
	
}
