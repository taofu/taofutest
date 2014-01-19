package com.jingfm.ViewManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.graphics.drawable.shapes.Shape;
import android.os.Handler;
import android.os.Message;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer.OnDrawerCloseListener;
import android.widget.SlidingDrawer.OnDrawerOpenListener;
import android.widget.SlidingDrawer.OnDrawerScrollListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.adapter.AbstractDragAdapter;
import com.jingfm.adapter.ConversationAdapter;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.UserChatRequestApi;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.LoginDataDTO;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.SiteMessageDTO;
import com.jingfm.api.model.message.UserOfflineMessageDTO;
import com.jingfm.api.model.socketmessage.SocketPChatDTO;
import com.jingfm.api.model.socketmessage.SocketPChatPayloadDTO;
import com.jingfm.api.model.socketmessage.SocketPChatPayloadShareTrackDTO;
import com.jingfm.api.model.socketmessage.SocketPChatPayloadType;
import com.jingfm.customer_views.CustomSlidingDrawer;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.customer_views.MyViewPager;
import com.jingfm.customer_views.NoScrollGridView;
import com.jingfm.customer_views.ResizeLayout;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.AsyncImageLoader.ImageCallback;
import com.jingfm.tools.EmojiManager;
import com.jingfm.tools.EmojiManager.EmojiData;
import com.jingfm.tools.JingTools;

public class ChatViewManager extends AbstractDragAdapter implements OnClickListener,ResizeLayout.ResizeListener {
	private MainActivity mContext;
	private View mChatView;
	private Handler mHandler;
	private HashMap<String, List<ChatItem>> mMessageMap = new HashMap<String, List<ChatItem>>();
	private HashMap<String, Integer> mHistoryMap = new HashMap<String, Integer>();
	private List<ChatItem> chatContent;
	private DragRefreshListView mListView;
	private ChatUserData mCurrentChatUserData;
	private EditText mEditText;
	private String mThisUserId;
	private String mThisAvatarUrl;
	private String mFirstTimeStamp;
	private long mLastTimeStamp;
	private boolean isLoading;
	private ResizeLayout resize_layout;
	private View send_button;
	private ConversationAdapter mConversationAdapter;

	public ChatViewManager(MainActivity context) {
		this.mContext = context;
		setupThisUser(mContext.getmLoginData());
		initHandler();
		initChatView();
		mConversationAdapter = new ConversationAdapter(context);
	}

	public void setupThisUser(LoginDataDTO loginData) {
		mThisUserId = "" + loginData.getUsr().getId();
		mThisAvatarUrl = CustomerImageRule.ID2URL(
				Constants.ID2URL_KEY_WORD_AVATAR, mContext.getmLoginData()
						.getUsr().getAvatar(),Constants.ID2URL_DEFAULT_BITRATE_AVATAR);
		getOfflineMessage(mContext.getmLoginData());
	}

	private void getOfflineMessage(final LoginDataDTO loginDataDTO) {
		new Thread(){
			public void run() {
				HashMap<Object, Object> params = new HashMap<Object, Object>();
				params.put("uid", "" + loginDataDTO.getUsr().getId());
				final ResultResponse<ListResult<UserOfflineMessageDTO>> rs = UserChatRequestApi.fetchPersonalOfflineMessage(params);
				if (rs != null && rs.isSuccess()) {
					mHandler.post(new Runnable() {
						
						@Override
						public void run() {
							List<UserOfflineMessageDTO> list = rs.getResult().getItems();
							//TODO
//							收到的是离线消息，但是不能走hasNewMessage接口，
//							所以需要创建一个模式可以参照已有的消息模式处理
//							mContext.getmViewManagerCenter().newMessageNotify(rs.getResult().getChats().size());
//							for (int i = 0; i < list.size(); i++) {
//								UserOfflineMessageDTO data = list.get(i);
//								SocketPChatDTO socketPChatDTO = new SocketPChatDTO();
//								socketPChatDTO.setCtt(data.getCmbt());
//								socketPChatDTO.setTs(data.getTs());
//								socketPChatDTO.setFuid(data.getFrd_id());
//								socketPChatDTO.setNick(data.getFrd());
//								mContext.getmViewManagerCenter().hasNewMessage(socketPChatDTO);
//							}
						}
					});
				}
			};
		}.start();
	}
	
