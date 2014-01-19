package com.jingfm.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.RecommendRequestApi;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.LoginDataDTO;
import com.jingfm.api.model.RecommendAppDTO;
import com.jingfm.background_model.LocalCacheManager;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.AsyncImageLoader.ImageCallback;
import com.jingfm.tools.JingTools;

public class RecommendAppAdapter extends AbstractDragAdapter{

	protected static final int MSG_SET_NOTIFY = 110;

	private Handler mHandler;

	private DragRefreshListView mListView;

	private MainActivity mContext;

	private int mIndexOnServer;

	private long mLastRefreshTime;

	private List<RecommendAppDTO> appList = new ArrayList<RecommendAppDTO>();

	private LoginDataDTO mLoginData;

	protected boolean isNeedSave;

	public RecommendAppAdapter(MainActivity context) {
		this.mContext = context;
		initHandler();
	}

	@Override
	public void setListView(DragRefreshListView listView) {
		mListView = listView;
		if (mListView == null) {
			return;
		}

//	    android:background="#FF343536"
	    mListView.setBackgroundColor(0xFF343536);
		mListView.setPullRefreshEnable(false);
		mListView.setPullLoadEnable(false);
		mListView.setSelector(R.drawable.draw_nothing);
		mListView.setDividerHeight(0);
		mListView.setDrawingCacheEnabled(true);
		mListView.setFadingEdgeLength(0);
//		mListView.setBackgroundColor(0);
		mListView.setAdapter(this);
		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		mListView.setXListViewListener(this);
		mListView.setCacheColorHint(0);
		notifyDataSetChanged();
		mListView.invalidate();
	};

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
	public int getCount() {
		return appList == null ? 0 : appList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public void notifyDataSetChanged() {
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mContext).inflate(
				R.layout.recomand_app_item, null);
		final ImageView app_icon = (ImageView) convertView
				.findViewById(R.id.music_cover);
		AsyncImageLoader.getInstance().loadBitmapByUrl(
				appList.get(position).getP(),
				AsyncImageLoader.IMAGE_TYPE_ORIGINAL, new ImageCallback() {

					@Override
					public void imageLoaded(final Bitmap bitmap, String imageUrl) {
						if (bitmap == null) {
							return;
						}
						mHandler.post(new Runnable() {

							@Override
							public void run() {
								try {
									Bitmap tmpBitmap = AsyncImageLoader.toRoundCorner(bitmap, bitmap.getWidth()*18/100);
									app_icon.setImageBitmap(tmpBitmap);
								} catch (Exception e) {
								}
							}
						});
					}
				});
		convertView.findViewById(R.id.music_cv_default).setBackgroundColor(0);
		convertView.findViewById(R.id.music_cv_mask).setBackgroundColor(0);
		TextView app_main_text = (TextView) convertView
				.findViewById(R.id.music_name);
		TextView app_sub_text = (TextView) convertView
				.findViewById(R.id.artist_name);
		app_main_text.setText(appList.get(position).getN());
		app_sub_text.setText(appList.get(position).getD());
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		try {
			position -= 1;
			Intent intent = new Intent(Intent.ACTION_VIEW);
			Uri uri = Uri.parse(appList.get(position).getL());
			intent.setData(uri);
			mContext.startActivity(intent);
		} catch (Exception e) {
			// TODO: handle exception
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
		mIndexOnServer = 0;
		getData();
	}

	@Override
	public void onLoadMore() {
		getData();
	}

	public void initData(LoginDataDTO loginData) {
		if (mLoginData == loginData) {
			return;
		}
		mLoginData = loginData;
		if (appList.isEmpty()) {
			getData();
		}
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
		// uid ouid st ps index
		final Map<Object, Object> map = new HashMap<Object, Object>();
		map.put("uid", "" + mLoginData.getUsr().getId());
		map.put("st", "" + mIndexOnServer);
		map.put("ps", "" + Constants.DEFAULT_NUM_OF_LOAD);
		new Thread() {
			public void run() {
				final ResultResponse<ListResult<RecommendAppDTO>> resultResponse = RecommendRequestApi
						.fetch(map);
				if (resultResponse != null
						&& resultResponse.isSuccess()) {
					List<RecommendAppDTO> list = resultResponse.getResult().getItems();
				}
				mHandler.post(new Runnable() {

					@Override
					public void run() {
						if (resultResponse != null
								&& resultResponse.isSuccess()) {
							List<RecommendAppDTO> listResult = resultResponse
									.getResult().getItems();
							if (mIndexOnServer <= 0) {
								appList.clear();
							}
							for (RecommendAppDTO recommendAppDTO : listResult) {
								appList.add(recommendAppDTO);
							}
							if (mListView != null) {
								mListView.stopRefresh();
								mListView.stopLoadMore();
								mListView.setPullLoadEnable(!(listResult.size() < Constants.DEFAULT_NUM_OF_LOAD));
							}
							if (mIndexOnServer <= 0) {
								notifyDataSetChanged();
								if (mListView != null) {
									mListView.invalidate();
								}
							}
							mIndexOnServer += listResult.size();
						} else {
							Toast.makeText(mContext, "加载失败", 0).show();
						}
					}
				});
			};
		}.start();
	}

	@Override
	public String getTitleText() {
		// TODO Auto-generated method stub
		return null;
	}

}
