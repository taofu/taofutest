package com.jingfm.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.MainActivity.ChangeDataAnimateCallBack;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.Constants.KTC;
import com.jingfm.ViewManager.LoginStateChangeListener;
import com.jingfm.ViewManager.ViewManagerCenter;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.UserPersonalRequestApi;
import com.jingfm.api.model.LoginDataDTO;
import com.jingfm.api.model.TickerDTO;
import com.jingfm.api.model.UserPersonalDataDTO;
import com.jingfm.background_model.SettingManager;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.AsyncImageLoader.ImageCallback;
import com.jingfm.tools.JingTools;

public class LeftViewAdapter extends BaseAdapter implements
		OnItemClickListener, LoginStateChangeListener {

	public static final int ITEM_TYPE_HEAD = 0;
	public static final int ITEM_TYPE_SEPARATOR = 1;
	public static final int ITEM_TYPE_ITEM = 2;
	public static final int ITEM_TYPE_ITEM_WITH_NEW_MESSAGE = 3;

	public static final int ITEM_USER_PROFILE = 0;

	public static final int INDEX_ITEM_MUSIC_EXPLORE = 1;
	public static final int INDEX_ITEM_FAV_MUSIC = 2;
	public static final int INDEX_ITEM_CONVERSATION = 3;
	public static final int INDEX_ITEM_LOCAL_CACHE = 4;
	public static final int INDEX_ITEM_AI_RADIO = 5;
	public static final int INDEX_ITEM_TICKER = 6;
	public static final int INDEX_ITEM_APP_SETTING = 7;
	public static final int INDEX_ITEM_EXIT = 8;
//	public static final int INDEX_ITEM_FRIENDS = 2;
//	public static final int INDEX_ITEM_TOP_USERS = 2;


//	public static final int INDEX_ITEM_RANKING_LIST = 5;

//	public static final int INDEX_ITEM_RECOMMEND_APP = 9;
	protected static final int MSG_SET_NOTIFY = 0;
	private static final int MSG_UPDATE_TIME = 1;
	private ViewManagerCenter mViewManagerCenter;
	private MainActivity mContext;
	private ArrayList<LeftListItemData> mainList = new ArrayList<LeftListItemData>(
			17);
	private ArrayList<View> mCacheViews = new ArrayList<View>();
	private boolean isGuestMode = true;
	private UserHomePageAdapter mUserHomePageAdapter;
	private TickerAdapter mTickerAdapter;
	private TopUserAdapter mTopUserAdapter;
	private Handler mHandler;
	private List<TickerDTO> mTickerDTOList;
	private int mUserOnlineTotalTime;
	private TextView mUserTotalTimeView;
	private int mLastPosition = INDEX_ITEM_AI_RADIO;
	private int mFriendsNotifyNum;
	private int mTickerNotifyNum;
	protected RankListAdapter mRankListAdapter;
	private boolean isBigFont;
	private ImageView avatarView;

	public LeftViewAdapter(MainActivity context) {
		this.mContext = context;
		initHandler();
		initLeftViewDatas();
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
				case MSG_UPDATE_TIME:
					if (!mContext.isOfflineMode() && mUserOnlineTotalTime >= 0) {
						String text = mContext.onlineTimeToText(mUserOnlineTotalTime);
						mUserTotalTimeView.setText(text.substring(0, text.length()-3));
						mUserOnlineTotalTime++;
					}
					sendEmptyMessageDelayed(MSG_UPDATE_TIME, 30000);
					break;
				}
			}
		};
	}

	public void setmViewManagerCenter(ViewManagerCenter mViewManagerCenter) {
		this.mViewManagerCenter = mViewManagerCenter;
		mContext.addLoginListener(this);
	}

	public void initLeftViewDatas() {
		Resources resources = mContext.getResources();
		mainList.clear();
		mainList.add(new LeftListItemData(ITEM_TYPE_HEAD, "用户昵称", null, 0,
				"已经听了00小时00分钟"));
		mainList.add(new LeftListItemData(ITEM_TYPE_ITEM, resources
				.getString(R.string.menu_7), null, R.drawable.menu_spotlight,
				null));
		mainList.add(new LeftListItemData(ITEM_TYPE_ITEM, resources
				.getString(R.string.menu_4), null, R.drawable.menu_ilike, null));
		mainList.add(new LeftListItemData(ITEM_TYPE_ITEM_WITH_NEW_MESSAGE, resources
				.getString(R.string.menu_8), null, R.drawable.menu_gift, null));
		mainList.add(new LeftListItemData(ITEM_TYPE_ITEM, resources
				.getString(R.string.menu_5), null, R.drawable.menu_local, null));
		mainList.add(new LeftListItemData(ITEM_TYPE_ITEM, resources
				.getString(R.string.menu_3), null, R.drawable.menu_genius, null));
		mainList.add(new LeftListItemData(ITEM_TYPE_ITEM_WITH_NEW_MESSAGE, resources
				.getString(R.string.menu_0), null, R.drawable.menu_ticker, null));
		mainList.add(new LeftListItemData(ITEM_TYPE_ITEM, resources
				.getString(R.string.menu_9), null, R.drawable.menu_settings,
				null));
		mainList.add(new LeftListItemData(ITEM_TYPE_ITEM, resources
				.getString(R.string.menu_11), null, R.drawable.menu_logout,
				null));
		mCacheViews.clear();
		for (int i = 0; i < mainList.size(); i++) {
			mCacheViews.add(putViewInCache(i));
		}
		mCacheViews.add(putViewInCache(mainList.size()));
		mUserHomePageAdapter = new UserHomePageAdapter(mContext);
		refreshTextSize();
	}
	
	public UserHomePageAdapter getmUserHomePageAdapter() {
		if (mUserHomePageAdapter == null) {
			mUserHomePageAdapter = new UserHomePageAdapter(mContext);
		}
		return mUserHomePageAdapter;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		refreshItemBackgroudColor();
		switch (position) {
		case ITEM_USER_PROFILE:
			KTC.rep("CabinetMenu", "OpenHomepageMenu", "");
			mContext.changeData((mLastPosition != position),
					new ChangeDataAnimateCallBack() {
						@Override
						public void doChangeData() {
							mContext.setJingTitleText(mContext.getmLoginData().getUsr().getNick());
							mUserHomePageAdapter.showMyHomePage();
							mTickerDTOList = null;
							mViewManagerCenter.setChangeToView(mUserHomePageAdapter.getShowingView(),mUserHomePageAdapter.getTitleText());
						}
					});
			break;
		case INDEX_ITEM_TICKER:
			KTC.rep("CabinetMenu", "OpenTickerMenu", "");
			mContext.changeData((mLastPosition != position),
					new ChangeDataAnimateCallBack() {

						@Override
						public void doChangeData() {
							mContext.setJingTitleText(mainList.get(position).itemMainText);
							mViewManagerCenter
									.setChangeToListView(getmTickerAdapter());
							if(mTickerNotifyNum >0){
								getmTickerAdapter().onRefresh();
							}
						}
					});
			break;
		case INDEX_ITEM_AI_RADIO:
			KTC.rep("CabinetMenu", "OpenPersonalRadioMenu", "");
			if (mContext.isOfflineMode()) {
				mContext.toastOffLine();
				return;
			}
			mContext.changeData((mLastPosition != position),
					new ChangeDataAnimateCallBack() {
						@Override
						public void doChangeData() {
							mContext.playAiRadio(mainList.get(position).itemMainText);
							mViewManagerCenter.removeAllViewsAddNew(null);
						}
					});
			break;
		case INDEX_ITEM_FAV_MUSIC:
			KTC.rep("CabinetMenu", "OpenFavoriteMenu", "");
			mContext.changeData((mLastPosition != position),
					new ChangeDataAnimateCallBack() {
						@Override
						public void doChangeData() {
							mContext.setJingTitleText(mainList.get(position).itemMainText);
							mViewManagerCenter.showMusicViewFav();
						}
					});
			break;
		case INDEX_ITEM_LOCAL_CACHE:
			KTC.rep("CabinetMenu", "OpenCachedMenu", "");
			mContext.changeData((mLastPosition != position),
					new ChangeDataAnimateCallBack() {
						@Override
						public void doChangeData() {
							mContext.setJingTitleText(mainList.get(position).itemMainText);
							mViewManagerCenter.showMusicViewLocal();
						}
					});
			break;
//		case INDEX_ITEM_RANKING_LIST:
//			GoogleTrackerManger.getInstance().sendView(GoogleTrackerManger.AppScreen_Chart);
//			mContext.changeData((mLastPosition != position),
//					new ChangeDataAnimateCallBack() {
//						@Override
//						public void doChangeData() {
//							mContext.setJingTitleText(mainList.get(position).itemMainText);
//							if (mRankListAdapter == null) {
//								mRankListAdapter = new RankListAdapter(mContext);
//							}
//							mViewManagerCenter.setChangeToListView(mRankListAdapter);
//						}
//					});
//			break;
		case INDEX_ITEM_MUSIC_EXPLORE:
			KTC.rep("CabinetMenu", "OpenExploreMenu", "");
			if (mContext.isOfflineMode()) {
				mContext.toastOffLine();
//				mContext.centerViewBack();
				return;
			}
			mContext.changeData((mLastPosition != position),
					new ChangeDataAnimateCallBack() {
						@Override
						public void doChangeData() {
							mContext.setJingTitleText(mainList.get(position).itemMainText);
							mViewManagerCenter.startMusicExplore();
						}
					});
			break;
		case INDEX_ITEM_CONVERSATION:
			KTC.rep("CabinetMenu", "OpenChatMenu", "");
			if (mContext.isOfflineMode()) {
				mContext.toastOffLine();
//				mContext.centerViewBack();
				return;
			}
			mContext.changeData((mLastPosition != position),
					new ChangeDataAnimateCallBack() {
						@Override
						public void doChangeData(){
							mContext.setJingTitleText(mainList.get(position).itemMainText);
							mViewManagerCenter.setChangeToListView(mContext.getmViewManagerCenter().getmChatViewManager().getCoversationAdapter());
						}
					});
			break;
		case INDEX_ITEM_APP_SETTING:
			KTC.rep("CabinetMenu", "OpenSettingMenu", "");
			mContext.changeData((mLastPosition != position),
					new ChangeDataAnimateCallBack() {
						@Override
						public void doChangeData() {
							mContext.setJingTitleText(mainList.get(position).itemMainText);
							mViewManagerCenter.ShowSettingView();
						}
					});

			break;
		case INDEX_ITEM_EXIT:
			if (isGuestMode) {
				mContext.centerViewBack();
				mContext.showFinishDialog();
			}else{
				mContext.changeData(false,
						new ChangeDataAnimateCallBack() {
							@Override
							public void doChangeData() {
								mContext.showFinishDialog();
							}
						});
			}
			break;
		}
		mLastPosition = position;
		mCacheViews.get(position).setBackgroundColor(
				mContext.getResources().getColor(
						R.color.menu_item_press_bg));
	}

	public TickerAdapter getmTickerAdapter() {
		if (mTickerAdapter == null) {
			mTickerAdapter = new TickerAdapter(mContext);
			mTickerAdapter.initData(null,""+mContext.getUserId(),null);
		}
		return mTickerAdapter;
	}
	
	public TopUserAdapter getmTopUserAdapter() {
		if (mTopUserAdapter == null) {
			mTopUserAdapter = new TopUserAdapter(mContext);
		}
		return mTopUserAdapter;
	}

	void refreshItemBackgroudColor() {
		int color = mContext.getResources().getColor(R.color.menu_item_bg);
		mCacheViews.get(ITEM_USER_PROFILE).setBackgroundColor(color);
		mCacheViews.get(INDEX_ITEM_TICKER).setBackgroundColor(color);
		mCacheViews.get(INDEX_ITEM_AI_RADIO).setBackgroundColor(color);
		mCacheViews.get(INDEX_ITEM_FAV_MUSIC).setBackgroundColor(color);
		mCacheViews.get(INDEX_ITEM_LOCAL_CACHE).setBackgroundColor(color);
		mCacheViews.get(INDEX_ITEM_MUSIC_EXPLORE).setBackgroundColor(color);
		mCacheViews.get(INDEX_ITEM_CONVERSATION).setBackgroundColor(color);
		mCacheViews.get(INDEX_ITEM_APP_SETTING).setBackgroundColor(color);
	}

	@Override
	public int getCount() {
		return mainList.size();
	}

	@Override
	public Object getItem(int position) {
		return mainList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return mainList.get(position).hashCode();
	}

	public View putViewInCache(int position) {
		if (position == getCount()) {
			View itemView = LayoutInflater.from(mContext).inflate(
					R.layout.left_menu_head_guest, null);
			itemView.setMinimumHeight(JingTools.dip2px(mContext, 60));
			return itemView;
		}
		LeftListItemData data = mainList.get(position);
		View itemView = null;
		TextView textView = null;
		switch (data.itemType) {
		case ITEM_TYPE_HEAD:
			itemView = LayoutInflater.from(mContext).inflate(
					R.layout.left_menu_head, null);
			itemView.setMinimumHeight(JingTools.dip2px(mContext, 60));
			textView = (TextView) itemView.findViewById(R.id.main_text);
			textView.setText("" + data.itemMainText);
			mUserTotalTimeView = ((TextView) itemView.findViewById(R.id.sub_text));
			mUserTotalTimeView.setText(data.itemSubText);
			textView.setText("" + data.itemMainText);
			break;
		case ITEM_TYPE_SEPARATOR:
			itemView = LayoutInflater.from(mContext).inflate(
					R.layout.left_menu_separator, null);
			textView = (TextView) itemView.findViewById(R.id.main_text);
			textView.setText("" + data.itemMainText);
			break;
		case ITEM_TYPE_ITEM:
			itemView = LayoutInflater.from(mContext).inflate(
					R.layout.left_menu_item, null);
			itemView.setMinimumHeight(JingTools.dip2px(mContext, 60));
			textView = (TextView) itemView.findViewById(R.id.main_text);
			textView.setText("" + data.itemMainText);
			ImageView imageView = (ImageView) itemView.findViewById(R.id.icon);
			if (data.imageResId != 0) {
				imageView.setImageResource(data.imageResId);
			} else {
			}
			break;
		case ITEM_TYPE_ITEM_WITH_NEW_MESSAGE:
			itemView = LayoutInflater.from(mContext).inflate(
					R.layout.left_menu_item_with_new_message, null);
			textView = (TextView) itemView.findViewById(R.id.main_text);
			textView.setText("" + data.itemMainText);
			ImageView imageView2 = (ImageView) itemView.findViewById(R.id.icon);
			if (data.imageResId != 0) {
				imageView2.setImageResource(data.imageResId);
			} else {
			}
			break;
		}
		return itemView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0 && isGuestMode) {
			position = getCount();
		}
		return mCacheViews.get(position);
	}

	public class LeftListItemData {
		public int itemType = LeftViewAdapter.ITEM_TYPE_ITEM;
		public String itemMainText = "";
		public String imageUrl = "";
		public int imageResId;
		public String itemSubText = "";

		public LeftListItemData(int itemType, String itemMainText,
				String imageUrl, int imageResId, String itemSubText) {
			this.itemType = itemType;
			this.itemMainText = itemMainText;
			this.imageUrl = imageUrl;
			this.imageResId = imageResId;
			this.itemSubText = itemSubText;
		}
	}

	@Override
	public void onLogin(LoginDataDTO data) {
		isGuestMode = data.getUsr().isGuest();
		if (!isGuestMode) {
			refreshUserView(data);
			((TextView) mCacheViews.get(INDEX_ITEM_EXIT)
					.findViewById(R.id.main_text))
					.setText(mContext.getString(R.string.menu_11));
		}
//		}else{
//			((TextView) mCacheViews.get(INDEX_ITEM_LOGOUT)
//					.findViewById(R.id.main_text))
//					.setText(mContext.getString(R.string.menu_12));
//		}
		notifyDataSetChanged();
	}

	private void refreshUserView(final LoginDataDTO data) {
		final View view = mCacheViews.get(0);
		String name = data.getUsr().getNick();
		TextView textView = ((TextView) view.findViewById(R.id.main_text));
		if (name.length() > 10) {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
		} else {
			textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20);
		}
		textView.setText(name);
		avatarView = ((ImageView) view.findViewById(R.id.headViewImageView));
		AsyncImageLoader.getInstance().loadUpdateBitmapByUrl(
				CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_AVATAR, data.getUsr().getAvatar(),Constants.ID2URL_DEFAULT_BITRATE_AVATAR)
				, AsyncImageLoader.IMAGE_TYPE_ROUND_CORNER,
				new ImageCallback() {

					@Override
					public void imageLoaded(final Bitmap mBitmap, String imageUrl) {
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								avatarView.setImageBitmap(mBitmap);
							}
						});
					}
				});
		mContext.addAvatarImageView(avatarView);
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("uid", "" + data.getUsr().getId());
		map.put("ouid", "" + data.getUsr().getId());
		map.put("st", "0");
		map.put("ps", "" + Constants.DEFAULT_TICKER_NUM_OF_LOAD);
		new Thread() {
			public void run() {
				final ResultResponse<UserPersonalDataDTO> resultResponse = UserPersonalRequestApi
						.userFetchPersonal(map);
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						if (resultResponse != null
								&& resultResponse.isSuccess()) {
//							UserPersonalDataDTO mUserFetchPersonal = resultResponse.getResult();
							mTickerDTOList = resultResponse.getResult()
									.getTickers();
							try {
								mUserOnlineTotalTime = Integer
										.parseInt(resultResponse.getResult()
												.getPt());
							} catch (Exception e) {
								mUserOnlineTotalTime = -1;
							}
							updateTime();
						} else {
							Toast.makeText(mContext, "加载失败", 0).show();
						}
					}
				});
			};
		}.start();
	}

	public void refreshTextSize(){
		if (isBigFont == SettingManager.getInstance().isBigFont()) {
			return;
		}
		isBigFont = SettingManager.getInstance().isBigFont();
		for (int i = 0; i < mCacheViews.size(); i++) {
			View view = mCacheViews.get(i);
			TextView textMain = ((TextView) view.findViewById(R.id.main_text));
			if (textMain != null) {
				if (isBigFont) {
					textMain.setTextSize(JingTools.px2sp(mContext,(textMain.getTextSize() + JingTools.sp2px(mContext, 2))));
				}else{
					textMain.setTextSize(JingTools.px2sp(mContext,(textMain.getTextSize() - JingTools.sp2px(mContext, 2))));
				}
			}
			TextView textSub = ((TextView) view.findViewById(R.id.sub_text));
			if (textSub != null) {
				if (isBigFont) {
					textMain.setTextSize(JingTools.px2sp(mContext,(textMain.getTextSize() + JingTools.sp2px(mContext, 2))));
				}else{
					textMain.setTextSize(JingTools.px2sp(mContext,(textMain.getTextSize() - JingTools.sp2px(mContext, 2))));
				}
			}
		}
	}
	
	@Override
	public void onLogout() {
		isGuestMode = true;
		notifyDataSetChanged();
		mHandler.removeMessages(MSG_UPDATE_TIME);
		tickerNotifyChange(-mTickerNotifyNum);
		friendsNotifyChange(-mFriendsNotifyNum);
	}

	private void updateTime() {
		mHandler.sendEmptyMessage(MSG_UPDATE_TIME);
	}

	public synchronized void friendsNotifyChange(int count){
		mContext.getmViewManagerCenter().newMessageNotify(count);
		mFriendsNotifyNum += count;
		TextView text = (TextView)mCacheViews.get(INDEX_ITEM_CONVERSATION).findViewById(R.id.new_message);
		if(mFriendsNotifyNum <= 0){
			mFriendsNotifyNum = 0;
			text.setVisibility(View.GONE);
		}else{
			text.setVisibility(View.VISIBLE);
			text.setText("" + mFriendsNotifyNum);
		}
	}
	public synchronized void tickerNotifyChange(int count){
		mContext.getmViewManagerCenter().newMessageNotify(count);
		mTickerNotifyNum += count;
		TextView text = (TextView)mCacheViews.get(INDEX_ITEM_TICKER).findViewById(R.id.new_message);
		if(mTickerNotifyNum <= 0){
			mTickerNotifyNum = 0;
			text.setVisibility(View.GONE);
		}else{
			text.setVisibility(View.VISIBLE);
			text.setText("" + mTickerNotifyNum);
		}
	}

	public void clearTickerNotifyChange() {
		tickerNotifyChange(-mTickerNotifyNum);
	}

	public void setUserOnlineTime(int onLineTime) {
		mUserOnlineTotalTime = onLineTime;
	}
	
	public View getAvatarView(){
		return avatarView;
	}

}