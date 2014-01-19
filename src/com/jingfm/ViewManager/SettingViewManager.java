package com.jingfm.ViewManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.jingfm.Jing3rdPartBindActivity;
import com.jingfm.MainActivity;
import com.jingfm.MainActivity.CallBacker;
import com.jingfm.MainActivity.ChangeDataAnimateCallBack;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.Constants.KTC;
import com.jingfm.adapter.RecommendAppAdapter;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.UserOAuthRequestApi;
import com.jingfm.api.business.UserPersonalRequestApi;
import com.jingfm.api.business.UserRequestApi;
import com.jingfm.api.model.UserDTO;
import com.jingfm.api.model.UserDetailDTO;
import com.jingfm.api.model.UserPersonalDataDTO;
import com.jingfm.background_model.SettingManager;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.customer_views.JingClearCacheView;
import com.jingfm.customer_views.JingSeekBar;
import com.jingfm.customer_views.JingSettingSwitch;
import com.jingfm.customer_views.Switch;
import com.jingfm.customer_views.TouchScrollContainer;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.AsyncImageLoader.ImageCallback;
import com.jingfm.tools.JingTools;

public class SettingViewManager implements OnCheckedChangeListener,
		OnClickListener {
	protected static final int MSG_SET_NOTIFY = 0;
	private View mSettingView;
	private MainActivity mContext;
	private SettingManager mSettingManager;
	private Handler mHandler;
	private JingSettingSwitch rotateEnable, bigFontEable,pauseOutEable,remind_who_love_enable, remind_interrelated_enable,
			remind_instrument_enable,renren, sina_weibo, qq_weibo;

	// 缓存，保存当前的引擎参数到下一次启动应用程序使用.
	private int width;
	private TouchScrollContainer mainLayout;
	protected boolean initailizing;
	private UserInfoManager mUserInfoManager;
	protected boolean isViewLocked;
	private ImageView setting_avatar;
	private ImageView setting_cover;
	private View setting_button_edit_avatar;
	private View setting_button_edit_background_cover;
	private JingSettingSwitch[] mSwichArray;
	private View radio_1;
	private View radio_2;
	private View radio_3;
	private View[] mSleepSeekArray;
	private RecommendAppAdapter mRecommendAppAdapter;

	public SettingViewManager(MainActivity context,
			TouchScrollContainer mainLayout) {
		this.mContext = context;
		this.mainLayout = mainLayout;
		initHandler();
		initView();
	}

	public View getSettingView() {
		refreshSync3rdPartSwitch();
		return mSettingView;
	}

	private void refreshSync3rdPartSwitch() {
		refreshImage();
		renren.setChecked(mContext.getmLoginData().getSnstokens()
				.containsKey(UserOAuthRequestApi.OAuth_Renren_Identify));
		sina_weibo.setChecked(mContext.getmLoginData().getSnstokens()
				.containsKey(UserOAuthRequestApi.OAuth_Sina_WEIBO_Identify));
		qq_weibo.setChecked(mContext.getmLoginData().getSnstokens()
				.containsKey(UserOAuthRequestApi.OAuth_Qq_WEIBO_Identify));
		int time = mContext.getSleepTime();
		refreshSleepTime(time);
		refreshAllSwitchBg();
	}

	private void refreshSleepTime(int time) {
		int cursor = 0;
		if (time != 0) {
			if (time <= (1*15*60*1000)) {
				cursor = 1;
			}else if (time <= (1*30*60*1000)) {
				cursor = 2;
			}else if (time <= (1*60*60*1000)) {
				cursor = 3;
			}else if (time <= (1*120*60*1000)) {
				cursor = 4;
			}
		}
		for (int i = 0; i < mSleepSeekArray.length; i++) {
			if (i == cursor) {
				mSleepSeekArray[i].setBackgroundResource(R.drawable.draw_round_jing_green);
			}else{
				mSleepSeekArray[i].setBackgroundResource(R.drawable.draw_round_jing_gray);
			}
		}
	}

	private void refreshImage() {
		new Thread(){
			public void run() {
				if (mContext.getUserId() == 0) {
					return;
				}
				final HashMap<String, String> map = new HashMap<String, String>();
				map.put("uid", "" + mContext.getUserId());
				map.put("ouid", "" + mContext.getUserId());
				map.put("st", "0");
				map.put("ps", "" + 1);
				final ResultResponse<UserPersonalDataDTO> resultResponse = UserPersonalRequestApi
						.userFetchPersonal(map);
				if (resultResponse != null
						&& resultResponse.isSuccess()) {
					UserPersonalDataDTO userPersonalDataDTO = resultResponse
							.getResult();
					final String avatarUrl = CustomerImageRule.ID2URL(
							Constants.ID2URL_KEY_WORD_AVATAR,
							userPersonalDataDTO.getUser().getAvatar(),Constants.ID2URL_DEFAULT_BITRATE_AVATAR);
					final String coverUrl = CustomerImageRule.ID2URL(
							Constants.ID2URL_KEY_WORD_COVER,
							userPersonalDataDTO.getCover(),
							Constants.ID2URL_DEFAULT_BITRATE_COVER);
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if (!avatarUrl.equals(setting_avatar.getTag())) {
								setting_avatar.setTag(avatarUrl);
								setting_avatar.setImageBitmap(null);
								AsyncImageLoader.getInstance().loadTempBitmapByUrl(avatarUrl,
										AsyncImageLoader.IMAGE_TYPE_ROUND, new ImageCallback() {
											@Override
											public void imageLoaded(final Bitmap bitmap,
													final String imageUrl) {
												if (!imageUrl.startsWith("" + setting_avatar.getTag())) {
													return;
												}
												if (JingTools.isUiThread()) {
													setting_avatar.setImageBitmap(bitmap);
												} else {
													mHandler.post(new Runnable() {
														@Override
														public void run() {
															if (!imageUrl.startsWith(""
																	+ setting_avatar.getTag())) {
																return;
															}
															setting_avatar.setImageBitmap(bitmap);
														}
													});
												}
											}
										});
							}
							if (!coverUrl.equals(setting_cover.getTag())) {
								setting_cover.setTag(coverUrl);
								setting_cover.setImageBitmap(null);
								AsyncImageLoader.getInstance().loadTempBitmapByUrl(coverUrl,
										AsyncImageLoader.IMAGE_TYPE_ORIGINAL, new ImageCallback() {
									@Override
									public void imageLoaded(final Bitmap bitmap,
											final String imageUrl) {
										if (!imageUrl.startsWith("" + setting_cover.getTag())) {
											return;
										}
										if (JingTools.isUiThread()) {
											setting_cover.setImageBitmap(bitmap);
										} else {
											mHandler.post(new Runnable() {
												@Override
												public void run() {
													if (!imageUrl.startsWith(""
															+ setting_cover.getTag())) {
														return;
													}
													setting_cover.setImageBitmap(bitmap);
												}
											});
										}
									}
								});
							}
						}
					});
				} else {
//					Toast.makeText(mContext, "加载失败", 0).show();
				}
			};
		}.start();
	}
	
	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_SET_NOTIFY:
					break;
				}
			}
		};
	}

	private void initView() {
		mSettingManager = mContext.getSettingManager();
		mSettingView = LayoutInflater.from(mContext).inflate(
				R.layout.center_list_settings_view, null);

//		width = mSettingView.getWidth(); // 屏幕宽度（像素）
		width = JingTools.screenWidth - JingTools.dip2px(mContext, 20); // 屏幕宽度（像素）
		initSleepSeek();
		rotateEnable = (JingSettingSwitch) mSettingView.findViewById(R.id.rotateEnable);
		rotateEnable.setDisallowInterceptView(mainLayout);
		bigFontEable = (JingSettingSwitch) mSettingView.findViewById(R.id.bigFoutEable);
		bigFontEable.setDisallowInterceptView(mainLayout);
		pauseOutEable = (JingSettingSwitch) mSettingView.findViewById(R.id.plugout_pause);
		pauseOutEable.setDisallowInterceptView(mainLayout);

		remind_who_love_enable = (JingSettingSwitch) mSettingView
				.findViewById(R.id.remind_who_love_enable);
		remind_who_love_enable.setDisallowInterceptView(mainLayout);
		remind_interrelated_enable = (JingSettingSwitch) mSettingView
				.findViewById(R.id.remind_interrelated_enable);
		remind_interrelated_enable.setDisallowInterceptView(mainLayout);
		remind_instrument_enable = (JingSettingSwitch) mSettingView
				.findViewById(R.id.remind_instrument_enable);
		remind_instrument_enable.setDisallowInterceptView(mainLayout);

		renren = (JingSettingSwitch) mSettingView.findViewById(R.id.renren_enable);
		renren.setDisallowInterceptView(mainLayout);
		sina_weibo = (JingSettingSwitch) mSettingView.findViewById(R.id.xina_enable);
		sina_weibo.setDisallowInterceptView(mainLayout);
		qq_weibo = (JingSettingSwitch) mSettingView.findViewById(R.id.qq_enable);
		qq_weibo.setDisallowInterceptView(mainLayout);

		rotateEnable.setChecked(mSettingManager.isCoverRoation());
		bigFontEable.setChecked(mSettingManager.isBigFont());
		pauseOutEable.setChecked(mSettingManager.isPauseOut());

		remind_who_love_enable.setChecked(!mSettingManager.isNotifyFriendFav());
		remind_interrelated_enable.setChecked(!mSettingManager
				.isNotifyLocationNEWS());
		remind_instrument_enable.setChecked(!mSettingManager
				.isNotifyInstruments());

		// 设置完是否选中，再加监听，否则会触发事件。
		rotateEnable.setOnCheckedChangeListener(this);
		bigFontEable.setOnCheckedChangeListener(this);
		pauseOutEable.setOnCheckedChangeListener(this);
		remind_who_love_enable.setOnCheckedChangeListener(this);
		remind_interrelated_enable.setOnCheckedChangeListener(this);
		remind_instrument_enable.setOnCheckedChangeListener(this);
		renren.setOnCheckedChangeListener(this);
		sina_weibo.setOnCheckedChangeListener(this);
		qq_weibo.setOnCheckedChangeListener(this);
		radio_1 = mSettingView.findViewById(R.id.radio_1);
		radio_2 = mSettingView.findViewById(R.id.radio_2);
		radio_3 = mSettingView.findViewById(R.id.radio_3);
		final View radio_text_1 = mSettingView.findViewById(R.id.radio_text_1);
		final View radio_text_2 = mSettingView.findViewById(R.id.radio_text_2);
		final View radio_text_3 = mSettingView.findViewById(R.id.radio_text_3);
		refreshMusicQuality();
		OnClickListener onClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (v.equals(radio_1)
						||v.equals(radio_text_1)) {
					mSettingManager.setMusicQuality(SettingManager.MUSIC_QUALITY_MODE_LOW);
				}else if (v.equals(radio_2)
						||v.equals(radio_text_2)) {
					mSettingManager
					.setMusicQuality(SettingManager.MUSIC_QUALITY_MODE_HIGH);

				}else if (v.equals(radio_3)
						||v.equals(radio_text_3)) {
					mSettingManager
					.setMusicQuality(SettingManager.MUSIC_QUALITY_MODE_AUTO);
				}
				refreshMusicQuality();
				mContext.refreshPlayHighMode();
			}
		};
		radio_1.setOnClickListener(onClickListener);
		radio_2.setOnClickListener(onClickListener);
		radio_3.setOnClickListener(onClickListener);
		radio_text_1.setOnClickListener(onClickListener);
		radio_text_2.setOnClickListener(onClickListener);
		radio_text_3.setOnClickListener(onClickListener);
		mSettingView.findViewById(R.id.base_info_layout).setOnClickListener(
				this);
		mSettingView.findViewById(R.id.password_layout)
				.setOnClickListener(this);
		mSettingView.findViewById(R.id.recommed_app_layout)
		.setOnClickListener(this);
		mSettingView.findViewById(R.id.clear_layout).setOnClickListener(this);
		mSettingView.findViewById(R.id.privacy_layout).setOnClickListener(this);
		mSettingView.findViewById(R.id.about_us_layout)
				.setOnClickListener(this);
		mSettingView.findViewById(R.id.logout_layout)
		.setOnClickListener(this);
		setting_button_edit_avatar = mSettingView
				.findViewById(R.id.setting_button_edit_avatar);
		setting_button_edit_background_cover = mSettingView
				.findViewById(R.id.setting_button_edit_background_cover);
		setting_button_edit_avatar.setOnClickListener(this);
		setting_button_edit_background_cover.setOnClickListener(this);
		setting_avatar = (ImageView) mSettingView
				.findViewById(R.id.setting_avatar);
		View setting_avatar_layout = mSettingView
				.findViewById(R.id.setting_avatar_layout);
		setting_cover = (ImageView) mSettingView
				.findViewById(R.id.setting_cover);
		setting_cover.setLayoutParams(new FrameLayout.LayoutParams(width,width));
		View setting_avatar_button_container = mSettingView
				.findViewById(R.id.setting_avatar_button_container);
		FrameLayout.LayoutParams lp = (android.widget.FrameLayout.LayoutParams) setting_avatar_button_container.getLayoutParams();
		lp.width = width;
		lp.height =  width*4/10;
		FrameLayout.LayoutParams lp2 = (android.widget.FrameLayout.LayoutParams) setting_avatar_layout.getLayoutParams();
		lp2.width = lp2.height = width/5;
		lp2.gravity = Gravity.CENTER;
		lp2.setMargins(0, 0, 0, lp.height/2);
		mSwichArray = new JingSettingSwitch[]{rotateEnable, bigFontEable,pauseOutEable,remind_who_love_enable, remind_interrelated_enable,
				remind_instrument_enable,renren, sina_weibo, qq_weibo};
		setting_button_edit_avatar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mContext.changeAvatar(setting_avatar);
			}
		});
		setting_button_edit_background_cover.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mContext.changeAvatar(setting_cover);
			}
		});
	}

	private void initSleepSeek() {
		mSleepSeekArray = new View[5];
		mSleepSeekArray[0] = mSettingView.findViewById(R.id.seek_1);
		mSleepSeekArray[1] = mSettingView.findViewById(R.id.seek_2);
		mSleepSeekArray[2] = mSettingView.findViewById(R.id.seek_3);
		mSleepSeekArray[3] = mSettingView.findViewById(R.id.seek_4);
		mSleepSeekArray[4] = mSettingView.findViewById(R.id.seek_5);
		final View[] sleepSeekTextArray = new View[5];
		sleepSeekTextArray[0] = mSettingView.findViewById(R.id.seek_text_1);
		sleepSeekTextArray[1] = mSettingView.findViewById(R.id.seek_text_2);
		sleepSeekTextArray[2] = mSettingView.findViewById(R.id.seek_text_3);
		sleepSeekTextArray[3] = mSettingView.findViewById(R.id.seek_text_4);
		sleepSeekTextArray[4] = mSettingView.findViewById(R.id.seek_text_5);
		OnClickListener onClickListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				for (int i = 0; i < mSleepSeekArray.length; i++) {
					if (v.equals(mSleepSeekArray[i])
							|| v.equals(sleepSeekTextArray[i])) {
						switch (i) {
						case 0:
							mContext.setSleepTime((int) (0));
							refreshSleepTime(0);
							break;
						case 1:
							KTC.rep("Setting", "UseTimerPlay", "15");
							mContext.setSleepTime((int) (i * 15));
							refreshSleepTime(i*15*60*1000);
							break;
						case 2:
							KTC.rep("Setting", "UseTimerPlay", "30");
							mContext.setSleepTime((int) (i * 15));
							refreshSleepTime(i*15*60*1000);
							break;
						case 3:
							KTC.rep("Setting", "UseTimerPlay", "60");
							mContext.setSleepTime(60);
							refreshSleepTime(60*60*1000);
							break;
						case 4:
							KTC.rep("Setting", "UseTimerPlay", "120");
							mContext.setSleepTime(120);
							refreshSleepTime(120*60*1000);
							break;
						}
						break;
					}
				}
			}
		};
		for (int i = 0; i < mSleepSeekArray.length; i++) {
			mSleepSeekArray[i].setOnClickListener(onClickListener);
			sleepSeekTextArray[i].setOnClickListener(onClickListener);
		}
	}

	private void refreshMusicQuality() {
		radio_1.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.draw_round_white_sub));
		radio_2.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.draw_round_white_sub));
		radio_3.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.draw_round_white_sub));
		switch (mSettingManager.getmMusicQualityMode()) {
		case SettingManager.MUSIC_QUALITY_MODE_LOW:
			KTC.rep("Setting", "AudioQuality", "128");
			radio_1.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.draw_round_jing_green));
			break;
		case SettingManager.MUSIC_QUALITY_MODE_HIGH:
			KTC.rep("Setting", "AudioQuality", "256");
			radio_2.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.draw_round_jing_green));
			break;
		case SettingManager.MUSIC_QUALITY_MODE_AUTO:
			KTC.rep("Setting", "AudioQuality", "auto");
			radio_3.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.draw_round_jing_green));
			break;
		}
	}

	public void refreshAllSwitchBg(){
		for (int i = 0; i < mSwichArray.length; i++) {
			if (mSwichArray[i].isChecked()) {
				mSwichArray[i].setTrackDrawable(mContext.getResources().getDrawable(R.drawable.setting_switch_on));
			} else {
				mSwichArray[i].setTrackDrawable(mContext.getResources().getDrawable(R.drawable.setting_switch_off));
			}
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
//		arg1 = !arg1;
		refreshAllSwitchBg();
		switch (arg0.getId()) {
		case R.id.rotateEnable:
			mSettingManager.setCoverRoation(arg1);
			KTC.rep("Setting", "CoverRotation", arg1?"on":"off");
			mContext.getmViewManagerCenter().getmPlayingViewManager().refreshRotateState(mContext.isPlaying());
			break;
		case R.id.bigFoutEable:
			mSettingManager.setBigFont(arg1);
			mContext.changeFontSize();
			break;
		case R.id.plugout_pause:
			mSettingManager.setPauseOut(arg1);
			break;
		case R.id.remind_who_love_enable:
			mSettingManager.setNotifyFriendFav(arg1);
			break;

		case R.id.remind_interrelated_enable:
			mSettingManager.setNotifyLocationNEWS(arg1);
			break;

		case R.id.remind_instrument_enable:
			mSettingManager.setNotifyInstruments(arg1);
			break;

		case R.id.renren_enable:
			if (mContext.isOfflineMode()) {
				mContext.toastOffLine();
				return;
			}
			setSync3rdPart(UserOAuthRequestApi.OAuth_Renren_Identify,arg1);
			break;

		case R.id.xina_enable:
			if (mContext.isOfflineMode()) {
				mContext.toastOffLine();
				return;
			}
			setSync3rdPart(UserOAuthRequestApi.OAuth_Sina_WEIBO_Identify,arg1);
			break;

		case R.id.qq_enable:
			if (mContext.isOfflineMode()) {
				mContext.toastOffLine();
				return;
			}
			setSync3rdPart(UserOAuthRequestApi.OAuth_Qq_WEIBO_Identify,arg1);
			break;
		}

	}

	private void setSync3rdPart(final String identify, boolean arg1) {
		if (mContext.getmLoginData().getSnstokens().containsKey(identify) == arg1) {
			return;
		}
		if (arg1) {
			String url = UserOAuthRequestApi.getBindAuthorizeUrl(""
					+ mContext.getUserId(),
					identify);
			Intent intent = new Intent(mContext, Jing3rdPartBindActivity.class);
			intent.putExtra("url", url);
			intent.putExtra("identify", identify);
			intent.putExtra("uid", "" + mContext.getUserId());
			mContext.startActivityForResult(intent,
					MainActivity.START_ACTIVIY_REQUEST_CODE_3rd_part_bind);
		}else{
			//TODO 解除绑定
			final HashMap<Object, Object> params = new HashMap<Object, Object>();
			params.put("uid",  ""+mContext.getUserId());
			params.put("identify",identify);
			new Thread(){
				public void run() {
					final ResultResponse<String> rs = UserOAuthRequestApi.postRemoveBind(params);
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							if (rs.isSuccess()) {
								mContext.getmLoginData().getSnstokens().remove(identify);
								Toast.makeText(mContext, "解绑成功", 0).show();
							}
							refreshSync3rdPartSwitch();
						}
					});
					
				};
			}.start();
		}
	}

	@Override
	public void onClick(View v) {
		View newView = null;
		switch (v.getId()) {
		case R.id.base_info_layout:
			if (mContext.isOfflineMode()) {
				mContext.toastOffLine();
				return;
			}
			KTC.rep("Setting", "ModifyProfile", "");
			Toast.makeText(mContext, "正在读取个人信息...", 0).show();
			initUserInfoView();
			break;
		case R.id.password_layout:
			KTC.rep("Setting", "ChangePassword", "");
			if (mContext.isOfflineMode()) {
				mContext.toastOffLine();
				return;
			}
			// 修改密码
			initPasswordView();
			break;
		case R.id.recommed_app_layout:
			DragRefreshListView dragRefreshListView = new DragRefreshListView(mContext);
			if (mRecommendAppAdapter == null) {
				mRecommendAppAdapter = new RecommendAppAdapter(mContext);
				mRecommendAppAdapter.initData(mContext.getmLoginData());
			}
			dragRefreshListView.setAdapter(mRecommendAppAdapter);
			mRecommendAppAdapter.setListView(dragRefreshListView);
			KTC.rep("Setting", "BrowseRecommendApps", "");
			mContext.getmViewManagerCenter().pushNewView(dragRefreshListView, null);
			break;
		case R.id.clear_layout:
			 newView = new FrameLayout(mContext);
			 newView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
			 int size = JingTools.getShowWidth(mContext)*4/10;
			 final JingClearCacheView jingClearCacheView = new JingClearCacheView(mContext);
			 android.widget.FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(size,size);
			 lp.gravity = Gravity.CENTER;
			 jingClearCacheView.setLayoutParams(lp);
			 ((ViewGroup)newView).addView(jingClearCacheView);
			 jingClearCacheView.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (isViewLocked) {
						return;
					}
					isViewLocked = true;
					mContext.getmViewManagerCenter().getMainLayout().freeze();
					jingClearCacheView.startAmin(new CallBacker() {
						
						@Override
						public void onCallBack() {
							mContext.clearCacheFiles();
							mContext.getmViewManagerCenter().refreshRightButtonState();
							Toast.makeText(mContext, "清理完毕", 1).show();
							mContext.getmViewManagerCenter().getMainLayout().unfreeze();
							isViewLocked = false;
						}
					});
				}
			});
			mContext.getmViewManagerCenter().pushNewView(newView, null);
			break;
		case R.id.privacy_layout:
			newView = LayoutInflater.from(mContext).inflate(
					R.layout.setting_privacy_info, null);
			mContext.getmViewManagerCenter().pushNewView(newView, null);
			break;
		case R.id.about_us_layout:
			// mContext.startActivity(new Intent(mContext,
			// AboutUsActivity.class));
			// mContext.overridePendingTransition(R.anim.push_left_in,
			// R.anim.do_nothing);
			newView = LayoutInflater.from(mContext).inflate(
					R.layout.setting_about_us, null);
			mContext.getmViewManagerCenter().pushNewView(newView, null);
			break;
		case R.id.logout_layout:
			mContext.changeData(true,
					new ChangeDataAnimateCallBack() {
						@Override
						public void doChangeData() {
							mContext.logout();
						}
					});
			break;
		}

	}

	private void initUserInfoView() {
		if (initailizing) {
			return;
		}
		if (mUserInfoManager == null) {
			mUserInfoManager = new UserInfoManager();
		}
		mUserInfoManager.initInfoViewAndPush();
	}

	private void initPasswordView() {
		if (mUserInfoManager == null) {
			mUserInfoManager = new UserInfoManager();
		}
		mUserInfoManager.initAndPushPasswordView();
	}

	public void bindBack(int resultCode, Intent data) {
		switch (resultCode) {
		case Activity.RESULT_OK:
			if (data == null) {
				Toast.makeText(mContext.getApplicationContext(), "绑定失败请重试", 0)
				.show();
				break;
			}
			if (UserOAuthRequestApi.OAuth_Sina_WEIBO_Identify.equals(data
					.getStringExtra("identify"))) {
				if (!data.getStringExtra("uid").equals(
						"" + mContext.getUserId())) {
					return;
				}
				mContext.getmLoginData().getSnstokens()
						.put(UserOAuthRequestApi.OAuth_Sina_WEIBO_Identify, "");
				Toast.makeText(mContext, "绑定新浪微博成功", 1).show();
			} else if (UserOAuthRequestApi.OAuth_Renren_Identify.equals(data
					.getStringExtra("identify"))) {
				if (!data.getStringExtra("uid").equals(
						"" + mContext.getUserId())) {
					return;
				}
				mContext.getmLoginData().getSnstokens()
						.put(UserOAuthRequestApi.OAuth_Renren_Identify, "");
				Toast.makeText(mContext, "绑定人人网成功", 1).show();
			} else if (UserOAuthRequestApi.OAuth_Qq_WEIBO_Identify.equals(data
					.getStringExtra("identify"))) {
				if (!data.getStringExtra("uid").equals(
						"" + mContext.getUserId())) {
					return;
				}
				mContext.getmLoginData().getSnstokens()
						.put(UserOAuthRequestApi.OAuth_Qq_WEIBO_Identify, "");
				Toast.makeText(mContext, "绑定腾讯微博成功", 1).show();
			}
			break;
		case Jing3rdPartBindActivity.RESULT_FAILED:
			Toast.makeText(mContext.getApplicationContext(), "绑定失败请重试", 0)
					.show();
		case Activity.RESULT_CANCELED:
			break;
		}
		refreshSync3rdPartSwitch();
	}

	public class UserInfoManager implements OnClickListener {

		protected TextView age;
		private LinearLayout avatar_layout;
		private LinearLayout cover_layout;
		private LinearLayout nick_layout;
		private LinearLayout sex_layout;
		private LinearLayout age_layout;
		private LinearLayout signature_layout;
		private TextView sex;
		private EditText nick;
		private EditText signature;
		private ImageView headViewImageView;
		private int age_;
		private Calendar calendar;
		private int year;
		private int monthOfYear;
		private int dayOfMonth;
		private DatePickerDialog datePickerDialog;
		protected boolean isChange;
		private UserDetailDTO mUserDetailDTO;
		private View mUserInfoView;
		private View mPasswordView;
		private EditText old_pass;
		private EditText new_pass;
		private EditText again_new_pass;
		protected String oldP;
		protected String newP;
		protected String againNewP;
		protected String tmpSex;

		public void initInfoViewAndPush() {
			new Thread() {
				public void run() {
					initailizing = true;
					if (mUserDetailDTO != null) {
						pushInfoView();
						return;
					}
					UserDTO usr = mContext.getmLoginData().getUsr();
					HashMap<String, String> params = new HashMap<String, String>();
					params.put("uid", "" + usr.getId());
					final ResultResponse<UserDetailDTO> rs = UserRequestApi
							.fetchProfile(params);
					if (rs.isSuccess()) {
						mUserDetailDTO = rs.getResult();
						pushInfoView();
					}
					initailizing = false;
				}
			}.start();
		}

		public void initAndPushPasswordView() {
			pushPasswordView();
		}

		protected void pushInfoView() {
			if (mSettingView.getParent() == null) {
				return;
			}
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					if (mUserInfoView == null) {
						initInfoView();
					}
					mUserInfoView.setVisibility(View.VISIBLE);
					mContext.getmViewManagerCenter().pushNewView(mUserInfoView,
							new Runnable() {
								@Override
								public void run() {
									if (!isChange) {
										return;
									}
									new Thread() {
										@Override
										public void run() {
											HashMap<Object, Object> params = new HashMap<Object, Object>();
											params.put("uid",
													mUserDetailDTO.getId());
											params.put("nick", mUserDetailDTO
													.getNickname());
											params.put("bday", mUserDetailDTO
													.getBirthday());
											params.put("permailink",
													mUserDetailDTO
															.getPermalink());
											params.put("sex",
													mUserDetailDTO.getSex());
											params.put("bio",
													mUserDetailDTO.getBio());
											ResultResponse<UserDetailDTO> pls_post = UserRequestApi
													.updateProfile(params);

											if (pls_post.isSuccess()) {
												mContext.getmLoginData()
														.getUsr()
														.setNick(
																mUserDetailDTO
																		.getNickname());
											} else {
												mHandler.post(new Runnable() {
													
													@Override
													public void run() {
														Toast.makeText(mContext,
																"数据不合法，提交失败",
																Toast.LENGTH_SHORT)
																.show();
													}
												});
											}
										}
									}.start();
								}
							});
				}
			});
		}

		private void initInfoView() {
			mUserInfoView = LayoutInflater.from(mContext).inflate(
					R.layout.setting_user_info, null);
			avatar_layout = (LinearLayout) mUserInfoView
					.findViewById(R.id.avatar_layout);
			cover_layout = (LinearLayout) mUserInfoView
					.findViewById(R.id.cover_layout);
			nick_layout = (LinearLayout) mUserInfoView
					.findViewById(R.id.nick_layout);
			sex_layout = (LinearLayout) mUserInfoView
					.findViewById(R.id.sex_layout);
			age_layout = (LinearLayout) mUserInfoView
					.findViewById(R.id.age_layout);
			signature_layout = (LinearLayout) mUserInfoView
					.findViewById(R.id.signature_layout);

			sex = (TextView) mUserInfoView.findViewById(R.id.sex);
			age = (TextView) mUserInfoView.findViewById(R.id.age);

			nick = (EditText) mUserInfoView.findViewById(R.id.nick);
			signature = (EditText) mUserInfoView.findViewById(R.id.signature);
			headViewImageView = (ImageView) mUserInfoView
					.findViewById(R.id.headViewImageView);

			avatar_layout.setOnClickListener(this);
			cover_layout.setOnClickListener(this);
			nick_layout.setOnClickListener(this);
			sex_layout.setOnClickListener(this);
			age_layout.setOnClickListener(this);
			signature_layout.setOnClickListener(this);

			try {
				age_ = JingTools.getOldInfo(mUserDetailDTO.getBirthday());
				age.setText("" + age_ + "岁");
			} catch (ParseException e1) {
				e1.printStackTrace();
			}
			tmpSex = mUserDetailDTO.getSex();
			sex.setText(mUserDetailDTO.getSex());

			nick.setText(mUserDetailDTO.getNickname());
			signature.setText(mUserDetailDTO.getBio());
			// 创建各种日期格式的对象
			final SimpleDateFormat sdfCHINESE = new SimpleDateFormat(
					"yyyy-MM-dd", Locale.CHINESE);
			// 用来获取日期和时间的
			calendar = Calendar.getInstance();
			if (mUserDetailDTO.getBirthday() != null) {

				Date date;
				try {
					date = sdfCHINESE.parse(mUserDetailDTO.getBirthday());
					calendar.setTime(date);
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}
			year = calendar.get(Calendar.YEAR);
			monthOfYear = calendar.get(Calendar.MONTH);
			dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
			datePickerDialog = new DatePickerDialog(mContext,
					new DatePickerDialog.OnDateSetListener() {

						private boolean isChange;

						@Override
						public void onDateSet(DatePicker arg0, int y, int m,
								int d) {
							isChange = true;
							year = y;
							monthOfYear = m;
							dayOfMonth = d;
							calendar.set(year, monthOfYear, dayOfMonth);
							String time = sdfCHINESE.format(calendar.getTime());
							try {
								age_ = JingTools.getOldInfo(time);
								age.setText("" + age_ + "岁");
							} catch (ParseException e1) {
								e1.printStackTrace();
							}
							if (age_ > 0) {
								mUserDetailDTO.setBirthday(time);
							}
						}
					}, year, monthOfYear, dayOfMonth);
			String imageUrl = CustomerImageRule.ID2URL("avatar",
					mUserDetailDTO.getAvatar(), "UM");
			AsyncImageLoader.getInstance().loadBitmapByUrl(imageUrl,
					AsyncImageLoader.IMAGE_TYPE_ROUND,
					new AsyncImageLoader.ImageCallback() {

						@Override
						public void imageLoaded(final Bitmap bitmap, String imageUrl) {
							mHandler.post(new Runnable() {
								
								@Override
								public void run() {
									headViewImageView.setImageBitmap(bitmap);
								}
							});
						}
					});
			nick.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
					isChange = true;
					if (JingTools.judgeNickName(nick)) {
						mUserDetailDTO.setNickname(arg0.toString().trim());
					}
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
					// mUserDetailDTO.setNickname(arg0.toString());
					isChange = true;
				}

				@Override
				public void afterTextChanged(Editable arg0) {
					isChange = true;
				}
			});
			signature.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
					isChange = true;
					if(JingTools.judgeSignature(signature)){
						mUserDetailDTO.setBio(arg0.toString().trim());
					}
				}

				@Override
				public void beforeTextChanged(CharSequence arg0, int arg1,
						int arg2, int arg3) {
					mUserDetailDTO.setBio(arg0.toString().trim());
					isChange = true;
				}

				@Override
				public void afterTextChanged(Editable arg0) {
					isChange = true;
				}
			});
		}

		@Override
		public void onClick(View v) {
			switch (v.getId()) {
			case R.id.avatar_layout:
				mContext.changeAvatar(headViewImageView);
				break;
			case R.id.cover_layout:
				mContext.changeCover(null);
				break;

			case R.id.sex_layout:
				int choice;
				if (mUserDetailDTO.getSex() != null
						&& mUserDetailDTO.getSex().equals("女")) {
					choice = 1;
				} else {
					choice = 0;
					mUserDetailDTO.setSex("男");
				}
				new AlertDialog.Builder(mContext)
						.setSingleChoiceItems(new String[] { "男", "女" },
								choice, new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										isChange = true;
										if (whichButton == 0) {
											tmpSex = "男";
										} else {
											tmpSex = "女";
										}
									}
								})
						.setNegativeButton("取消",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
									}
								})
						.setPositiveButton("确定",
								new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int whichButton) {
										isChange = true;
										mUserDetailDTO.setSex(tmpSex);
										sex.setText(mUserDetailDTO.getSex());
									}
								}).create().show();
				break;

			case R.id.age_layout:
				if (datePickerDialog != null && !datePickerDialog.isShowing()) {
					datePickerDialog.show();
				}
				break;
			default:
				break;
			}
		}

		public void initPasswordView() {
			mPasswordView = LayoutInflater.from(mContext).inflate(
					R.layout.setting_change_password, null);
			old_pass = (EditText) mPasswordView.findViewById(R.id.old_pass);
			new_pass = (EditText) mPasswordView.findViewById(R.id.new_pass);
			again_new_pass = (EditText) mPasswordView
					.findViewById(R.id.again_new_pass);

			old_pass.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					oldP = s.toString();
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					oldP = s.toString();
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});

			new_pass.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					newP = s.toString();
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					newP = s.toString();
				}

				@Override
				public void afterTextChanged(Editable s) {
				}
			});

			again_new_pass.addTextChangedListener(new TextWatcher() {

				@Override
				public void onTextChanged(CharSequence s, int start,
						int before, int count) {
					againNewP = s.toString();
				}

				@Override
				public void beforeTextChanged(CharSequence s, int start,
						int count, int after) {
					againNewP = s.toString();
				}

				@Override
				public void afterTextChanged(Editable s) {

				}
			});
			OnEditorActionListener onEditorActionListener = new OnEditorActionListener() {
				
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (actionId == 0 && v != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
						actionId = v.getImeOptions();
					}
					switch (actionId) {
					case EditorInfo.IME_ACTION_NEXT:
						if (!JingTools.judgePassword(v)) {
							return true;
						}
						if (v == old_pass) {
							new_pass.requestFocus();
						} else if (v == new_pass) {
							again_new_pass.requestFocus();
						}
						return true;
					case EditorInfo.IME_ACTION_DONE:
						if (!JingTools.judgePassword(old_pass)) {
							return true;
						}
						if (!JingTools.judgePassword(new_pass)) {
							return true;
						}
						if (!JingTools.judgePassword(again_new_pass)) {
							return true;
						}
						if (!new_pass.getText().toString().equals(again_new_pass.getText().toString())) {
							Toast.makeText(mContext, "新密码输入两次不一致", 1).show();
							return true;
						}
						Toast.makeText(mContext, "正在提交，请稍后", 1).show();
						new Thread() {
							@Override
							public void run() {
								HashMap<Object, Object> params = new HashMap<Object, Object>();
								params.put("uid", mContext.getmLoginData()
										.getUsr().getId());
								params.put("oldpwd", oldP);
								params.put("newpwd", newP);
								final ResultResponse<String> pls_post = UserRequestApi
										.changePwd(params);
								mHandler.post(new Runnable() {
									
									@Override
									public void run() {
										if (pls_post.isSuccess()) {
											Toast.makeText(mContext, "更改成功",
													Toast.LENGTH_SHORT).show();
											mContext.onKeyDown(KeyEvent.KEYCODE_BACK, new KeyEvent(KeyEvent.ACTION_DOWN,KeyEvent.KEYCODE_BACK));
										} else {
											Toast.makeText(mContext, "更改失败请重试",
													Toast.LENGTH_SHORT).show();
										}
									}
								});
							}
						}.start();
						return true;
					}
					return true;
				}
			};
			old_pass.setOnEditorActionListener(onEditorActionListener);
			new_pass.setOnEditorActionListener(onEditorActionListener);
			again_new_pass.setOnEditorActionListener(onEditorActionListener);
		}

		protected void pushPasswordView() {
			initPasswordView();
			mContext.getmViewManagerCenter().pushNewView(mPasswordView,null);
		}
	}

}
