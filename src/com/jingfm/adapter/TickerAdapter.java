package com.jingfm.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.TextView;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.MainActivity.ChangeDataAnimateCallBack;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.ViewManager.ViewManagerCenter.LinkedViewData;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.UserFriendRequestApi;
import com.jingfm.api.business.UserMusicRequestApi;
import com.jingfm.api.business.UserTickerRequestApi;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.TickerDTO;
import com.jingfm.background_model.LocalCacheManager;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.AsyncImageLoader.ImageCallback;
import com.jingfm.tools.JingTools;

public class TickerAdapter extends AbstractDragAdapter {

	protected static final int MSG_SET_NOTIFY = 110;

	private MainActivity mContext;

	private List<TickerDTO> mainList = new ArrayList<TickerDTO>();
	private Map<String,Long> mUsersPtMap = new HashMap<String,Long>();

	private AsyncImageLoader asyncImageLoader;

	public int playType;

	private int playingTid;

	private OnClickListener mLikeViewListener;

	private OnClickListener mShareViewListener;

	private OnClickListener mPlayViewListener;
	
	private Handler mHandler;

	private DragRefreshListView mListView;

	private int mServerIndex;

	public int width;

	private boolean isLoading;

	private long mLastRefreshTime;

	private UserHomePageAdapter mUserHomePageAdapter;

	private String mOuid;

	private boolean isNoNeedSave;

	private OnClickListener mToUserHomePageListener;