	@Override
	public void setListView(DragRefreshListView listView) {
		mListView = listView;
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(false);
		mListView.setSelector(R.drawable.draw_nothing);
		mListView.setAdapter(this);
//		mListView.setDivider(mContext.getResources().getDrawable(R.drawable.draw_nothing));
//		mListView.setDividerHeight(JingTools.dip2px(mContext, 4));
		mListView.setXListViewListener(this);
		mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		notifyDataSetChanged();
		mListView.invalidate();
	};

	private void initChatView() {
		mChatView = LayoutInflater.from(mContext).inflate(
				R.layout.center_chat_view, null);
		resize_layout = (ResizeLayout) mChatView.findViewById(R.id.resize_layout);
		resize_layout.setResizeListener(this);
		mEditText = (EditText) mChatView.findViewById(R.id.chat_edit_text);
		mEditText.setOnEditorActionListener(new OnEditorActionListener() {

			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event) {
				if (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					actionId = v.getImeOptions();
				}
				switch (actionId) {
				case EditorInfo.IME_ACTION_SEND:
					sendMsg(mEditText.getText().toString());
					break;
				}
				return true;
			}
		});
		send_button = mChatView.findViewById(R.id.send_button);
		send_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				sendMsg(mEditText.getText().toString());
			}
		});
		View shell = mChatView.findViewById(R.id.chat_edit_shell);
		ViewGroup emoji_view_container = (ViewGroup)mChatView.findViewById(R.id.emoji_view_container);
		int width = JingTools.getShowWidth(mContext);
		int hight = JingTools.getShowWidth(mContext)/5*3;
		LayoutParams lay = shell.getLayoutParams();
		lay.width = width;
		lay.height = hight;
		shell.setLayoutParams(lay);
		final CustomSlidingDrawer customSlidingDrawer = (CustomSlidingDrawer) mChatView.findViewById(R.id.chat_edit_sliding_drawer);
		customSlidingDrawer.setTouchableIds(new int[] {R.id.send_button,R.id.chat_edit_text});
		customSlidingDrawer.setOnDrawerScrollListener(new OnDrawerScrollListener() {
			
			@Override
			public void onScrollStarted() {
				InputMethodManager imm = (InputMethodManager) mContext
						.getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(customSlidingDrawer.getWindowToken(), 0);
			}
			
			@Override
			public void onScrollEnded() {
				
			}
		});
		Button emoji_button = (Button) mChatView.findViewById(R.id.emoji_button);
		emoji_button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				mEditText.clearFocus();
				hideSoftKeyboard();
				customSlidingDrawer.animateToggle();
			}
		});
		customSlidingDrawer.setOnDrawerCloseListener(new OnDrawerCloseListener() {
			
			@Override
			public void onDrawerClosed() {
//				mListView.scrollBy(0, mEditText.getHeight() - customSlidingDrawer.getHeight());
				mChatView.findViewById(R.id.view_buttom_margin_close).setVisibility(View.VISIBLE);
				mChatView.findViewById(R.id.view_buttom_margin_open).setVisibility(View.GONE);
			}
		});
		customSlidingDrawer.setOnDrawerOpenListener(new OnDrawerOpenListener() {
			
			@Override
			public void onDrawerOpened() {
//				mListView.scrollBy(0, customSlidingDrawer.getHeight() - mEditText.getHeight());
				mChatView.findViewById(R.id.view_buttom_margin_close).setVisibility(View.GONE);
				mChatView.findViewById(R.id.view_buttom_margin_open).setVisibility(View.VISIBLE);
			}
		});
		mEditText.setOnFocusChangeListener(new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					mContext.startCacheAnimation(200,new AnimationListener(){

						@Override
						public void onAnimationStart(Animation animation) {
							mHandler.post(new Runnable() {
								
								@Override
								public void run() {
									customSlidingDrawer.close();
								}
							});
						}
						@Override
						public void onAnimationEnd(Animation animation) {
						}

						@Override
						public void onAnimationRepeat(Animation animation) {
						}
					});
				}
			}
		});
		MyViewPager viewPager = (MyViewPager) mChatView.findViewById(R.id.emoji_view_pager);
		viewPager.setDisallowInterceptView(mContext.getmViewManagerCenter()
				.getMainLayout());
		EmojiPagerAdapter pagerAdapter = new EmojiPagerAdapter(viewPager,emoji_view_container);
		viewPager.setAdapter(pagerAdapter);
		mListView = (DragRefreshListView) mChatView
				.findViewById(R.id.list_view);
		mListView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
