package com.jingfm.ViewManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.Constants.KTC;
import com.jingfm.ViewManager.ViewManagerRight.MainItem;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.UserAppRequestApi;
import com.jingfm.api.business.UserAutoSearchApi;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.NtlgPrefixDTO;
import com.jingfm.api.model.PrefixDTO;
import com.jingfm.api.model.UserDTO;
import com.jingfm.customer_views.BackImageButton;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.customer_views.DragRefreshListView.IXListViewListener;
import com.jingfm.third_part_api.VoiceCallbacker;
import com.jingfm.third_part_api.VoiceHelper;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.JingTools;


public class ViewManagerRight extends BaseAdapter implements OnItemClickListener, IXListViewListener {
	protected static final int MSG_REFRESH_NEW_BADGES = 10;
	protected static final int MSG_MAKE_POP = 11;
	protected static final int MSG_MAKE_POP_UNLOCK = 12;
	private static final long VOICE_ANIMATION_SCALE_DURATION = 1000;
	private MainActivity mContext;
	private Handler mHandler;
	private View mBaseView;
	private View mainLayout;
	private DragRefreshListView mSearchListView;
	private EditText mSearchText;
	private boolean isViewsLock;
	private BackImageButton back_buton;
	public boolean isAsyncTaskLock;
	private ArrayList<MainItem> mainItems = new ArrayList<MainItem>();
	private ArrayList<View> mItemTitleList = new ArrayList<View>();
	private View search_button;
	private boolean isRefreshing;
	protected int mNumOfList = 20;
	private SharedPreferences mSharedPreferences;
	private boolean isNeedResume;
	private View voice_search_animation_layout;
	private View voice_search_done_layout;
	private View[] mItemTitle;
	private boolean needCover;
	private boolean onShowMode;
	private static String[] types = new String[]{"tag","ats","sta","alike","frd","cmbt","title"};

	public ViewManagerRight(MainActivity context, View baseView) {
		this.mContext = context;
		this.mBaseView = baseView;
		initHandler();
		initView();
		initVoice();
	}
	private void initVoice() {
		mSharedPreferences = mContext.getSharedPreferences(mContext.getPackageName(),
				Context.MODE_PRIVATE);
		Editor editor = mSharedPreferences.edit();
		editor.putBoolean("com.iflytek.isr.showhelp",false);
		editor.commit();
	}
	
	private void quitVoiceUI() {
		if (isNeedResume && ! mContext.isPlaying()) {
			isNeedResume = false;
			mContext.musicPlay();
			voice_search_animation_layout.setVisibility(View.GONE);
			voice_search_done_layout.setVisibility(View.GONE);
		}
	}
	
	private void initHandler() {
		this.mHandler = new Handler();
	}

