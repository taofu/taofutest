package com.jingfm.ViewManager;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.jingfm.Jing3rdPartLoginActivity;
import com.jingfm.MainActivity;
import com.jingfm.MainActivity.ActivityCallback;
import com.jingfm.MainActivity.MainViewAsyncTask;
import com.jingfm.R;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.UserChatRequestApi;
import com.jingfm.api.business.UserOAuthRequestApi;
import com.jingfm.api.business.UserRequestApi;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.LoginDataDTO;
import com.jingfm.api.model.message.UserOfflineMessageDTO;
import com.jingfm.api.model.socketmessage.SocketPChatDTO;
import com.jingfm.background_model.SettingManager;
import com.jingfm.tools.JingTools;

public class LoginViewManager {

	private static final int LOGIN_INDEX_REG_TYPE = 0;
	private static final int LOGIN_INDEX_LOGIN_TYPE = 1;
	private static final int LOGIN_INDEX_REG_INPUT = 2;
	private static final int LOGIN_INDEX_LOGIN_INPUT = 3;
	private static final int LOGIN_INDEX_FORGOT_PD = 4;

	private static final int VIEW_IN_ANIMATE_DIRECTION_TOP = 0;
	private static final int VIEW_IN_ANIMATE_DIRECTION_BOTTOM = 1;
	private static final int VIEW_IN_ANIMATE_DIRECTION_LEFT = 2;
	private static final int SEX_MARK_UNKNOWN = 0;
	private static final int SEX_MARK_MALE = 1;
	private static final int SEX_MARK_FEMALE = 2;

	private MainActivity mContext;
	private View registSelect;
	private View loginSelect;
	private View loginInputView;
	private View registInputView;
	private ViewGroup mContainer;
	private OnTouchListener loginTouchListener;
	protected boolean isLoginViewsLock;
	private boolean isLoginModeNoRegister = true;
	private View showInView;
	private Set<LoginStateChangeListener> loginStateChangeListenerSet = new HashSet<LoginStateChangeListener>();
	private String mRegisterAvatarPath;
	private View forgotPasswordView;
	private int mSexMark = SEX_MARK_UNKNOWN;
	private Handler mHandler;
	private View slider_title_shell;
	private View slider_icon;
	private View slider_title;
	private View slider_title_sub;
	private ViewGroup mLoginInputViewGroup;
	private TextView mRedButton;
	private TextView mBlueButton;
	private NewbieManager mNewbieManager;
	private OnClickListener mLoginOnclickListener;
	private OnClickListener mRegisterOnclickListener;
	private OnClickListener mBackOnclickListener;
	private View login_base_view;

	public LoginViewManager(MainActivity context, ViewGroup container) {
		this.mContext = context;
		this.mContainer = container;
		initHandler();
	}

	private void initHandler() {
		mHandler = new Handler();
	}

