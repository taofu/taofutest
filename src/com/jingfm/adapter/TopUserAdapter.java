package com.jingfm.adapter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.ViewManager.ViewManagerCenter.LinkedViewData;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.UserRequestApi;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.LoginDataDTO;
import com.jingfm.api.model.UserFrequentDTO;
import com.jingfm.background_model.LocalCacheManager;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.AsyncImageLoader.ImageCallback;
import com.jingfm.tools.JingTools;

public class TopUserAdapter extends AbstractDragAdapter implements OnClickListener {

	protected static final int MSG_SET_NOTIFY = 110;

	protected static final int DEFAULT_BITMAP_WIDTH = 200;

	private MainActivity mContext;

	private List<UserFrequentDTO> mainList = new ArrayList<UserFrequentDTO>(12);

	private AsyncImageLoader asyncImageLoader;

	private Handler mHandler;

	private DragRefreshListView mListView;

	private UserHomePageAdapter mUserHomePageAdapter;

	private Bitmap mIconBackground;

	private int badgeWidth;

	private Bitmap mIconForeground;

	private int mDataCountNeedLoad;

	private Bitmap mIconDefault;

	private int dividerH;
	
	private boolean isNeedSave;

	public TopUserAdapter(MainActivity context) {
		this.mContext = context;
		initHandler();
		asyncImageLoader = AsyncImageLoader.getInstance();
		try {
			mainList = LocalCacheManager.getInstance().loadCacheData(mainList, TopUserAdapter.class.getName());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
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
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(false);
		mListView.setSelector(R.drawable.draw_nothing);
		mListView.setDividerHeight(0);
		mListView.setDrawingCacheEnabled(true);
		mListView.setFadingEdgeLength(0);
		mListView.setBackgroundColor(0);
		mListView.setDivider(null);
		mListView.setAdapter(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		mListView.setXListViewListener(this);
		if (mIconBackground == null) {
			InputStream is = mContext.getResources().openRawResource(
					R.drawable.top_user_icon_bg);
			mIconBackground = BitmapFactory.decodeStream(is);
			InputStream is1 = mContext.getResources().openRawResource(
					R.drawable.top_user_cover_mask);
			mIconForeground = BitmapFactory.decodeStream(is1);
			try {
				is.close();
				is1.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			int screenWidth = JingTools.getShowWidth(mContext);
			badgeWidth = screenWidth * 30/100;
			if (JingTools.getDeviceAspectRatio(mContext) == 1.5) {
				badgeWidth = (int) (badgeWidth*0.9f);
			}
			int width = badgeWidth -(int)(badgeWidth/75f*3f);
			int pH = ((View)mListView.getParent()).getHeight();
			int rowCount = pH/badgeWidth;
			mDataCountNeedLoad =  rowCount * 3;
			dividerH = (pH-(rowCount*badgeWidth))*100/(rowCount+2)/4/100;
			mListView.setDividerHeight(dividerH);
			mIconForeground = Bitmap.createScaledBitmap(mIconForeground,width, width, true);
			mIconBackground = Bitmap.createScaledBitmap(mIconBackground, badgeWidth, badgeWidth, true);
			mIconDefault = getShadowBitmap(Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_4444));
		}
		mListView.setPadding(0, dividerH*4, 0, 0);
		notifyDataSetChanged();
		mListView.invalidate();
	};
	
	private Bitmap getShadowBitmap(Bitmap bitmap) {
		int width = badgeWidth -(int)(badgeWidth/75f*3f);
		Bitmap tmpBitmap;
		try {
			tmpBitmap = Bitmap.createScaledBitmap(bitmap,width,width, true);
		} catch (Exception e) {
			tmpBitmap = bitmap;
		} catch (OutOfMemoryError e) {
			tmpBitmap = bitmap;
		}
		try {
			return AsyncImageLoader.mergeBitmap(mIconBackground, AsyncImageLoader.mergeBitmap(tmpBitmap,mIconForeground),
					(badgeWidth - width)/2,0) ;
		} catch (Exception e) {
			return tmpBitmap;
		} catch (OutOfMemoryError e) {
			return tmpBitmap;
		}
	}
	
	@Override
	public int getCount() {
		return mainList.size() / 3;
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
	
	static class ViewHolder{
		ImageView imageView_01;
		TextView textViewMain_01;
		TextView textViewSub_01;
		ImageView imageView_02;
		TextView textViewMain_02;
		TextView textViewSub_02;
		ImageView imageView_03;
		TextView textViewMain_03;
		TextView textViewSub_03;
	}

	@Override
	public View getView(int position,View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		if (convertView == null || convertView.getTag() == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.center_list_top_user_item, null);
			viewHolder = new ViewHolder();
			viewHolder.imageView_01  =  ((ImageView) convertView.findViewById(R.id.f_image));
			viewHolder.textViewMain_01 = ((TextView) convertView.findViewById(R.id.f_hour));
			viewHolder.textViewSub_01 = ((TextView) convertView.findViewById(R.id.f_name));
			viewHolder.imageView_02  =  ((ImageView) convertView.findViewById(R.id.m_image));
			viewHolder.textViewMain_02 = ((TextView) convertView.findViewById(R.id.m_hour));
			viewHolder.textViewSub_02 = ((TextView) convertView.findViewById(R.id.m_name));
			viewHolder.imageView_03  =  ((ImageView) convertView.findViewById(R.id.l_image));
			viewHolder.textViewMain_03 = ((TextView) convertView.findViewById(R.id.l_hour));
			viewHolder.textViewSub_03 = ((TextView) convertView.findViewById(R.id.l_name));
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		UserFrequentDTO data;
		convertView.setMinimumHeight(badgeWidth);
		data = mainList.get(position * 3 + 0);
		try {
			viewHolder.textViewMain_01.setText(""+(Integer.parseInt(data.getPt())/3600) +" Hours");
		} catch (Exception e) {
			viewHolder.textViewMain_01.setText("0" + " Hours");
		}
		viewHolder.textViewSub_01.setText(data.getNick());
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(badgeWidth, badgeWidth);
		layoutParams.gravity = Gravity.CENTER;
		final String url = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_COVER, data.getCover(),"CS");
		data.setImageUrl(url);
//		Log.e("kid_debug","CustomerImageRule.ID2URL url: " + url);
		final ImageView imageView_01 = viewHolder.imageView_01;
		if (!data.equals(imageView_01.getTag())) {
			imageView_01.setTag(data);
			imageView_01.setOnClickListener(this);
			imageView_01.setImageBitmap(mIconDefault);
			imageView_01.setLayoutParams(layoutParams);
			asyncImageLoader.loadUpdateBitmapByUrl(url,AsyncImageLoader.IMAGE_TYPE_ROUND_CORNER,
					new ImageCallback() {
	
						@Override
						public void imageLoaded(final Bitmap bitmap, final String imageUrl){
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									UserFrequentDTO dataTag = (UserFrequentDTO) imageView_01.getTag();
									if (dataTag != null && imageUrl.startsWith(dataTag.getImageUrl())) {
										imageView_01.setImageBitmap(getShadowBitmap(bitmap));
									}
								}
							});
						}
					});
		}
		data = mainList.get(position * 3 + 1);
		try {
			viewHolder.textViewMain_02.setText(""+(Integer.parseInt(data.getPt())/3600) +" Hours");
		} catch (Exception e) {
			viewHolder.textViewMain_02.setText("0" + " Hours");
		}
		viewHolder.textViewSub_02.setText(data.getNick());
		final String url2 = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_COVER, data.getCover(),"CS");
		data.setImageUrl(url2);
