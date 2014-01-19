package com.jingfm.background_model;

import java.util.HashSet;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.jingfm.tools.JingTools;

public class SettingManager {

	public static final String LAST_GUEST_ID = "LastGuestId";
	public static final String LAST_USER_NAME = "LastUserName";
	public static final String LAST_USER_ID = "LastUserId";
	public static final String ATOKEN = "Atoken";
	public static final String RTOKEN = "Rtoken";
	
	public static final String COVER_ROTATION_KEYW_WORD = "CoverRoation";
	private boolean isCoverRoation;

	public static final String BIG_FONT_KEY_WORD = "BigFont";
	private boolean isBigFont;

	public static final String MUSIC_QUALITY_KEY_WORD = "MusicQuality";
	public static final int MUSIC_QUALITY_MODE_LOW = 0;
	public static final int MUSIC_QUALITY_MODE_HIGH = 1;
	public static final int MUSIC_QUALITY_MODE_AUTO = 2;
	private int mMusicQualityMode;

	public static final String NOTIFICATION_FRIEND_FAV_KEY_WORD = "FriendFav";
	private boolean isNotifyFriendFav;

	public static final String NOTIFICATION_LOCATION_NEWS_KEY_WORD = "LocationNEWS";
	private boolean isNotifyLocationNEWS;
	public static final String NOTIFICATION_INSTRUMENTS_KEY_WORD = "Instruments";
	private boolean isNotifyInstruments;

	public static final String SOCAIL_NETWORK_RENREN_SHARE_KEY_WORD = "SNS_renren";
	private boolean isSyncRenren;
	public static final String SOCAIL_NETWORK_WEIBO_SHARE_KEY_WORD = "SNS_weibo";
	private boolean isSyncWeibo;
	public static final String SOCAIL_NETWORK_TENCENT_WEIBO_SHARE_KEY_WORD = "SNS_tencent_weibo";
	private boolean isSyncTencentWeibo;

	public static final String FIRST_SHOW_GUIDE_NLG = "FIRST_SHOW_GUIDE_NLG";
	private boolean isNoNeedShowNlgGuide;
	
	public static final String FIRST_SHOW_GUIDE_PLAYING = "FIRST_SHOW_GUIDE_PLAYING_NEW";
	private boolean isNoNeedShowPlayingGuide;
	
	public static final String FIRST_SHOW_GUIDE_PLAYING_SUB_LIST = "FIRST_SHOW_GUIDE_PLAYING_SUB_LIST";
	private boolean isNoNeedShowPlayingSubListGuide;

	public static final String FIRST_SHOW_GUIDE_SLIDE = "FIRST_SHOW_GUIDE_SLIDE";
	private boolean isNoNeedShowSlideGuide;
	
	public static final String FIRST_SHOW_GUIDE_DOWNLOAD_MUSIC = "FIRST_SHOW_GUIDE_DOWNLOAD_MUSIC";
	private boolean isNoNeedShowDownloadMusicGuide;
	
	public static final String FIRST_SHOW_GUIDE_BACK_PLAYING_VIEW = "FIRST_SHOW_GUIDE_BACK_PLAYING_VIEW";
	private boolean isNoNeedShowBackPlayingViewGuide;

	public static final String FIRST_RUN_APP = "FIRST_RUN_APP";
	private boolean isNoNeedSendFirstRun;
	
	public static final String FIRST_RUN_APP_NEED_SHOW_VIEW = "FIRST_RUN_APP_NEED_SHOW_VIEW";
	public static final String PAUSE_OUT_KEY_WORD = "PAUSE_OUT_KEY_WORD";
	private boolean isNoNeedShowFirstView;
	
	
	private static SettingManager instance;
	private static String Shared_Preferences_NAME = "JingSetting";
	HashSet<SettingChangedListener> listenerList = new HashSet<SettingChangedListener>();

	public static SettingManager getInstance() {
		if (instance == null) {
			instance = new SettingManager();
		}
		return instance;
	}

	private SharedPreferences mSharedPreferences;
	private Editor mSharedPreferencesEditor;