//				hideSoftKeyboard();
				return false;
			}
		});
		setListView(mListView);
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				default:
					break;
				}
			}
		};
	}

	public View getView(ChatUserData chatUserData) {
		switchToUser(chatUserData);
		mContext.getmViewManagerCenter().clearNewChatMessage(chatUserData.getOuid());
		return mChatView;
	}
	
	public View getmChatView() {
		return mChatView;
	}

	@Override
	public int getCount() {
		return chatContent == null ? 0 : chatContent.size() + 2;
	}

	@Override
	public Object getItem(int position) {
		return chatContent == null ? null : chatContent.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (position == 0 || position == getCount() - 1) {
			View view = new View(mContext);
			view.setMinimumHeight(JingTools.dip2px(mContext, 12));
			return view;
		}
		position--;
		final ChatItem chatItem = chatContent.get(position);
		if (chatItem.socketPChatPayloadShareTrackDTO != null) {
			if (chatItem.uid.equals(mThisUserId)) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.chat_right_item_with_music, null);
			} else {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.chat_left_item_with_music, null);
			}
			MusicDTO musicDTO = chatItem.socketPChatPayloadShareTrackDTO.toMusicDTO();
			final ImageButton imageButton = (ImageButton)convertView.findViewById(R.id.image_button_play);
			String imageUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_ALBUM, musicDTO.getFid(),
					Constants.ID2URL_DEFAULT_BITRATE_ALBUM);
			AsyncImageLoader.getInstance().loadBitmapByUrl(imageUrl, AsyncImageLoader.IMAGE_TYPE_ORIGINAL, new ImageCallback() {
				
				@Override
				public void imageLoaded(final Bitmap bitmap, String imageUrl) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							imageButton.setBackgroundDrawable(new BitmapDrawable(bitmap));
						}
					});
				}
			});
			imageButton.setTag(musicDTO);
			imageButton.setOnClickListener(this);
			imageButton.setImageResource(R.drawable.ticker_play);
			if (mContext.isCurrentDtoPlaying(""+musicDTO.getTid()) 
					&& mContext.isPlaying()) {
				imageButton.setImageResource(R.drawable.ticker_pause);
			}
			((TextView) convertView.findViewById(R.id.ctt)).setText(chatItem.ctt);
			((TextView) convertView.findViewById(R.id.ctt_details)).setText(musicDTO.getAtn() + " - " + musicDTO.getAn());
		} else {
			if (chatItem.uid == null) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.chat_time_item, null);
					((TextView) convertView.findViewById(R.id.ctt)).setText(chatItem.ctt);
			} else {
				if (chatItem.uid.equals(mThisUserId)) {
						convertView = LayoutInflater.from(mContext).inflate(
								R.layout.chat_right_item, null);
						((ImageView) convertView.findViewById(R.id.ctt_emoji)).setBackgroundResource(R.drawable.emoji_message_bg_right);
				} else {
						convertView = LayoutInflater.from(mContext).inflate(
								R.layout.chat_left_item, null);
						((ImageView) convertView.findViewById(R.id.ctt_emoji)).setBackgroundResource(R.drawable.emoji_message_bg_left);
				}
				if (EmojiManager.isEmoji(chatItem.ctt)) {
					((TextView) convertView.findViewById(R.id.ctt)).setVisibility(View.GONE);
					convertView.findViewById(R.id.ctt_arrow).setVisibility(View.GONE);
					((ImageView) convertView.findViewById(R.id.ctt_emoji)).setVisibility(View.VISIBLE);
					((ImageView) convertView.findViewById(R.id.ctt_emoji)).setImageResource(EmojiManager.getInstance().getEmoji(chatItem.ctt).rid);
					((ImageView) convertView.findViewById(R.id.ctt_emoji)).setBackgroundColor(0);
				}else{
					((TextView) convertView.findViewById(R.id.ctt)).setVisibility(View.VISIBLE);
					convertView.findViewById(R.id.ctt_arrow).setVisibility(View.VISIBLE);
					((ImageView) convertView.findViewById(R.id.ctt_emoji)).setVisibility(View.GONE);
					((TextView) convertView.findViewById(R.id.ctt)).setText(chatItem.ctt);
				}
			}
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
		if (isLoading || mCurrentChatUserData == null) {
			return;
		}
		Integer index = mHistoryMap.get(mCurrentChatUserData.getOuid());
		if (index == null) {
			mHistoryMap.put(mCurrentChatUserData.getOuid(),Integer.valueOf(0));
			index = 0;
		}
		if (index < 0) {
			isLoading = false;
			mListView.stopRefresh();
			return;
		}
		final int indexOfServer = index;
		new Thread(){
			public void run() {
				getChatHistroy(mCurrentChatUserData.getOuid(),indexOfServer);
			}
		}.start();
	}

	private synchronized void getChatHistroy(final String fuid,final int index) {
		isLoading = true;
		HashMap<Object, Object> params = new HashMap<Object, Object>();
//				uid fuid st ps
		params.put("uid", mThisUserId);
		params.put("fuid", fuid);
		params.put("st", index);
		params.put("ps", Constants.DEFAULT_CHAT_HISTORY_OF_LOAD);
		final ResultResponse<ListResult<SiteMessageDTO>> rs = UserChatRequestApi.fetchChatctt(params);
		mHandler.post(new Runnable() {
			
			@Override
			public void run() {
				if (mListView != null) {
					mListView.stopRefresh();
				}
				if (mCurrentChatUserData != null && fuid.equals(mCurrentChatUserData.getOuid())) {
					if (rs != null && rs.isSuccess()) {
						List<SiteMessageDTO> list = rs.getResult().getItems();
						for (int i = 0; i < list.size(); i++) {
							doNeedTimeStampInHistory(fuid,list.get(i).getTs());
							mMessageMap.get(fuid).add(0,new ChatItem(list.get(i)));
							indexOfServerPlus(fuid);
						}
						if (list.size() < Constants.DEFAULT_CHAT_HISTORY_OF_LOAD) {
							mHistoryMap.put(fuid, Integer.valueOf(-1));
						}
						notifyDataSetChanged();
						if (index == 0) {
							mListView.smoothScrollToPosition(getCount() - 1);
						}
					}
				}
				isLoading = false;
			}
		});
	}

	protected void doNeedTimeStampInHistory(String fuid,String time) {
		ChatItem chatTime = new ChatItem(null,time, "");
		if (mFirstTimeStamp == null) {
			mFirstTimeStamp = JingTools.getDateString(System.currentTimeMillis());
		}
		try {
			String timeStampHour = mFirstTimeStamp.substring(0,mFirstTimeStamp.length()-5);
			if (!timeStampHour.equals(time.substring(0,time.length()-5))) {
				mMessageMap.get(fuid).add(0,chatTime);
				mFirstTimeStamp = time;
				return;
			}
			String timeStampMintues = mFirstTimeStamp.substring(mFirstTimeStamp.length()-5, mFirstTimeStamp.length()-7);
			String historyMintues = time.substring(time.length()-5, time.length()-7);
			if (Integer.parseInt(timeStampMintues) - Integer.parseInt(historyMintues) > 30) {
				mMessageMap.get(fuid).add(0,chatTime);
				mFirstTimeStamp = time;
				return;
			}
		} catch (Exception e) {
		}
	}

	@Override
	public void onLoadMore() {
		mListView.stopLoadMore();
	}

	private void switchToUser(ChatUserData chatUserData) {
		if (mCurrentChatUserData != null
				&& chatUserData != null
				&& mCurrentChatUserData.getOuid().equals(chatUserData.getOuid())) {
			return;
		}
		mCurrentChatUserData = chatUserData;
		mListView.stopRefresh();
		isLoading = false;
		getChatContent(chatUserData.getOuid());
		notifyDataSetChanged();
		mListView.setVisibility(View.GONE);
		mListView.setVisibility(View.VISIBLE);
		if (getCount() - 1 > 0) {
			mListView.smoothScrollToPosition(getCount() - 1);
		}
		if (chatContent.isEmpty()) {
			onRefresh();
		}
	}

	public List<ChatItem> getChatContent(String ouid){
		chatContent = mMessageMap.get(ouid);
		if (chatContent == null) {
			chatContent = new ArrayList<ChatItem>();
			mMessageMap.put(ouid, chatContent);
		}
		return chatContent;
	}
	
	public void hasNewMessage(final SocketPChatDTO socketPChatDTO) {
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				ChatUserData chatUserData = new ChatUserData(socketPChatDTO.getFuid(),socketPChatDTO.getNick(),socketPChatDTO.getFid());
				switchToUser(chatUserData);
				doNeedTimeStamp();
				ChatItem chatItem = new ChatItem(socketPChatDTO);
				getChatContent(""+socketPChatDTO.getFuid()).add(chatItem);
				notifyDataSetChanged();
				mListView.invalidate();
				mListView.setSelection(chatContent.size() - 1);
				indexOfServerPlus(chatUserData.getOuid());
				boolean needNotify = !mContext.getmViewManagerCenter().isTalkingToThisUser(chatUserData.getOuid());
				mConversationAdapter.addMessage(chatUserData,socketPChatDTO.getCtt(),socketPChatDTO.getTs(),socketPChatDTO.getPayload(),needNotify);
			}
		});
	}

	private void indexOfServerPlus(String fuid) {
		Integer index = mHistoryMap.get(fuid);
		if (index == null) {
			index = 0;
		}
		mHistoryMap.put(fuid, Integer.valueOf(index + 1));
	}

	protected void sendMsg(String ms) {
		if (ms == null || ms.trim().length() == 0) {
			return;
		}
		doNeedTimeStamp();
		if (ms.charAt(0) != 007) {
			ms = ms.trim();
		}
		mContext.sendMessage(mThisUserId, mCurrentChatUserData.getOuid(), ms);
		chatContent.add(new ChatItem(mThisUserId, ms, mThisAvatarUrl));
		mConversationAdapter.addMessage(mCurrentChatUserData,ms);
		mEditText.setText("");
//		hideSoftKeyboard();
		notifyDataSetChanged();
		mListView.invalidate();
		mListView.setSelection(chatContent.size() - 1);
		indexOfServerPlus(mCurrentChatUserData.getOuid());
	}

	private void doNeedTimeStamp() {
		long nowTime = System.currentTimeMillis();
		 if (mLastTimeStamp - nowTime > 1000*60*30) {
			 String dateTime = JingTools.getDateString(System.currentTimeMillis());
			 chatContent.add(new ChatItem(null, dateTime, null));
		 }else{
			 mLastTimeStamp = nowTime;
		 }
	}

	public void hideSoftKeyboard() {
		InputMethodManager imm = (InputMethodManager) mContext
				.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(mChatView.getWindowToken(), 0);
	}

	public class ChatItem {
		public String uid;
		public String ctt;
		public String imageUrl;
		public SocketPChatPayloadShareTrackDTO socketPChatPayloadShareTrackDTO;

		public ChatItem(SocketPChatDTO socketPChatDTO) {
			uid = socketPChatDTO.getFid();
			ctt = socketPChatDTO.getCtt();
			imageUrl = CustomerImageRule.ID2URL(
					Constants.ID2URL_KEY_WORD_AVATAR, socketPChatDTO.getFid(),Constants.ID2URL_DEFAULT_BITRATE_AVATAR);
			SocketPChatPayloadDTO socketPChatPayloadDTO = socketPChatDTO
					.getPayload();
			if (socketPChatPayloadDTO != null) {
				if (socketPChatPayloadDTO.getT() == SocketPChatPayloadType.SHARETRACK
						.getPrefix()) {
					// //歌曲分享为内容的聊天
					socketPChatPayloadShareTrackDTO = ((SocketPChatPayloadShareTrackDTO) socketPChatPayloadDTO);
				}
			}
		}
		
		public ChatItem(SiteMessageDTO siteMessageDTO) {
			if (siteMessageDTO.isSf()) {
				uid = mThisUserId;
				imageUrl = mThisAvatarUrl;
			}else{
				uid = mCurrentChatUserData.getOuid();
				imageUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_AVATAR,mCurrentChatUserData.getFid(),"UM");
			}
			ctt = siteMessageDTO.getCtt();
			// //歌曲分享为内容的聊天
			socketPChatPayloadShareTrackDTO = siteMessageDTO.getPayload();
		}
		
		public ChatItem(String uid, String ctt, String imageUrl) {
			this.uid = uid;
			this.ctt = ctt;
			this.imageUrl = imageUrl;
		}
	}

	public void shareMusic(final ChatUserData chatUserData, final SocketPChatDTO socketPChatDTO) {
		switchToUser(chatUserData);
		final SocketPChatPayloadDTO pay = socketPChatDTO.getPayload();
		if (pay == null) {
			return;
		}
		if (pay instanceof SocketPChatPayloadShareTrackDTO) {
			new Thread() {
				public void run() {
					HashMap<Object, Object> params = new HashMap<Object, Object>();
					// uid ouid tid
					params.put("uid", ""
							+ mContext.getUserId());
					params.put("ouid", "" + chatUserData.getOuid());
					params.put("ctt", "" + socketPChatDTO.getCtt());
					params.put("tid", "" + ((SocketPChatPayloadShareTrackDTO) pay).getTid());
					final ResultResponse<String> rs = UserChatRequestApi
							.postShareMusic(params);
					mHandler.post(new Runnable() {

						@Override
						public void run() {
							if (rs.isSuccess()) {
								Toast.makeText(mContext, "分享成功", 1).show();
							} else {
								Toast.makeText(mContext,
										"分享失败 code: " + rs.getCode()+" msg: " + rs.getCodemsg(), 1).show();
							}
						}
					});
				};
			}.start();
		}
		doNeedTimeStamp();
		ChatItem chatItem = new ChatItem(socketPChatDTO);
		chatItem.uid = mThisUserId;
		chatItem.imageUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_AVATAR, mContext.getmLoginData().getUsr().getAvatar(),Constants.ID2URL_DEFAULT_BITRATE_AVATAR);
		chatContent.add(chatItem);
		mEditText.setText("");
		hideSoftKeyboard();
		notifyDataSetChanged();
		mListView.invalidate();
		mListView.setSelection(chatContent.size() - 1);
	}
	
	public String getmCurrentFuid() {
		return mCurrentChatUserData == null? "":mCurrentChatUserData.getOuid();
	}
	
	@Override
	public void onClick(View v) {
		MusicDTO musicDTO = (MusicDTO) v.getTag();
		if (musicDTO != null) {
			if (mContext.isCurrentDtoPlaying(""+musicDTO.getTid())) {
				if (mContext.isPlaying()) {
					mContext.musicPause();
					((ImageButton)v).setImageResource(R.drawable.ticker_pause);
				}else{
					mContext.musicPlay();
					((ImageButton)v).setImageResource(R.drawable.ticker_play);
				}
			}else{
				ArrayList<MusicDTO> list = new ArrayList<MusicDTO>();
				list.add(musicDTO);
				mContext.setSubListTitle("收听分享");
				mContext.startNewSubList(list,Constants.UNKNOWN_M_VALUE,Constants.UNKNOWN_CMBT_VALUE, null);
				((ImageButton)v).setImageResource(R.drawable.ticker_pause);
			}
		}
	}

	@Override
	public String getTitleText() {
		return "消息中心";
	}
	
	@Override
	public void notifyDataSetChanged() {
		if (chatContent != null
				&& !chatContent.isEmpty()
				&& JingTools.isValidString(chatContent.get(0).uid)) {
			int lastInsertIndex = 0;
			for (int i = 0; i < chatContent.size(); i++) {
				if (!JingTools.isValidString(chatContent.get(i).uid)) {
					ChatItem chat = chatContent.get(i);
					chatContent.remove(i);
					chatContent.add(lastInsertIndex,chat);
					lastInsertIndex = i+1;
				}
			}
		}
		if (chatContent != null
				&& chatContent.size() > 2) {
			for (int i = 0; i < chatContent.size() - 1; i++) {
				if (!JingTools.isValidString(chatContent.get(i).uid)
						&& !JingTools.isValidString(chatContent.get(i+ 1).uid)) {
					chatContent.remove(i + 1);
				}
			}
		}
		
		super.notifyDataSetChanged();
	}
	
	public class EmojiPagerAdapter extends PagerAdapter{

		private EmojiData[] mArray;
		private GridView[] mGridViewArray;

		public EmojiPagerAdapter(final ViewPager viewPager, ViewGroup emoji_view_container) {
			mArray = EmojiManager.getInstance().getEmojiDataArray();
			final int itemCount = (mArray.length-30)/10 + (mArray.length%10 == 0 ? 0: 1);
			BaseAdapter baseAdapter = new BaseAdapter() {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					ImageView imageView = new ImageView(mContext);
					imageView.setAdjustViewBounds(true);
					if (parent.getTag() != null) {
						int itemIndex = (Integer) parent.getTag();
						int emojiIndex = itemIndex * 10 + position + 30;
						if (emojiIndex < mArray.length ) {
							imageView.setTag(mArray[emojiIndex]);
							imageView.setImageResource(mArray[emojiIndex].rid);
						}
					}
					imageView.setOnClickListener(new OnClickListener() {
						
						@Override
						public void onClick(View v) {
							if (v.getTag() == null) {
								return;
							}
							char[] cttData = ((EmojiData)v.getTag()).content.toCharArray();
							char[] data = new char[cttData.length + 2];
							data[0] = 7;
							for (int i = 0; i < cttData.length; i++) {
								data[i+1] = cttData[i];
							}
							data[data.length - 1 ] = 7;
							sendMsg(new String(data));
						}
					});
					return imageView;
				}
				@Override
				public long getItemId(int position) {return 0;}
				@Override
				public Object getItem(int position) {return null;}
				
				@Override
				public int getCount() {
					return 10;
				}
			};
			int spacing = JingTools.getShowWidth(mContext) * 5 /100;
			mGridViewArray = new GridView[itemCount];
			for (int i = 0; i < mGridViewArray.length; i++) {
				mGridViewArray[i] = new NoScrollGridView(mContext);
				LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
				mGridViewArray[i].setLayoutParams(layoutParams);
				mGridViewArray[i].setPadding(spacing,spacing/2, spacing, 0);
				mGridViewArray[i].setHorizontalSpacing(spacing);
				mGridViewArray[i].setVerticalSpacing(spacing);
				mGridViewArray[i].setTag(Integer.valueOf(i));
				mGridViewArray[i].setVerticalScrollBarEnabled(false);
				mGridViewArray[i].setNumColumns(5);
				mGridViewArray[i].setFadingEdgeLength(0);
				mGridViewArray[i].setSelector(R.drawable.draw_nothing);
				mGridViewArray[i].setAdapter(baseAdapter);
			}
			viewPager.setPadding(0, 0, 0, spacing);
			viewPager.getViewTreeObserver().addOnGlobalLayoutListener(
					new ViewTreeObserver.OnGlobalLayoutListener() {

						@Override
						public void onGlobalLayout() {
							if (mGridViewArray != null) {
								for (int i = 0; i < mGridViewArray.length; i++) {
//									mGridViewArray[i].setLayoutParams(new LayoutParams(viewPager.getWidth(),viewPager.getHeight()));
									mGridViewArray[i].setMinimumWidth(viewPager.getWidth());
									mGridViewArray[i].setMinimumHeight(viewPager.getHeight());
								}
							}
							viewPager.getViewTreeObserver()
									.removeGlobalOnLayoutListener(this);
						}
					});
			LinearLayout linearLayout = new LinearLayout(mContext);
			FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT);
			layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
			layoutParams.setMargins(0, 0, 0, JingTools.dip2px(mContext, 4));
			linearLayout.setLayoutParams(layoutParams);
			linearLayout.setHorizontalScrollBarEnabled(false);
			linearLayout.setOrientation(LinearLayout.HORIZONTAL);
			emoji_view_container.addView(linearLayout);
