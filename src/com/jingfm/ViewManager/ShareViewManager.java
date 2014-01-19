package com.jingfm.ViewManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.jingfm.Jing3rdPartBindActivity;
import com.jingfm.Jing3rdPartLoginActivity;
import com.jingfm.MainActivity;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.Constants.KTC;
import com.jingfm.ViewManager.ChatViewManager.ChatUserData;
import com.jingfm.ViewManager.ViewManagerCenter.LinkedViewData;
import com.jingfm.adapter.AbstractDragAdapter;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.UserFriendRequestApi;
import com.jingfm.api.business.UserOAuthRequestApi;
import com.jingfm.api.business.UserSearchApi;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.UserFrdDTO;
import com.jingfm.api.model.UserSnsFrdDTO;
import com.jingfm.api.model.socketmessage.SocketPChatDTO;
import com.jingfm.api.model.socketmessage.SocketPChatPayloadShareTrackDTO;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.third_part_api.WeixinUtil;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.AsyncImageLoader.ImageCallback;
import com.jingfm.tools.JingTools;

public class ShareViewManager implements OnClickListener {
	private MainActivity mContext;
	private Handler mHandler;
	private View share_buttons_layout;
	private Animation silde_in_from_buttom_anim;
	private Animation silde_away_to_buttom_anim;
	protected boolean isShareAnimating;
	private WeixinUtil mWeixinUtil;
	private View share_layout;
	private View share_content_layout;
	private TextView share_song_name;
	private EditText share_content;
	private ImageView share_image;
	private TextView share_target;
	private Animation silde_in_from_top_anim;
	private Animation silde_away_to_buttom_anim_hide_all;
	private Button share_button_cancel;
	private Button share_button_commit;
	private SharedMusicDTO mMusicDtoBeShared;
	private MyFriendsSearchListAdapter mMyFriendsSearchListAdapter;
	private ArrayList<UserFrdDTO> mMyFriendsSearchList = new ArrayList<UserFrdDTO>();
	private OnTouchListener mFriendSelectListener;
	private View myfriendSearchView;
	private int mServerIndex;
	private String mSearchWord;
	private boolean isLoading;
	