	private String mLastUserName;
	private String mAtoken;
	private String mRtoken;
	private String mLastUserId;
	private String mGuestId;
	private boolean isPauseOut;

	private SettingManager() {
	}

	public void init(Context context) {
		mSharedPreferences = context.getSharedPreferences(
				Shared_Preferences_NAME, Context.MODE_PRIVATE);
		mLastUserName = mSharedPreferences.getString(
				LAST_USER_NAME, "");
		mGuestId = mSharedPreferences.getString(
				LAST_GUEST_ID, "");
		mLastUserId = mSharedPreferences.getString(
				LAST_USER_ID, "");
		mAtoken = mSharedPreferences.getString(
				ATOKEN, "");
		mRtoken = mSharedPreferences.getString(
				RTOKEN, "");
		isCoverRoation = mSharedPreferences.getBoolean(
				COVER_ROTATION_KEYW_WORD, true);
		isBigFont = mSharedPreferences.getBoolean(BIG_FONT_KEY_WORD, false);
		isPauseOut = mSharedPreferences.getBoolean(PAUSE_OUT_KEY_WORD, true);

		mMusicQualityMode = mSharedPreferences.getInt(MUSIC_QUALITY_KEY_WORD,
				MUSIC_QUALITY_MODE_AUTO);

		isNotifyFriendFav = mSharedPreferences.getBoolean(
				NOTIFICATION_FRIEND_FAV_KEY_WORD, true);
		isNotifyLocationNEWS = mSharedPreferences.getBoolean(
				NOTIFICATION_LOCATION_NEWS_KEY_WORD, true);
		isNotifyInstruments = mSharedPreferences.getBoolean(
				NOTIFICATION_INSTRUMENTS_KEY_WORD, true);

		isSyncRenren = mSharedPreferences.getBoolean(
				SOCAIL_NETWORK_RENREN_SHARE_KEY_WORD, false);
		isSyncWeibo = mSharedPreferences.getBoolean(
				SOCAIL_NETWORK_WEIBO_SHARE_KEY_WORD, false);
		isSyncTencentWeibo = mSharedPreferences.getBoolean(
				SOCAIL_NETWORK_TENCENT_WEIBO_SHARE_KEY_WORD, false);
		
		isNoNeedShowBackPlayingViewGuide = mSharedPreferences.getBoolean(
				FIRST_SHOW_GUIDE_BACK_PLAYING_VIEW, false);
		isNoNeedShowDownloadMusicGuide = mSharedPreferences.getBoolean(
				FIRST_SHOW_GUIDE_DOWNLOAD_MUSIC, false);
		isNoNeedShowNlgGuide = mSharedPreferences.getBoolean(
				FIRST_SHOW_GUIDE_NLG, false);
		isNoNeedShowPlayingGuide = mSharedPreferences.getBoolean(
				FIRST_SHOW_GUIDE_PLAYING, false);
		isNoNeedShowPlayingSubListGuide = mSharedPreferences.getBoolean(
				FIRST_SHOW_GUIDE_PLAYING_SUB_LIST, false);
		isNoNeedShowSlideGuide = mSharedPreferences.getBoolean(
				FIRST_SHOW_GUIDE_SLIDE, false);
		isNoNeedSendFirstRun = mSharedPreferences.getBoolean(
				FIRST_RUN_APP, false);
		isNoNeedShowFirstView = mSharedPreferences.getBoolean(
				FIRST_RUN_APP_NEED_SHOW_VIEW, false);
		mSharedPreferencesEditor = mSharedPreferences.edit();
	}

	public SharedPreferences getmSharedPreferences() {
		return mSharedPreferences;
	}

	public void sendSettingChanged() {
		for (SettingChangedListener listener : listenerList) {
			listener.onSettingChanged();
		}
	}

	public void addSettingChangedListener(SettingChangedListener listener) {
		listenerList.add(listener);
		for (SettingChangedListener tmpListener : listenerList) {
			tmpListener.onSettingChanged();
		}
	}

	public void removeSettingChangedListener(SettingChangedListener listener) {
		listenerList.remove(listener);
	}

	public boolean isCoverRoation() {
		return isCoverRoation;
	}