	private void initView() {
		mainLayout = LayoutInflater.from(mContext).inflate(
				R.layout.right_search_list, null);
		search_button = mainLayout.findViewById(R.id.search_button);
		search_button.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				if (mContext.isOfflineMode()) {
					mContext.toastOffLine();
					return true;
				}
				if (event.getAction() == MotionEvent.ACTION_DOWN) {
					Editable searchWord = mSearchText.getText();
					startSearch(searchWord.toString());
				}
				return true;
			}
		});
		mSearchText = (EditText) mainLayout
				.findViewById(R.id.search_text);
		mSearchText.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if (mSearchText.length() < 3) {
					return false;
				}
				if (keyCode == KeyEvent.KEYCODE_DEL) {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						try {
							String textTail = mSearchText.getText().toString().substring(mSearchText.getSelectionStart(),mSearchText.getText().length());
							String text = mSearchText.getText().toString().substring(0,mSearchText.getSelectionStart());
							int pst = text.lastIndexOf("+");
							if (pst == text.length() - 1) {
								pst = text.substring(0, pst).lastIndexOf("+");
								if (pst > 0) {
									mSearchText.setText(text.substring(0, pst + 1) + textTail);
									mSearchText.setSelection(mSearchText.length() - textTail.length());
								}else{
									mSearchText.setText("");
								}
								return true;
							}
						} catch (Exception e) {
						}
					}else{
						return true;
					}
				}
				return false;
			}
		});
		mSearchText.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView arg0, int arg1, KeyEvent arg2) {
				if (arg1 == 0 && arg2.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
					arg1 = arg0.getImeOptions();
				}
				if (arg1 == EditorInfo.IME_ACTION_SEARCH) {
					final String searchWord = mSearchText.getText().toString();
					if (!searchWord.isEmpty()) {
						startSearch(searchWord.toString());
					}
					return true;
				}
				return false;
			}
		});
		mSearchText.addTextChangedListener(new TextWatcher() {
			private Timer mAutoSearchTimer;

			@Override
			public void onTextChanged(CharSequence s, int start, int before,
					int count) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}

			@Override
			public void afterTextChanged(final Editable s) {
				if (s.length() == 0 || mContext.getmLoginData() == null) {
					return;
				}
				if (mAutoSearchTimer != null) {
					mAutoSearchTimer.cancel();
				}
				mAutoSearchTimer = new Timer();
				mAutoSearchTimer.schedule(new TimerTask() {

					@Override
					public void run() {
						String key = s.toString();
						int index = key.lastIndexOf('+');
						if (index > 0 && index + 1 < s.length()) {
							key = s.subSequence(index + 1,s.length()).toString();
							needCover = false;
						}else{
							needCover = true;
						}
						if (key.length() > 0 && !key.equals("+")) {
							getNtlgSatuoData(key);
						}
					}
				}, 100);
			}
		});
		back_buton = (BackImageButton) mainLayout
				.findViewById(R.id.right_view_back_buton);
		mSearchListView = (DragRefreshListView) mainLayout
				.findViewById(R.id.nlg_list_view);
		mSearchListView.setDivider(mContext.getResources().getDrawable(R.drawable.draw_nothing));
		mSearchListView.setPullLoadEnable(false);
		mSearchListView.setAdapter(this);
		mSearchListView.setXListViewListener(this);
		mSearchListView.setOnItemClickListener(this);
		mSearchListView.setPadding(JingTools.dip2px(mContext, 20), 0, JingTools.dip2px(mContext, 13), 0);
		mainLayout.setVisibility(View.GONE);
		((ViewGroup) mBaseView.findViewById(R.id.main_view_container_01))
			.addView(mainLayout);
		// mSearchListView.setPullRefreshEnable(false);
		mItemTitle = new View[6];
		int[] mItemTitleIconRes = new int[]{
				R.drawable.right_item_title_alike,
				R.drawable.right_item_title_ats,
				R.drawable.right_item_title_npersonal_music,
				R.drawable.right_item_title_search_select,
				R.drawable.right_item_title_sta,
				R.drawable.right_item_title_friend
				};
		String[] mItemTitleTextRes = new String[]{
				"相似歌曲",
				"艺人",
				"自然语言",
				"标签",
				"原声专辑",
				"好友"
		};
		String[] mItemSubtitleTextRes = new String[]{
				"按照相似歌曲进行细分",
				"按照艺人进行细分",
				"按照自然语言进行细分",
				"按照标签进行细分",
				"按照原声专辑进行细分",
				"按照好友进行细分"
		};
		for (int i = 0; i < mItemTitle.length; i++) {
			mItemTitle[i] = LayoutInflater.from(mContext).inflate(
					R.layout.right_menu_item_title, null);
			((ImageView) mItemTitle[i].findViewById(R.id.icon)).setImageResource(mItemTitleIconRes[i]);
			((TextView) mItemTitle[i].findViewById(R.id.title_text)).setText(mItemTitleTextRes[i]);
			((TextView) mItemTitle[i].findViewById(R.id.sub_text)).setText(mItemSubtitleTextRes[i]);
		}
	}
	
	@Override
	public void onRefresh() {
		if (onShowMode) {
			onShow();
		}else{
			refreshDefaultNtlg();
		}
	}
	
	@Override
	public void onLoadMore() {
		mSearchListView.stopLoadMore();
	}

	private void getNtlgSatuoData(String keyword) {
		if (mContext.getmLoginData() == null) {
			return;
		}
		isRefreshing = true;
		UserDTO usr = mContext.getmLoginData().getUsr();
		HashMap<Object, Object> params = new HashMap<Object, Object>();
		params.put("st", "" + 0);
		params.put("ps", "" + mNumOfList);
		ResultResponse<List<NtlgPrefixDTO>> rs = null;
		if (usr.isGuest()) {
			rs = UserAutoSearchApi
					.fetchNtlgSauto(params);
		}else{
			params.put("q", keyword);
			params.put("u", ""+usr.getId());
			rs = UserAutoSearchApi.fetchNtlgSauto(params);
		}
		final ResultResponse<List<NtlgPrefixDTO>> theRs = rs;
		mHandler.post(new Runnable() {
			public void run() {
				isRefreshing = false;
				mSearchListView.stopRefresh();
				if (theRs != null && theRs.isSuccess()) {
					final List<NtlgPrefixDTO> list = theRs.getResult();
					if (list == null || list.isEmpty()) {
						return;
					}
					mainItems.clear();
					mItemTitleList.clear();
					ArrayList<MainItem> itemsArt = new ArrayList<MainItem>();
					ArrayList<MainItem> itemsAlike = new ArrayList<MainItem>();
					ArrayList<MainItem> itemsCmbt = new ArrayList<MainItem>();
					ArrayList<MainItem> itemsTag = new ArrayList<MainItem>();
					ArrayList<MainItem> itemsSoundTrack = new ArrayList<MainItem>();
					ArrayList<MainItem> itemsOthers = new ArrayList<MainItem>();
					for (NtlgPrefixDTO ntlgPrefixDTO : list) {
							if (NtlgPrefixDTO.IsearchType.Artist.getName()
									.equals(ntlgPrefixDTO.getT())) {
								if (!mItemTitleList.contains(mItemTitle[1])) {
									mItemTitleList.add(mItemTitle[1]);
								}
								itemsArt.add(new MainItem(ntlgPrefixDTO));
							}else if (NtlgPrefixDTO.IsearchType.Alike.getName()
									.equals(ntlgPrefixDTO.getT())) {
								if (!mItemTitleList.contains(mItemTitle[0])) {
									mItemTitleList.add(mItemTitle[0]);
								}
								itemsAlike.add(new MainItem(ntlgPrefixDTO));
							}else if (NtlgPrefixDTO.IsearchType.Cmbt.getName()
									.equals(ntlgPrefixDTO.getT())) {
								if (!mItemTitleList.contains(mItemTitle[2])) {
									mItemTitleList.add(mItemTitle[2]);
								}
								itemsCmbt.add(new MainItem(ntlgPrefixDTO));
							}else if (NtlgPrefixDTO.IsearchType.SoundTrack.getName()
									.equals(ntlgPrefixDTO.getT())) {
								if (!mItemTitleList.contains(mItemTitle[4])) {
									mItemTitleList.add(mItemTitle[4]);
								}
								itemsSoundTrack.add(new MainItem(ntlgPrefixDTO));
							}else if (NtlgPrefixDTO.IsearchType.Tag.getName()
									.equals(ntlgPrefixDTO.getT())) {
								if (!mItemTitleList.contains(mItemTitle[3])) {
									mItemTitleList.add(mItemTitle[3]);
								}
								itemsTag.add(new MainItem(ntlgPrefixDTO));
							}else{
								if (!mItemTitleList.contains(mItemTitle[5])) {
									mItemTitleList.add(mItemTitle[5]);
								}
								itemsOthers.add(new MainItem(ntlgPrefixDTO));
							}
					}
					for (int i = 0; i < mItemTitleList.size(); i++) {
						for (int j = 0; j < mItemTitle.length; j++) {
							if (mItemTitle[j] == mItemTitleList.get(i)) {
								mainItems.add(new MainItem(mItemTitle[j]));
								switch (j) {
								case 0:
									mainItems.addAll(itemsAlike);
									break;
								case 1:
									mainItems.addAll(itemsArt);
									break;
								case 2:
									mainItems.addAll(itemsCmbt);
									break;
								case 3:
									mainItems.addAll(itemsTag);
									break;
								case 4:
									mainItems.addAll(itemsSoundTrack);
									break;
								case 5:
									mainItems.addAll(itemsOthers);
									break;
								}
								break;
							}
						}
					}
					notifyDataSetChanged();
				}
			}
		});
	}
	
	protected void refreshDefaultNtlg() {
		onShowMode = false;
		final Editable keyword = mSearchText.getText();
		if (keyword.length() > 0) {
			new Thread() {
				public void run() {
					getNtlgSatuoData(keyword.toString());
				};
			}.start();
		}
	}
	
	protected void startSearch(String searchWord) {
		if (JingTools.isValidString(searchWord)) {
			mContext.searchMainListByCmbt(searchWord,0,true,null);
			viewGoBack();
		}
	}

	public void setSearchText(String keyWord) {
		if (keyWord.endsWith("+")) {
			mSearchText.setText(keyWord);
		}else{
			mSearchText.setText(keyWord + "+");
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final TextView textView = ((TextView) view.findViewById(R.id.main_text));
		if (textView == null) {
			return;
		}
		try {
			position--;
			MainItem data = mainItems.get(position);
			if (data.equals(types[0])) {
				KTC.rep("SearchMenu", "UseRecommendedByTag", "");
			}else if (data.equals(types[1])) {
				KTC.rep("SearchMenu", "UseRecommendedByArtist", "");
			}else if (data.equals(types[2])) {
				KTC.rep("SearchMenu", "UseRecommendedByOST", "");
			}else if (data.equals(types[3])) {
				KTC.rep("SearchMenu", "UseRecommendedBySml", "");
			}else if (data.equals(types[4])) {
				KTC.rep("SearchMenu", "UseRecommendedByFriends", "");
			}else if (data.equals(types[5])) {
				KTC.rep("SearchMenu", "UseRecommendedByNtlg", "");
			}
		} catch (Exception e) {
		}
		final Object str = textView.getTag();
		if (JingTools.isValidString(str)) {
			KTC.rep("SearchMenu", "UseTextSearch", "");
			mContext.searchMainListByCmbt(str.toString(), 0, true, null);
			mHandler.post(new Runnable() {
				@Override
				public void run() {
					Toast.makeText(mContext, "正在搜索: " + str, 1).show();
				}
			});
			if (back_buton.getOnClickListener() != null) {
				back_buton.getOnClickListener().onClick(back_buton);
			}
		}
	}

	@Override
	public int getCount() {
		return mainItems.size() + 1;
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
			if (convertView == null 
					|| convertView.getTag() == null
					|| R.layout.right_menu_voice_search != (Integer)convertView.getTag()) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.right_menu_voice_search, null);
				convertView.setTag(Integer.valueOf(R.layout.right_menu_voice_search));
				((TextView) convertView.findViewById(R.id.main_text))
				.setText("用语言搜索你想听的音乐");
				((TextView) convertView.findViewById(R.id.sub_text))
						.setText("用语言搜索你想听的音乐");
				((ImageView) convertView.findViewById(R.id.icon)).setImageResource(R.drawable.voice);
				final View voice_search_text_layout = convertView.findViewById(R.id.voice_search_text_layout);
				voice_search_animation_layout = convertView.findViewById(R.id.voice_search_animation_layout);
				voice_search_done_layout = convertView.findViewById(R.id.voice_search_done_layout);
				View voice_search_round = voice_search_animation_layout.findViewById(R.id.voice_search_round);
				View voice_search_ring = voice_search_animation_layout.findViewById(R.id.voice_search_ring);
				ScaleAnimation roundPopAnimation = new ScaleAnimation(0.25f, 1.0f, 0.25f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
						0.5f);
				roundPopAnimation.setRepeatCount(Animation.INFINITE);
				roundPopAnimation.setRepeatMode(Animation.REVERSE);
				roundPopAnimation.setDuration(VOICE_ANIMATION_SCALE_DURATION);
				voice_search_round.startAnimation(roundPopAnimation);
				ScaleAnimation ringPopAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f,
						Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
						0.5f);
				ringPopAnimation.setRepeatCount(Animation.INFINITE);
				ringPopAnimation.setRepeatMode(Animation.REVERSE);
				ringPopAnimation.setDuration(VOICE_ANIMATION_SCALE_DURATION);
				voice_search_ring.startAnimation(ringPopAnimation);
				OnClickListener onClickListener = new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						if (v.equals(voice_search_text_layout)) {
							if (mContext.isPlaying()) {
								isNeedResume = true;
								mContext.musicPause();
							}
//							mRecognizerDialog.cancel();
//							mSynthesizerPlayer.cancel();
							voice_search_animation_layout.setVisibility(View.VISIBLE);
							voice_search_done_layout.setVisibility(View.VISIBLE);
							try {
								VoiceHelper.startVoiceRecognition(mContext);
								mHandler.postDelayed(new Runnable() {
									
									@Override
									public void run() {
										doCancelVoice();
									}
								}, 7000);
							} catch (Exception e) {
								doCancelVoice();
								Toast.makeText(mContext, "语音无法使用", 0).show();
							}
						}else if (v.equals(voice_search_animation_layout)) {
							doCancelVoice();
						}else if (v.equals(voice_search_done_layout)) {
							doStartVoice();
						}
					}
				};
				voice_search_text_layout.setOnClickListener(onClickListener);
				voice_search_animation_layout.setOnClickListener(onClickListener);
				voice_search_done_layout.setOnClickListener(onClickListener);
			}
			return convertView;
		}
		position--;
		final MainItem data = mainItems.get(position);
		if (types[6].equals(data.type)) {
			return data.view;
		}
		if (convertView == null 
				|| convertView.getTag() == null
				|| R.layout.right_menu_item != (Integer)convertView.getTag()) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.right_menu_item, null);
			convertView.setTag(Integer.valueOf(R.layout.right_menu_item));
		}
		ImageView imageViewAdd = (ImageView) convertView.findViewById(R.id.sub_icon_01);
		imageViewAdd.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				String text = "" + mSearchText.getText();
