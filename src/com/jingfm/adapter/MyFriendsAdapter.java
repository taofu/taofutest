package com.jingfm.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.ViewManager.ChatViewManager.ChatUserData;
import com.jingfm.ViewManager.LoginStateChangeListener;
import com.jingfm.ViewManager.ViewManagerCenter.LinkedViewData;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.UserFriendRequestApi;
import com.jingfm.api.business.UserOAuthRequestApi;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.LoginDataDTO;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.UserFrdDTO;
import com.jingfm.api.model.UserSnsFrdDTO;
import com.jingfm.api.model.socketmessage.SocketPChatDTO;
import com.jingfm.api.model.socketmessage.SocketPChatPayloadShareTrackDTO;
import com.jingfm.api.model.socketmessage.SocketPUserSignedoffDTO;
import com.jingfm.api.model.socketmessage.SocketPUserSignedonDTO;
import com.jingfm.background_model.LocalCacheManager;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.AsyncImageLoader.ImageCallback;
import com.jingfm.tools.JingTools;

public class MyFriendsAdapter extends AbstractDragAdapter {

	protected static final int MSG_SET_NOTIFY = 110;

	private MainActivity mContext;

	private List<UserFrdDTO> mainList = new ArrayList<UserFrdDTO>();

	private Handler mHandler;

	private int mFriendsIndexOnServer = -100;

	private DragRefreshListView mListView;

	private LayoutInflater mInflater;

	private AsyncImageLoader mAsyncImageLoader;

	private int mServerIndex = 0;

	private boolean isLoading;

	private long mLastRefreshTime;

	private OnTouchListener mChatButtonListener;

	private OnClickListener mFollowListener;

	private OnTouchListener mPokeButtonListener;

	protected boolean isButtonLocked;

	private String mOuid;

	private OnTouchListener mIconButtonListener;

	private boolean isNoNeedSave;

	private boolean isNeedTitle = true;

	private boolean hasMore;

	private HashMap<String, Integer> mNewMessageCountMap = new HashMap<String, Integer>();