//			InputStream is = mContext.getResources().openRawResource(
//					R.drawable.emoji_bg);
//            BitmapDrawable draw = new BitmapDrawable(is);
//            draw.setTileModeX(TileMode.REPEAT);//横向平铺
//            draw.setTileModeY(TileMode.MIRROR);//纵向拉伸
//            emoji_view_container.setBackgroundDrawable(draw);
            emoji_view_container.setBackgroundColor(0xFF1E1E1E);
			final ShapeDrawable activeDrawable = new ShapeDrawable();
		    final ShapeDrawable inactiveDrawable = new ShapeDrawable();
		    int mIndicatorSize = JingTools.dip2px(mContext, 10);
		    activeDrawable.setBounds(0, 0, (int) mIndicatorSize,
		            (int) mIndicatorSize);
		    inactiveDrawable.setBounds(0, 0, (int) mIndicatorSize,
		            (int) mIndicatorSize);
		    int textColor[] = new int[2];
		    textColor[1] = mContext.getResources().getColor(R.color.white_sub);
		    textColor[0] = mContext.getResources().getColor(R.color.jing_green);
		    TypedArray a = mContext.getTheme().obtainStyledAttributes(textColor);
		    Shape s1 = new OvalShape();
		    s1.resize(mIndicatorSize, mIndicatorSize);
		    Shape s2 = new OvalShape();
		    s2.resize(mIndicatorSize, mIndicatorSize);
		    ((ShapeDrawable) activeDrawable).getPaint().setColor(
		            a.getColor(0, mContext.getResources().getColor(R.color.jing_green)));
		    ((ShapeDrawable) inactiveDrawable).getPaint().setColor(
		            a.getColor(1, Color.DKGRAY));
		    ((ShapeDrawable) activeDrawable).setShape(s1);
		    ((ShapeDrawable) inactiveDrawable).setShape(s2);
			final View[] pagePointArray = new View[itemCount];
			for (int i = 0; i < pagePointArray.length; i++) {
				View view = new View(mContext);
				LinearLayout.LayoutParams  layoutParamsPoint = new LinearLayout.LayoutParams(JingTools.dip2px(mContext, 10),JingTools.dip2px(mContext, 10));
				layoutParamsPoint.setMargins(JingTools.dip2px(mContext, 10), 0, JingTools.dip2px(mContext, 10), JingTools.dip2px(mContext, 2));
				view.setLayoutParams(layoutParamsPoint);
				view.setBackgroundDrawable(i == 0 ? activeDrawable : inactiveDrawable);
				linearLayout.addView(view);
				pagePointArray[i] = view;
			}
			viewPager.setOnPageChangeListener(new OnPageChangeListener() {
				
				@Override
				public void onPageSelected(int arg0) {
					changePagePoint(arg0);
				}
				
				@Override
				public void onPageScrolled(int arg0, float arg1, int arg2) {
//					changePagePoint(arg0);
				}
				
				@Override
				public void onPageScrollStateChanged(int arg0) {
//					changePagePoint(arg0);
				}
				
				private void changePagePoint(int arg0) {
					for (int i = 0; i < pagePointArray.length; i++) {
						if (i == arg0) {
							pagePointArray[i].setBackgroundDrawable(activeDrawable);
						}else{
							pagePointArray[i].setBackgroundDrawable(inactiveDrawable);
						}
					}
				}
			});
		}
		
		@Override
		public int getCount() {
			return mGridViewArray.length;
		}
		
		/** 
         * 从指定的position创建page 
         * 
         * @param container ViewPager容器 
         * @param position The page position to be instantiated. 
         * @return 返回指定position的page，这里不需要是一个view，也可以是其他的视图容器. 
         */  
        @Override  
        public Object instantiateItem(ViewGroup collection, int position) {
        		int w = mGridViewArray[position].getWidth();
        		int h = mGridViewArray[position].getHeight();
        		Log.e("kid_debug","w: " + w);
        		Log.e("kid_debug","h: " + h);
        		try {
            		collection.addView(mGridViewArray[position]);
			} catch (Exception e) {
			}
        		return mGridViewArray[position];
        }  
  
        /** 
         * <span style="font-family:'Droid Sans';">从指定的position销毁page</span> 
         *  
         *  
         *<span style="font-family:'Droid Sans';">参数同上</span> 
         */  
        @Override  
        public void destroyItem(View collection, int position, Object view) {  
//            ((ViewPager) collection).removeView(mListViews.get(position));  
        }  
		
        @Override  
        public void finishUpdate(View arg0) {}  
        @Override  
        public void restoreState(Parcelable arg0, ClassLoader arg1) {}  
        @Override  
        public Parcelable saveState() {return null;}  
        @Override  
        public void startUpdate(View arg0) {}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}  
	}

	@Override
	public void onResize(int w, int h, int oldw, int oldh) {
		if (w == 0 || oldw == 0) {
			return;
		}
		int offset = oldh - h;
		if (Math.abs(offset) > JingTools.dip2px(mContext, 10)) {
			if (offset < 0) {
				mEditText.clearFocus();
			}else{
				mListView.smoothScrollToPositionFromTop(getCount() -1, 0);
			}
		}
	}

	public AbstractDragAdapter getCoversationAdapter() {
		return mConversationAdapter;
	}

	public static class ChatUserData{
		private String ouid;
		private String nick;
		private String fid;
		
		public ChatUserData(String ouid, String nick, String fid) {
			this.ouid = ouid;
			this.nick = nick;
			this.fid = fid;
		}
		public String getOuid() {
			return ouid;
		}
		public void setOuid(String ouid) {
			this.ouid = ouid;
		}
		public String getNick() {
			return nick;
		}
		public void setNick(String nick) {
			this.nick = nick;
		}
		public String getFid() {
			return fid;
		}
		public void setFid(String fid) {
			this.fid = fid;
		}
	}
	
}