	public void setCoverRoation(boolean isCoverRoation) {
		if (this.isCoverRoation != isCoverRoation) {
			this.isCoverRoation = isCoverRoation;
			mSharedPreferencesEditor.putBoolean(COVER_ROTATION_KEYW_WORD,
					isCoverRoation);
			mSharedPreferencesEditor.commit();
		}
	}

	public boolean isBigFont() {
		return isBigFont;
	}

	public void setLastUserName(String userName) {
		if (JingTools.isValidString(userName)) {
			this.mLastUserName = userName;
			mSharedPreferencesEditor.putString(LAST_USER_NAME, mLastUserName);
			mSharedPreferencesEditor.commit();
		}
	}
	public void setGuestId(String guestId) {
		if (JingTools.isValidString(guestId)) {
			this.mGuestId = guestId;
			mSharedPreferencesEditor.putString(LAST_GUEST_ID, guestId);
			mSharedPreferencesEditor.commit();
		}
	}
	
	public void setLastUserId(String userId) {
		if (!JingTools.isValidString(userId)) {
			userId = "";
		}
		this.mLastUserId = userId;
		mSharedPreferencesEditor.putString(LAST_USER_ID, userId);
		mSharedPreferencesEditor.commit();
	}
	
	public void setAtoken(String atoken) {
		if (!JingTools.isValidString(atoken)) {
			atoken = "";
		}
		this.mAtoken = atoken;
		mSharedPreferencesEditor.putString(ATOKEN, atoken);
		mSharedPreferencesEditor.commit();
	}
	
	public void setRtoken(String rtoken) {
		if (!JingTools.isValidString(rtoken)) {
			rtoken = "";
		}
		this.mRtoken = rtoken;
		mSharedPreferencesEditor.putString(RTOKEN, rtoken);
		mSharedPreferencesEditor.commit();
	}
	
	public void setBigFont(boolean isBigFont) {
		if (this.isBigFont != isBigFont) {
			this.isBigFont = isBigFont;
			mSharedPreferencesEditor.putBoolean(BIG_FONT_KEY_WORD, isBigFont);
			mSharedPreferencesEditor.commit();
		}
	}

	public int getmMusicQualityMode() {
		return mMusicQualityMode;
	}

	public void setmMusicQualityMode(int mMusicQualityMode) {
		if (this.mMusicQualityMode != mMusicQualityMode) {
			this.mMusicQualityMode = mMusicQualityMode;
			mSharedPreferencesEditor.putInt(MUSIC_QUALITY_KEY_WORD,
					mMusicQualityMode);
			mSharedPreferencesEditor.commit();
		}
	}

	public boolean isNotifyFriendFav() {
		return isNotifyFriendFav;
	}

	public void setNotifyFriendFav(boolean isNotifyFriendFav) {
		if (this.isNotifyFriendFav != isNotifyFriendFav) {
			this.isNotifyFriendFav = isNotifyFriendFav;
			mSharedPreferencesEditor.putBoolean(
					NOTIFICATION_FRIEND_FAV_KEY_WORD, isNotifyFriendFav);
			mSharedPreferencesEditor.commit();
		}
	}

	public boolean isNotifyLocationNEWS() {
		return isNotifyLocationNEWS;
	}

	public void setNotifyLocationNEWS(boolean isNotifyLocationNEWS) {
		if (this.isNotifyLocationNEWS != isNotifyLocationNEWS) {
			this.isNotifyLocationNEWS = isNotifyLocationNEWS;
			mSharedPreferencesEditor.putBoolean(
					NOTIFICATION_LOCATION_NEWS_KEY_WORD, isNotifyLocationNEWS);
			mSharedPreferencesEditor.commit();
		}
	}

	public boolean isNotifyInstruments() {
		return isNotifyInstruments;
	}

	public void setNotifyInstruments(boolean isNotifyInstruments) {
		if (this.isNotifyInstruments != isNotifyInstruments) {
			this.isNotifyInstruments = isNotifyInstruments;
			mSharedPreferencesEditor.putBoolean(
					NOTIFICATION_LOCATION_NEWS_KEY_WORD, isNotifyInstruments);
			mSharedPreferencesEditor.commit();
		}
	}