//				if (needCover || !text.contains("+")) {
//					text = "";
//					needCover = false;
//				}else{
//					
//				}
				if (!JingTools.isValidString(text)
						|| text.endsWith("+")) {
					mSearchText.setText(text + data.mainText.trim() + "+");
				}else{
					try {
						text = text.substring(0,text.lastIndexOf('+') + 1);
					} catch (Exception e) {
						text = "";
					}
					mSearchText.setText(text + data.mainText.trim() + "+");
				}
				mSearchText.setSelection(mSearchText.getText().length());
			}
		});
		final TextView textView = ((TextView) convertView.findViewById(R.id.main_text));
		if(types[5].equals(data.type)){
			imageViewAdd.setVisibility(View.GONE);
			textView.setPadding(0, 0, 0, 0);
		}else{
			imageViewAdd.setVisibility(View.VISIBLE);
			textView.setPadding(0, 0, JingTools.dip2px(mContext, 20), 0);
		}
		textView.setText(data.mainText);
		textView.setTag(data.keyWord);
		final ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
		final String drawableUrl = data.drawableUrl;
		if (JingTools.isValidString(drawableUrl)
				&& !drawableUrl.equals(imageView.getTag())) {
			imageView.setTag(drawableUrl);
			imageView.setImageBitmap(null);
			imageView.setBackgroundResource(R.drawable.badge_bg);
			int padding = JingTools.dip2px(mContext, 4);
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
		((TextView) convertView.findViewById(R.id.sub_text))
				.setText(data.subText);
		if (position+2 == getCount()
				|| (position < mainItems.size() -1) && !data.type.equals(mainItems.get(position+1).type)) {
			convertView.findViewById(R.id.deliver_line).setBackgroundColor(0);
		}else{
			convertView.findViewById(R.id.deliver_line).setBackgroundColor(
					mContext.getResources().getColor(R.color.item_deliver_line));
		}
		return convertView;
	}

	protected void doCancelVoice() {
		VoiceHelper.cancelVoiceRecognitionDiy();
		quitVoiceUI();
		voice_search_animation_layout.setVisibility(View.GONE);
		voice_search_done_layout.setVisibility(View.GONE);
	}
	protected void doStartVoice() {
		VoiceHelper.getInstance().stopVoiceRecognitionDiy(mContext, new VoiceCallbacker(){

			@Override
			public Object onCallBack(final Object obj) {
				if (obj != null) {
					KTC.rep("SearchMenu", "UseVoiceSearch", "");
					mContext.searchMainListByCmbt(obj.toString(), 0, true, null);
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							Toast.makeText(mContext, "正在搜索: " + obj, 1).show();
						}
					});
					back_buton.getOnClickListener().onClick(back_buton);
				}
				return null;
			}
			
		});
		quitVoiceUI();
		voice_search_animation_layout.setVisibility(View.GONE);
		voice_search_done_layout.setVisibility(View.GONE);
	}

	public static class MainItem {
		public String mainText;
		public String keyWord;
		public String subText;
		public String drawableUrl;
		public String type;
		public View view;

		public MainItem(String mainText, String subText, String drawableUrl,String type) {
			this.mainText = mainText;
			this.subText = subText;
			this.keyWord = mainText;
			this.drawableUrl = drawableUrl;
			this.type = type;
		}
		public MainItem(View view) {
			this.type = types[6];
			this.view = view;
		}
		public MainItem(NtlgPrefixDTO ntlgPrefixDTO) {
			this.mainText = ntlgPrefixDTO.getN();
			for(NtlgPrefixDTO.IsearchType type : NtlgPrefixDTO.IsearchType.values()){
				if (ntlgPrefixDTO.getT().equals(type.getName())) {
					this.subText = type.getSubtitle();
					break;
				}
			}
			if (NtlgPrefixDTO.IsearchType.Artist.getName().equals(ntlgPrefixDTO.getT())
					|| NtlgPrefixDTO.IsearchType.Tag.getName().equals(ntlgPrefixDTO.getT())) {
				this.drawableUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_ARTIST, ntlgPrefixDTO.getFid(), "SS");
			}else if (NtlgPrefixDTO.IsearchType.Alike.getName().equals(ntlgPrefixDTO.getT())
					|| NtlgPrefixDTO.IsearchType.Cmbt.getName().equals(ntlgPrefixDTO.getT())
					|| NtlgPrefixDTO.IsearchType.SoundTrack.getName().equals(ntlgPrefixDTO.getT())) {
				this.drawableUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_ALBUM, ntlgPrefixDTO.getFid(), "AS");
			}else if (NtlgPrefixDTO.IsearchType.Friend.getName().equals(ntlgPrefixDTO.getT())) {
				this.drawableUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_AVATAR, ntlgPrefixDTO.getFid(), "US");
			}
			this.type = ntlgPrefixDTO.getT();
			if (!JingTools.isValidString(type)) {
				type = types[5];
			}
			this.keyWord = mainText;
			if (types[3].equals(type)) {
				this.keyWord = "%" + ntlgPrefixDTO.getAn() + "的" + mainText;
			}else if (types[4].equals(type)) {
				this.keyWord = "@" + mainText;
			}
		}
		
		public MainItem(PrefixDTO prefixDTO) {
			this.mainText = prefixDTO.getN();
			this.subText = NtlgPrefixDTO.IsearchType.Cmbt.getSubtitle();
			this.drawableUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_ALBUM, prefixDTO.getFid(),"AS");
			this.type = types[5];
			this.keyWord = this.mainText;
		}
	}
	
	private void hideKeybroad() {
		((InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(mSearchText.getWindowToken(), 0);
	}

	public void viewGoBack() {
		hideKeybroad();
		back_buton.setOnClickListener(null);
		mContext.hideSearchView();
	}

	public boolean sendKeyEvent(KeyEvent event) {
		if (isViewsLock) {
			return true;
		}
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:
			if (back_buton.getOnClickListener() != null) {
				back_buton.getOnClickListener().onClick(back_buton);
				return true;
			}
			break;
		}
		return false;
	}

	public void setOffLine(boolean isOfflineMode) {
		mSearchText.setFocusable(!isOfflineMode);
		mSearchText.setFocusableInTouchMode(!isOfflineMode);
	}

	public boolean isShowing(){
		return getMainLayout().getVisibility() != View.GONE;
	}
	
	public void onShow() {
		onShowMode = true;
		back_buton.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				mSearchText.clearFocus();
				viewGoBack();
			}
		});
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
						isRefreshing = false;
						if (theRs != null && theRs.isSuccess()) {
							final ListResult<PrefixDTO> list = theRs.getResult();
							if (list == null || list.getItems().isEmpty()) {
								return;
							}
							mainItems.clear();
							mainItems.add(new MainItem(mItemTitle[2]));
							for (PrefixDTO prefixDTO : list.getItems()) {
								mainItems.add(new MainItem(prefixDTO));
							}
							mSearchListView.stopRefresh();
							notifyDataSetChanged();
						}
					}
				});
			};
		}.start();
	}

	public View getMainLayout() {
		return mainLayout;
	}

}
