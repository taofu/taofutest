package com.jingfm.ViewManager;

import java.util.ArrayList;
import java.util.List;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextPaint;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.jingfm.MainActivity;
import com.jingfm.MainActivity.ChangeDataAnimateCallBack;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.Constants.KTC;
import com.jingfm.ViewManager.ChatViewManager.ChatUserData;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.model.MovinfoDTO;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.MusicInfoDTO;
import com.jingfm.api.model.TrackDTO;
import com.jingfm.api.model.UserFrdDTO;
import com.jingfm.background_model.SettingManager;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.customer_views.DragRefreshListView.IXListViewListener;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.JingTools;

public class MusicInfoViewManager extends BaseAdapter implements
		MusicInfoListener {
	private int mLastOnItemClickIndex;
	private MainActivity mContext;
	private Handler mHandler;
	private MusicInfoDTO mMusicInfoDTO;
	private MusicDTO mLastMusicDto;
	private FrameLayout mFirstItem;
	private TextView[] mDividerItems = new TextView[4];
	private ViewGroup mBaseContainer;
	private Animation mCloseAnimation;
	private Animation mShowAnimation;
	private TextView mMusicName;
	private ArrayList<MusicFollower> followListenerList = new ArrayList<MusicFollower>();
	private List<UserFrdDTO> mUserFrdDTOList = new ArrayList<UserFrdDTO>();
	private List<String> mMovieInfoList = new ArrayList<String>();
	protected List<TrackDTO> mTrackDtoList = new ArrayList<TrackDTO>();
	
	public MusicInfoViewManager(MainActivity context, ViewGroup viewGroup) {
		this.mContext = context;
		mBaseContainer = viewGroup;
		initHandler();
		initView();
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

	public void setMusicInfo(MusicInfoDTO dto) {
		if (mMusicInfoDTO != dto) {
			mUserFrdDTOList.clear();
			mMovieInfoList.clear();
			mMusicInfoDTO = dto;
			notifyDataSetChanged();
		}
	}

	private void initView() {
		final DragRefreshListView mListView = new DragRefreshListView(mContext);
		mListView.setAdapter(this);
		mListView.setDividerHeight(0);
		mListView.setPadding(0,
				JingTools.dip2px(mContext, 10), 0, 0);
		mListView.setPullRefreshVisable(false);
		mListView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
			}
		});
		mListView.setXListViewListener(new IXListViewListener() {
			@Override
			public void onRefresh() {
				mListView.stopRefresh();
			}

			@Override
			public void onLoadMore() {
				mListView.stopLoadMore();
			}
		});
		mListView.setPullLoadEnable(false);
		mBaseContainer.addView(mListView);
		mFirstItem = new FrameLayout(mContext);
		mMusicName = new TextView(mContext);
		ImageView button_close = new ImageView(mContext);
		button_close.setImageResource(R.drawable.button_close);
		TextPaint tp = mMusicName.getPaint();
		tp.setFakeBoldText(true);
		mFirstItem.addView(mMusicName);
		mFirstItem.addView(button_close);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_VERTICAL;
		mMusicName.setLayoutParams(lp);
		lp = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		lp.setMargins(0, 0, JingTools.dip2px(mContext, 30), 0);
		lp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		button_close.setLayoutParams(lp);
		View view = new View(mContext);
		view.setBackgroundColor(0xFFFF0000);
		view.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				closeView();
			}
		});
		lp = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT,
				FrameLayout.LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
		view.setLayoutParams(lp);
		mFirstItem.addView(view);
		mFirstItem.setMinimumHeight(JingTools.dip2px(mContext, 70));
		mDividerItems[0] = new TextView(mContext);
		mDividerItems[0].setText("正在跟听");
		mDividerItems[1] = new TextView(mContext);
		mDividerItems[1].setText("谁还喜欢了这首歌");
		mDividerItems[2] = new TextView(mContext);
		mDividerItems[2].setText("周边信息");
		mDividerItems[3] = new TextView(mContext);
		mDividerItems[3].setText("翻唱信息");
		for (int i = 0; i < mDividerItems.length; i++) {
			mDividerItems[i].setGravity(Gravity.CENTER_VERTICAL);
			tp = mDividerItems[i].getPaint();
			tp.setFakeBoldText(true);
			mDividerItems[i].setMinimumHeight(JingTools.dip2px(mContext, 30));
			mDividerItems[i].setBackgroundColor(0xA0222222);
		}
		mCloseAnimation = AnimationUtils.loadAnimation(mContext,
				R.anim.push_bottom_out);
		mShowAnimation = AnimationUtils.loadAnimation(mContext,
				R.anim.push_bottom_in);
		mCloseAnimation.setAnimationListener(new AnimationListener() {
			@Override
			public void onAnimationStart(Animation animation) {
			}
			@Override
			public void onAnimationRepeat(Animation animation) {
			}
			@Override
			public void onAnimationEnd(Animation animation) {
				mBaseContainer.clearAnimation();
				mBaseContainer.setVisibility(View.GONE);
			}
		});
	}

	public void showView() {
		KTC.rep("Home", "ClickInfoBtn", KTC.getMusicProgress());
		mBaseContainer.setVisibility(View.VISIBLE);
		mBaseContainer.startAnimation(mShowAnimation);
	}

	public void closeView() {
		mBaseContainer.startAnimation(mCloseAnimation);
	}

	@Override
	public void refreshMusicInfoDTO(final MusicDTO musicDto,
			final MusicInfoDTO dto, final List<UserFrdDTO> userFrdDTOList) {
		if (musicDto == null 
				|| dto == null) {
			return;
		}
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mLastMusicDto = musicDto;
				setMusicInfo(dto);
				checkMovInfo(dto.getMov_info());
				if (dto.getOrgn() != null) {
					mTrackDtoList = dto.getOrgn();
				}
				if (mLastMusicDto != null) {
					mMusicName.setText(mLastMusicDto.getN());
				}
				if (userFrdDTOList != null) {
					mUserFrdDTOList.clear();
					mUserFrdDTOList.addAll(userFrdDTOList);
				}
				notifyDataSetChanged();
			}
		});
	}
	
	/*@Override
	public void clearMusicInfoDTO() {
		mUserFrdDTOList.clear();
		mTrackDtoList.clear();
	}*/

	@Override
	public void notifyDataSetChanged() {
		cheackEmpty();
		super.notifyDataSetChanged();
	}
	
	public void cheackEmpty(){
		mContext.getmViewManagerCenter()
		.getmPlayingViewManager()
		.hideInfoButton(followListenerList.isEmpty()
				&& mUserFrdDTOList.isEmpty()
				&& mTrackDtoList.isEmpty()
				&& mMovieInfoList.isEmpty());
	}
	
	private synchronized void checkMovInfo(MovinfoDTO mov_info) {
		if (mov_info == null
				|| !SettingManager.getInstance().isNotifyLocationNEWS()) {
			return;
		}
		String tips = "";
		if (JingTools.isValidString(mov_info.getEnding_song())) {
			tips = "电影<" + mov_info.getEnding_song() + ">的" + "片尾曲" + "哦！";
		} else if (JingTools.isValidString(mov_info.getIn_song())) {
			tips = "电影<" + mov_info.getIn_song() + ">的" + "插曲" + "哦！";
		} else if (JingTools.isValidString(mov_info.getJingle())) {
			tips = "广告<" + mov_info.getJingle() + ">的" + "歌曲" + "哦！";
		} else if (JingTools.isValidString(mov_info.getOpening_song())) {
			tips = "电影<" + mov_info.getOpening_song() + ">的" + "片头曲" + "哦！";
		} else if (JingTools.isValidString(mov_info.getTheme_song())) {
			tips = "电影<" + mov_info.getTheme_song() + ">的" + "主题曲" + "哦！";
		}
		final String movieInfo = tips;
		mHandler.post(new Runnable() {
			@Override
			public void run() {
				mMovieInfoList.add(movieInfo);
				notifyDataSetChanged();
			}
		});
	}

	@Override
	public int getCount() {
		return mUserFrdDTOList.size() + mMovieInfoList.size() + followListenerList.size() + mTrackDtoList.size() + 5;
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
			convertView = mFirstItem;
			mFirstItem.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					closeView();
				}
			});
		} else if (position == 1) {
			convertView = followListenerList.isEmpty()?new View(mContext):mDividerItems[0];
		} else if (position == followListenerList.size()+2) {
			convertView = mUserFrdDTOList.isEmpty()?new View(mContext):mDividerItems[1];
		} else if (position == followListenerList.size() + mUserFrdDTOList.size()+3) {
			convertView = mMovieInfoList.isEmpty()?new View(mContext):mDividerItems[2];
		} else if (position == followListenerList.size() + mUserFrdDTOList.size() + mMovieInfoList.size()+4) {
			convertView = mTrackDtoList.isEmpty()?new View(mContext):mDividerItems[3];
		} else {
			if (convertView == null
					|| convertView.getTag() == null
					|| R.layout.music_info_item != (Integer) convertView
							.getTag()) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.music_info_item, null);
				convertView.setTag(Integer.valueOf(R.layout.music_info_item));
			}
			String mainText = "";
			String subText  = "";
			String imageUrl = "";
			final ImageView imageButton = (ImageView) convertView
					.findViewById(R.id.sub_icon);
			final ImageView imageButton01 = (ImageView) convertView
					.findViewById(R.id.sub_icon_01);
			imageButton.setVisibility(View.GONE);
			imageButton01.setVisibility(View.GONE);
			imageButton.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(final View v) {
					if (v.getTag() != null) {
						closeView();
						mContext.changeData(true, new ChangeDataAnimateCallBack() {
							
							@Override
							public void doChangeData() {
								mContext.getmViewManagerCenter()
										.removeAllViewsAddNew(mContext.getmViewManagerCenter()
										.getmChatViewManager().getView((ChatUserData) v.getTag()));
							}
						});
					}
				}
			});
			imageButton01.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (v.getTag() != null) {
						mContext.kickFollower(mContext.getUserId(), ""+v.getTag());
						for (int i = 0; i < followListenerList.size(); i++) {
							if (followListenerList.get(i).id.equals(v.getTag())) {
								followListenerList.remove(i);
								notifyDataSetChanged();
								break;
							}
						}
					}
				}
			});
			if (!followListenerList.isEmpty() && position < followListenerList.size() + 1 + 1) {
				position -= 2;
				MusicFollower data = followListenerList.get(position) ;
				mainText = data.nick;
				subText = data.id;
				imageUrl = CustomerImageRule.ID2URL(
						Constants.ID2URL_KEY_WORD_AVATAR, data.avatar,
						Constants.ID2URL_DEFAULT_BITRATE_AVATAR);
				imageButton.setVisibility(View.VISIBLE);
				imageButton01.setVisibility(View.VISIBLE);
				imageButton.setTag(new ChatUserData(data.id, data.nick,imageUrl));
				imageButton01.setTag(data.id);
			}else if (!mUserFrdDTOList.isEmpty() && position < followListenerList.size() + 1 + mUserFrdDTOList.size() + 1 + 1) {
				position -= 3 + followListenerList.size();
				UserFrdDTO data = mUserFrdDTOList.get(position);
				mainText = data.getNick();
				int time = 0;
				try {
					time = Integer.parseInt(data
							.getPt());
				} catch (Exception e) {
				}
				subText = "已经收听了" + mContext.onlineTimeToText(time);
				imageUrl = CustomerImageRule.ID2URL(
						Constants.ID2URL_KEY_WORD_AVATAR, data.getAvatar(),
						Constants.ID2URL_DEFAULT_BITRATE_AVATAR);
				imageButton.setVisibility(View.VISIBLE);
				imageButton.setTag(new ChatUserData(data.getUid(), data.getNick(),imageUrl));
				imageButton01.setVisibility(View.GONE);
			}else if (!mMovieInfoList.isEmpty() && position < followListenerList.size() + 1 + mUserFrdDTOList.size() + 1 + mMovieInfoList.size() + 1 + 1) {
				position -= 4 + followListenerList.size() + mUserFrdDTOList.size();
				mainText = mMovieInfoList.get(position);
				subText = "";
				imageUrl = CustomerImageRule.ID2URL(
						Constants.ID2URL_KEY_WORD_ALBUM, mLastMusicDto.getFid(),
						Constants.ID2URL_DEFAULT_BITRATE_ALBUM);
			}else if (!mTrackDtoList.isEmpty() && position <  followListenerList.size() + 1 + mUserFrdDTOList.size() + 1 + mMovieInfoList.size() + 1 + mTrackDtoList.size() + 1 + 1) {
				position -= 5 + followListenerList.size() + mUserFrdDTOList.size() + mMovieInfoList.size();
				mainText = mTrackDtoList.get(position).getN();
				subText = mTrackDtoList.get(position).getAtst() + "也演艺过这首歌曲哦";
				imageUrl = CustomerImageRule.ID2URL(
						Constants.ID2URL_KEY_WORD_ALBUM, mTrackDtoList.get(position).getFid(),
						Constants.ID2URL_DEFAULT_BITRATE_ALBUM);
			}
			final String drawableUrl = imageUrl;	
			final ImageView imageView = (ImageView) convertView
					.findViewById(R.id.icon);
			if (JingTools.isValidString(drawableUrl)
					&& !drawableUrl.equals(imageView.getTag())) {
				imageView.setTag(drawableUrl);
				imageView.setImageBitmap(null);
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
											if (imageUrl
													.startsWith((String) imageView
															.getTag())) {
												imageView
														.setImageBitmap(bitmap);
											}
										}
									});
								}
							}
						});
			}
			((TextView) convertView.findViewById(R.id.main_text)).setText(mainText);
			((TextView) convertView.findViewById(R.id.sub_text)).setText(subText);
		}
		convertView.setPadding(JingTools.dip2px(mContext, 20), 0, 0, 0);
		return convertView;
	}

	public boolean sendKeyEvent(KeyEvent event) {
		switch (event.getKeyCode()) {
		case KeyEvent.KEYCODE_BACK:
			if (mBaseContainer.getVisibility() != View.GONE) {
				closeView();
				return true;
			}
		}
		return false;
	}

	public void addFollower(String id, String nick, String avatar) {
		MusicFollower musicFollower = new MusicFollower(id,nick,avatar);
		if (!followListenerList.contains(musicFollower)) {
			followListenerList.add(musicFollower);
		}
		notifyDataSetChanged();
	}

	public static class MusicFollower {
		public String id;
		public String nick;
		public String avatar;
		public boolean selected;

		public MusicFollower(String id, String nick, String avatar) {
			this.id = id;
			this.nick = nick;
			this.avatar = avatar;
		}

		@Override
		public boolean equals(Object o) {
			if (o != null) {
				if (this != o) {
					if (o instanceof MusicFollower) {
						return this.id.equals(((MusicFollower) o).id);
					} else {
						return false;
					}
				} else {
					return true;
				}
			}
			return false;
		}

		@Override
		public int hashCode() {
			return this.id.hashCode();
		}
	}

	public void removeFollower(String id) {
		if (JingTools.isValidString(id)) {
			for (int i = 0; i < followListenerList.size(); i++) {
				if (id.equals(followListenerList.get(i).id)) {
					followListenerList.remove(i);
					break;
				}
			}
		}
	}


}