	public MyFriendsAdapter(MainActivity context) {
		this.mContext = context;
		mInflater = LayoutInflater.from(context);
		initHandler();
		mAsyncImageLoader = AsyncImageLoader.getInstance();
		mIconButtonListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (mContext.isOfflineMode()) {
						mContext.toastOffLine();
						return true;
					}
					LinkedViewData oldLinkedViewData;
					LinkedViewData newLinkedViewData;
					if (isNeedTitle) {
						isNeedTitle = false;
						oldLinkedViewData = mContext.getmViewManagerCenter()
								.createLinkedViewData(
										LinkedViewData.FRIEND_LIST_WITH_TITLE,
										"" + mOuid, new ArrayList(mainList));
						newLinkedViewData = mContext
								.getmViewManagerCenter()
								.createLinkedViewData(
										LinkedViewData.USER_HOME_PAGE,
										"" + ((UserFrdDTO) v.getTag()).getUid(),
										null);
					} else {
						oldLinkedViewData = mContext.getmViewManagerCenter()
								.createLinkedViewData(
										LinkedViewData.FRIEND_LIST, "" + mOuid,
										new ArrayList(mainList));
						newLinkedViewData = mContext
								.getmViewManagerCenter()
								.createLinkedViewData(
										LinkedViewData.USER_HOME_PAGE,
										"" + ((UserFrdDTO) v.getTag()).getUid(),
										null);
					}
					mContext.getmViewManagerCenter().pushLinkedViews(
							oldLinkedViewData, newLinkedViewData);
				}
				return true;
			}
		};
		mChatButtonListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (mContext.isOfflineMode()) {
						mContext.toastOffLine();
						return true;
					}
					UserFrdDTO data = (UserFrdDTO) v.getTag();
					LinkedViewData oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.FRIEND_LIST_WITH_TITLE, null,null);;
					LinkedViewData newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.CHAT_VIEW,data.getUid(), new ChatUserData(data.getUid(), data.getNick(), data.getAvatar()),null);
					mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData, newLinkedViewData);
				}
				return true;
			}
		};
		mFollowListener = new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mContext.isOfflineMode()) {
					mContext.toastOffLine();
					return;
				}
				if (mContext.isBeFollowedMode()) {
					Toast.makeText(mContext, "你的好友正在跟听你", 1).show();
					return;
				}
				if (mContext.isFollowingOther()) {
					Toast.makeText(mContext, "你正在跟听别人", 1).show();
					return;
				}
				UserFrdDTO data = (UserFrdDTO) v.getTag();
				if (data != null) {
					mContext.followListenRequest(mContext.getmLoginData()
							.getUsr().getId(), Integer.parseInt(data.getUid()),
							data.getNick());
				}
			}
		};
		mPokeButtonListener = new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					if (mContext.isOfflineMode()) {
						mContext.toastOffLine();
						return true;
					}
					if (isButtonLocked) {
						return true;
					}
					isButtonLocked = true;
					final UserFrdDTO data = (UserFrdDTO) v.getTag();
					if (data != null) {
						new Thread() {
							public void run() {
								if (data instanceof UserSnsFrdDTO) {
									HashMap<Object, Object> params = new HashMap<Object, Object>();
									params.put("uid", mContext.getUserId());
									// identify suid nick
									params.put("identify",
											((UserSnsFrdDTO) data)
													.getIdentify());
									params.put("suid",
											((UserSnsFrdDTO) data).getAuid());
									params.put("nick", data.getNick());
									final ResultResponse<String> rs = UserOAuthRequestApi
											.postInviteFriend(params);
									mHandler.post(new Runnable() {

										@Override
										public void run() {
											if (rs != null && rs.isSuccess()) {
												Toast.makeText(mContext,
														"提醒发送成功", 1).show();
											} else {
												Toast.makeText(mContext,
														"提醒发送失败", 1).show();
											}
											isButtonLocked = false;
										}
									});
								} else {
									HashMap<Object, Object> params = new HashMap<Object, Object>();
									// uid frdid
									params.put("uid", "" + mContext.getUserId());
									params.put("frdid", "" + data.getUid());
									final ResultResponse<String> rs = UserFriendRequestApi
											.remindFrd(params);
									mHandler.post(new Runnable() {
										@Override
										public void run() {
											if (rs != null && rs.isSuccess()) {
												Toast.makeText(mContext,
														"提醒成功", 1).show();
											} else {
												Toast.makeText(mContext,
														"提醒失败", 1).show();
											}
											isButtonLocked = false;
										}
									});
								}
							};
						}.start();
					}
				}
				return true;
			}
		};
	}

	public OnTouchListener getmChatButtonListener() {
		return mChatButtonListener;
	}

	public OnClickListener getmFollowListener() {
		return mFollowListener;
	}

	public OnTouchListener getmPokeButtonListener() {
		return mPokeButtonListener;
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_SET_NOTIFY:
					notifyDataSetChanged();
					break;
				}
			}
		};
	}

	@Override
	public void setListView(DragRefreshListView listView) {
		mListView = listView;
		if (mListView == null) {
			return;
		}
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(hasMore);
		mListView.setSelector(R.drawable.draw_nothing);
		mListView.setDividerHeight(0);
		mListView.setDrawingCacheEnabled(true);
		mListView.setSelector(R.drawable.draw_nothing);
		mListView.setFadingEdgeLength(0);
		mListView.setBackgroundColor(0);
		mListView.setAdapter(this);
		mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		mListView.setXListViewListener(this);
		notifyDataSetChanged();
		mListView.invalidate();
		int padding = JingTools.dip2px(mContext, 12);
		mListView.setPadding(padding, 0, padding, 0);
	};

	@Override
	public int getCount() {
		return this.mainList.size() + 4;
	}

	@Override
	public Object getItem(int arg0) {
		return this.mainList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	static class ViewHolder {
		TextView text;
		TextView text2;
		TextView new_message;
		ImageView icon;
		View avatar_layou;
		ImageView button_chat;
		ImageView button_follow;
		ImageView button_poke;
		ImageView online_light;
		ImageView friends_share_select;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null 
				|| convertView.getTag() == null
				|| !(convertView.getTag() instanceof Integer)
				|| R.layout.center_list_friends_item != (Integer)convertView.getTag()) {
			convertView = mInflater.inflate(R.layout.center_list_friends_item,
					null);
			convertView.setTag(Integer.valueOf(R.layout.center_list_friends_item));
			holder = new ViewHolder();
			holder.text = (TextView) convertView.findViewById(R.id.name);
			holder.text2 = (TextView) convertView.findViewById(R.id.content);
			holder.icon = (ImageView) convertView
					.findViewById(R.id.headViewImageView);
			holder.avatar_layou = convertView.findViewById(R.id.avatar_layou);
			holder.button_chat = (ImageView) convertView
					.findViewById(R.id.friends_chat);
			holder.button_follow = (ImageView) convertView
					.findViewById(R.id.friends_follow_listen);
			holder.button_poke = (ImageView) convertView
					.findViewById(R.id.friends_follow_poke);
			holder.friends_share_select = (ImageView) convertView
					.findViewById(R.id.friends_share_select);
			holder.online_light = (ImageView) convertView
					.findViewById(R.id.friends_online);
			holder.new_message = (TextView) convertView
					.findViewById(R.id.new_message);
			ImageView friends_follow_add = (ImageView) convertView
					.findViewById(R.id.friends_follow_add);
			// friends_follow_add
			friends_follow_add.setVisibility(View.GONE);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		View deliver_line = convertView.findViewById(R.id.deliver_line);
		if (deliver_line != null) {
			deliver_line.setBackgroundColor(mContext.getResources().getColor(R.color.item_deliver_line));
		}
		switch (position) {
		case 0:
			convertView = new View(mContext);
			convertView.setMinimumHeight(JingTools.dip2px(mContext, 12));
			break;
		case 1:
			if (isNeedTitle) {
				holder.text.setText("搜索好友");
				holder.text2.setText("通过搜索用户的名字来查看谁在使用jing");
				holder.icon.setTag(""+R.drawable.friends_ext_search);
				holder.icon.setImageResource(R.drawable.friends_ext_search);
				holder.new_message.setVisibility(View.GONE);
				holder.avatar_layou.setOnTouchListener(null);
				holder.button_chat.setVisibility(View.GONE);
				holder.button_follow.setVisibility(View.GONE);
				holder.button_poke.setVisibility(View.GONE);
				holder.online_light.setVisibility(View.GONE);
				holder.friends_share_select.setVisibility(View.GONE);
				if (deliver_line != null) {
					deliver_line.setBackgroundColor(mContext.getResources().getColor(R.color.item_deliver_line));
				}
			}else{
				convertView = new View(mContext);
				convertView.setMinimumHeight(0);
			}
			break;
		case 2:
			if (isNeedTitle) {
				holder.text.setText("社交网络");
				holder.text2.setText("通过社交网络添加好友。");
				holder.icon.setTag(""+R.drawable.friends_ext_sns);
				holder.icon.setImageResource(R.drawable.friends_ext_sns);
				holder.new_message.setVisibility(View.GONE);
				holder.avatar_layou.setOnTouchListener(null);
				holder.button_chat.setVisibility(View.GONE);
				holder.button_follow.setVisibility(View.GONE);
				holder.button_poke.setVisibility(View.GONE);
				holder.online_light.setVisibility(View.GONE);
				holder.friends_share_select.setVisibility(View.GONE);
				if (deliver_line != null) {
					deliver_line.setBackgroundColor(mContext.getResources().getColor(R.color.item_deliver_line));
				}
			}else{
				convertView = new View(mContext);
				convertView.setMinimumHeight(0);
			}
			break;
		case 3:
			if (isNeedTitle) {
				holder.text.setText("可能感兴趣的人");
				holder.text2.setText("通过分析你的行为和关系帮你推荐的。");
				holder.icon.setTag(""+R.drawable.friends_ext_may_know);
				holder.icon.setImageResource(R.drawable.friends_ext_may_know);
				holder.new_message.setVisibility(View.GONE);
				holder.avatar_layou.setOnTouchListener(null);
				holder.button_chat.setVisibility(View.GONE);
				holder.button_follow.setVisibility(View.GONE);
				holder.button_poke.setVisibility(View.GONE);
				holder.online_light.setVisibility(View.GONE);
				holder.friends_share_select.setVisibility(View.GONE);
				if (deliver_line != null) {
					deliver_line.setBackgroundColor(0);
				}
			}else{
				convertView = new View(mContext);
				convertView.setMinimumHeight(0);
			}
			break;
		default:
			if (position == getCount() - 1
					&& deliver_line != null) {
				deliver_line.setBackgroundColor(0);
			}
			position -= 4;
			UserFrdDTO data = mainList.get(position);
			holder.text.setText(data.getNick());
			try {
				String text = mContext.onlineTimeToText(Integer.parseInt(data
						.getPt()));
				text.replace("已经听", "Ta收听");
				holder.text2.setText(text);
			} catch (Exception e) {
				holder.text2.setText(mContext.onlineTimeToText(0));
			}
			Integer count = mNewMessageCountMap.get(data.getUid());
			if (count != null && count != 0) {
				holder.new_message.setVisibility(View.VISIBLE);
				holder.new_message.setText("" + count);
			} else {
				holder.new_message.setVisibility(View.GONE);
			}
			holder.avatar_layou.setTag(data);
			holder.avatar_layou.setOnTouchListener(mIconButtonListener);
			holder.button_chat.setTag(data);
			holder.button_chat.setOnTouchListener(mChatButtonListener);
			holder.button_follow.setTag(data);
			holder.button_follow.setOnClickListener(mFollowListener);
			holder.button_poke.setTag(data);
			holder.button_poke.setOnTouchListener(mPokeButtonListener);
			holder.online_light.setVisibility(data.isOl() ? View.VISIBLE
					: View.GONE);
			// friends_chat
			if (data.isFrdshp()) {
				holder.button_chat.setVisibility(View.VISIBLE);
			} else {
				holder.button_chat.setVisibility(View.GONE);
			}

			// button_poke
			if (data.isFlwd() && !data.isFrdshp() && data.isOl()) {
				holder.button_poke.setVisibility(View.VISIBLE);
			} else {
				holder.button_poke.setVisibility(View.GONE);
			}

			// friends_follow_listen
			if (data.isFlwd() && data.isOl()) {
				holder.button_follow.setVisibility(View.VISIBLE);
			} else {
				holder.button_follow.setVisibility(View.GONE);
			}
			final ImageView tmpIconView = holder.icon;
			final String iconUrl = CustomerImageRule.ID2URL(
					Constants.ID2URL_KEY_WORD_AVATAR, data.getAvatar(),
					Constants.ID2URL_DEFAULT_BITRATE_AVATAR);
			if (!iconUrl.equals(tmpIconView.getTag())) {
				tmpIconView.setTag(iconUrl);
				tmpIconView.setImageBitmap(null);
				mAsyncImageLoader.loadTempBitmapByUrl(iconUrl,
						AsyncImageLoader.IMAGE_TYPE_ROUND_CORNER,
						new ImageCallback() {
							@Override
							public void imageLoaded(final Bitmap bitmap,
									final String imageUrl) {
								if (!imageUrl.startsWith(""
										+ tmpIconView.getTag())) {
									return;
								}
								if (bitmap == null) {
									mAsyncImageLoader.loadTempBakBitmapByUrl(
											iconUrl,
											AsyncImageLoader.IMAGE_TYPE_ROUND,
											new ImageCallback() {

												@Override
												public void imageLoaded(
														final Bitmap bitmap,
														final String imageUrl) {
													if (!imageUrl.startsWith(""
															+ tmpIconView
																	.getTag())) {
														return;
													}
													mHandler.post(new Runnable() {
														@Override
														public void run() {
															if (!imageUrl
																	.startsWith(""
																			+ tmpIconView
																					.getTag())) {
																return;
															}
															tmpIconView
																	.setImageBitmap(bitmap);
														}
													});
												}
											});
								} else {
									mHandler.post(new Runnable() {
										@Override
										public void run() {
											if (!imageUrl.startsWith(""
													+ tmpIconView.getTag())) {
												return;
											}
											tmpIconView.setImageBitmap(bitmap);
										}
									});
								}
							}
						});
			}
			break;
		}
		return convertView;
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
			if (Constants.REFRESH_TIME_LIMIT > System.currentTimeMillis()
					- mLastRefreshTime) {
				mListView.stopRefresh();
				return;
			} else {
				mLastRefreshTime = System.currentTimeMillis();
				mListView.setRefreshTime(JingTools
						.getDateString(mLastRefreshTime));
			}
		}
		mServerIndex = 0;
		getData();
	}

	@Override
	public void onLoadMore() {
		getData();
	}

	public void setNeedTitle(boolean b) {
		isNeedTitle = b;
	}

	public View initData(List list, String ouid, DragRefreshListView listView,
			boolean needTitle) {
		isNeedTitle = needTitle;
		return initData(list, ouid, listView);
	}

	public View initData(List list, String ouid, DragRefreshListView listView) {
		if (listView == null) {
			listView = new DragRefreshListView(mContext);
		}
		setListView(listView);
		if (mainList.isEmpty() && ("" + mContext.getUserId()).equals(ouid)) {
			try {
				mainList = LocalCacheManager.getInstance().loadCacheData(
						mainList, MyFriendsAdapter.class.getName() + ouid);
				mListView.setPullLoadEnable(true);
			} catch (Exception e) {
				e.printStackTrace();
			}
			mOuid = ouid;
			notifyDataSetChanged();
			onRefresh();
		} else if (!ouid.equals(mOuid)) {
			mOuid = ouid;
			mServerIndex = 0;
			mainList.clear();
			if (list == null || list.isEmpty()) {
				mainList.clear();
				notifyDataSetChanged();
				onRefresh();
			} else {
				for (int i = 0; i < list.size(); i++) {
					mainList.add((UserFrdDTO) list.get(i));
				}
				notifyDataSetChanged();
			}
		}
		return mListView;
	}

	public void getData() {
		if (mContext.isOfflineMode()) {
			if (mListView != null) {
				mListView.stopRefresh();
				mListView.stopLoadMore();
				mListView.setPullLoadEnable(false);
			}
			return;
		}
		if (isLoading) {
			return;
		}
		isLoading = true;
		new Thread() {
			public void run() {
				final String lastOuid = mOuid;
				final HashMap<String, String> map = new HashMap<String, String>();
				map.put("uid", "" + mContext.getUserId());
				map.put("st", "" + mServerIndex);
				map.put("ps", "" + Constants.DEFAULT_NUM_OF_LOAD);
				if (mServerIndex > 0) {
					map.put("index", "" + mFriendsIndexOnServer);
				}
				String ouid = mOuid;
				ResultResponse<ListResult<UserFrdDTO>> tempRs = null;
				if (mOuid.endsWith(Constants.AC_ATTENTIONDED_KEYWORD)) {
					ouid = mOuid.substring(0, mOuid.length()
							- Constants.AC_ATTENTIONDED_KEYWORD.length());
					map.put("ouid", "" + ouid);
					tempRs = UserFriendRequestApi.fetchFollowings(map);
				} else if (mOuid.endsWith(Constants.BE_ATTENTIONDED_KEYWORD)) {
					ouid = mOuid.substring(0, mOuid.length()
							- Constants.BE_ATTENTIONDED_KEYWORD.length());
					map.put("ouid", "" + ouid);
					tempRs = UserFriendRequestApi.fetchFolloweds(map);
				} else {
					map.put("ouid", "" + ouid);
					tempRs = UserFriendRequestApi.fetchFriendsOrder(map);
				}
				final ResultResponse<ListResult<UserFrdDTO>> resultResponse = tempRs;
				if (!isNoNeedSave && ("" + mContext.getUserId()).equals(ouid)) {
					try {
						LocalCacheManager.getInstance().saveCacheData(
								resultResponse.getResult().getItems(),
								MyFriendsAdapter.class.getName() + ouid);
						isNoNeedSave = true;
					} catch (Exception e) {
						e.printStackTrace();
					}
				}

				mHandler.post(new Runnable() {

					@Override
					public void run() {
						if (!lastOuid.equals(mOuid)) {
							return;
						}
						List<UserFrdDTO> listResult = null;
						if (resultResponse != null
								&& resultResponse.isSuccess()) {
							listResult = resultResponse.getResult().getItems();
							if (mServerIndex <= 0) {
								mainList.clear();
							}
							mFriendsIndexOnServer = resultResponse.getResult()
									.getIndex();
							for (UserFrdDTO userFrdDTO : listResult) {
								mainList.add(userFrdDTO);
							}
							mServerIndex += listResult.size();
						} else {
							Toast.makeText(mContext, "加载失败", 0).show();
						}
						if (mListView != null) {
							mListView.stopRefresh();
							mListView.stopLoadMore();
							if (listResult != null) {
								hasMore = listResult.size() == Constants.DEFAULT_NUM_OF_LOAD;
							}
							mListView.setPullLoadEnable(hasMore);
						}
						notifyDataSetChanged();
						isLoading = false;
					}
				});
			};
		}.start();
	}

	public void frdOnline(SocketPUserSignedonDTO sDTO) {
		for (int i = 0; i < mainList.size(); i++) {
			if (mainList.get(i).getUid().equals(sDTO.getUid())) {
				Log.e("kid_debug",
						"mainList.get(i).getUid().equals(sDTO.getUid())");
				if (!mainList.get(i).isOl()) {
					mainList.get(i).setOl(true);
					mHandler.sendEmptyMessage(MSG_SET_NOTIFY);
				}
				break;
			}
		}
	}

	public void frdOff(SocketPUserSignedoffDTO sDTO) {
		for (int i = 0; i < mainList.size(); i++) {
			if (mainList.get(i).getUid().equals(sDTO.getUid())) {
				if (mainList.get(i).isOl()) {
					mainList.get(i).setOl(false);
					mHandler.sendEmptyMessage(MSG_SET_NOTIFY);
				}
				break;
			}
		}
	}

	@Override
	public String getTitleText() {
		return null;
	}

	public List getMainList() {
		return mainList;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		HashMap<Object, Object> params = new HashMap<Object, Object>();
		params.put("uid", ""+mContext.getUserId());
		switch (position) {
		case 2://"搜索好友"
			mContext.getmViewManagerCenter().getFriendsViewManager().showSearchView();
			break;
		case 3://"好友请求"
			LinkedViewData oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.FRIEND_LIST_WITH_TITLE, null,null);
			LinkedViewData newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.FRIEND_LIST_SNS, null, null);
			mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData, newLinkedViewData);
			isButtonLocked = false;
			break;
		case 4://"可能感兴趣的人"
//			u q st ps
			params.put("u", ""+mContext.getUserId());
			params.put("q", ""+mContext.getUserId());
			params.put("st", ""+0);
			params.put("ps", ""+Constants.DEFAULT_NUM_OF_LOAD);
			mContext.getmViewManagerCenter().getFriendsViewManager().showMaybeHaveInteresting(params);
			break;
		}
	}

	public void hasNewMessage(SocketPChatDTO socketPChatDTO) {
		String fuid = socketPChatDTO.getFuid();
		Integer count = mNewMessageCountMap.get(fuid);
		if (count == null) {
			count = 0;
		}
		count = Integer.valueOf(count + 1);
		notifyDataSetChanged();
		mNewMessageCountMap.put(fuid, count);
		mContext.getmViewManagerCenter().getmChatViewManager()
				.hasNewMessage(socketPChatDTO);
	}

	public Integer clearNewChatMessage(String fuid) {
		Integer rs = mNewMessageCountMap.remove(fuid);
		notifyDataSetChanged();
		return rs;
	}

}