//		Log.e("kid_debug","CustomerImageRule.ID2URL url2: " + url2);
		final ImageView imageView_02 = ((ImageView) convertView.findViewById(R.id.m_image));
		if (!data.equals(imageView_02.getTag())) {
			imageView_02.setTag(data);
			imageView_02.setOnClickListener(this);
			imageView_02.setImageBitmap(mIconDefault);
			imageView_02.setLayoutParams(layoutParams);
			asyncImageLoader.loadUpdateBitmapByUrl(url2,AsyncImageLoader.IMAGE_TYPE_ROUND_CORNER,
					new ImageCallback() {

						@Override
						public void imageLoaded(final Bitmap bitmap, final String imageUrl) {
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									UserFrequentDTO dataTag = (UserFrequentDTO) imageView_02.getTag();
									if (bitmap != null && dataTag != null && imageUrl.startsWith(dataTag.getImageUrl())) {
										imageView_02.setImageBitmap(getShadowBitmap(bitmap));
									}
								}
							});
						}
					});
		}
		
		data = mainList.get(position * 3 + 2);
		try {
			viewHolder.textViewMain_03.setText(""+(Integer.parseInt(data.getPt())/3600) +" Hours");
		} catch (Exception e) {
			viewHolder.textViewMain_03.setText("0" + " Hours");
		}
		viewHolder.textViewSub_03.setText(data.getNick());
		final String url3 = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_COVER, data.getCover(),"CS");
		data.setImageUrl(url3);
		Log.e("kid_debug","CustomerImageRule.ID2URL url3: " + url3);
		final ImageView imageView_03 = ((ImageView) convertView.findViewById(R.id.l_image));
		if (!data.equals(imageView_01.getTag())) {
			imageView_03.setTag(data);
			imageView_03.setOnClickListener(this);
			imageView_03.setImageBitmap(mIconDefault);
			imageView_03.setLayoutParams(new FrameLayout.LayoutParams(badgeWidth, badgeWidth));
			imageView_03.setLayoutParams(layoutParams);
			asyncImageLoader.loadUpdateBitmapByUrl(url3,AsyncImageLoader.IMAGE_TYPE_ROUND_CORNER,
					new ImageCallback() {
						@Override
						public void imageLoaded(final Bitmap bitmap, final String imageUrl) {
							if (bitmap == null) {
								return;
							}
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									UserFrequentDTO dataTag = (UserFrequentDTO) imageView_03.getTag();
									if (dataTag != null && imageUrl.startsWith(dataTag.getImageUrl())) {
										imageView_03.setImageBitmap(getShadowBitmap(bitmap));
									}
								}
							});
						}
					});
		}
		return convertView;
	}

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
		getData();
	}

	@Override
	public void onLoadMore() {
		if (mListView != null) {
			mListView.stopLoadMore();
		}
	}

	public View initData(List list,DragRefreshListView listView) {
		if (listView == null) {
			listView = new DragRefreshListView(mContext);
		}
		setListView(listView);
		if (list == null || list.isEmpty()) {
			mainList.clear();
			notifyDataSetChanged();
			getData();
		}else{
			if (mainList != list) {
				mainList.clear();
				for (int i = 0; i < list.size(); i++) {
					mainList.add((UserFrequentDTO) list.get(i));
				}
			}
			notifyDataSetChanged();
		}
		mListView.setTag("最优听者");
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
		// uid ps
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("uid", "" + mContext.getUserId());
		map.put("ps", "" + mDataCountNeedLoad );
		new Thread() {
			public void run() {
				final ResultResponse<ListResult<UserFrequentDTO>> resultResponse = UserRequestApi
						.fetchFrequent(map);
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						if (resultResponse != null
								&& resultResponse.isSuccess()) {
							List<UserFrequentDTO> listResult = resultResponse
									.getResult().getItems();
							mainList.clear();
							for (UserFrequentDTO userFrequentDTO : listResult) {
								mainList.add(userFrequentDTO);
							}
							if(!isNeedSave){
								try {
									LocalCacheManager.getInstance().saveCacheData(mainList, TopUserAdapter.class.getName());
									isNeedSave = true;
								} catch (Exception e) {
								}
							}
							notifyDataSetChanged();
						} else {
							Toast.makeText(mContext, "加载失败", 0).show();
						}
						if (mListView != null) {
							mListView.stopRefresh();
						}
					}
				});
			};
		}.start();
	}

	public void refreshData(LoginDataDTO getmLoginData) {
		getData();
	}

	@Override
	public void onClick(View v) {
		UserFrequentDTO data = (UserFrequentDTO) v.getTag();
		if (mUserHomePageAdapter == null) {
			mUserHomePageAdapter = mContext.getmViewManagerLeft().getLeftViewAdapter().getmUserHomePageAdapter();
		}
		LinkedViewData oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.TOP_USERS,  null, new ArrayList(mainList));
		LinkedViewData newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.USER_HOME_PAGE,""+data.getUid(), null);
		mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData, newLinkedViewData);
	}

	@Override
	public String getTitleText() {
		// TODO Auto-generated method stub
		return null;
	}
}