	private void initLoginViews() {
		login_base_view = LayoutInflater.from(mContext).inflate(
				R.layout.login_base_view, null);
//		login_sign_in_text
		slider_title_shell = login_base_view.findViewById(R.id.slider_title_shell);
		slider_title_shell.setVisibility(View.GONE);
		slider_title = login_base_view.findViewById(R.id.slider_title);
		slider_title_sub = login_base_view.findViewById(R.id.slider_title_sub);
		slider_icon = login_base_view.findViewById(R.id.slider_icon);
		mRedButton = (TextView)login_base_view.findViewById(R.id.login_go_back_text);
		mBlueButton = (TextView)login_base_view.findViewById(R.id.login_register_text);
		mBlueButton.findViewById(R.id.login_register_text).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				showLoginSelectView();
			}
		});
		mContainer.addView(login_base_view);
		mLoginInputViewGroup = (ViewGroup)login_base_view.findViewById(R.id.login_animation_layout);
		registSelect = LayoutInflater.from(mContext).inflate(
				R.layout.login_select_dialog, null);
		loginSelect = LayoutInflater.from(mContext).inflate(
				R.layout.login_select_dialog, null);
		loginInputView = LayoutInflater.from(mContext).inflate(
				R.layout.login_dialog, null);
		registInputView = LayoutInflater.from(mContext).inflate(
				R.layout.login_register_dialog, null);
		forgotPasswordView = LayoutInflater.from(mContext).inflate(
				R.layout.login_forgot_password, null);
		forgotPasswordView.setVisibility(View.GONE);
		((TextView) loginSelect.findViewById(R.id.login_select_title)).setText("选择登录方式");
		((TextView) loginSelect.findViewById(R.id.login_select_subtitle)).setText("你可以通过下面三种方式登录并使用Jing");
		((TextView) registSelect.findViewById(R.id.login_select_title)).setText("选择注册方式");
		((TextView) registSelect.findViewById(R.id.login_select_subtitle)).setText("你可以通过下面三种方式注册到Jing");
		final TextView checkbox_male = (TextView) registInputView
				.findViewById(R.id.checkbox_male);
		final TextView checkbox_female = (TextView) registInputView
				.findViewById(R.id.checkbox_female);
		OnClickListener checkbox_onClickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (v == checkbox_male) {
					checkbox_male.setTextColor(mContext.getResources().getColor(R.color.register_sex_checked));
					checkbox_female.setTextColor(mContext.getResources().getColor(R.color.register_sex_uncheck));
					mSexMark = SEX_MARK_MALE;
				} else if (v == checkbox_female) {
					checkbox_male.setTextColor(mContext.getResources().getColor(R.color.register_sex_uncheck));
					checkbox_female.setTextColor(mContext.getResources().getColor(R.color.register_sex_checked));
					mSexMark = SEX_MARK_FEMALE;
				}
			}
		};
		checkbox_male.setOnClickListener(checkbox_onClickListener);
		checkbox_female.setOnClickListener(checkbox_onClickListener);
		registInputView.findViewById(R.id.checkbox_female);
		mLoginInputViewGroup.addView(loginSelect, LOGIN_INDEX_REG_TYPE);
		mLoginInputViewGroup.addView(registSelect, LOGIN_INDEX_LOGIN_TYPE);
		mLoginInputViewGroup.addView(registInputView, LOGIN_INDEX_REG_INPUT);
		mLoginInputViewGroup.addView(loginInputView, LOGIN_INDEX_LOGIN_INPUT);
		mLoginInputViewGroup.addView(forgotPasswordView, LOGIN_INDEX_FORGOT_PD);
		
		TextView main_text = (TextView) loginSelect
				.findViewById(R.id.main_text);
		final TextView sub_text = (TextView) loginSelect
				.findViewById(R.id.sub_text);
		main_text.setText(mContext.getText(R.string.login_type_main_text));
		sub_text.setText(mContext.getText(R.string.login_type_sub_text));

		final Button button_cancel1 = (Button) loginSelect
				.findViewById(R.id.button_cancel);
		final Button button_cancel2 = (Button) registSelect
				.findViewById(R.id.button_cancel);
		final Button button_submit1 = (Button) loginInputView
				.findViewById(R.id.button_submit);
		final Button button_forgot_password = (Button) loginInputView
				.findViewById(R.id.button_forgot_password);
		final Button button_submit2 = (Button) registInputView
				.findViewById(R.id.button_submit);
		final Button button_submit3 = (Button) forgotPasswordView
				.findViewById(R.id.button_submit);

		final EditText login_email1 = (EditText) loginInputView
				.findViewById(R.id.login_email);
		login_email1.setText(SettingManager.getInstance().getLastUserName());
		final EditText login_email2 = (EditText) registInputView
				.findViewById(R.id.login_email);
		final EditText login_email3 = (EditText) forgotPasswordView
				.findViewById(R.id.login_email);
		final EditText login_password1 = (EditText) loginInputView
				.findViewById(R.id.login_password);
		final EditText login_password2 = (EditText) registInputView
				.findViewById(R.id.login_password);
		final EditText register_name = (EditText) registInputView
				.findViewById(R.id.register_name);
		final ImageView register_avatar = (ImageView) registInputView
				.findViewById(R.id.register_avatar);
		register_avatar.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Intent intent = new Intent();
					/* 开启Pictures画面Type设定为image */
					intent.setType("image/*");
					/* 使用Intent.ACTION_GET_CONTENT这个Action */
					intent.setAction(Intent.ACTION_GET_CONTENT);
					/* 取得相片后返回本画面 */
					mContext.setActivityCallback(new ActivityCallback() {

						@Override
						public void onActivityCallback(String[] params) {
							if (params.length <= 0) {
								return;
							}
							String file = params[0];
							FileInputStream fileInputStream = null;
							try {
								fileInputStream = new FileInputStream(file);
								register_avatar.setImageBitmap(BitmapFactory
										.decodeStream(fileInputStream));
							} catch (FileNotFoundException e) {
								e.printStackTrace();
							}finally{
								try {
									if (fileInputStream != null) {
										fileInputStream.close();
									}
								} catch (Exception e2) {
								}
							}
							mRegisterAvatarPath = file;
						}
					});
					mContext.startActivityForResult(intent,
							MainActivity.START_ACTIVIY_REQUEST_CODE_PICK_AVATER);
				}
				return true;
			}
		});

		OnEditorActionListener actionListener = new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if (arg1 == 0 && arg2 != null && arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					arg1 = arg0.getImeOptions();
				}
				switch (arg1) {
				case EditorInfo.IME_ACTION_NEXT:
					if (arg0 == login_email1) {
						if (!JingTools.judgeEmailAddress(arg0)) {
							return true;
						}
						login_password1.requestFocus();
					} else if (arg0 == login_email2) {
						if (!JingTools.judgeEmailAddress(arg0)) {
							return true;
						}
						login_password2.requestFocus();
					} else if (arg0 == register_name) {
						if (!JingTools.judgeNickName(arg0)) {
							return true;
						}
						login_email2.requestFocus();
					}
					break;
				case EditorInfo.IME_ACTION_DONE:
					if (!JingTools.judgePassword(arg0)) {
						return true;
					}
					hideSoftKeyboard();
					if (arg0 == login_password1) {
						doLogin(login_email1, login_password1);
					} else if (arg0 == login_password2) {
						doRegister(login_email2, login_password2,
								register_name,mRegisterAvatarPath);
					}
					break;
				case EditorInfo.IME_ACTION_SEND:
					if (!JingTools.judgeEmailAddress(arg0)) {
						return true;
					}
					sendForgotPassword(arg0.getText().toString());
					break;
				default:
					break;
				}
				return true;
			}
		};
		login_email1.setOnEditorActionListener(actionListener);
		login_password1.setOnEditorActionListener(actionListener);
		register_name.setOnEditorActionListener(actionListener);
		login_email2.setOnEditorActionListener(actionListener);
		login_password2.setOnEditorActionListener(actionListener);
		login_email3.setOnEditorActionListener(actionListener);

		OnClickListener onButtonClickListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				v.requestFocus();
				if (v == button_cancel1 || v == button_cancel2) {
					doGuestLogin();
				} else if (v == button_submit1) {
					doLogin(login_email1, login_password1);
				} else if (v == button_submit2) {
					doRegister(login_email2, login_password2, register_name, mRegisterAvatarPath);
				} else if (v == button_forgot_password) {
					newViewIn(VIEW_IN_ANIMATE_DIRECTION_TOP,
							forgotPasswordView);
				} else if (v == button_submit3) {
					// TODO
					if (!JingTools.judgeEmailAddress(login_email3)) {
						return;
					}
					sendForgotPassword(login_email3.getText().toString());
				}
			}
		};
		button_cancel1.setOnClickListener(onButtonClickListener);
		button_cancel2.setOnClickListener(onButtonClickListener);
		button_submit1.setOnClickListener(onButtonClickListener);
		button_submit2.setOnClickListener(onButtonClickListener);
		button_forgot_password.setOnClickListener(onButtonClickListener);
		button_submit3.setOnClickListener(onButtonClickListener);
		final View login_type_weibo = loginSelect.findViewById(R.id.type_weibo);
		final View login_type_renren = loginSelect
				.findViewById(R.id.type_renren);
		final View login_type_email = loginSelect.findViewById(R.id.type_email);
		final View regist_type_weibo = registSelect
				.findViewById(R.id.type_weibo);
		final View regist_type_renren = registSelect
				.findViewById(R.id.type_renren);
		final View regist_type_email = registSelect
				.findViewById(R.id.type_email);
		loginTouchListener = new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (isLoginViewsLock) {
					return true;
				}
				if (v.equals(login_type_email)) {
					isLoginViewsLock = true;
					newViewIn(VIEW_IN_ANIMATE_DIRECTION_TOP, loginInputView);
				} else if (v.equals(regist_type_email)) {
					isLoginViewsLock = true;
					newViewIn(VIEW_IN_ANIMATE_DIRECTION_TOP, registInputView);
				} else if (v.equals(login_type_weibo)
						|| v.equals(regist_type_weibo)) {
					isLoginViewsLock = true;
					Intent i = new Intent();
					String url = UserOAuthRequestApi
							.getLoginAuthorizeUrl(UserOAuthRequestApi.OAuth_Sina_WEIBO_Identify);
					i.putExtra("url", url);
					i.setClass(mContext, Jing3rdPartLoginActivity.class);
					((Activity) mContext)
							.startActivityForResult(
									i,
									MainActivity.START_ACTIVIY_REQUEST_CODE_3rd_part_login);
//					((Activity) mContext)
//					.startActivityForResult(
//							new Intent(mContext,MainLoginActivity.class),
//							MainActivity.START_ACTIVIY_REQUEST_CODE_3rd_part_login);
				} else if (v.equals(login_type_renren)
						|| v.equals(regist_type_renren)) {
					isLoginViewsLock = true;
					Intent i = new Intent();
					String url = UserOAuthRequestApi
							.getLoginAuthorizeUrl(UserOAuthRequestApi.OAuth_Renren_Identify);
					i.putExtra("url", url);
					i.setClass(mContext, Jing3rdPartLoginActivity.class);
					((Activity) mContext)
							.startActivityForResult(
									i,
									MainActivity.START_ACTIVIY_REQUEST_CODE_3rd_part_login);
				}
				return true;
			}
		};
		login_type_weibo.setOnTouchListener(loginTouchListener);
		login_type_renren.setOnTouchListener(loginTouchListener);
		login_type_email.setOnTouchListener(loginTouchListener);
		regist_type_weibo.setOnTouchListener(loginTouchListener);
		regist_type_renren.setOnTouchListener(loginTouchListener);
		regist_type_email.setOnTouchListener(loginTouchListener);
		mContainer.setOnTouchListener(loginTouchListener);
		loginSelect.setVisibility(View.GONE);
		registSelect.setVisibility(View.GONE);
		loginInputView.setVisibility(View.GONE);
		registInputView.setVisibility(View.GONE);
		mContainer.setBackgroundColor(0x88000000);
		mRedButton.setText("登录");
		mBackOnclickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isLoginViewsLock) {
					return;
				}
				mLoginInputViewGroup.setVisibility(View.VISIBLE);
				newViewIn(VIEW_IN_ANIMATE_DIRECTION_TOP, null);
				mRedButton.setOnClickListener(mLoginOnclickListener);
				mBlueButton.setOnClickListener(mRegisterOnclickListener);
				mRedButton.setText("登录");
				mBlueButton.setText("注册");
			}
		};
		mLoginOnclickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isLoginViewsLock || showInView == loginSelect) {
					return;
				}
				mLoginInputViewGroup.setVisibility(View.VISIBLE);
				newViewIn(VIEW_IN_ANIMATE_DIRECTION_TOP, loginSelect);
				if (v.equals(mRedButton)) {
					mRedButton.setOnClickListener(mBackOnclickListener);
					mRedButton.setText("返回");
				}
				if (v.equals(mBlueButton)) {
					mLoginInputViewGroup.setVisibility(View.VISIBLE);
					mBlueButton.setOnClickListener(mRegisterOnclickListener);
					mBlueButton.setText("注册");
				}
			}
		};
		mRegisterOnclickListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (isLoginViewsLock || showInView == registSelect) {
					return;
				}
				mLoginInputViewGroup.setVisibility(View.VISIBLE);
				newViewIn(VIEW_IN_ANIMATE_DIRECTION_TOP, registSelect);
				if (v.equals(mBlueButton)) {
					mLoginInputViewGroup.setVisibility(View.VISIBLE);
					mBlueButton.setOnClickListener(mLoginOnclickListener);
					mBlueButton.setText("登录");
					mRedButton.setOnClickListener(mBackOnclickListener);
					mRedButton.setText("返回");
				}
			}
		};
		mRedButton.setOnClickListener(mLoginOnclickListener);
		mBlueButton.setOnClickListener(mRegisterOnclickListener);
		slider_title_shell.setOnTouchListener(new OnTouchListener() {
			private float x_temp01;
			private float y_temp01;
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				float x = event.getX();
				float y = event.getY();
				float MINIMUM_OFFSET = JingTools.screenWidth /10f;
				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN: 
					x_temp01 = x;
					y_temp01 = y;
					break;
				case MotionEvent.ACTION_UP: 
					float x_temp02 = x;
					float y_temp02 = y;
					if (x_temp01 != 0 && y_temp01 != 0){
						if (x_temp01 > x_temp02 + MINIMUM_OFFSET){
							changeGuidePage(false);
						}else if (x_temp01 + MINIMUM_OFFSET < x_temp02){
							changeGuidePage(true);
						}
					}
					break;
				case MotionEvent.ACTION_MOVE: 
					break;
				}
				return true;
			}
		});
	}

	protected void sendForgotPassword(final String email) {
//		Toast.makeText(mContext, "忘记密码： " + email, 1).show();
		hideSoftKeyboard();
		new Thread(){
			public void run() {
//				email
				HashMap<Object, Object> params = new HashMap<Object, Object>();
				params.put("email", email);
				final ResultResponse<String> rs = UserRequestApi.forgotPassword(params);
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						if (rs != null && rs.isSuccess()) {
							Toast.makeText(mContext, "重置密码的链接已经发送至你的邮箱请查收。", 1).show();
						}else{
							Toast.makeText(mContext, "发送失败请重试", 1).show();
						}
					}
				});
				
			};
		}.start();
	}

	protected void doGuestLogin() {
		LoginDataDTO data = mContext.getmLoginData();
		if (data != null && data.getUsr().isGuest()) {
			removeLoginViews();
//			if (!mContext.isFirstSearchDone()) {
//				mContext.getmViewManagerCenter().showNewbieViews();
//			}
		} else {
			MainViewAsyncTask at = mContext
					.getMainViewAsyncTaskInstance(MainActivity.ASYNC_TASK_TYPE_GUEST_LOGIN);
			if (at != null) {
				at.execute((String[])null);
			}
		}
	}

	protected void doLogin(EditText login_email1, EditText login_password1) {
		if (!JingTools.judgeEmailAddress(login_email1)) {
			return;
		}
		if (!JingTools.judgePassword(login_password1)) {
			return;
		}
		String str1 = login_email1.getText().toString();
		String str2 = login_password1.getText().toString();
		MainViewAsyncTask at = mContext
				.getMainViewAsyncTaskInstance(MainActivity.ASYNC_TASK_TYPE_USER_LOGIN);
		if (at != null) {
			at.execute(str1, str2);
		}
	}

	public void showNewbieViews() {
		mNewbieManager = new NewbieManager(mContext);
		((ViewGroup) login_base_view).removeAllViews();
		login_base_view.setVisibility(View.VISIBLE);
		((ViewGroup) login_base_view).addView(mNewbieManager.getView());
		mNewbieManager.getView().bringToFront();
		mNewbieManager.getView().startAnimation(AnimationUtils.loadAnimation(mContext,
				R.anim.push_right_in));
	}
	
	protected void doRegister(EditText login_email2, EditText login_password2,
			EditText register_name,String registerAvatarPath) {
		if (!JingTools.judgeNickName(register_name)) {
			return;
		}
		if (!JingTools.judgeEmailAddress(login_email2)) {
			return;
		}
		if (!JingTools.judgePassword(login_password2)) {
			return;
		}
		String str1 = login_email2.getText().toString();
		String str2 = login_password2.getText().toString();
		String str3 = register_name.getText().toString();
		String str4 = "";
		if (mSexMark == SEX_MARK_MALE) {
			str4 = "男";
		} else if (mSexMark == SEX_MARK_FEMALE) {
			str4 = "女";
		}
		MainViewAsyncTask at = mContext
				.getMainViewAsyncTaskInstance(MainActivity.ASYNC_TASK_TYPE_USER_REGISTER);
		if (at != null) {
			at.execute(str1, str2, str3, str4, registerAvatarPath);
		}
	}

	public void addLoginListener(LoginStateChangeListener listener) {
		loginStateChangeListenerSet.add(listener);
	}

	public void delLoginListener(LoginStateChangeListener listener) {
		loginStateChangeListenerSet.remove(listener);
	}

	public void showLoginView() {
		if (mContext.getmLoginData() != null
				&& !mContext.getmLoginData().getUsr().isGuest()) {
			loginDone(mContext.getmLoginData());
			return;
		}
		mContext.setJingTitleText("Jing");
		isLoginViewsLock = false;
		mContainer.setVisibility(View.VISIBLE);
		mContainer.setFocusable(true);
		mContainer.bringToFront();
		initLoginViews();
		mRedButton.setText("登录");
		mRedButton.setOnClickListener(mLoginOnclickListener);
		mBlueButton.setText("注册");
		mBlueButton.setOnClickListener(mRegisterOnclickListener);
		if (!isNetworkConnected(mContext)) {
			Toast.makeText(mContext, "当前无网络", 1).show();
		}
	}

	protected void showLoginSelectView() {
		if (isLoginViewsLock) {
			return;
		}
		if (isLoginModeNoRegister) {
			newViewIn(VIEW_IN_ANIMATE_DIRECTION_TOP, registSelect);
		} else {
			newViewIn(VIEW_IN_ANIMATE_DIRECTION_BOTTOM, loginSelect);
		}
	}

	private void newViewIn(int direction, final View newView) {
		isLoginViewsLock = true;
		Animation animationOut = null;
		Animation animationIn = null;
		switch (direction) {
		case VIEW_IN_ANIMATE_DIRECTION_TOP:
			animationOut = AnimationUtils.loadAnimation(mContext,
					R.anim.push_top_out);
			animationIn = AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_in);
			break;
		case VIEW_IN_ANIMATE_DIRECTION_BOTTOM:
			animationOut = AnimationUtils.loadAnimation(mContext,
					R.anim.push_bottom_out);
			animationIn = AnimationUtils.loadAnimation(mContext,
					R.anim.push_top_in);
			break;
		case VIEW_IN_ANIMATE_DIRECTION_LEFT:
			animationOut = AnimationUtils.loadAnimation(mContext,
					R.anim.push_left_out);
			animationIn = AnimationUtils.loadAnimation(mContext,
					R.anim.push_left_in);
			break;
		}
		if (newView != null) {
			newView.setVisibility(View.VISIBLE);
			newView.bringToFront();
		}
		final Animation tmpAnimationOut = animationOut;
		AnimationListener animationListener = new AnimationListener() {

			@Override
			public void onAnimationStart(Animation animation) {
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				isLoginViewsLock = false;
				if (showInView != newView) {
					if (showInView != null) {
						showInView.setVisibility(View.GONE);
					}
					showInView = newView;
					isLoginModeNoRegister = 
								showInView == loginSelect
							|| showInView == loginInputView;
				}
			}
		};
		animationIn.setAnimationListener(animationListener);
		animationOut.setAnimationListener(animationListener);
		if (newView != null) {
			newView.startAnimation(animationIn);
		}
		if (showInView != null) {
			showInView.startAnimation(animationOut);
		}
	}

	public boolean isNetworkConnected(Context context) {
		if (context != null) {
			ConnectivityManager mConnectivityManager = (ConnectivityManager) context
					.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo mNetworkInfo = mConnectivityManager
					.getActiveNetworkInfo();
			if (mNetworkInfo != null) {
				return mNetworkInfo.isAvailable();
			}
		}
		return false;
	}

	public void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mContainer.getWindowToken(), 0);
	}

	public void removeLoginViews() {
		hideSoftKeyboard();
		mContext.getmViewManagerCenter().refreshRightButtonState();
		mContainer.setVisibility(View.GONE);
		mContainer.removeAllViews();
		loginSelect = null;
		loginInputView = null;
		registInputView = null;
		registSelect = null;
	}

	public void loginOk() {
		loginDone(mContext.getmLoginData());
		mContext.onNetworkStateChange(true);
	}

	public void loginDone(LoginDataDTO loginDataDTO) {
		if (loginDataDTO == null) {
			return;
		}
		if (loginDataDTO.getUsr().getNewbie() > 0) {
			showNewbieViews();
			return;
		}else{
			removeLoginViews();
		}
		if (loginDataDTO.getUsr().isGuest()) {
			mContext.startGuestHeartBeat();
		}else{
			mContext.refreshUserCm();
			mContext.stopGuestHeartBeat();
			mContext.setupWebView();
			try {
				mContext.setJingTitleText(loginDataDTO.getPld().getCmbt());
			} catch (Exception e) {
			}
			mContext.getmViewManagerCenter().initMusicViewManager();
		}
		for (LoginStateChangeListener listener : loginStateChangeListenerSet) {
			listener.onLogin(loginDataDTO);
		}
		mContext.getmViewManagerCenter().getmChatViewManager();
	}

	public boolean sendKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:
			if (mContainer.getVisibility() != View.GONE) {
				if (loginSelect == null) {
					return false;
				}
			}
			break;
		}
		return false;
	}

	public void logout() {
		mContext.postQuitInfo();
		mContext.destoryWebView();
		for (LoginStateChangeListener listener : loginStateChangeListenerSet) {
			listener.onLogout();
		}
		mContext.clearLoginDataDTO();
		showLoginView();
	}

	public void setLoginViewsLock(boolean b) {
		isLoginViewsLock = b;
	}

	public void showLoginGudieView() {
		showLoginView();
		mLoginInputViewGroup.setVisibility(View.GONE);
//		slider_title_shell.setVisibility(View.VISIBLE);
		showInView = null;
	}
	protected void changeGuidePage(boolean b) {
		Animation animationOut;
		final Animation animationIn;
		Animation animationOutIcon;
		final Animation animationInIcon;
		if (b) {
			animationOut = AnimationUtils.loadAnimation(mContext,
					R.anim.silde_away_to_right);
			animationOutIcon = AnimationUtils.loadAnimation(mContext,
					R.anim.silde_away_to_right);
			animationIn = AnimationUtils.loadAnimation(mContext,
					R.anim.silde_in_from_left);
			animationInIcon = AnimationUtils.loadAnimation(mContext,
					R.anim.silde_in_from_left);
		}else{
			animationOut = AnimationUtils.loadAnimation(mContext,
					R.anim.silde_away_to_left);
			animationOutIcon = AnimationUtils.loadAnimation(mContext,
					R.anim.silde_away_to_left);
			animationIn = AnimationUtils.loadAnimation(mContext,
					R.anim.silde_in_from_right);
			animationInIcon = AnimationUtils.loadAnimation(mContext,
					R.anim.silde_in_from_right);
		}
		animationOut.setFillAfter(true);
		animationOut.setDuration(800);
		animationOutIcon.setFillAfter(true);
		animationOutIcon.setDuration(1000);
		animationIn.setFillAfter(true);
		animationIn.setDuration(800);
		animationInIcon.setFillAfter(true);
		animationInIcon.setDuration(1000);
		animationOutIcon.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {}
			@Override
			public void onAnimationRepeat(Animation animation) {}
			@Override
			public void onAnimationEnd(Animation animation) {
				slider_title.startAnimation(animationIn);
				slider_title_sub.startAnimation(animationIn);
				slider_icon.startAnimation(animationInIcon);
			}
		});
		slider_title.startAnimation(animationOut);
		slider_title_sub.startAnimation(animationOut);
		slider_icon.startAnimation(animationOutIcon);
	}
}
