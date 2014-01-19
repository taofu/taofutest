package com.jingfm.adapter;

import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;

import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.customer_views.DragRefreshListView.IXListViewListener;
import com.jingfm.customer_views.DragRefreshListView.OnXScrollListener;

public abstract class AbstractDragAdapter extends BaseAdapter implements OnItemClickListener,OnXScrollListener,IXListViewListener{
	public abstract void setListView(DragRefreshListView listView);
	public abstract String getTitleText();
}
