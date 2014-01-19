package com.jingfm.ViewManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
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
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.Constants.KTC;
import com.jingfm.ViewManager.ViewManagerCenter.LinkedViewData;
import com.jingfm.adapter.AbstractDragAdapter;
import com.jingfm.adapter.MyFriendsAdapter;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.UserChatRequestApi;
import com.jingfm.api.business.UserFriendRequestApi;
import com.jingfm.api.business.UserOAuthRequestApi;
import com.jingfm.api.business.UserSearchApi;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.UserAttentionFrdDTO;
import com.jingfm.api.model.UserFrdDTO;
import com.jingfm.api.model.UserMtknownFrdDTO;
import com.jingfm.api.model.UserSnsFrdDTO;
import com.jingfm.api.model.socketmessage.SocketPChatDTO;
import com.jingfm.api.model.socketmessage.SocketPUserSignedoffDTO;
import com.jingfm.api.model.socketmessage.SocketPUserSignedonDTO;
import com.jingfm.api.model.sysmessage.FlwdSysMessageDTO;
import com.jingfm.api.model.sysmessage.InhsSysMessageDTO;
import com.jingfm.api.model.sysmessage.RmndSysMessageDTO;
import com.jingfm.api.model.sysmessage.SysMessageDTO;
import com.jingfm.api.model.sysmessage.SysMessageType;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.model.SNSBean;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.AsyncImageLoader.ImageCallback;
import com.jingfm.tools.JingTools;

public class FriendsViewManager {
	protected static final int MSG_REFRESH_VIEW = 0;
	protected static final int MSG_REFRESH_COVER = 1;
	
	private static final int EXTENSION_ITEM_SEARCH = 0;
	private static final int EXTENSION_ITEM_FRIEND_SNS = 1;
	private static final int EXTENSION_ITEM_MAYBE_INTERESTING = 2;
	private static final int EXTENSION_ITEM_BIND_SINA_WEIBO = 0;
	private static final int EXTENSION_ITEM_BIND_RENREN = 1;
	private static final int EXTENSION_ITEM_BIND_TENCENT_WEIBO = 2;

	private int mLastOnItemClickIndex;
	