	public ShareViewManager(MainActivity context) {
		this.mContext = context;
		initHandler();
		initShareView();
		initButtons();
		mFriendSelectListener = new OnTouchListener() {
			
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (mContext.isOfflineMode()) {
						mContext.toastOffLine();
						return true;
					}
					UserFrdDTO data = (UserFrdDTO) v.getTag();
					if (data != null && mMusicDtoBeShared != null) {
						SocketPChatDTO socketPChatDTO = new SocketPChatDTO();
						SocketPChatPayloadShareTrackDTO socketPChatPayloadDTO = mMusicDtoBeShared.musicDto
								.toShareMusicDTO();
						socketPChatDTO.setCtt(socketPChatPayloadDTO.getN());
						socketPChatDTO.setFuid(data.getUid());
						socketPChatDTO.setPayload(socketPChatPayloadDTO);
						ChatUserData chatUserData = new ChatUserData(data.getUid(), data.getNick(), data.getAvatar());
						mContext.getmViewManagerCenter().getmChatViewManager()
							.shareMusic(chatUserData,socketPChatDTO);
						mContext.getmViewManagerCenter().pushChatView(chatUserData);
					}
				}
				return true;
			}
		};
	}

	private void initShareView() {
		share_layout = LayoutInflater.from(mContext).inflate(
				R.layout.share_layout,null);
		share_content_layout = share_layout
				.findViewById(R.id.share_content_layout);
		share_song_name = (TextView) share_content_layout
				.findViewById(R.id.share_song_name);
		share_content = (EditText) share_content_layout
				.findViewById(R.id.share_content);
		View share_image_layout = share_content_layout
				.findViewById(R.id.share_image_layout);
		View send_buttons_layout = share_content_layout
				.findViewById(R.id.send_buttons_layout);
		RelativeLayout.LayoutParams lp = (android.widget.RelativeLayout.LayoutParams) share_content_layout.getLayoutParams();
		lp.width = share_content.getLayoutParams().height
				= share_image_layout.getLayoutParams().height 
				= share_image_layout.getLayoutParams().width 
				= JingTools.screenWidth *8/10;
		lp.height = lp.width * 76 / 100 * 2; 
		send_buttons_layout.setPadding(0, 0, 0, lp.width * 24 / 100);
		share_content.getLayoutParams().height = lp.height/2;
		share_image = (ImageView) share_content_layout
				.findViewById(R.id.share_image);
		share_target = (TextView) share_content_layout
				.findViewById(R.id.share_target);
		share_button_cancel = (Button) share_content_layout
				.findViewById(R.id.share_button_cancel);
		share_button_commit = (Button) share_content_layout
				.findViewById(R.id.share_button_commit);
		share_button_cancel.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideSoftKeyboard();
				hideContentLayout();
			}
		});
		share_buttons_layout = share_layout
				.findViewById(R.id.share_buttons_layout);
		share_buttons_layout.setVisibility(View.GONE);
		silde_in_from_top_anim = AnimationUtils.loadAnimation(
				mContext.getApplicationContext(), R.anim.silde_in_from_top);
		silde_in_from_buttom_anim = AnimationUtils.loadAnimation(
				mContext.getApplicationContext(), R.anim.silde_in_from_buttom);
		silde_in_from_buttom_anim.setDuration(300);
		silde_in_from_buttom_anim.setInterpolator(
				mContext.getApplicationContext(),
				android.R.anim.accelerate_decelerate_interpolator);
		silde_away_to_buttom_anim = AnimationUtils.loadAnimation(
				mContext.getApplicationContext(), R.anim.silde_away_to_buttom);
		silde_away_to_buttom_anim.setDuration(300);
		silde_away_to_buttom_anim.setInterpolator(
				mContext.getApplicationContext(),
				android.R.anim.accelerate_decelerate_interpolator);
		silde_away_to_buttom_anim_hide_all = AnimationUtils.loadAnimation(
				mContext.getApplicationContext(), R.anim.silde_away_to_buttom);
		silde_away_to_buttom_anim.setDuration(300);
		silde_away_to_buttom_anim.setInterpolator(
				mContext.getApplicationContext(),
				android.R.anim.accelerate_decelerate_interpolator);
		AnimationListener animationListener = new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
				isShareAnimating = true;
				mContext.setAnimateContainerInterrupter(true);
			}

			@Override
			public void onAnimationRepeat(Animation animation) {
			}

			@Override
			public void onAnimationEnd(Animation animation) {
				if (animation.equals(silde_away_to_buttom_anim)) {
					share_buttons_layout.setVisibility(View.GONE);
					hideSoftKeyboard();
				} else if (animation.equals(silde_away_to_buttom_anim_hide_all)) {
					hideAllNoAnimate();
					hideSoftKeyboard();
				} else if (animation.equals(silde_in_from_top_anim)) {
					share_content.requestFocus();
					if (JingTools.isValidString(mMusicDtoBeShared.cmbt)) {
						share_content.setText("这个条件不错，分享给你听");
					}else{
						share_content.setText("这首歌很好听，分享给大家一起享受~");
					}
					share_content.setSelection(share_content.length());
					showSoftKeyboard();
				}
				isShareAnimating = false;
			}
		};
		silde_in_from_top_anim.setAnimationListener(animationListener);
		silde_in_from_buttom_anim.setAnimationListener(animationListener);
		silde_away_to_buttom_anim.setAnimationListener(animationListener);
		silde_away_to_buttom_anim_hide_all
				.setAnimationListener(animationListener);
	}

	public void hideContentLayout() {
		if (isShareAnimating) {
			return;
		}
		share_content_layout.startAnimation(silde_away_to_buttom_anim_hide_all);
	}
	public void hideAllView() {
		if (share_buttons_layout.getVisibility() == View.VISIBLE) {
			 hideShareView(true);
		}
		if (share_content_layout.getVisibility() == View.VISIBLE) {
			hideContentLayout();
		}
	}

	public void hideShareView(boolean b) {
		if (isShareAnimating) {
			return;
		}
		if (b) {
			share_buttons_layout.startAnimation(silde_away_to_buttom_anim_hide_all);
		}else{
			hideAllNoAnimate();
		}
	}
	
	public void hideAllNoAnimate(){
		share_buttons_layout.setVisibility(View.GONE);
		share_layout.setVisibility(View.GONE);
		mContext.getAnimateContainer().setPadding(0, 0, 0, 0);
		mContext.getAnimateContainer().setVisibility(View.GONE);
		mContext.setAnimateContainerInterrupter(false);
	}
	
	private void initButtons() {
		initButton(R.id.share_renren_button);
		initButton(R.id.share_sina_weibo_button);
		initButton(R.id.share_tencent_weibo_button);
		initButton(R.id.share_micro_message_friends_button);
		initButton(R.id.share_micro_message_button);
		initButton(R.id.share_jing_button);
		initButton(R.id.share_cancel);
	}

	private void initButton(int rid) {
		View button = share_buttons_layout.findViewById(rid);
		if (button == null) {
			return;
		}
		button.setTag(new Integer(rid));
		button.setOnClickListener(this);
	}

	public void showShareView(SharedMusicDTO musicDtoBeShared,int paddingButtom) {
		if (musicDtoBeShared == null || isShareAnimating) {
			return;
		}
		mMusicDtoBeShared = musicDtoBeShared;
		if (JingTools.isValidString(mMusicDtoBeShared.cmbt)) {
			share_buttons_layout.findViewById(R.id.share_jing_button).setVisibility(View.GONE);
		}else{
			share_buttons_layout.findViewById(R.id.share_jing_button).setVisibility(View.VISIBLE);
		}
		mContext.getAnimateContainer().setVisibility(View.VISIBLE);
		mContext.getAnimateContainer().setPadding(0, 0, 0, paddingButtom);
		if (share_layout.getParent() == null) {
			mContext.getAnimateContainer().addView(share_layout);
		}
		share_layout.setVisibility(View.VISIBLE);
		share_content_layout.setVisibility(View.GONE);
		share_buttons_layout.setVisibility(View.VISIBLE);
		share_buttons_layout.startAnimation(silde_in_from_buttom_anim);
	}

	public void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(share_layout.getWindowToken(), 0);
	}
	
	public void showSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.showSoftInput(share_content, InputMethodManager.SHOW_IMPLICIT);
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

	@Override
	public void onClick(View v) {
		startShare((Integer) v.getTag());
	}

	private void startShare(Integer tag) {
		String imageUrl;
		switch (tag) {
		case R.id.share_renren_button:
			if (JingTools.isValidString(mMusicDtoBeShared.cmbt)) {
				KTC.rep("Home", "ShareSearchTo", "renren" + ":" + KTC.getTimeKeyString());
			}else{
				KTC.rep("Home", "ShareTrackTo" , "renren" + ":" + KTC.getTimeKeyString());
			}
			share3rdPart(UserOAuthRequestApi.OAuth_Renren_Identify);
			break;
		case R.id.share_sina_weibo_button:
			if (JingTools.isValidString(mMusicDtoBeShared.cmbt)) {
				KTC.rep("Home", "ShareSearchTo", "weibo" + ":" + KTC.getTimeKeyString());
			}else{
				KTC.rep("Home", "ShareTrackTo", "weibo" + ":" + KTC.getTimeKeyString());
			}
			share3rdPart(UserOAuthRequestApi.OAuth_Sina_WEIBO_Identify);
			break;
		case R.id.share_tencent_weibo_button:
			if (JingTools.isValidString(mMusicDtoBeShared.cmbt)) {
				KTC.rep("Home", "ShareSearchTo", "qq" + ":" + KTC.getTimeKeyString());
			}else{
				KTC.rep("Home", "ShareTrackTo", "qq" + ":" + KTC.getTimeKeyString());
			}
			share3rdPart(UserOAuthRequestApi.OAuth_Qq_WEIBO_Identify);
			break;
		case R.id.share_micro_message_friends_button:
			if (mWeixinUtil == null) {
				mWeixinUtil = new WeixinUtil(mContext);
			}
			try {
				imageUrl = CustomerImageRule.ID2URL(
						Constants.ID2URL_KEY_WORD_ALBUM,
						mMusicDtoBeShared.musicDto.getFid(),
						Constants.ID2URL_DEFAULT_BITRATE_ALBUM);
				if (JingTools.isValidString(mMusicDtoBeShared.cmbt)) {
					KTC.rep("Home", "ShareSearchTo", "wxFriend" + ":" + KTC.getTimeKeyString());
					mWeixinUtil.sendRadio(mMusicDtoBeShared.musicDto.getTid(),
							mMusicDtoBeShared.musicDto.getMid(), mMusicDtoBeShared.musicDto.getN(),
							imageUrl,mMusicDtoBeShared.cmbt, false);
				}else{
					KTC.rep("Home", "ShareTrackTo", "wxFriend" + ":" + KTC.getTimeKeyString());
					mWeixinUtil.sendMusic(mMusicDtoBeShared.musicDto.getTid(),
							mMusicDtoBeShared.musicDto.getMid(), mMusicDtoBeShared.musicDto.getN(),
							imageUrl, false);
				}
			} catch (Exception e) {
			}
			break;
		case R.id.share_micro_message_button:
			if (mWeixinUtil == null) {
				mWeixinUtil = new WeixinUtil(mContext);
			}
			try {
				imageUrl = CustomerImageRule.ID2URL(
						Constants.ID2URL_KEY_WORD_ALBUM,
						mMusicDtoBeShared.musicDto.getFid(),
						Constants.ID2URL_DEFAULT_BITRATE_ALBUM);
				if (JingTools.isValidString(mMusicDtoBeShared.cmbt)) {
					KTC.rep("Home", "ShareSearchTo", "wxCircle" + ":" + KTC.getTimeKeyString());
					mWeixinUtil.sendRadio(mMusicDtoBeShared.musicDto.getTid(),
							mMusicDtoBeShared.musicDto.getMid(), mMusicDtoBeShared.musicDto.getN(),
							imageUrl,mMusicDtoBeShared.cmbt, true);
				}else{
					KTC.rep("Home", "ShareTrackTo", "wxCircle" + ":" + KTC.getTimeKeyString());
					mWeixinUtil.sendMusic(mMusicDtoBeShared.musicDto.getTid(),
							mMusicDtoBeShared.musicDto.getMid(), mMusicDtoBeShared.musicDto.getN(),
							imageUrl, true);
				}
			} catch (Exception e) {
			}
			break;
		case R.id.share_jing_button:
			hideShareView(false);
			shareMusicToFriendView();
			break;
		case R.id.share_cancel:
			hideShareView(true);
			break;
		}
	}

	private void shareMusicToFriendView() {
		if (mMyFriendsSearchListAdapter == null) {
			initFriendsSearchView();
		}
		myfriendSearchView.setVisibility(View.VISIBLE);
		mContext.getmViewManagerCenter().removeAllViewsAddNew(myfriendSearchView);
	}
	private void initFriendsSearchView() {
		myfriendSearchView = LayoutInflater.from(mContext).inflate(R.layout.center_list_friends_search, null);
		final EditText friendsSearchEditText = (EditText) myfriendSearchView.findViewById(R.id.search_friends_text);
		friendsSearchEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if (arg1 == 0 && arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					arg1 = arg0.getImeOptions();
				}
				if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
					final String searchWord = friendsSearchEditText.getText().toString();
					if (!searchWord.isEmpty()) {
						getMyFriendsSearchData(searchWord);
						hideSoftKeyboard();
					}
					return true;
				}
				return false;
			}
		});
		final ImageView search_friends_button = (ImageView) myfriendSearchView.findViewById(R.id.search_friends_button);
		search_friends_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getMyFriendsSearchData(friendsSearchEditText.getText().toString());
				hideSoftKeyboard();
			}
		});
		DragRefreshListView myfriendSearchListView = (DragRefreshListView) myfriendSearchView.findViewById(R.id.friends_search_list_view);
		mMyFriendsSearchListAdapter = new MyFriendsSearchListAdapter();
		mMyFriendsSearchListAdapter.setListView(myfriendSearchListView);
		getMyFriendsSearchData("");
	}

	protected void getMyFriendsSearchData(final String searchWord) {
		if (isLoading) {
			return;
		}
		isLoading = true;
		if (!(searchWord+"").equals(mSearchWord)) {
			mServerIndex = 0;
		}
		mSearchWord = searchWord;
		new Thread(){
			public void run() {
				if (JingTools.isValidString(searchWord)) {
					HashMap<Object,Object> params = new HashMap<Object,Object>();
					params.put("u", ""+mContext.getUserId());
					params.put("q", ""+searchWord);
					params.put("st", ""+mServerIndex);
					params.put("ps", ""+Constants.DEFAULT_NUM_OF_LOAD);
					final ResultResponse<ListResult<UserFrdDTO>> rs = UserSearchApi.fetchFrdNick(params);
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if(rs != null && rs.isSuccess()){
								List<UserFrdDTO> list = rs.getResult().getItems();
								mMyFriendsSearchList.clear();
								mMyFriendsSearchList.addAll(list);
								mMyFriendsSearchListAdapter.notifyDataSetChanged();
							}else{
								Toast.makeText(mContext, "取列表失败", 0).show();
							}
							isLoading = false;
						}
					});
				}else{
					final HashMap<String, String> map = new HashMap<String, String>();
					map.put("uid", "" + mContext.getUserId());
					map.put("ouid", "" + mContext.getUserId());
					map.put("st", "" + mServerIndex);
					map.put("ps", "" + Constants.DEFAULT_NUM_OF_LOAD);
					final ResultResponse<ListResult<UserFrdDTO>> resultResponse = UserFriendRequestApi
							.fetchFriendsOrder(map);
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							List<UserFrdDTO> listResult = null;
							if (resultResponse != null
									&& resultResponse.isSuccess()) {
								listResult = resultResponse.getResult().getItems();
								if (mServerIndex <= 0) {
									mMyFriendsSearchList.clear();
								}
								for (UserFrdDTO userFrdDTO : listResult) {
									mMyFriendsSearchList.add(userFrdDTO);
								}
								mServerIndex += listResult.size();
							} else {
								Toast.makeText(mContext, "加载失败", 0).show();
							}
							mMyFriendsSearchListAdapter.stopLoadMore();
							if (listResult != null) {
								mMyFriendsSearchListAdapter.setPullLoadEnable(!(listResult.size() < Constants.DEFAULT_NUM_OF_LOAD));
							}
							mMyFriendsSearchListAdapter.notifyDataSetChanged();
							isLoading = false;
						}
					});
				}
				
			};
		}.start();
	}

	
	public void share3rdPart(final String identify) {
		Map<String, String> map = mContext.getmLoginData().getSnstokens();
		Log.e("kid_debug", "" + map);
		boolean binded1 = mContext.getmLoginData().getSnstokens().keySet()
				.contains(identify);
		if (binded1) {
			showShareContent(identify);
		} else {
			String url = UserOAuthRequestApi.getBindAuthorizeUrl(""
					+ mContext.getUserId(), identify);
			Intent intent1 = new Intent(mContext, Jing3rdPartBindActivity.class);
			intent1.putExtra("url", url);
			intent1.putExtra("identify", identify);
			intent1.putExtra("uid", ""
					+ mContext.getUserId());
			mContext.startActivityForResult(
					intent1,
					MainActivity.START_ACTIVIY_REQUEST_CODE_3rd_part_bind_in_share);
		}
	}

	private void showShareContent(final String identify) {
		if (isShareAnimating) {
			return;
		}
		if (identify == UserOAuthRequestApi.OAuth_Qq_WEIBO_Identify) {
			share_target.setText("分享到腾讯微博");
		} else if (identify == UserOAuthRequestApi.OAuth_Sina_WEIBO_Identify) {
			share_target.setText("分享到新浪微博");
		} else if (identify == UserOAuthRequestApi.OAuth_Renren_Identify) {
			share_target.setText("分享到人人网");
		}
		share_song_name.setText(mMusicDtoBeShared.musicDto.getN());
		String url = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_ALBUM,mMusicDtoBeShared.musicDto.getFid(),
				Constants.ID2URL_DEFAULT_BITRATE_ALBUM);
		AsyncImageLoader.getInstance().loadBitmapByUrl(url, AsyncImageLoader.IMAGE_TYPE_ORIGINAL, new ImageCallback() {
			
			@Override
			public void imageLoaded(final Bitmap bitmap, String imageUrl) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						share_image.setImageBitmap(bitmap);
					}
				});
			}
		});
		share_content_layout.setVisibility(View.VISIBLE);
		share_content_layout.startAnimation(silde_in_from_top_anim);
		share_buttons_layout.startAnimation(silde_away_to_buttom_anim);
		share_button_commit.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				hideSoftKeyboard();
				share_content_layout
						.startAnimation(silde_away_to_buttom_anim_hide_all);
				new Thread() {
					@Override
					public void run() {
						HashMap<Object, Object> params = new HashMap<Object, Object>();
						params.put("uid", ""
								+ mContext.getUserId());
						params.put("tid", "" + mMusicDtoBeShared.musicDto.getTid());
						params.put("identify", "" + identify);
						ResultResponse<String> result;
						if (JingTools.isValidString(mMusicDtoBeShared.cmbt)) {
							params.put("q", "" + mMusicDtoBeShared.cmbt);
							params.put("cmbt", "" + share_content.getText());
							result = UserOAuthRequestApi
									.postShareCmbt(params);
						}else{
							params.put("c", "" + share_content.getText());
							result = UserOAuthRequestApi
									.postShareMusic(params);
						}
						final ResultResponse<String> rs = result;
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								if (rs.isSuccess()) {
									Toast.makeText(mContext, "分享成功", 0).show();
								} else {
									if ("802".equals(rs.getCode())) {
										Toast.makeText(mContext, "授权已过期请重新绑定",
												1).show();
									}

								}
							}
						});
					}
				}.start();
			}
		});

	}

	public void bindBack(int resultCode, Intent data) {
		switch (resultCode) {
		case Activity.RESULT_OK:
			if (UserOAuthRequestApi.OAuth_Sina_WEIBO_Identify.equals(data
					.getStringExtra("identify"))) {
				if (!data.getStringExtra("uid").equals(
						"" + mContext.getUserId())) {
					return;
				}
				KTC.rep("Chatting", "UseConnectToWeibo", "");
				mContext.getmLoginData().getSnstokens()
						.put(UserOAuthRequestApi.OAuth_Sina_WEIBO_Identify, "");
				startShare(R.id.share_sina_weibo_button);
			} else if (UserOAuthRequestApi.OAuth_Renren_Identify.equals(data
					.getStringExtra("identify"))) {
				KTC.rep("Chatting", "UseConnectToRenren", "");
				mContext.getmLoginData().getSnstokens()
						.put(UserOAuthRequestApi.OAuth_Renren_Identify, "");
				startShare(R.id.share_renren_button);
			} else if (UserOAuthRequestApi.OAuth_Qq_WEIBO_Identify.equals(data
					.getStringExtra("identify"))) {
				KTC.rep("Chatting", "UseConnectToTencent", "");
				mContext.getmLoginData().getSnstokens()
						.put(UserOAuthRequestApi.OAuth_Qq_WEIBO_Identify, "");
				startShare(R.id.share_tencent_weibo_button);
			}
			break;
		case Jing3rdPartLoginActivity.RESULT_FAILED:
			Toast.makeText(mContext.getApplicationContext(), "绑定失败请重试", 0)
					.show();
		case Activity.RESULT_CANCELED:
			break;
		}
	}

	public boolean isShowing() {
		return share_layout.getVisibility() == View.VISIBLE;
	}
	
	
	class MyFriendsSearchListAdapter extends AbstractDragAdapter{

		private DragRefreshListView mListView;
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
		}
		public void setPullLoadEnable(boolean b) {
			mListView.setPullLoadEnable(b);
		}
		public void stopLoadMore() {
			mListView.stopLoadMore();
		}
		@Override
		public void onXScrolling(View view) {
		}
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
		}
		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
		}
		@Override
		public void onRefresh() {
		}
		@Override
		public void onLoadMore() {
			getMyFriendsSearchData(mSearchWord);
		}
		@Override
		public int getCount() {
			return mMyFriendsSearchList == null?0:mMyFriendsSearchList.size();
		}
		@Override
		public Object getItem(int position) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return 0;
		}

		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			convertView = LayoutInflater.from(mContext).inflate(R.layout.center_list_friends_item,
					null);
			UserFrdDTO data = mMyFriendsSearchList.get(position);
			TextView textView = (TextView) convertView.findViewById(R.id.name);
			TextView textView2 = (TextView) convertView.findViewById(R.id.content);
			try {
				String text = mContext.onlineTimeToText(Integer.parseInt(data.getPt()));
				text.replace("已经听", "Ta收听");
				textView2.setText(text);
			} catch (Exception e) {
				textView2.setText(mContext.onlineTimeToText(0));
			}
			final ImageView icon = (ImageView) convertView
					.findViewById(R.id.headViewImageView);
			final View avatar_layou = convertView
					.findViewById(R.id.avatar_layou);
			String imageUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_AVATAR, data.getAvatar(),Constants.ID2URL_DEFAULT_BITRATE_AVATAR);
			if (!imageUrl.equals(""+icon.getTag())) {
				icon.setImageBitmap(null);
				icon.setTag(imageUrl);
				AsyncImageLoader.getInstance().loadTempBitmapByUrl(imageUrl, AsyncImageLoader.IMAGE_TYPE_ORIGINAL, new ImageCallback() {
					
					@Override
					public void imageLoaded(final Bitmap bitmap, String imageUrl) {
						if (imageUrl.startsWith(""+icon.getTag())) {
							mHandler.post(new Runnable() {
								
								@Override
								public void run() {
									icon.setImageBitmap(bitmap);
								}
							});
						}
					}
				});
			}
			ImageView friends_share_select = (ImageView) convertView
					.findViewById(R.id.friends_share_select);
			friends_share_select.setTag(data);
			friends_share_select.setOnTouchListener(mFriendSelectListener);
			if (data.isFrdshp()) {
				friends_share_select.setVisibility(View.VISIBLE);
			}else{
				friends_share_select.setVisibility(View.GONE);
			}
			textView.setText(data.getNick());
			return convertView;
		}

		@Override
		public void setListView(DragRefreshListView listView) {
			mListView = listView;
			if (mListView == null) {
				return;
			}
			mListView.setPullRefreshEnable(false);
			mListView.setPullLoadEnable(false);
			mListView.setSelector(R.drawable.draw_nothing);
			mListView.setDividerHeight(0);
			mListView.setDrawingCacheEnabled(true);
			mListView.setFadingEdgeLength(0);
			mListView.setBackgroundColor(0);
			mListView.setAdapter(this);
			mListView.setOnItemClickListener(this);
			mListView.setOnScrollListener(this);
			mListView.setXListViewListener(this);
			notifyDataSetChanged();
			mListView.invalidate();
		}
		@Override
		public String getTitleText() {
			return null;
		}
	}
	
	public static class SharedMusicDTO{
		private MusicDTO musicDto;
		private String cmbt;
		public SharedMusicDTO(MusicDTO musicDto) {
			this.musicDto = musicDto;
		}
		public SharedMusicDTO(MusicDTO musicDto,String cmbt) {
			this.musicDto = musicDto;
			this.cmbt = cmbt;
		}
	}
}
