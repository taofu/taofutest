package com.jingfm.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.MainActivity.ChangeDataAnimateCallBack;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.ChartRequestApi;
import com.jingfm.api.model.ChartNodeDTO;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.LoginDataDTO;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.background_model.LocalCacheManager;
import com.jingfm.background_model.PlayerManager;
import com.jingfm.background_model.PlayerManager.PlayListOverListener;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.customer_views.ShowAllScrollGridView;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.AsyncImageLoader.ImageCallback;
import com.jingfm.tools.JingTools;

public class RankListAdapter extends AbstractDragAdapter {

	protected static final int MSG_SET_NOTIFY = 110;
	protected static final int DEFAULT_TEXT_BG = 0xFFF1F1F1;

	private List<ChartNodeDTO> mRankList = new ArrayList<ChartNodeDTO>();
	
	private MainActivity mContext;

	private Map<String, ListResult<ChartNodeDTO>> mResultResponseMap;
	
	private final String ROOT_ARGS = "0";
	
	private LoginDataDTO mLoginData;

	private Handler mHandler;

	private DragRefreshListView mListView;
	private HashMap<String, String> map = new HashMap<String, String>();

	private RankAdapter mRankAdapter;

	private int width;

	private boolean isNeedSave;
	private String mCurrentChartNodeDTO;

