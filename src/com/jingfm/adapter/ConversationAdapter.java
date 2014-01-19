package com.jingfm.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.graphics.Bitmap;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingfm.MainActivity;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.ViewManager.ChatViewManager.ChatUserData;
import com.jingfm.ViewManager.FriendsViewManager;
import com.jingfm.ViewManager.LoginStateChangeListener;
import com.jingfm.ViewManager.ViewManagerCenter.LinkedViewData;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.model.LoginDataDTO;
import com.jingfm.api.model.socketmessage.SocketPChatPayloadDTO;
import com.jingfm.background_model.LocalCacheManager;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.model.CoversationDTO;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.JingTools;

public class ConversationAdapter extends AbstractDragAdapter implements LoginStateChangeListener{

	private MainActivity mContext;

	private List<CoversationDTO> mainList = new ArrayList<CoversationDTO>();
	private Map<String,Integer> notifyMap = new HashMap<String,Integer>();
	private Handler mHandler;
	private DragRefreshListView mListView;

	private Integer mCurrentUid;

	public ConversationAdapter(MainActivity context) {
		this.mContext = context;
		context.addLoginListener(this);
		setListView(new DragRefreshListView(mContext));
		initHandler();
		initData();
	}
	
	private void initData() {
		if(mContext.getmLoginData() != null
				&& !mContext.getmLoginData().getUsr().isGuest()){
			mCurrentUid = mContext.getUserId();
			try {
				List tempList = LocalCacheManager.getInstance().loadCacheData(mainList, ConversationAdapter.class.getName() + mCurrentUid);
				for (int i = 0; i < tempList.size(); i++) {
					addData((CoversationDTO) tempList.get(i));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private void initHandler() {
		mHandler = new Handler();
	}
	
	@Override
	public void setListView(DragRefreshListView listView) {
		mListView = listView;
		mListView.setPullRefreshVisable(false);
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(false);
		mListView.setFastScrollEnabled(true);
		mListView.setSelector(R.drawable.draw_nothing);
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setDividerHeight(0);
		mListView.setDrawingCacheEnabled(true);
		mListView.setFadingEdgeLength(0);
		mListView.setBackgroundColor(0);
		mListView.setAdapter(this);
		mListView.setScrollBarStyle(0);
		mListView.setOnItemClickListener(this);
		mListView.setXListViewListener(this);
		int padding = JingTools.dip2px(mContext, 12);
		mListView.setPadding(padding, 0, padding, 0);
		notifyDataSetChanged();
		mListView.invalidate();
		mListView.setTag(getTitleText());
	};
	

	@Override
	public int getCount() {
		return mainList.size() + 2;
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
		if (position == 0 ) {
			convertView = new View(mContext);
			convertView.setMinimumHeight(JingTools.dip2px(mContext, 12));
		}else{
			position--;
			convertView = getConvertView(convertView);
			View deliver_line = convertView.findViewById(R.id.deliver_line);
			TextView mainTextView = (TextView) convertView
					.findViewById(R.id.main_text);
			TextView subTextView = (TextView) convertView
					.findViewById(R.id.sub_text);
			View notifyView = convertView
					.findViewById(R.id.new_message);
			final ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
			if (position == 0) {
				mainTextView.setText("查看好友列表");
				subTextView.setText("按照最近登录顺序查看好友列表");
				imageView.setTag("" + R.drawable.friends_list);
				imageView.setImageResource(R.drawable.friends_list);
				deliver_line.setBackgroundColor(0);
				notifyView.setVisibility(View.GONE);
			}else{
				position--;
				CoversationDTO data = mainList.get(position);
				Integer count = notifyMap.get(data.getOuid());
				notifyView.setVisibility(View.VISIBLE);
				if (count != null && count > 0) {
					notifyView.setVisibility(View.VISIBLE);
					notifyView.setBackgroundResource(R.drawable.online_round);
				}else{
					notifyView.setBackgroundResource(R.drawable.offline_round);
				}
				mainTextView.setText(data.getNick());
				subTextView.setText(data.getLastMessage());
				final String drawableUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_AVATAR, data.getFid(), Constants.ID2URL_DEFAULT_BITRATE_AVATAR);
				if (JingTools.isValidString(drawableUrl)
						&& !drawableUrl.equals(imageView.getTag())) {
					imageView.setTag(drawableUrl);
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
				if (deliver_line != null) {
					if (position == getCount() - 3) {
						deliver_line.setBackgroundColor(0);
					}else{
						deliver_line.setBackgroundColor(mContext.getResources().getColor(R.color.item_deliver_line));
					}
				}
			}
		}
		return convertView;
	}

	private View getConvertView(View convertView) {
		if (convertView == null 
				|| convertView.getTag() == null
				|| R.layout.conversation_item != (Integer)convertView.getTag()) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.conversation_item, null);
				convertView.setTag(Integer.valueOf(R.layout.conversation_item));
		}
		return convertView;
	}

	public void addMessage(ChatUserData chatUserData,String ctt){
		addMessage(chatUserData,ctt,System.currentTimeMillis(),null,false);
	}
	public void addMessage(ChatUserData chatUserData,String ctt,long time, SocketPChatPayloadDTO socketPChatPayloadDTO,boolean needNotify){
		CoversationDTO data = new CoversationDTO(chatUserData,ctt,time,socketPChatPayloadDTO);
		addData(data);
		if (needNotify) {
			Integer count = notifyMap.get(chatUserData.getOuid());
			if (count == null) {
				count = 0;
			}
			notifyMap.put(chatUserData.getOuid(), count + 1);
		}
	}
	
	private void addData(CoversationDTO data) {
		if (data == null
				|| !JingTools.isValidString(data.getOuid())) {
			return;
		}
		for (int i = 0; i < mainList.size(); i++) {
			CoversationDTO coversationDTO = mainList.get(i);
			if (JingTools.isValidString(coversationDTO.getOuid())) {
				if (data.getOuid().equals(coversationDTO.getOuid())) {
					if (data.getMessageTime() > coversationDTO.getMessageTime()) {
						mainList.remove(i);
						i--;
					}
				}
			}else{
				mainList.remove(i);
				i--;
			}
		}
		mainList.add(0,data);
		for (int i = 0; i < mainList.size(); i++) {
			for (int j = i+1; j < mainList.size(); j++) {
				if (mainList.get(i).getOuid().equals(mainList.get(j).getOuid())) {
					mainList.remove(j);
					j--;
				}
			}
		}
		notifyDataSetChanged();
		try {
			if (JingTools.isValidString(mCurrentUid)) {
				LocalCacheManager.getInstance().saveCacheData(mainList, ConversationAdapter.class.getName() + mCurrentUid);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		position -= 2;
		if (position < 0) {
			return;
		}else if (position == 0) {
			FriendsViewManager friendsViewManager = mContext.getmViewManagerCenter().getFriendsViewManager();
			friendsViewManager.onShowing(null,""+mContext.getUserId());
			LinkedViewData oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.CONVERSIATION, null,null);
			LinkedViewData newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.FRIEND_LIST_WITH_TITLE, null,null);
			mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData, newLinkedViewData);
		}else{
			position--;
			CoversationDTO data = mainList.get(position);
			notifyMap.put(data.getOuid(), 0);
			notifyDataSetChanged();
			mContext.getmViewManagerCenter().pushChatView(new ChatUserData(data.getOuid(), data.getNick(), data.getFid()));
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
		mListView.stopRefresh();
	}
	@Override
	public void onLoadMore() {
		mListView.stopLoadMore();
	}
	@Override
	public String getTitleText() {
		return "消息中心";
	}

	@Override
	public void onLogin(LoginDataDTO data) {
		initData();
	}

	@Override
	public void onLogout() {
		mainList.clear();
		mCurrentUid = null;
	}

}