	public boolean isSyncRenren() {
		return isSyncRenren;
	}

	public void setSyncRenren(boolean isSyncRenren) {
		if (this.isSyncRenren != isSyncRenren) {
			this.isSyncRenren = isSyncRenren;
			mSharedPreferencesEditor.putBoolean(
					SOCAIL_NETWORK_RENREN_SHARE_KEY_WORD, isSyncRenren);
			mSharedPreferencesEditor.commit();
		}
	}

	public boolean isSyncWeibo() {
		return isSyncWeibo;
	}

	public void setSyncWeibo(boolean isSyncWeibo) {
		if (this.isSyncWeibo != isSyncWeibo) {
			this.isSyncWeibo = isSyncWeibo;
			mSharedPreferencesEditor.putBoolean(
					SOCAIL_NETWORK_WEIBO_SHARE_KEY_WORD, isSyncWeibo);
			mSharedPreferencesEditor.commit();
		}
	}

	public boolean isSyncTencentWeibo() {
		return isSyncTencentWeibo;
	}

	public void setSyncTencentWeibo(boolean isSyncTencentWeibo) {
		if (this.isSyncTencentWeibo != isSyncTencentWeibo) {
			this.isSyncTencentWeibo = isSyncTencentWeibo;
			mSharedPreferencesEditor.putBoolean(
					SOCAIL_NETWORK_TENCENT_WEIBO_SHARE_KEY_WORD,
					isSyncTencentWeibo);
			mSharedPreferencesEditor.commit();
		}
	}

	public interface SettingChangedListener {
		public void onSettingChanged();
	}

	public void setMusicQuality(int musicQualityModeLow) {
		if (this.mMusicQualityMode != musicQualityModeLow) {
			this.mMusicQualityMode = musicQualityModeLow;
			mSharedPreferencesEditor.putInt(MUSIC_QUALITY_KEY_WORD,MUSIC_QUALITY_MODE_AUTO);
			mSharedPreferencesEditor.commit();
		}
	}

	public String getGuestId() {
		return mGuestId;
	}
	
	public String getLastUserName() {
		return mLastUserName;
	}
	
	public String getmAtoken() {
		return mAtoken;
	}
	
	public String getmRtoken() {
		return mRtoken;
	}

	public String getmLastUserId() {
		return mLastUserId;
	}
	
	
	public boolean isNoNeedShowNlgGuide() {
		return isNoNeedShowNlgGuide;
	}

	public void setNoNeedShowNlgGuide(boolean isFirstShowNlgGuide) {
		if (this.isNoNeedShowNlgGuide != isFirstShowNlgGuide) {
			this.isNoNeedShowNlgGuide = isFirstShowNlgGuide;
			mSharedPreferencesEditor.putBoolean(
					FIRST_SHOW_GUIDE_NLG,
					isFirstShowNlgGuide);
			mSharedPreferencesEditor.commit();
		}
	}
	
	public boolean isNoNeedShowPlayingGuide() {
		return isNoNeedShowPlayingGuide;
	}
	
	public void setNoNeedShowPlayingGuide(boolean isFirstShowPlayingGuide) {
		if (this.isNoNeedShowPlayingGuide != isFirstShowPlayingGuide) {
			this.isNoNeedShowPlayingGuide = isFirstShowPlayingGuide;
			mSharedPreferencesEditor.putBoolean(
					FIRST_SHOW_GUIDE_PLAYING,
					isFirstShowPlayingGuide);
			mSharedPreferencesEditor.commit();
		}
	}
	
	public boolean isNoNeedShowPlayingSubListGuide() {
		return isNoNeedShowPlayingSubListGuide;
	}
	
	public void setNoNeedShowPlayingSubListGuide(boolean isFirstShowPlayingSubListGuide) {
		if (this.isNoNeedShowPlayingSubListGuide != isFirstShowPlayingSubListGuide) {
			this.isNoNeedShowPlayingSubListGuide = isFirstShowPlayingSubListGuide;
			mSharedPreferencesEditor.putBoolean(
					FIRST_SHOW_GUIDE_PLAYING_SUB_LIST,
					isFirstShowPlayingSubListGuide);
			mSharedPreferencesEditor.commit();
		}
	}
	public boolean isNoNeedShowSlideGuide() {
		return isNoNeedShowSlideGuide;
	}
	