	public RankListAdapter(MainActivity context) {
		this.mContext = context;
		mLoginData = mContext.getmLoginData();
		map.put("uid", "" + mLoginData.getUsr().getId());
		initHandler();
		try {
			mRankList = LocalCacheManager.getInstance().loadCacheData(mRankList, RankListAdapter.class.getName());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		initData();
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
		if (getCount() == 0) {
			getData(ROOT_ARGS);
		}
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(false);
		mListView.setSelector(R.drawable.draw_nothing);
		mListView.setFastScrollEnabled(true);
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
	};

	@Override
	public int getCount() {
		return mRankList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	static class ViewHolder {
		TextView name;
		TextView num;
		ImageView cover;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (width == 0) {
			width = JingTools.getShowWidth(mContext);
		}
		convertView = LayoutInflater.from(mContext).inflate(R.layout.center_list_rank_item, null);
		TextView name = (TextView) convertView.findViewById(R.id.name);
		final ImageView cover = (ImageView) convertView.findViewById(R.id.cover);
		TextView periodical_num = (TextView) convertView.findViewById(R.id.periodical_num);
		LayoutParams layoutParams = new LayoutParams(width,width*2/3);
		cover.setLayoutParams(layoutParams);
		
		ChartNodeDTO chartNodeDTO = mRankList.get(position);
		name.setText("查看"+chartNodeDTO.getName()+"的榜单");
		periodical_num.setText(""+chartNodeDTO.getLastcount());
		String fid = chartNodeDTO.getFid();
		String imageUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_CHART_COVER, fid,Constants.ID2URL_DEFAULT_BITRATE_CHART_COVER_H);
		AsyncImageLoader.getInstance().loadBitmapByUrl(imageUrl, AsyncImageLoader.IMAGE_TYPE_ORIGINAL, new ImageCallback() {
			
			@Override
			public void imageLoaded(final Bitmap bitmap, String imageUrl) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (bitmap == null) {
							return;
						}
//						int dstHeight = bitmap.getHeight() * cover.getWidth()/bitmap.getWidth();
						try {
//							Bitmap tmpBitmap = AsyncImageLoader.toTopCorner(bitmap, cover.getWidth()*10/1000);
							cover.setImageBitmap(bitmap);	
						} catch (OutOfMemoryError e) {
						}
					}
				});
			}
		});
		return convertView;
	}
	

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (mContext.isOfflineMode()) {
			mContext.toastOffLine();
			return;
		}
		if (mRankAdapter == null) {
			mRankAdapter = new RankAdapter();
		}
		position -= 1;
		ChartNodeDTO chartNodeDTO = mRankList.get(position);
		ScrollView scrollView = new ScrollView(mContext);
		scrollView.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		scrollView.setFadingEdgeLength(0);
		LinearLayout linearLayout = new LinearLayout(mContext);
		linearLayout.setLayoutParams(new android.widget.LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT));
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		scrollView.addView(linearLayout);
		final ImageView imageView = new ImageView(mContext);
		imageView.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));
		linearLayout.addView(imageView);
		imageView.setScaleType(ScaleType.FIT_XY);
		imageView.setAdjustViewBounds(true);
		String imageUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_CHART_COVER, chartNodeDTO.getFid(),Constants.ID2URL_DEFAULT_BITRATE_CHART_COVER_H);
		AsyncImageLoader.getInstance().loadBitmapByUrl(imageUrl, AsyncImageLoader.IMAGE_TYPE_ORIGINAL, new ImageCallback() {
			
			@Override
			public void imageLoaded(final Bitmap bitmap, String imageUrl) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (bitmap == null) {
							return;
						}
						Bitmap rsBitmap = bitmap;
						if (bitmap.getWidth() < JingTools.getShowWidth(mContext)) {
							int hight = bitmap.getHeight() *JingTools.getShowWidth(mContext) /bitmap.getWidth();
							try {
								rsBitmap = Bitmap.createScaledBitmap(rsBitmap, JingTools.getShowWidth(mContext), hight, true);
							} catch (Exception e) {
							}
						}
						imageView.setImageBitmap(rsBitmap);	
					}
				});
			}
		});
		GridView gridView = new ShowAllScrollGridView(mContext);
		linearLayout.addView(gridView);
		mRankAdapter.setGridView(gridView);
		getDataPeriodical(chartNodeDTO.getFid(),chartNodeDTO.getChilds(),gridView);
		mContext.getmViewManagerCenter().pushNewView(scrollView,new Runnable() {
			@Override
			public void run() {
				mContext.setJingTitleText(mContext.getString(R.string.menu_6));
			}
		});
		mCurrentChartNodeDTO = chartNodeDTO.getTitle();
		mContext.setJingTitleText(mCurrentChartNodeDTO);
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
//		getData(ROOT_ARGS);
	}
	@Override
	public void onLoadMore() {
	}

	public void initData() {
		if (JingTools.isValidString(ROOT_ARGS)) {
			getData(ROOT_ARGS);
		}
	}
	public void getData(String nodeIDs) {
		map.put("nodeids",nodeIDs);
		new Thread() {
			public void run() {
				final ResultResponse<Map<String, ListResult<ChartNodeDTO>>> resultResponse = ChartRequestApi
						.fetchCharts(map);
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						if (resultResponse != null
								&& resultResponse.isSuccess() && resultResponse.getResult() != null) {
							mResultResponseMap = resultResponse.getResult();
							try {
								mRankList = mResultResponseMap.get(ROOT_ARGS).getItems();
								if(!isNeedSave && !mRankList.isEmpty()){
									try {
										LocalCacheManager.getInstance().saveCacheData(mRankList, RankListAdapter.class.getName());
										isNeedSave = true;
									} catch (Exception e) {
									}
								}
	//							JingTools.listReverse(mRankList);
								notifyDataSetChanged();
								if (mListView != null) {
									mListView.invalidate();
								}
							} catch (Exception e) {
								return;
							}
						} else {
							Toast.makeText(mContext, "加载失败", 0).show();
						}
					}
				});
			};
		}.start();
	}
	

	private void getDataPeriodical(final String fid, final List<ChartNodeDTO> yearNodes, final GridView gridView) {
		String nodeIDs = "";
		for (int i = 0; i < yearNodes.size(); i++) {
			if (i != 0) {
				nodeIDs+=",";
			}
			nodeIDs += yearNodes.get(i).getNext();
		}
		map.put("nodeids",nodeIDs);
		new Thread() {
			public void run() {
				final ResultResponse<Map<String, ListResult<ChartNodeDTO>>> resultResponse = ChartRequestApi
						.fetchCharts(map);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (resultResponse != null
								&& resultResponse.isSuccess()) {
							Map<String, ListResult<ChartNodeDTO>> periodicalMap = resultResponse.getResult();
							mRankAdapter.setFid(fid);
							mRankAdapter.setList(yearNodes);
							mRankAdapter.setMap(periodicalMap);
							mRankAdapter.notifyDataSetChanged();
							if (gridView != null) {
								gridView.invalidate();
							}
						} else {
							Toast.makeText(mContext, "加载失败", 0).show();
						}
					}
				});
			};
		}.start();
	}
	
	public class RankAdapter extends BaseAdapter implements OnClickListener{
		
		private static final int DEFAULT_NUM_COLUMNS = 3;
		private Map<String, ListResult<ChartNodeDTO>> mPeriodicalMap;
		private HashMap<Object, Object> params = new HashMap<Object, Object>();
		private List<ChartNodeDTO> yearList;
		private String mFid;
		private View mainView;
		private ImageView title_cover;
		protected ArrayList<ShowGridItemBean> mShowList =  new ArrayList<ShowGridItemBean>();
		private View mLastSelectedView;
		private boolean isLoading;

		public RankAdapter() {
			params.put("uid", ""+mLoginData.getUsr().getId());
			mainView = LayoutInflater.from(mContext).inflate(R.layout.center_rank_periodical_grid, null);
			title_cover = (ImageView) mainView.findViewById(R.id.title_cover);
		}
		
		public void setFid(String fid) {
			mFid = fid;
			String imageUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_COVER, fid);
			AsyncImageLoader.getInstance().loadBitmapByUrl(imageUrl, AsyncImageLoader.IMAGE_TYPE_ORIGINAL, new ImageCallback() {
				
				@Override
				public void imageLoaded(final Bitmap bitmap, String imageUrl) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							title_cover.setImageBitmap(bitmap);	
						}
					});
				}
			});
		}

		public void setGridView(GridView gridView) {
			gridView.setBackgroundColor(0);
			gridView.setSelector(R.drawable.draw_nothing);
			gridView.setNumColumns(DEFAULT_NUM_COLUMNS);
			gridView.setDrawingCacheEnabled(true);
			gridView.setFadingEdgeLength(0);
			gridView.setBackgroundColor(0xFFF1F1F1);
			gridView.setHorizontalSpacing(JingTools.dip2px(mContext, 1));
			gridView.setVerticalSpacing(JingTools.dip2px(mContext, 1));
			gridView.setAdapter(this);
		}

		public void setList(List<ChartNodeDTO> yearList){
			this.yearList = yearList;
		}
		
		public void setMap(Map<String, ListResult<ChartNodeDTO>> periodicalMap){
			this.mPeriodicalMap = periodicalMap ;
			makeList();
		}

		private void makeList() {
			mHandler.post(new Runnable() {
				
				@Override
				public void run() {
					if (yearList == null || mPeriodicalMap == null) {
						return;
					}
					mShowList.clear();
					for (int i = 0; i < yearList.size(); i++) {
						mShowList.add(new ShowGridItemBean(yearList.get(i).getName(),yearList.get(i).getName(),null));
						List<ChartNodeDTO> listNode = mPeriodicalMap.get(yearList.get(i).getNext()).getItems();
						if (listNode != null) {
							for (int j = 0; j < listNode.size(); j++) {
								mShowList.add(new ShowGridItemBean(yearList.get(i).getName(),listNode.get(j).getName(),listNode.get(j).getNext()));
							}
							while (mShowList.size()%3 != 0) {
								mShowList.add(new ShowGridItemBean(null,null,null));
							}
						}
					}
					notifyDataSetChanged();
				}
			});
		}

		@Override
		public int getCount() {
			return mShowList == null?0:mShowList.size();
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
			if (convertView == null) {
				convertView = new TextView(mContext);
				TextView textView = ((TextView)convertView);
				textView.setTextColor(0xff000000);
				textView.setGravity(Gravity.CENTER);
				textView.setMinHeight(JingTools.dip2px(mContext, 60));
				textView.setTextColor(0xFF666666);
				textView.setShadowLayer(1, 0, 1, 0xFF000000);
			}
			String name = mShowList.get(position).name; 
			String next = mShowList.get(position).next;
			convertView.setTag(mShowList.get(position));
			convertView.setOnClickListener(this);
			convertView.setBackgroundColor(0xFFFFFFFF);
			if (name == null) {
				((TextView)convertView).setText("");
			}else{
				if (name.startsWith("2")) {
					convertView.setBackgroundColor(0xFF2dcb73);
					((TextView)convertView).setTextColor(0xFF333333);
				}
				((TextView)convertView).setText(name);
			}
			return convertView;
		}

		@Override
		public void onClick(View v) {
			if (isLoading) {
				return;
			}
			final ShowGridItemBean itemBean = (ShowGridItemBean) v.getTag();
			if (itemBean == null) {
				return;
			}
			isLoading = true;
			if (mLastSelectedView != null) {
				if (mLastSelectedView instanceof TextView) {
					((TextView) mLastSelectedView).setTextColor(0xFF666666);
				}
				mLastSelectedView.setBackgroundColor(0xFFFFFFFF);
			}
			v.setBackgroundColor(0xFF2dcb73);
			if (v instanceof TextView) {
				((TextView) v).setTextColor(0xFF333333);
			}
			mLastSelectedView = v;
			if(!JingTools.isValidString(itemBean.next)){
				isLoading = false;
				return;
			}
			params.put("nodeids", itemBean.next);
			Toast.makeText(mContext, "正在获取音乐列表...", 1).show();
			new Thread(){
				public void run() {
					final ResultResponse<ListResult<MusicDTO>> resultResponse = ChartRequestApi.fetchChartMusic(params);
					
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if (resultResponse.isSuccess()) {
								randomPlayList(resultResponse.getResult().getItems(),resultResponse.getResult().getM());
							}else{
								Toast.makeText(mContext, "取列表失败", 0).show();
							}
							isLoading = false;
						}
						private void randomPlayList(final List<MusicDTO> items,final int M) {
							Collections.shuffle(items);
							mContext.startNewSubList(items,M,Constants.UNKNOWN_CMBT_VALUE,
									new PlayListOverListener() {
										@Override
										public void setIndexOfServer(int indexOfServer) {
										}
										@Override
										public void onPlayListOver(PlayerManager pm) {
											mHandler.post(new Runnable() {
												public void run() {
													randomPlayList(items,M);
												};	
											});
										}
										@Override
										public int getIndexOfServer() {
											return 0;
										}
									});	
							mContext.setSubListTitle(mCurrentChartNodeDTO +itemBean.year + itemBean.name);
							mContext.changeData(true, new ChangeDataAnimateCallBack() {
								
								@Override
								public void doChangeData() {
									mContext.getmViewManagerCenter().removeAllViewsAddNew(null);
								}
							});
						}
				});
				};
			}.start();
		}
	}
	
	public class ShowGridItemBean {
		public ShowGridItemBean(String year,String name, String next) {
			this.year = year;
			this.name = name;
			this.next = next;
		}
		public String year;
		public String name;
		public String next;
	}

	@Override
	public String getTitleText() {
		return "音乐榜单";
	}
}