	public TickerAdapter(MainActivity context) {
		this.mContext = context;
		asyncImageLoader = AsyncImageLoader.getInstance();
		initHandler();
		mLikeViewListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				final TickerDTO tickerDTO = (TickerDTO) v.getTag();
				if (tickerDTO == null) {
					return;
				}
				ImageView imageView = ((ImageView) v
						.findViewById(R.id.fav_image));
				imageView.setScaleType(ScaleType.CENTER_INSIDE);
				if (!tickerDTO.isLoved()) {
					imageView.setImageResource(R.drawable.ticker_liked);
					
				} else {
					imageView.setImageResource(R.drawable.ticker_like);
				}
				if (mContext.isCurrentDtoPlaying(""+tickerDTO.getTid())) {
					mContext.musicFav(mContext.getUserId());
				}else{
					new Thread(){
						public void run() {
							HashMap<Object, Object> params = new HashMap<Object, Object>();
							params.put("uid", ""+mContext.getUserId());
							params.put("tid", tickerDTO.getTid());
							UserMusicRequestApi.musicLoveAct(params);
						};
					}.start();
				}
				tickerDTO.setLoved(!tickerDTO.isLoved());
				
			}
		};
		mShareViewListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				TickerDTO tickerDTO = (TickerDTO) v.getTag();
				if (tickerDTO == null) {
					return;
				}
				MusicDTO musicDTO = tickerDTO.toMusicDTO();
				mContext.showShareView(musicDTO);
			}
		};
		mPlayViewListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TickerDTO tickerDTO = (TickerDTO) v.getTag();
				if (tickerDTO == null) {
					return;
				}
				if (mContext.isCurrentDtoPlaying(""+tickerDTO.getTid())) {
					if (mContext.isPlaying()) {
						mContext.musicPause();
					}else{
						mContext.musicPlay();
					}
					((ImageView)v).setImageResource(mContext.isPlaying()?R.drawable.ticker_pause:R.drawable.ticker_play);
				}else{
					ArrayList<MusicDTO> list = new ArrayList<MusicDTO>();
					list.add(tickerDTO.toMusicDTO());
					mContext.startNewSubList(list,Constants.UNKNOWN_M_VALUE,Constants.UNKNOWN_CMBT_VALUE, null);
					mContext.setSubListTitle("收听动态");
					((ImageView)v).setImageResource(R.drawable.ticker_pause);
					mContext.changeData(true, new ChangeDataAnimateCallBack() {
						
						@Override
						public void doChangeData() {
							mContext.getmViewManagerCenter().removeAllViewsAddNew(null);
						}
					});
				}
			}
		};
		mToUserHomePageListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				TickerDTO data = (TickerDTO) v.getTag();
				if (data == null) {
					return;
				}
				mContext.getmViewManagerCenter();
				LinkedViewData oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.USER_TICKER_PAGE, ""+mOuid, new ArrayList(mainList));
				LinkedViewData newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.USER_HOME_PAGE, ""+data.getUid(), null);
				mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData,newLinkedViewData);
			}
		};
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(final Message msg) {
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
		mListView.setPullLoadEnable(false);
		mListView.setSelector(R.drawable.draw_nothing);
		mListView.setDividerHeight(0);
		mListView.setDrawingCacheEnabled(true);
		mListView.setFastScrollEnabled(true);
		mListView.setFooterDividersEnabled(false);
		mListView.setFadingEdgeLength(0);
		mListView.setBackgroundColor(0);
		mListView.setAdapter(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		mListView.setXListViewListener(this);
		notifyDataSetChanged();
		mListView.invalidate();
	};
	
	
	@Override
	public int getCount() {
		return mainList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return mainList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}
	
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		HoldView holdView = null;
		width = parent.getWidth();
		if (convertView == null || convertView.getTag() == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.center_list_ticker_item, null);
			holdView = new HoldView(convertView);
			convertView.setTag(holdView);
		} else {
			holdView = (HoldView) convertView.getTag();
		}
		holdView.initView(mainList.get(position));
		return convertView;
	}

	public class HoldView {
		private TextView music_name;
		private ImageView iconImageView;
		private TextView name_text,time_text,ticker_time;
		private ImageView headViewImageView;
		private View headViewImageView_shell;

		private ImageView playView;
		private ImageView shareView, likeView;

		public HoldView(View view) {
			music_name = (TextView) view.findViewById(R.id.music_name);
			iconImageView = (ImageView) view.findViewById(R.id.music_cover);

			name_text = (TextView) view.findViewById(R.id.name_text);
			time_text = (TextView) view.findViewById(R.id.time_text);
			ticker_time = (TextView) view.findViewById(R.id.ticker_time);
			LayoutParams layoutParams2 = (LayoutParams) iconImageView
					.getLayoutParams();
			layoutParams2.height = width;
			iconImageView.setLayoutParams(layoutParams2);
			headViewImageView = (ImageView) view
					.findViewById(R.id.headViewImageView);
			headViewImageView_shell = view
					.findViewById(R.id.headViewImageView_shell);
			headViewImageView.layout(headViewImageView.getLeft(),
					headViewImageView.getTop(), headViewImageView.getRight(),
					headViewImageView.getRight());
			playView = (ImageView) view.findViewById(R.id.play_but);
			shareView = (ImageView) view.findViewById(R.id.share_image);
			likeView = (ImageView) view.findViewById(R.id.fav_image);
		}

		public void initView(TickerDTO dto) {
			music_name.setText(dto.getAtn() + " - " + dto.getTit());
			name_text.setText(dto.getNick());
			Long seconds = mUsersPtMap.get(""+dto.getUid());
			time_text.setText("已经收听了 " + JingTools.formatSeconds(seconds == null ? 0 : seconds));
			seconds = Math.abs((System.currentTimeMillis() - dto.getTs()))  / 1000;
			ticker_time.setText(JingTools.formatSecondsShort(seconds == null ? 0 : seconds));
//			headViewImageView.setImageResource(R.drawable.chat_avatar_bg);
			likeView.setImageResource(dto.isLoved() ? R.drawable.ticker_liked
							: R.drawable.ticker_like);
			boolean rs = mContext.isCurrentDtoPlaying(""+dto.getTid()) && mContext.isPlaying();
			playView.setImageResource(rs?R.drawable.ticker_pause:R.drawable.ticker_play);
			playView.setTag(dto);
			playView.setOnClickListener(mPlayViewListener);
			likeView.setTag(dto);
			likeView.setOnClickListener(mLikeViewListener);
			headViewImageView_shell.setTag(dto);
			headViewImageView_shell.setOnClickListener(mToUserHomePageListener);
			if (JingTools.isValidString(dto.getAvt())) {
				final String imageUrl1 = CustomerImageRule.ID2URL(
						Constants.ID2URL_KEY_WORD_AVATAR, dto.getAvt(), "US");
				if (!imageUrl1.equals(headViewImageView.getTag())) {
					headViewImageView.setTag(imageUrl1);
					if (mContext.isMyUrlUpdata(imageUrl1)) {
						Bitmap bitmap = mContext.getAvatarAfterChange();
//						headViewImageView.setImageBitmap(AsyncImageLoader.toRoundCorner(bitmap, bitmap.getWidth()*15/100));
						headViewImageView.setImageBitmap(bitmap);
					}else{
						headViewImageView.setImageBitmap(null);
						asyncImageLoader.loadTempBitmapByUrl(imageUrl1,
								AsyncImageLoader.IMAGE_TYPE_ORIGINAL,
								new AsyncImageLoader.ImageCallback() {
							@Override
							public void imageLoaded(final Bitmap mBitmap,
									final String imageUrl) {
								if (!imageUrl.startsWith(imageUrl1)) {
									return;
								}
								if (mBitmap == null) {
									asyncImageLoader.loadTempBakBitmapByUrl(imageUrl1, AsyncImageLoader.IMAGE_TYPE_ORIGINAL, new ImageCallback() {

										@Override
										public void imageLoaded(final Bitmap bitmap, final String imageUrl) {
											mHandler.post(new Runnable() {
												@Override
												public void run() {
													if (imageUrl.startsWith((String) headViewImageView.getTag())) {
//														headViewImageView.setImageBitmap(AsyncImageLoader.toRoundCorner(bitmap, bitmap.getWidth()*15/100));
														headViewImageView.setImageBitmap(bitmap);
													}
												}
											});
										}
									});
								}else{
									mHandler.post(new Runnable() {
										@Override
										public void run() {
											if (imageUrl.startsWith((String) headViewImageView.getTag())) {
//												headViewImageView.setImageBitmap(AsyncImageLoader.toRoundCorner(mBitmap, mBitmap.getWidth()*15/100));
												headViewImageView.setImageBitmap(mBitmap);
											}
										}
									});
								}
							}
						});
					}
				}
			}
			shareView.setTag(dto);
			shareView.setOnClickListener(mShareViewListener);
			if (JingTools.isValidString(dto.getFid())) {
				String imageUrl2 = CustomerImageRule.ID2URL(
						Constants.ID2URL_KEY_WORD_ALBUM, dto.getFid(), 
						Constants.ID2URL_DEFAULT_BITRATE_ALBUM);
				if (!imageUrl2.equals(iconImageView.getTag())) {
					iconImageView.setTag(imageUrl2);
					iconImageView.setImageResource(R.drawable.draw_color_cover_default);
					asyncImageLoader.loadBitmapByUrl(imageUrl2,
							AsyncImageLoader.IMAGE_TYPE_ORIGINAL,
							new AsyncImageLoader.ImageCallback() {
								@Override
								public void imageLoaded(final Bitmap mBitmap,
										final String imageUrl) {
									if (mBitmap == null) {
										return;
									}
									mHandler.post(new Runnable() {
	
										@Override
										public void run() {
											if (imageUrl.startsWith((String) iconImageView.getTag())) {
//												iconImageView.setImageBitmap(AsyncImageLoader.toRoundCorner(mBitmap, mBitmap.getWidth()*8/1000));
												iconImageView.setImageBitmap(mBitmap);
											}
										}
									});
								}
							});
				}
			}
		}
	}

	public void setPlayType(int playType, int tid) {
		this.playType = playType;
		this.playingTid = tid;
		notifyDataSetChanged();
	}

	public void addOne(TickerDTO dto) {
		this.mainList.add(0, dto);
		notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	}

	@Override
	public void onXScrolling(View view) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem,
			int visibleItemCount, int totalItemCount) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onRefresh() {
		if (mListView != null) {
			if (Constants.REFRESH_TIME_LIMIT > System.currentTimeMillis() - mLastRefreshTime ) {
				mListView.stopRefresh();
				return;
			}else{
				mLastRefreshTime = System.currentTimeMillis();
				mListView.setRefreshTime(JingTools.getDateString(mLastRefreshTime));
			}
		}
		getNewData();
	}

	@Override
	public void onLoadMore() {
		getMoreData();
	}

	public View initData(List list, String ouid, DragRefreshListView listView) {
		if (mainList.isEmpty() && (""+mContext.getUserId()).equals(ouid)) {
			try {
				mainList = LocalCacheManager.getInstance().loadCacheData(mainList, TickerAdapter.class.getName() + ouid);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
			this.mOuid = ouid;
			notifyDataSetChanged();
			onRefresh();
		}else if (!ouid.equals(mOuid)) {
			this.mOuid = ouid;
			mServerIndex = 0;
			if (list == null || list.isEmpty()) {
				mainList.clear();
				notifyDataSetChanged();
				onRefresh();
			}else{
				if (mainList != list) {
					mainList.clear();
					for (int i = 0; i < list.size(); i++) {
						mainList.add((TickerDTO) list.get(i));
					}
				}
			}
			setListView(listView);
		}
		return mListView;
	}

	public void getNewData() {
		mServerIndex = 0;
		mContext.getmViewManagerLeft().clearTickerNotifyChange();
		getData();
	}

	public void getMoreData() {
		mServerIndex += Constants.DEFAULT_NUM_OF_LOAD;
		getData();
	}

	public void getData() {
		if (!JingTools.isValidString(mOuid) || isLoading) {
			return;
		}
		if (mContext.isOfflineMode()) {
			mContext.toastOffLine();
			return;
		}
		isLoading = true;
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("uid", "" + mContext.getUserId());
		map.put("st", "" + mServerIndex);
		map.put("ps", "" + Constants.DEFAULT_NUM_OF_LOAD);
		new Thread() {
			public void run() {
				final ResultResponse<ListResult<TickerDTO>> resultResponse = UserTickerRequestApi
						.fetchLoveRecents(map);
				if (!isNoNeedSave && resultResponse != null
						&& resultResponse.isSuccess() && !resultResponse
						.getResult().getItems().isEmpty()) {
					try {
						LocalCacheManager.getInstance().saveCacheData(resultResponse
								.getResult().getItems(), TickerAdapter.class.getName() + mContext.getUserId());
						isNoNeedSave = true;
					} catch (Exception e) {
					}
				}
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						List<TickerDTO> listResult = null;
						if (resultResponse != null
								&& resultResponse.isSuccess()) {
							listResult = resultResponse
									.getResult().getItems();
							if (mServerIndex == 0) {
								mainList.clear();
							}
							for (TickerDTO tickerDTO : listResult) {
								mainList.add(tickerDTO);
							}
							notifyDataSetChanged();
						} else {
							Toast.makeText(mContext, "加载失败", 0).show();
						}
						if (mListView != null) {
							mListView.stopRefresh();
							mListView.stopLoadMore();
							if (listResult != null) {
								mListView.setPullLoadEnable(!(listResult.size() < Constants.DEFAULT_NUM_OF_LOAD));
							}
							mListView.invalidate();
						}
						isLoading = false;
					}
				});
				if (resultResponse != null
						&& resultResponse.isSuccess() && !resultResponse
						.getResult().getItems().isEmpty()) {
					List<String> uidList = new ArrayList<String>();
					StringBuffer stringBuffer = new StringBuffer();
					for (int i = 0; i < resultResponse
							.getResult().getItems().size(); i++) {
						String uid = "" + resultResponse.getResult().getItems().get(i).getUid();
						if (!uidList.contains(uid)) {
							if (i != 0) {
								stringBuffer.append(',');
							}
							stringBuffer.append(uid);
							uidList.add(uid);
						}
					}
					Map<String, String> paramsPt = new HashMap<String, String>();
					paramsPt.put("uid",""+mContext.getUserId());
					paramsPt.put("uids", stringBuffer.toString());
					ResultResponse<List<String>> rs = UserFriendRequestApi.fetchUsersPt(paramsPt);
					if (rs != null && rs.isSuccess()) {
						for (int i = 0; i < Math.min(uidList.size(), rs.getResult().size()); i++) {
							try {
								mUsersPtMap.put(uidList.get(i),Long.parseLong(rs.getResult().get(i)));
							} catch (Exception e) {
							}
						}
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								notifyDataSetChanged();
							}
						});
					}
				}
				
			};
		}.start();
	}

	@Override
	public String getTitleText() {
		// TODO Auto-generated method stub
		return null;
	}

}