	private MainActivity mContext;
	private View mFriendsView;
	private DragRefreshListView mFriendslistView;
	private Handler mHandler;
	private MyFriendsAdapter mMyFriendsAdapter;
	private View my_friends_check_but;
	private View social_check_but;
	private ListView list_view_sns;
	private ListView list_view_extend;
	private View anim_line;
	private View mFriendsSearchView;
	private FriendsSearchListAdapter mFriendsSearchListAdapter;
	private ArrayList<UserFrdDTO> mFriendsSearchList = new ArrayList<UserFrdDTO>();
	private EditText mFriendsSearchEditText;
	private View mFriendsSearchEditTextLayout;
	private OnClickListener mFriendsAddListener;
	protected boolean isButtonLocked;
	private DragRefreshListView mFriendsSearchListView;
	private OnTouchListener mFriendSelectListener;
	public int mExtensionLoadMoreIndex;
	public boolean isloading;
	protected ExtendAdapter mExtendAdapter;
	private int mNewAttend2u;
	private TextView new_message_extended;
	private SnsAdapter mSnsAdapter;
	
	
	public FriendsViewManager(MainActivity context) {
		this.mContext = context;
		initHandler();
		initFriendsView();
		initFriendsSearchView();
		mMyFriendsAdapter = new MyFriendsAdapter(mContext);
		mFriendsAddListener = new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				if (isButtonLocked) {
					return;
				}
				isButtonLocked = true;
				v.setVisibility(View.GONE);
				new Thread(){
					public void run() {
						final UserFrdDTO data = (UserFrdDTO) v.getTag();
						if (data == null) {
							return;
						}
						HashMap<Object, Object> params = new HashMap<Object, Object>();
						params.put("uid", mContext.getUserId());
						params.put("frdid", data.getUid());
						final ResultResponse<UserFrdDTO> rs = UserFriendRequestApi.followFrd(params);
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								if (rs != null && rs.isSuccess()) {
									data.setFlwd(true);
									if (data instanceof UserAttentionFrdDTO) {
										data.setFrdshp(true);
									}
									mFriendsSearchListAdapter.notifyDataSetChanged();
									Toast.makeText(mContext, "关注好友成功", 1).show();
								}else{
									data.setFlwd(false);
									v.setVisibility(View.VISIBLE);
									Toast.makeText(mContext, "关注好友失败", 1).show();
								}
								isButtonLocked = false;
							}
						});
					};
				}.start();
			}
		};
	}

	private void initFriendsView() {
		mFriendsView = LayoutInflater.from(mContext).inflate(R.layout.center_view_list_friends, null);
		anim_line = mFriendsView.findViewById(R.id.anim_line);
		new_message_extended = (TextView)mFriendsView.findViewById(R.id.new_message);
		mFriendslistView = (DragRefreshListView)mFriendsView.findViewById(R.id.list_view_friends);
		mFriendslistView.setSelector(R.drawable.draw_nothing);
		mFriendslistView.setPullRefreshEnable(true);
		mFriendslistView.setPullLoadEnable(false);
		my_friends_check_but = mFriendsView.findViewById(R.id.my_friends_check_but);
		social_check_but = mFriendsView.findViewById(R.id.social_check_but);
//		list_view_sns = (ListView)mFriendsView.findViewById(R.id.list_view_sns);
		list_view_extend = new ListView(mContext);
		setupExtendListView();
		list_view_sns = new ListView(mContext);
		setupSnsListView();
		OnTouchListener switcher = new OnTouchListener() {
			
			private TranslateAnimation backLeftAnimation;
			private TranslateAnimation toRightAnimation;
			private AnimationListener anim_line_listener;

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (anim_line_listener == null) {
					anim_line_listener = new AnimationListener() {
						public void onAnimationStart(Animation animation) {}
						public void onAnimationRepeat(Animation animation) {}
						public void onAnimationEnd(Animation animation) {
							FrameLayout.LayoutParams lp = (android.widget.FrameLayout.LayoutParams) anim_line.getLayoutParams();
							if (animation == backLeftAnimation) {
								lp.gravity = Gravity.BOTTOM | Gravity.LEFT;
							}else{
								lp.gravity = Gravity.BOTTOM | Gravity.RIGHT;
							}
							anim_line.setLayoutParams(lp);
						}
					};
				}
				if (v.equals(my_friends_check_but)) {
					if (mFriendslistView.getVisibility() == View.VISIBLE) {
						return true;
					}
					mFriendslistView.setVisibility(View.VISIBLE);
//					list_view_sns.setVisibility(View.GONE);
					if (backLeftAnimation == null) {
						backLeftAnimation = new TranslateAnimation(anim_line.getWidth(), 0,
								0, 0);
						backLeftAnimation.setDuration(200);
						backLeftAnimation.setAnimationListener(anim_line_listener);
					}
					anim_line.startAnimation(backLeftAnimation);
				}else if (v.equals(social_check_but)){
					if (list_view_sns.getVisibility() == View.VISIBLE) {
						return true;
					}
					list_view_sns.setVisibility(View.VISIBLE);
					mFriendslistView.setVisibility(View.GONE);
					setupExtendListView();
					if (toRightAnimation == null) {
						toRightAnimation = new TranslateAnimation(0, anim_line.getWidth(),
								0, 0);
						toRightAnimation.setAnimationListener(anim_line_listener);
						toRightAnimation.setDuration(200);
						toRightAnimation.setAnimationListener(anim_line_listener);
					}
					anim_line.startAnimation(toRightAnimation);
				}
				return true;
			}

		};
		my_friends_check_but.setOnTouchListener(switcher);
		social_check_but.setOnTouchListener(switcher);
	}
	private void setupExtendListView() {
		if (mExtendAdapter == null) {
			mExtendAdapter = new ExtendAdapter(mContext);
			list_view_extend.setAdapter(mExtendAdapter);
			list_view_extend.setSelector(R.drawable.draw_nothing);
			list_view_extend.setDivider(null);
			list_view_extend.setOnItemClickListener(mExtendAdapter);
			int padding = JingTools.dip2px(mContext, 12);
			list_view_extend.setPadding(padding, 0, padding, 0);
			mExtendAdapter.notifyDataSetChanged();
		}
	}
	private void setupSnsListView() {
		if (mSnsAdapter == null) {
			mSnsAdapter = new SnsAdapter(mContext);
			list_view_sns.setAdapter(mSnsAdapter);
			list_view_sns.setSelector(R.drawable.draw_nothing);
			list_view_sns.setOnItemClickListener(mSnsAdapter);
			list_view_sns.setDivider(null);
			int padding = JingTools.dip2px(mContext, 12);
			list_view_sns.setPadding(padding, 0, padding, 0);
			mSnsAdapter.notifyDataSetChanged();
		}
	}
	
	private View getConvertView(View convertView) {
		convertView = LayoutInflater.from(mContext).inflate(
				R.layout.center_list_friends_sns_item, null);
		return convertView;
	}
	
	private void initFriendsSearchView() {
		mFriendsSearchView = LayoutInflater.from(mContext).inflate(R.layout.center_list_friends_search, null);
		mFriendsSearchEditText = (EditText) mFriendsSearchView.findViewById(R.id.search_friends_text);
		mFriendsSearchEditTextLayout = mFriendsSearchView.findViewById(R.id.search_friends_text_layout);
		mFriendsSearchEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if (arg1 == 0 && arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					arg1 = arg0.getImeOptions();
				}
				if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
					InputMethodManager imm = (InputMethodManager) mContext
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(arg0.getWindowToken(), 0);
					final String searchWord = mFriendsSearchEditText.getText().toString();
					if (!searchWord.isEmpty()) {
						getSearchData(searchWord);
					}
					return true;
				}
				return false;
			}
		});
		final ImageView search_friends_button = (ImageView) mFriendsSearchView.findViewById(R.id.search_friends_button);
		search_friends_button.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				getSearchData(mFriendsSearchEditText.getText().toString());
				hideSoftKeyboard();
			}
		});
		mFriendsSearchListView = (DragRefreshListView) mFriendsSearchView.findViewById(R.id.friends_search_list_view);
		int padding = JingTools.dip2px(mContext, 12);
		mFriendsSearchListView.setPadding(padding, 0, padding, 0);
		mFriendsSearchListAdapter = new FriendsSearchListAdapter();
		mFriendsSearchListAdapter.setListView(mFriendsSearchListView);
		mFriendsSearchListAdapter.notifyDataSetChanged();
	}
	
	private void pushSnsList() {
		LinkedViewData oldLinkedViewData;
		LinkedViewData newLinkedViewData;
		oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.FRIEND_LIST_SNS, null,null);
		newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.FRIEND_LIST_SEARCH, null, null);
		mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData, newLinkedViewData);
	}
	private void pushSearchList() {
		LinkedViewData oldLinkedViewData;
		LinkedViewData newLinkedViewData;
//		oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.FRIEND_LIST_EXTEND, null,null);
		oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.FRIEND_LIST_WITH_TITLE, null,null);
		newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.FRIEND_LIST_SEARCH, null, null);
		mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData, newLinkedViewData);
	}
	
	public View getFriendSearchView(){
		isButtonLocked = false;
		return mFriendsSearchView;
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

	public View getView() {
		mFriendsView.setVisibility(View.VISIBLE);
		mFriendslistView.setVisibility(View.VISIBLE);
		FrameLayout.LayoutParams lp = (android.widget.FrameLayout.LayoutParams) anim_line.getLayoutParams();
		lp.gravity = Gravity.BOTTOM | Gravity.LEFT;
		anim_line.setLayoutParams(lp);
		if (mMyFriendsAdapter == null) {
			mMyFriendsAdapter = new MyFriendsAdapter(mContext);
		}
		mMyFriendsAdapter.onRefresh();
		return mFriendsView;
	}

	public void onShowing(List list, String ouid) {
		if (mMyFriendsAdapter == null) {
			mMyFriendsAdapter = new MyFriendsAdapter(mContext);
		}
		LayoutParams layoutParams = anim_line.getLayoutParams();
		layoutParams.width = JingTools.getShowWidth(mContext)/2;
		Log.i("kid_debug","layoutParams.width: " + layoutParams.width);
		anim_line.setLayoutParams(layoutParams);
		mFriendslistView.setXListViewListener(mMyFriendsAdapter);
		mFriendslistView.setOnItemClickListener(mMyFriendsAdapter);
		mFriendslistView.setOnScrollListener(mMyFriendsAdapter);
		mFriendslistView.setAdapter(mMyFriendsAdapter);
		mMyFriendsAdapter.initData(list,ouid,mFriendslistView,true);
	}

	public void frdOnline(SocketPUserSignedonDTO sDTO) {
		if (mMyFriendsAdapter != null) {
			mMyFriendsAdapter.frdOnline(sDTO);
		}
	}

	public void frdOff(SocketPUserSignedoffDTO sDTO) {
		if (mMyFriendsAdapter != null) {
			mMyFriendsAdapter.frdOff(sDTO);
		}
	}
	
	public void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mFriendsView.getWindowToken(), 0);
	}

	protected void getSearchData(final String searchWord) {
		if (isButtonLocked) {
			return;
		}
		isButtonLocked = true;
		new Thread(){
			public void run() {
				HashMap<Object,Object> params = new HashMap<Object,Object>();
				params.put("u", ""+mContext.getUserId());
				params.put("q", ""+searchWord);
				params.put("st", ""+0);
				params.put("ps", ""+Constants.DEFAULT_NUM_OF_LOAD);
				final ResultResponse<ListResult<UserFrdDTO>> rs = UserSearchApi.fetchNick(params);
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						if(rs != null && rs.isSuccess()){
							List<UserFrdDTO> list = rs.getResult().getItems();
							mFriendsSearchList.clear();
							mFriendsSearchList.addAll(list);
							mFriendsSearchListAdapter.notifyDataSetChanged();
						}else{
							Toast.makeText(mContext, "取列表失败", 0).show();
						}
						isButtonLocked = false;
					}
				});
			};
		}.start();
	}

	
	public void show3rdPartFriends(final HashMap<Object, Object> params) {
		mFriendsSearchEditTextLayout.setVisibility(View.GONE);
		new Thread(){
			public void run() {
				final ResultResponse<ListResult<UserSnsFrdDTO>> rs = UserOAuthRequestApi.fetchFriends(params);
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						if(rs != null && rs.isSuccess()){
							List<UserSnsFrdDTO> list = rs.getResult().getItems();
							mFriendsSearchList.clear();
							for (int i = 0; i < list.size(); i++) {
								list.get(i).setIdentify(""+params.get("identify"));
								mFriendsSearchList.add(list.get(i));
							}
							pushSnsList();
						}else{
							Toast.makeText(mContext, "取好友列表失败", 0).show();
						}
						isButtonLocked = false;
					}
				});
			};
		}.start();
	}
	
	class ExtendAdapter extends BaseAdapter implements OnItemClickListener{
		private List<SNSBean> list = new ArrayList<SNSBean>();
		
		public ExtendAdapter(Context context){
			list.add(new SNSBean(EXTENSION_ITEM_SEARCH, "搜索好友", "通过搜索用户的名字来查看谁在使用jing。", R.drawable.friends_ext_search));
			list.add(new SNSBean(EXTENSION_ITEM_FRIEND_SNS, "社交网络", "这里的人希望和你成为好友。", R.drawable.friends_ext_sns));
			list.add(new SNSBean(EXTENSION_ITEM_MAYBE_INTERESTING, "可能感兴趣的人", "通过分析你的行为和关系帮你推荐的。", R.drawable.friends_ext_may_know));
		}
		
		@Override
		public int getCount() {
			return list.size() + 1;
		}

		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}

		@Override
		public long getItemId(int arg0) {
			return arg0;
		}

		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg0 == 0) {
				arg1 = new View(mContext);
				arg1.setMinimumHeight(JingTools.dip2px(mContext, 12));
			}else{
				arg0--;
				SNSBean snsBean = list.get(arg0);
				arg1 = getConvertView(arg1);
				if (arg0 == getCount() - 2) {
					arg1.findViewById(R.id.deliver_line).setBackgroundColor(0);
				}
				if (arg0 == EXTENSION_ITEM_FRIEND_SNS && mNewAttend2u > 0) {
					((TextView)arg1.findViewById(R.id.new_message)).setVisibility(View.VISIBLE);
					((TextView)arg1.findViewById(R.id.new_message)).setText(String.valueOf(mNewAttend2u));
				}else{
					((TextView)arg1.findViewById(R.id.new_message)).setVisibility(View.GONE);
				}
				arg1.setTag(snsBean);
				((TextView)arg1.findViewById(R.id.name)).setText(snsBean.getName());
				((TextView)arg1.findViewById(R.id.content)).setText(snsBean.getContent());
				((ImageView)arg1.findViewById(R.id.headViewImageView)).setImageResource(snsBean.getDrawableId());
			}
			return arg1;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (mContext.isOfflineMode()) {
				mContext.toastOffLine();
				return;
			}
