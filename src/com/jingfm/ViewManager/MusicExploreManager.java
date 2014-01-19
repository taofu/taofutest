package com.jingfm.ViewManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Parcelable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingfm.MainActivity;
import com.jingfm.MainActivity.ChangeDataAnimateCallBack;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.Constants.KTC;
import com.jingfm.ViewManager.ViewManagerCenter.LinkedViewData;
import com.jingfm.ViewManager.ViewManagerRight.MainItem;
import com.jingfm.adapter.AbstractDragAdapter;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.BadgeRequestApi;
import com.jingfm.api.business.UserAppRequestApi;
import com.jingfm.api.business.UserAutoSearchApi;
import com.jingfm.api.business.UserFriendRequestApi;
import com.jingfm.api.model.BadgeDTO;
import com.jingfm.api.model.BadgeType;
import com.jingfm.api.model.BannerDTO;
import com.jingfm.api.model.BannerDetailDTO;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.PrefixDTO;
import com.jingfm.api.model.UserDTO;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.customer_views.JingPagerAdapter;
import com.jingfm.customer_views.JingViewPager;
import com.jingfm.customer_views.JingViewPager.OnPageChangeListener;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.AsyncImageLoader.ImageCallback;
import com.jingfm.tools.JingTools;

public class MusicExploreManager extends AbstractDragAdapter implements
		OnItemClickListener {
	private MainActivity mContext;
	private Handler mHandler;
	private DragRefreshListView mListView;
	private DragRefreshListView mSubListView;
	private DragRefreshListView mSubListViewForNlg;
	private List<MainItem> mainItems = new ArrayList<MainItem>();;
	public boolean isAsyncTaskLock;
	private MenuItem[] mMenuItem;
	protected int mNumOfList = 20;
	protected List<BannerDTO> mBannerList = new ArrayList<BannerDTO>();
	private List<BadgeDTO> mBadgeList;
	private List<BannerDetailDTO> mBannerDetailsList = new ArrayList<BannerDetailDTO>();
	private Object mCountOfBadgeNeedLoad = 20;
	private boolean isLoadingData;
	private SubListAdapter mSubListAdapter;
	private ImageView[] mBannerViewArray;
	private JingPagerAdapter mJingPagerAdapter;
	private View mHeadView;
	private boolean isBannerMode;
	private SubListAdapterForNlg mSubListAdapterForNlg;
	private JingViewPager music_explore_view_pager;

	public MusicExploreManager(MainActivity context) {
		this.mContext = context;
		initHandler();
		initItems();
		initHeadView();
	}

	private void initHeadView() {
		mHeadView = LayoutInflater.from(mContext).inflate(
				R.layout.music_explore_view_pager, null);
		final View music_explore_index_cursor_0 = mHeadView
				.findViewById(R.id.music_explore_index_cursor_0);
		final View music_explore_index_cursor_1 = mHeadView
				.findViewById(R.id.music_explore_index_cursor_1);
		final View music_explore_index_cursor_2 = mHeadView
				.findViewById(R.id.music_explore_index_cursor_2);
		music_explore_view_pager = (JingViewPager) mHeadView
				.findViewById(R.id.music_explore_view_pager);
		music_explore_view_pager
				.setOnPageChangeListener(new OnPageChangeListener() {
					@Override
					public void onPageSelected(int position) {
						Drawable drawableOn = mContext.getResources()
								.getDrawable(R.drawable.index_round_on);
						Drawable drawableOff = mContext.getResources()
								.getDrawable(R.drawable.index_round_off);
						music_explore_index_cursor_0
								.setBackgroundDrawable(drawableOff);
						music_explore_index_cursor_1
								.setBackgroundDrawable(drawableOff);
						music_explore_index_cursor_2
								.setBackgroundDrawable(drawableOff);
						switch (position) {
						case 0:
							music_explore_index_cursor_0
									.setBackgroundDrawable(drawableOn);
							break;
						case 1:
							music_explore_index_cursor_1
									.setBackgroundDrawable(drawableOn);
							break;
						case 2:
							music_explore_index_cursor_2
									.setBackgroundDrawable(drawableOn);
							break;
						}
					}

					@Override
					public void onPageScrolled(int position,
							float positionOffset, int positionOffsetPixels) {
						// TODO Auto-generated method stub

					}

					@Override
					public void onPageScrollStateChanged(int state) {
						// TODO Auto-generated method stub

					}
				});
		music_explore_view_pager.setCurrentItem(0);
		FrameLayout.LayoutParams layoutParams = (android.widget.FrameLayout.LayoutParams) music_explore_view_pager
				.getLayoutParams();
		layoutParams.height = (JingTools.screenWidth - JingTools.dip2px(
				mContext, 24)) / 2;
		music_explore_view_pager.setLayoutParams(layoutParams);
		music_explore_view_pager.setDisallowInterceptView(mContext
				.getmViewManagerCenter().getMainLayout());
		mJingPagerAdapter = new JingPagerAdapter() {
			@Override
			public void startUpdate(View container) {
			}

			@Override
			public Parcelable saveState() {
				return null;
			}

			@Override
			public void restoreState(Parcelable state, ClassLoader loader) {
			}

			public void destroyItem(View arg0, int arg1, Object arg2) {
				((ViewGroup) arg0).removeView(mBannerViewArray[arg1]);
			}

			@Override
			public void finishUpdate(View arg0) {
			}

			@Override
			public Object instantiateItem(View arg0, int arg1) {
				ViewGroup parentView = ((ViewGroup) arg0);
				parentView.addView(mBannerViewArray[arg1]);
				return mBannerViewArray[arg1];
			}

			// 判断是否由对象生成界面
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == (arg1);
			}

			@Override
			public int getCount() {
				return mBannerViewArray.length;
			}
		};
		music_explore_view_pager.setAdapter(mJingPagerAdapter);
		mHandler.postDelayed(new Runnable() {

			@Override
			public void run() {
				int index = (music_explore_view_pager.getCurrentItem() + 1)
						% mJingPagerAdapter.getCount();
				music_explore_view_pager.setCurrentItem(index, true);
				mHandler.postDelayed(this, 2000);
			}
		}, 2000);
	}

	private void fetchBannerList() {
		new Thread() {
			public void run() {
				final ResultResponse<List<BannerDTO>> rs = UserFriendRequestApi
						.fetchBanners();
				if (rs != null && rs.isSuccess()) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mBannerList = rs.getResult();
							notifyDataSetChanged();
						}
					});
				}
			};
		}.start();
	}

	private void fetchBannerDetails(final BannerDTO bannerDTO) {
		if (bannerDTO == null) {
			return;
		}
		if (JingTools.isValidString(bannerDTO.getHref())) {
			Intent intent = new Intent(Intent.ACTION_VIEW);  
			intent.setData(Uri.parse(bannerDTO.getHref()));  
			mContext.startActivity(intent);
			return;
		}
		mContext.getmViewManagerCenter().pushNewView(
				mSubListView, null);
		mBannerDetailsList.clear();
		new Thread() {
			public void run() {
				HashMap<String, String> params = new HashMap<String, String>();
				// params d bid st ps
				params.put("bid", bannerDTO.getId());
				params.put("st", "" + 0);
				params.put("ps", "" + 20);
				KTC.rep("Exploring", "ClickBanner", "banner_id");
				final ResultResponse<ListResult<BannerDetailDTO>> rs = UserFriendRequestApi
						.fetchBannersDTO(params);
				if (rs != null && rs.isSuccess()) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							mBannerDetailsList.addAll(rs.getResult().getItems());
							isBannerMode = true;
							mSubListAdapter.notifyDataSetChanged();
						}
					});
				}
			};
		}.start();
	}
	
	private void initItems() {
		mMenuItem = new MenuItem[] {
				new MenuItem("大家觉得赞的搜索条件", "看看其他人都是怎么发挥小Jing的极限",
						R.drawable.search_natural_language, ""),
				new MenuItem("音乐榜单", "罗列世界各大流行的音乐榜单",
						R.drawable.search_recommendation, ""),
						
				new MenuItem("地点音乐", "在不同的地点可以收听更适合的音乐",
						R.drawable.search_place, BadgeType.Locations.getName()),
				new MenuItem("状态音乐", "不同状态下收听的音乐各不同",
						R.drawable.search_state, BadgeType.Status.getName()),
				new MenuItem("情绪音乐", "音乐是情绪的宣泄，收听不同的音乐情绪",
						R.drawable.search_moods, BadgeType.Mood.getName()),
				new MenuItem("时间音乐", "应景的音乐和时间息息相关",
						R.drawable.search_timing, BadgeType.Time.getName()),
				new MenuItem("风格音乐", "收听不同音乐风格下的电台",
						R.drawable.search_genres, BadgeType.OtherGenre.getName()),
				new MenuItem("乐器音乐", "找到喜欢的乐器一口气听个够",
						R.drawable.search_instruments, BadgeType.MusicalInstruments.getName()) };
		mSubListAdapter = new SubListAdapter();
		mSubListAdapterForNlg = new SubListAdapterForNlg();
		mSubListView = new DragRefreshListView(mContext);
		mSubListViewForNlg = new DragRefreshListView(mContext);
		mSubListAdapter.setListView(mSubListView);
		mSubListAdapterForNlg.setListView(mSubListViewForNlg);
		mBannerViewArray = new ImageView[] { new ImageView(mContext),
				new ImageView(mContext), new ImageView(mContext) };
	}

	private void initHandler() {
		this.mHandler = new Handler();
	}

	@Override
	public void setListView(DragRefreshListView listView) {
		mListView = listView;
		mListView.setDivider(mContext.getResources().getDrawable(
				R.drawable.draw_nothing));
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(false);
		mListView.setAdapter(this);
		mListView.setXListViewListener(this);
		mListView.setOnItemClickListener(this);
		int padding = JingTools.dip2px(mContext, 12);
		mListView.setPadding(padding, 0, padding, 0);
		if (mBannerList.isEmpty()) {
			fetchBannerList();
		}
	}

	@Override
	public void onRefresh() {
		if (mBannerList.isEmpty()) {
			fetchBannerList();
		}
		mListView.stopRefresh();
	}

	@Override
	public void onLoadMore() {
		mListView.stopLoadMore();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view,
			final int position, long id) {
		if (isLoadingData) {
			return;
		}
		if (mBadgeList != null) {
			mBadgeList.clear();
			mSubListAdapter.notifyDataSetChanged();
		}
		switch (position) {
		case 0:

			break;
		case 2:
			KTC.rep("Exploring", "OpenBestSearch", "");
			mContext.getmViewManagerCenter().pushNewView(mSubListViewForNlg, null);
			mSubListAdapterForNlg.refreshData();
			break;
		case 3:
			KTC.rep("Exploring", "OpenChart", "");
			LinkedViewData oldLinkedViewData = mContext.getmViewManagerCenter()
					.createLinkedViewData(LinkedViewData.MUSIC_EXPLORER, null,
							null);
			LinkedViewData newLinkedViewData = mContext.getmViewManagerCenter()
					.createLinkedViewData(LinkedViewData.RANK_LIST, null, null);
			mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData,
					newLinkedViewData);
			break;
		default:
			isLoadingData = true;
			isBannerMode = false;
			mContext.getmViewManagerCenter().pushNewView(mSubListView, null);
			new Thread() {
				@Override
				public void run() {
					HashMap<Object, Object> map = new HashMap<Object, Object>();
					map.put("ps", mCountOfBadgeNeedLoad);
					map.put("t", mMenuItem[position - 2].type);
					map.put("uid", mContext.getUserId());
					switch (position) {
					case 1:
						KTC.rep("Exploring", "OpenTime", "");
						break;
					case 2:
						KTC.rep("Exploring", "OpenLocation", "");
						break;
					case 3:
						KTC.rep("Exploring", "OpenMoods", "");
						break;
					case 4:
						KTC.rep("Exploring", "OpenGenre", "");
						break;
					case 5:
						KTC.rep("Exploring", "OpenStatus", "");
						break;
					default:
						KTC.rep("Exploring", "OpenInstruments", "");
						break;
					}
					final ResultResponse<List<BadgeDTO>> rs = BadgeRequestApi
							.fetchBadges(map);
					if (rs != null && rs.isSuccess()) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mBadgeList = rs.getResult();
								mSubListAdapter.notifyDataSetChanged();
							}
						});
					}
					isLoadingData = false;
				}
			}.start();
			break;
		}
	}

	@Override
	public int getCount() {
		return mMenuItem.length + 1;
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
			convertView = mHeadView;
			if (!mBannerList.isEmpty()) {
				for (int i = 0; i < mBannerViewArray.length; i++) {
					final int index = i;
					String url = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_BANNER, mBannerList.get(i % mBannerList.size())
							.getFid(), "NO");
					mBannerViewArray[index].setOnTouchListener(new OnTouchListener() {
						private float mX;

						@Override
						public boolean onTouch(View v, MotionEvent event) {
							if (event.getAction() == MotionEvent.ACTION_DOWN) {
								if (mX == 0) {
									mX = event.getX();
									music_explore_view_pager.dispatchTouchEvent(event);
								}else{
									return false;
								}
							}
							if (event.getAction() == MotionEvent.ACTION_UP) {
								float distance = Math.abs(event.getX() - mX);
								mX = 0;
								if (distance < JingTools.dip2px(mContext, 100)) {
									fetchBannerDetails(mBannerList.get(index));
								}else{
									return false;
								}
							}
							return true;
						}
					});
					if (JingTools.isValidString(url)
							&& !url.equals(mBannerViewArray[i].getTag())) {
						mBannerViewArray[i].setTag(url);
						AsyncImageLoader.getInstance().loadBitmapByUrl(url,
								AsyncImageLoader.IMAGE_TYPE_ORIGINAL,
								new ImageCallback() {
									@Override
									public void imageLoaded(Bitmap bitmap,
											String imageUrl) {
										if (imageUrl
												.startsWith(mBannerViewArray[index]
														.getTag().toString())) {
											mBannerViewArray[index]
													.setImageBitmap(bitmap);
											
										}
									}
								});
					}
				}
			}
			return convertView;
		}
		position--;
		if (convertView == null || convertView.getTag() == null
				|| R.layout.right_menu_item != (Integer) convertView.getTag()) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.right_menu_item, null);
			convertView.setTag(Integer.valueOf(R.layout.right_menu_item));
			if (position == 1 || position + 2 == getCount()) {
				convertView.findViewById(R.id.deliver_line).setBackgroundColor(
						0);
			} else {
				convertView.findViewById(R.id.deliver_line).setBackgroundColor(
						mContext.getResources().getColor(
								R.color.item_deliver_line));
			}
		}
		final ImageView sub_icon = (ImageView) convertView.findViewById(R.id.sub_icon);
		sub_icon.setImageResource(R.drawable.search_select_left_arrow);
		ImageView iconView = (ImageView) convertView.findViewById(R.id.icon);
		iconView.setImageResource(mMenuItem[position].drawableRid);
		((TextView) convertView.findViewById(R.id.main_text))
				.setText(mMenuItem[position].mainText);
		((TextView) convertView.findViewById(R.id.sub_text))
				.setText(mMenuItem[position].subText);
		return convertView;
	}

	public class MenuItem {
		public String mainText;
		public String subText;
		public int drawableRid;
		public String type;

		public MenuItem(String mainText, String subText, int drawableRid,
				String type) {
			this.mainText = mainText;
			this.subText = subText;
			this.drawableRid = drawableRid;
			this.type = type;
		}
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
	public String getTitleText() {
		return null;
	}

	public void showSearchDialog(final String cmbt){
		AlertDialog alertDialog = new AlertDialog.Builder(mContext)
		.create();
		alertDialog.setCancelable(true);
		alertDialog.setMessage("收听 "+cmbt +" ?");
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if(which == AlertDialog.BUTTON_POSITIVE){
					mContext.searchMainListByCmbt(cmbt,
							0, true, null);
					mContext.changeData(true, new ChangeDataAnimateCallBack() {
						@Override
						public void doChangeData() {
							mContext.getmViewManagerCenter().removeAllViewsAddNew(null);
							mListView.setVisibility(View.VISIBLE);
						}
					});
				}
			}
		};
		alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "确定",
				listener);
		alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "取消",
				listener);
		alertDialog.show();
	}
	
	private class SubListAdapter extends AbstractDragAdapter {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			showSearchDialog(""+((TextView)view.findViewById(R.id.main_text)).getText());
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
		}

		@Override
		public int getCount() {
			if (isBannerMode) {
				return 1 + (mBannerDetailsList == null ? 0 : mBannerDetailsList.size());
			}
			return 1 + (mBadgeList == null ? 0 : mBadgeList.size());
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
			// TODO Auto-generated method stub convertView = new View(mContext);
			if (position == 0) {
				convertView = new View(mContext);
				convertView.setMinimumHeight(JingTools.dip2px(mContext, 12));
			} else {
				if (convertView == null
						|| convertView.getTag() == null
						|| R.layout.right_menu_item != (Integer) convertView
								.getTag()) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.right_menu_item, null);
					convertView.setTag(Integer
							.valueOf(R.layout.right_menu_item));
				}
				String text,url;
				if (isBannerMode) {
					url = CustomerImageRule.ID2URL(
							Constants.ID2URL_KEY_WORD_ALBUM,
							mBannerDetailsList.get(position - 1).getAlbum_fid(), Constants.ID2URL_DEFAULT_BITRATE_ALBUM);
					text = mBannerDetailsList.get(position - 1).getCmbt();
				} else {
					url = CustomerImageRule.ID2URL(
							Constants.ID2URL_KEY_WORD_ALBUM,
							mBadgeList.get(position - 1).getFid(), Constants.ID2URL_DEFAULT_BITRATE_ALBUM);
					text = mBadgeList.get(position - 1).getN();
				}
				final ImageView iconImageView = ((ImageView) convertView
						.findViewById(R.id.icon));
				iconImageView.setBackgroundResource(R.drawable.badge_bg);
				int padding = JingTools.dip2px(mContext, 4);
				iconImageView.setPadding(padding, 0, 0, padding);
				if (JingTools.isValidString(url)
						&& !url.equals(iconImageView.getTag())) {
					iconImageView.setTag(url);
					iconImageView.setImageBitmap(null);
					AsyncImageLoader.getInstance().loadBitmapByUrl(url,
							AsyncImageLoader.IMAGE_TYPE_ORIGINAL,
							new ImageCallback() {
								@Override
								public void imageLoaded(Bitmap bitmap,
										String imageUrl) {
									if (imageUrl.startsWith(iconImageView
											.getTag().toString())) {
										iconImageView.setImageBitmap(bitmap);
									}
								}
							});
				}
				((TextView) convertView.findViewById(R.id.main_text))
						.setText(text);
				((TextView) convertView.findViewById(R.id.sub_text))
						.setText("收听适合" + text + "的音乐");
				if (position + 1 == getCount()) {
					convertView.findViewById(R.id.deliver_line)
							.setBackgroundColor(0);
				} else {
					convertView.findViewById(R.id.deliver_line)
							.setBackgroundColor(
									mContext.getResources().getColor(
											R.color.item_deliver_line));
				}
			}
			return convertView;
		}

		@Override
		public void setListView(DragRefreshListView listView) {
			listView.setDivider(mContext.getResources().getDrawable(
					R.drawable.draw_nothing));
			listView.setPullRefreshEnable(false);
			listView.setPullLoadEnable(false);
			listView.setAdapter(this);
			listView.setXListViewListener(this);
			listView.setOnItemClickListener(this);
			int padding = JingTools.dip2px(mContext, 12);
			listView.setPadding(padding, 0, padding, 0);
		}

		@Override
		public String getTitleText() {
			return null;
		}
	}
	
	private class SubListAdapterForNlg extends AbstractDragAdapter {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			switch (position) {
			case 0:
				KTC.rep("Exploring", "Play", "Banner");
			case 1:
				KTC.rep("Exploring", "Play", "BestSearch");
				break;
			case 2:
				KTC.rep("Exploring", "Play", "Chart");
				break;
			case 3:
				KTC.rep("Exploring", "Play", "Location");
				break;
			case 4:
				KTC.rep("Exploring", "Play", "Status");
				break;
			case 5:
				KTC.rep("Exploring", "Play", "Time");
				break;
			case 6:
				KTC.rep("Exploring", "Play", "Moods");
				break;
			case 7:
				KTC.rep("Exploring", "Play", "Genre");
				break;
			case 8:
				KTC.rep("Exploring", "Play", "Instruments");
				break;
			case 9:
				KTC.rep("Exploring", "Play", "Time");
				break;
			case 10:
				KTC.rep("Exploring", "Play", "Time");
				break;
			}
			showSearchDialog(""+((TextView)view.findViewById(R.id.main_text)).getText());
		}
		
		public void refreshData() {
			new Thread(){
				public void run() {
					HashMap<Object, Object> params = new HashMap<Object, Object>();
					params.put("st", "" + 0);
					params.put("ps", "" + mNumOfList);
					UserDTO usr = mContext.getmLoginData().getUsr();
					ResultResponse<ListResult<PrefixDTO>> rs;
					if (usr.isGuest()) {
						rs = UserAppRequestApi
								.fetchNtlg(params);
					}else{
						params.put("u", ""+usr.getId());
						rs = UserAutoSearchApi.fetchNtlg(params);
					}
					final ResultResponse<ListResult<PrefixDTO>> theRs = rs;
					mHandler.post(new Runnable() {
						public void run() {
							if (theRs != null && theRs.isSuccess()) {
								final ListResult<PrefixDTO> list = theRs.getResult();
								if (list == null || list.getItems().isEmpty()) {
									return;
								}
								mainItems.clear();
								for (PrefixDTO prefixDTO : list.getItems()) {
									mainItems.add(new MainItem(prefixDTO));
								}
								notifyDataSetChanged();
							}
						}
					});
				};
			}.start();
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
		}
		
		@Override
		public int getCount() {
			return 1 + (mainItems == null ? 0 : mainItems.size());
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
			// TODO Auto-generated method stub convertView = new View(mContext);
			if (position == 0) {
				convertView = new View(mContext);
				convertView.setMinimumHeight(JingTools.dip2px(mContext, 12));
			} else {
				position--;
				if (convertView == null 
						|| convertView.getTag() == null
						|| R.layout.right_menu_item != (Integer)convertView.getTag()) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.right_menu_item, null);
					convertView.setTag(Integer.valueOf(R.layout.right_menu_item));
				}
				final ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
				final String drawableUrl = mainItems.get(position).drawableUrl;
				if (JingTools.isValidString(drawableUrl)
						&& !drawableUrl.equals(imageView.getTag())) {
					imageView.setTag(drawableUrl);
					imageView.setImageBitmap(null);
					imageView.setBackgroundResource(R.drawable.badge_bg);
					int padding = JingTools.dip2px(mContext, 3);
					imageView.setPadding(padding, 0, 0, padding);
					AsyncImageLoader.getInstance().loadBitmapByUrl(drawableUrl,
							AsyncImageLoader.IMAGE_TYPE_ORIGINAL,
							new AsyncImageLoader.ImageCallback() {
						@Override
						public void imageLoaded(final Bitmap bitmap,
								final String imageUrl) {
							if (imageUrl.startsWith(drawableUrl)
									&& bitmap != null) {
								mHandler.post(new Runnable() {
									@Override
									public void run() {
										if (imageUrl.startsWith((String) imageView.getTag())) {
											imageView.setImageBitmap(bitmap);
										}
									}
								});
							}
						}
					});
				}
				((TextView) convertView.findViewById(R.id.main_text))
						.setText(mainItems.get(position).mainText);
				((TextView) convertView.findViewById(R.id.sub_text))
						.setText(mainItems.get(position).subText);
				if (position+2 == getCount()
						|| (position < mainItems.size() -1) && !mainItems.get(position).type.equals(mainItems.get(position+1).type)) {
					convertView.findViewById(R.id.deliver_line).setBackgroundColor(0);
				}else{
					convertView.findViewById(R.id.deliver_line).setBackgroundColor(
							mContext.getResources().getColor(R.color.item_deliver_line));
				}
			}
			return convertView;
		}
		
		@Override
		public void setListView(DragRefreshListView listView) {
			listView.setDivider(mContext.getResources().getDrawable(
					R.drawable.draw_nothing));
			listView.setPullRefreshEnable(false);
			listView.setPullLoadEnable(false);
			listView.setAdapter(this);
			listView.setXListViewListener(this);
			listView.setOnItemClickListener(this);
			int padding = JingTools.dip2px(mContext, 12);
			listView.setPadding(padding, 0, padding, 0);
		}
		
		@Override
		public String getTitleText() {
			return null;
		}
	}
	
}
