package com.jingfm.ViewManager;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.jingfm.MainActivity;
import com.jingfm.R;
import com.jingfm.adapter.LeftViewAdapter;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.customer_views.TouchScrollContainer;

public class ViewManagerLeft{
	private MainActivity mContext;
	private Handler mainUiHandle;
	private View mBaseView;
	private TouchScrollContainer mainLayout;
	private DragRefreshListView mSearchListView;
	private LeftViewAdapter leftViewAdapter;

	public ViewManagerLeft(MainActivity context,View baseView) {
		this.mContext = context;
		this.mBaseView = baseView;
		initView();
	}
	private void initHandler() {
		this.mainUiHandle = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		};
	}

	public TouchScrollContainer getMainLayout() {
		return mainLayout;
	}

	private void initView() {
		mainLayout = new TouchScrollContainer(mContext);
		View leftMenuView = LayoutInflater.from(mContext)
				.inflate(R.layout.left_menu, null);
		ListView listview = (ListView) leftMenuView
				.findViewById(R.id.list_view);
		leftViewAdapter = new LeftViewAdapter(
				mContext);
		listview.setSelector(R.drawable.draw_nothing);
		listview.setAdapter(leftViewAdapter);
		listview.setOnItemClickListener(leftViewAdapter);
		listview.setOverScrollMode(View.OVER_SCROLL_NEVER);
		mainLayout.addView(leftMenuView);
		mainLayout.setTagText("layout2");
		mainLayout.freeze();
		((ViewGroup) mBaseView.findViewById(R.id.main_view_container_02))
				.addView(mainLayout);
	}

	public LeftViewAdapter getLeftViewAdapter() {
		return leftViewAdapter;
	}
	
	public void tickerNotifyChange(int count){
		leftViewAdapter.tickerNotifyChange(count);
	}
	
	public void friendsNotifyChange(int count){
		leftViewAdapter.friendsNotifyChange(count);
	}
	public void clearTickerNotifyChange() {
		leftViewAdapter.clearTickerNotifyChange();
	}
}