//			if (isButtonLocked) {
//				return;
//			}
//			isButtonLocked = true;
			position--;
			mLastOnItemClickIndex = position;
			mFriendsSearchListView.setPullRefreshEnable(false);
			mFriendsSearchListView.setPullLoadEnable(false);
			HashMap<Object, Object> params = new HashMap<Object, Object>();
			params.put("uid", ""+mContext.getUserId());
			switch (position) {
			case EXTENSION_ITEM_SEARCH://"搜索好友"
				KTC.rep("Chatting", "SearchFriends", "");
				mFriendsSearchList.clear();
				mFriendsSearchListAdapter.notifyDataSetChanged();
				startSearchFriends();
				break;
			case EXTENSION_ITEM_FRIEND_SNS://"好友请求"
				LinkedViewData oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.FRIEND_LIST_EXTEND, null,null);
				LinkedViewData newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.FRIEND_LIST_SNS, null, null);
				mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData, newLinkedViewData);
				isButtonLocked = false;
				break;
			case EXTENSION_ITEM_MAYBE_INTERESTING://"可能感兴趣的人"
//				u q st ps
				params.put("u", ""+mContext.getUserId());
				params.put("q", ""+mContext.getUserId());
				params.put("st", ""+0);
				params.put("ps", ""+Constants.DEFAULT_NUM_OF_LOAD);
				KTC.rep("Chatting", "UseRecommendedFriends", "");
				showMaybeHaveInteresting(params);
				break;
			}
		}

		private void showFriendRequest(final int index) {
			final HashMap<Object, Object> params = new HashMap<Object, Object>();
			params.put("uid", ""+mContext.getUserId());
			params.put("st", ""+index);
			params.put("ps", ""+Constants.DEFAULT_NUM_OF_LOAD);
			mFriendsSearchEditTextLayout.setVisibility(View.GONE);
			new Thread(){
				public void run() {
					final ResultResponse<ListResult<SysMessageDTO>> rs = UserChatRequestApi.fetchPersonalSysMessage(params);
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							if(rs != null && rs.isSuccess()){
								List<SysMessageDTO> list = rs.getResult().getItems();
								mFriendsSearchList.clear();
								clearNewAttend2u();
								for (int i = 0; i < list.size(); i++) {
									SysMessageDTO data = list.get(i);
									String t = data.getT();
									if(SysMessageType.RMND.getName().equals(t)){
										UserAttentionFrdDTO dto = new UserAttentionFrdDTO();
										dto.setFrdshp(((RmndSysMessageDTO)data).isFrdshp());
										dto.setNick(((RmndSysMessageDTO)data).getFrd());
										dto.setAvatar(((RmndSysMessageDTO)data).getAvatar());
										dto.setUid(""+((RmndSysMessageDTO)data).getFrd_id());
										dto.setSub_text(dto.getNick() + " 提醒你关注Ta");
										dto.setType(t);
										mFriendsSearchList.add(dto);
									}else if(SysMessageType.INHS.getName().equals(t)){
										try {
											for (int j = 0; j < ((InhsSysMessageDTO)data).getFrd_ids().size(); j++) {
												UserAttentionFrdDTO dto = new UserAttentionFrdDTO();
												dto.setUid(((InhsSysMessageDTO)data).getFrd_ids().get(j));
												dto.setAvatar(((InhsSysMessageDTO)data).getAvatars().get(j));
												dto.setNick(((InhsSysMessageDTO)data).getFrds().get(j));
												dto.setFrdshp(((InhsSysMessageDTO)data).getFrdshps().get(j));
												dto.setSub_text(dto.getNick() + " 通过社交网络入住了Jing");
												dto.setType(t);
												mFriendsSearchList.add(dto);
											}
										} catch (Exception e) {
											// TODO: handle exception
										}
									}else if(SysMessageType.FLWD.getName().equals(t)){
										UserAttentionFrdDTO dto = new UserAttentionFrdDTO();
										dto.setFrdshp(((FlwdSysMessageDTO)data).isFrdshp());
										dto.setNick(((FlwdSysMessageDTO)data).getFlwer());
										dto.setAvatar(((FlwdSysMessageDTO)data).getFlw_avatar());
										dto.setUid(""+((FlwdSysMessageDTO)data).getFlwer_id());
										dto.setSub_text(dto.getNick() + " 关注了你");
										dto.setType(t);
										mFriendsSearchList.add(dto);
									}
								}
								if (list != null) {
									mFriendsSearchListView.setPullLoadEnable(list.size() == Constants.DEFAULT_NUM_OF_LOAD);
								}
								if (index == 0) {
									pushSearchList();
								}
								mLastOnItemClickIndex += Constants.DEFAULT_AI_RIDIO_NUM_OF_LOAD;
							}else{
								Toast.makeText(mContext, "取可能喜欢的人列表失败", 0).show();
							}
							isButtonLocked = false;
							mFriendsSearchListView.stopLoadMore();
						}
					});
				};
			}.start();
		}

		public void startSearchFriends() {
			mFriendsSearchEditTextLayout.setVisibility(View.VISIBLE);
			mFriendsSearchList.clear();
			isButtonLocked = false;
			pushSearchList();
		}

		private void showMaybeHaveInteresting(final HashMap<Object, Object> params) {
			mFriendsSearchEditTextLayout.setVisibility(View.GONE);
			Toast.makeText(mContext, "正在获取可能感兴趣的人", 1).show();
			new Thread(){
				public void run() {
					final ResultResponse<ListResult<UserMtknownFrdDTO>> rs = UserFriendRequestApi.fetchMtknownFriends(params);
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							if(rs != null && rs.isSuccess()){
								List<UserMtknownFrdDTO> list = rs.getResult().getItems();
								mFriendsSearchList.clear();
								for (int i = 0; i < list.size(); i++) {
									mFriendsSearchList.add(list.get(i).toUserFrdDTO());
								}
								pushSearchList();
							}else{
								Toast.makeText(mContext, "取可能喜欢的人列表失败", 0).show();
							}
							isButtonLocked = false;
						}
					});
				};
			}.start();
		}
	}
	
	class SnsAdapter extends BaseAdapter implements OnItemClickListener{
		private List<SNSBean> list = new ArrayList<SNSBean>();
		
		public SnsAdapter(Context context){
			list.add(new SNSBean(EXTENSION_ITEM_BIND_SINA_WEIBO, "绑定新浪微博", "绑定后查看到你的新浪微博好友谁也在使用Jing。", R.drawable.friends_ext_wb));
			list.add(new SNSBean(EXTENSION_ITEM_BIND_RENREN, "绑定人人网", "绑定后查看你的人人网好友谁也在使用Jing。", R.drawable.friends_ext_rr));
			list.add(new SNSBean(EXTENSION_ITEM_BIND_TENCENT_WEIBO, "绑定腾讯微博", "绑定后查看你的腾讯微博好友谁也在使用Jing。", R.drawable.friends_ext_qq));
		}
		
		@Override
		public int getCount() {
			return list.size() + 1;
		}
		
		@Override
		public Object getItem(int arg0) {
			return list.get(arg0);
		}
		
		@Override
		public long getItemId(int arg0) {
			return arg0;
		}
		
		@Override
		public View getView(int arg0, View arg1, ViewGroup arg2) {
			if (arg0 == 0) {
				arg1 = new View(mContext);
				arg1.setMinimumHeight(JingTools.dip2px(mContext, 12));
			}else{
				arg0--;
				SNSBean snsBean = list.get(arg0);
				arg1 = LayoutInflater.from(mContext).inflate(R.layout.center_list_friends_sns_item, null);
				if (arg0 == getCount() - 2) {
					arg1.findViewById(R.id.deliver_line).setBackgroundColor(0);
				}
				if (arg0 == EXTENSION_ITEM_FRIEND_SNS && mNewAttend2u > 0) {
					((TextView)arg1.findViewById(R.id.new_message)).setVisibility(View.VISIBLE);
					((TextView)arg1.findViewById(R.id.new_message)).setText(String.valueOf(mNewAttend2u));
				}else{
					((TextView)arg1.findViewById(R.id.new_message)).setVisibility(View.GONE);
				}
				arg1.setTag(snsBean);
				((TextView)arg1.findViewById(R.id.name)).setText(snsBean.getName());
				((TextView)arg1.findViewById(R.id.content)).setText(snsBean.getContent());
				((ImageView)arg1.findViewById(R.id.headViewImageView)).setImageResource(snsBean.getDrawableId());
			}
			return arg1;
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (mContext.isOfflineMode()) {
				mContext.toastOffLine();
				return;
			}
//			if (isButtonLocked) {
//				return;
//			}
//			isButtonLocked = true;
			position--;
			mLastOnItemClickIndex = position;
			mFriendsSearchListView.setPullRefreshEnable(false);
			mFriendsSearchListView.setPullLoadEnable(false);
			HashMap<Object, Object> params = new HashMap<Object, Object>();
			params.put("uid", ""+mContext.getUserId());
			switch (position) {
			case EXTENSION_ITEM_BIND_SINA_WEIBO://"绑定新浪微博"
//				 params uid identify st ps
				boolean binded1 = mContext.getmLoginData().getSnstokens().containsKey(UserOAuthRequestApi.OAuth_Sina_WEIBO_Identify);
				if (binded1) {
					params.put("identify", UserOAuthRequestApi.OAuth_Sina_WEIBO_Identify);
					show3rdPartFriends(params);
					Toast.makeText(mContext, "正在获取新浪微博好友", 1).show();
				}else{
					Toast.makeText(mContext, "新浪微博未绑定", 1).show();
					isButtonLocked = false;
				}
				break;
			case EXTENSION_ITEM_BIND_RENREN://"绑定人人网"
				boolean binded2 = mContext.getmLoginData().getSnstokens().containsKey(UserOAuthRequestApi.OAuth_Renren_Identify);
				if (binded2) {
					params.put("identify", UserOAuthRequestApi.OAuth_Renren_Identify);
					Toast.makeText(mContext, "正在获取人人网好友", 1).show();
					show3rdPartFriends(params);
				}else{
					Toast.makeText(mContext, "人人网未绑定", 1).show();
					isButtonLocked = false;
				}
				break;
			case EXTENSION_ITEM_BIND_TENCENT_WEIBO://"绑定腾讯微博"
				boolean binded3 = mContext.getmLoginData().getSnstokens().containsKey(UserOAuthRequestApi.OAuth_Qq_WEIBO_Identify);
				if (binded3) {
					params.put("identify", UserOAuthRequestApi.OAuth_Qq_WEIBO_Identify);
					show3rdPartFriends(params);
					Toast.makeText(mContext, "正在获取腾讯微博好友", 1).show();
				}else{
					Toast.makeText(mContext, "腾讯微博未绑定", 1).show();
					isButtonLocked = false;
				}
				break;
			}
		}
	}
	
	class FriendsSearchListAdapter extends AbstractDragAdapter implements OnClickListener{

		private DragRefreshListView mListView;

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
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
			if (mListView != null) {
				mListView.stopRefresh();
				mListView.setPullRefreshEnable(false);
			}
		}

		@Override
		public void onLoadMore() {
			if (mLastOnItemClickIndex != EXTENSION_ITEM_FRIEND_SNS) {
				if (mListView != null) {
					mListView.stopLoadMore();
					mListView.setPullLoadEnable(false);
				}
			}else{
				mExtendAdapter.showFriendRequest(mExtensionLoadMoreIndex);
			}
		}

		@Override
		public int getCount() {
			return mFriendsSearchList == null?0:mFriendsSearchList.size() +1;
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
			if (position == 0) {
				convertView = new View(mContext);
				convertView.setMinimumHeight(JingTools.dip2px(mContext, 12));
				return convertView;
			}
			convertView = LayoutInflater.from(mContext).inflate(R.layout.center_list_friends_item,
					null);
			if (position == getCount() - 1) {
				convertView.findViewById(R.id.deliver_line).setBackgroundColor(0);
			}
			position --;
			UserFrdDTO data = mFriendsSearchList.get(position);
			TextView textView = (TextView) convertView.findViewById(R.id.name);
			TextView textView2 = (TextView) convertView.findViewById(R.id.content);
			final ImageView icon = (ImageView) convertView
					.findViewById(R.id.headViewImageView);
			final View avatar_layou = convertView
					.findViewById(R.id.avatar_layou);
			avatar_layou.setTag(data);
			avatar_layou.setOnClickListener(this);
			String imageUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_AVATAR, data.getAvatar(),Constants.ID2URL_DEFAULT_BITRATE_AVATAR);
			if (!imageUrl.equals(""+icon.getTag())) {
				icon.setImageBitmap(null);
				icon.setTag(imageUrl);
				AsyncImageLoader.getInstance().loadTempBitmapByUrl(imageUrl, AsyncImageLoader.IMAGE_TYPE_ROUND_CORNER, new ImageCallback() {
					
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
			ImageView button_poke = (ImageView) convertView
					.findViewById(R.id.friends_follow_poke);
			ImageView online_light = (ImageView) convertView
					.findViewById(R.id.friends_online);
			ImageView friends_chat = (ImageView) convertView
					.findViewById(R.id.friends_chat);
			ImageView friends_follow_add = (ImageView) convertView
					.findViewById(R.id.friends_follow_add);
			ImageView friends_follow_listen = (ImageView) convertView
					.findViewById(R.id.friends_follow_listen);
			ImageView friends_share_select = (ImageView) convertView
					.findViewById(R.id.friends_share_select);
			friends_follow_add.setTag(data);
			friends_follow_add.setOnClickListener(mFriendsAddListener);
			button_poke.setTag(data);
			button_poke.setOnTouchListener(mMyFriendsAdapter.getmPokeButtonListener());
			friends_chat.setTag(data);
			friends_chat.setOnTouchListener(mMyFriendsAdapter.getmChatButtonListener());
			friends_share_select.setTag(data);
			friends_share_select.setOnTouchListener(mFriendSelectListener);
			friends_follow_listen.setTag(data);
			friends_follow_listen.setOnClickListener(mMyFriendsAdapter.getmFollowListener());
			textView.setText(data.getNick());
//			textView2.setText("");
			try {
				String text = mContext.onlineTimeToText(Integer.parseInt(data.getPt()));
				text.replace("已经听", "Ta收听");
				textView2.setText(text);
			} catch (Exception e) {
				textView2.setText(mContext.onlineTimeToText(0));
			}
			if (data instanceof UserSnsFrdDTO) {
				online_light.setVisibility(View.GONE);
				friends_chat.setVisibility(View.GONE);
				friends_follow_add.setVisibility(View.GONE);
				friends_follow_listen.setVisibility(View.GONE);
				button_poke.setVisibility(((UserSnsFrdDTO) data).isInhs()?View.GONE:View.VISIBLE);
				textView2.setText("点击右侧小手邀请他加入");
			}else if (data instanceof UserAttentionFrdDTO) {
				String t = ((UserAttentionFrdDTO) data).getType();
				if(SysMessageType.RMND.getName().equals(t)){
				}else if(SysMessageType.INHS.getName().equals(t)){
				}else if(SysMessageType.FLWD.getName().equals(t)){
				}
				textView2.setText(((UserAttentionFrdDTO) data).getSub_text());
				online_light.setVisibility(View.GONE);
				if (data.isFrdshp()) {
					friends_chat.setVisibility(View.VISIBLE);
					friends_follow_add.setVisibility(View.GONE);
				}else{
					friends_chat.setVisibility(View.GONE);
					friends_follow_add.setVisibility(View.VISIBLE);
				}
				button_poke.setVisibility(View.GONE);
				friends_follow_listen.setVisibility(View.GONE);
			}else{
				online_light.setVisibility(data.isOl()?View.VISIBLE:View.GONE);
				//friends_chat
				if (data.isFrdshp()) {
					friends_chat.setVisibility(View.VISIBLE);
				}else{
					friends_chat.setVisibility(View.GONE);
				}
				
				//friends_follow_add
				if (!data.isFlwd()) {
					friends_follow_add.setVisibility(View.VISIBLE);
				}else{
					friends_follow_add.setVisibility(View.GONE);
				}
				
				//button_poke
				if (data.isFlwd() && !data.isFrdshp() && data.isOl()) {
					button_poke.setVisibility(View.VISIBLE);
				}else{
					button_poke.setVisibility(View.GONE);
				} 
				
				//friends_follow_listen
				if (data.isFlwd() && data.isOl()) {
					friends_follow_listen.setVisibility(View.VISIBLE);
				}else{
					friends_follow_listen.setVisibility(View.GONE);
				} 
			}
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
			// TODO Auto-generated method stub
			return null;
		}
		
		@Override
		public void onClick(View v) {
			UserFrdDTO data = ((UserFrdDTO)v.getTag());
			if (data == null || data instanceof UserSnsFrdDTO) {
				return;
			}
			LinkedViewData oldLinkedViewData;
			LinkedViewData newLinkedViewData;
			oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.FRIEND_LIST_SEARCH, null,null);
			newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.USER_HOME_PAGE, ""+data.getUid(), null);
			mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData, newLinkedViewData);
		}
	}
	
	public View makeLinkedView(String ouid,List list){
		return mMyFriendsAdapter.initData(list,ouid,new DragRefreshListView(mContext),false);
	}

	public View getViewWithTitle() {
		mMyFriendsAdapter.setNeedTitle(true);
		mContext.setJingTitleText(mContext.getString(R.string.menu_1));
		return mFriendsView;
	}

	public void hasNewMessage(SocketPChatDTO socketPChatDTO) {
		if (mMyFriendsAdapter == null) {
			mMyFriendsAdapter = new MyFriendsAdapter(mContext);
		}
		mMyFriendsAdapter.hasNewMessage(socketPChatDTO);
	}
	
	public Integer clearNewChatMessage(String fuid) {
		if (mMyFriendsAdapter == null) {
			mMyFriendsAdapter = new MyFriendsAdapter(mContext);
		}
		return mMyFriendsAdapter.clearNewChatMessage(fuid);
	}

	public void hasNewAttend2u() {
		mNewAttend2u++;
		new_message_extended.setVisibility(View.VISIBLE);
		new_message_extended.setText(String.valueOf(mNewAttend2u));
		if (mExtendAdapter != null) {
			mExtendAdapter.notifyDataSetChanged();
		}
	}
	public void clearNewAttend2u() {
		mContext.getmViewManagerLeft().friendsNotifyChange(-mNewAttend2u);
		mNewAttend2u = 0;
		new_message_extended.setVisibility(View.GONE);
		if (mExtendAdapter != null) {
			mExtendAdapter.notifyDataSetChanged();
		}
	}

	public View getExtendListView() {
		KTC.rep("Chatting", "UseSearchFriends", "");
		return list_view_extend;
	}
	public View getSnsListView() {
		return list_view_sns;
	}

	public void showSearchView() {
		mFriendsSearchList.clear();
		mFriendsSearchListAdapter.notifyDataSetChanged();
		mExtendAdapter.startSearchFriends();
	}

	public void showMaybeHaveInteresting(HashMap<Object, Object> params) {
		mExtendAdapter.showMaybeHaveInteresting(params);
	}
}