	public void setNoNeedShowSlideGuide(boolean isFirstShowSlideGuide) {
		if (this.isNoNeedShowSlideGuide != isFirstShowSlideGuide) {
			this.isNoNeedShowSlideGuide = isFirstShowSlideGuide;
			mSharedPreferencesEditor.putBoolean(
					FIRST_SHOW_GUIDE_SLIDE,
					isFirstShowSlideGuide);
			mSharedPreferencesEditor.commit();
		}
	}
	public boolean isNoNeedShowBackPlayingViewGuide() {
		return isNoNeedShowBackPlayingViewGuide;
	}
	
	public void setNoNeedShowBackPlayingViewGuide(boolean isNoNeedShowBackPlayingViewGuide) {
		if (this.isNoNeedShowBackPlayingViewGuide != isNoNeedShowBackPlayingViewGuide) {
			this.isNoNeedShowBackPlayingViewGuide = isNoNeedShowBackPlayingViewGuide;
			mSharedPreferencesEditor.putBoolean(
					FIRST_SHOW_GUIDE_BACK_PLAYING_VIEW,
					isNoNeedShowBackPlayingViewGuide);
			mSharedPreferencesEditor.commit();
		}
	}
	
	public boolean isNoNeedShowDownloadMusicGuide() {
		return isNoNeedShowDownloadMusicGuide;
	}
	
	public void setNoNeedShowDownloadMusicGuide(boolean isNoNeedShowDownloadMusicGuide) {
		if (this.isNoNeedShowDownloadMusicGuide != isNoNeedShowDownloadMusicGuide) {
			this.isNoNeedShowDownloadMusicGuide = isNoNeedShowDownloadMusicGuide;
			mSharedPreferencesEditor.putBoolean(
					FIRST_SHOW_GUIDE_DOWNLOAD_MUSIC,
					isNoNeedShowDownloadMusicGuide);
			mSharedPreferencesEditor.commit();
		}
	}
	public void clearAllGuide(){
		setNoNeedShowBackPlayingViewGuide(false);
		setNoNeedShowDownloadMusicGuide(false);
		setNoNeedShowNlgGuide(false);
		setNoNeedShowPlayingGuide(false);
		setNoNeedShowPlayingSubListGuide(false);
		setNoNeedShowSlideGuide(false);
	}
	
	public boolean isNoNeedSendFirstRun() {
		return isNoNeedSendFirstRun;
	}
	
	public void setNoNeedSendFirstRun(boolean isNoNeedSendFirstRun) {
		if (this.isNoNeedSendFirstRun != isNoNeedSendFirstRun) {
			this.isNoNeedSendFirstRun = isNoNeedSendFirstRun;
			mSharedPreferencesEditor.putBoolean(
					FIRST_RUN_APP,
					isNoNeedSendFirstRun);
			mSharedPreferencesEditor.commit();
		}
	}
	
	public boolean isNoNeedShowFirstView() {
		return isNoNeedShowFirstView;
	}
	
	public void setNoNeedShowFirstView(boolean isNoNeedShowFirstView) {
		if (this.isNoNeedShowFirstView != isNoNeedShowFirstView) {
			this.isNoNeedShowFirstView = isNoNeedShowFirstView;
			mSharedPreferencesEditor.putBoolean(
					FIRST_RUN_APP_NEED_SHOW_VIEW,
					isNoNeedShowFirstView);
			mSharedPreferencesEditor.commit();
		}
	}

	public boolean isPauseOut() {
		return isPauseOut;
	}
	
	public void setPauseOut(boolean isPauseOut) {
		if (this.isPauseOut != isPauseOut) {
			this.isPauseOut = isPauseOut;
			mSharedPreferencesEditor.putBoolean(PAUSE_OUT_KEY_WORD, isPauseOut);
			mSharedPreferencesEditor.commit();
		}
	}
}
