package com.jingfm.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.MainActivity.ChangeDataAnimateCallBack;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.Constants.KTC;
import com.jingfm.ViewManager.ChatViewManager.ChatUserData;
import com.jingfm.ViewManager.ViewManagerCenter.LinkedViewData;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.UserFavoritesRequestApi;
import com.jingfm.api.business.UserFriendRequestApi;
import com.jingfm.api.business.UserMusicRequestApi;
import com.jingfm.api.business.UserPersonalRequestApi;
import com.jingfm.api.business.UserTickerRequestApi;
import com.jingfm.api.model.BannerDetailDTO;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.TickerDTO;
import com.jingfm.api.model.UserFrdDTO;
import com.jingfm.api.model.UserPersonalDataDTO;
import com.jingfm.api.model.UserStatDTO;
import com.jingfm.api.model.UserUnlockBadgeDTO;
import com.jingfm.api.model.favorites.FavoritesCmbtDTO;
import com.jingfm.background_model.LocalCacheManager;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.model.PersonalData;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.AsyncImageLoader.ImageCallback;
import com.jingfm.tools.JingTools;

public class UserHomePageAdapter extends AbstractDragAdapter {

	protected static final int MSG_SET_NOTIFY = 110;
	protected static final int MSG_SET_NEW_LIST = 111;
	protected static final int MSG_REFRESH_TIME = 112;
	private static final String FAV_TK_STRING = " 首喜欢的歌曲";
	private static final String FRD_STRING = " 个关注的人";
	private static final String BE_FRD_STRING_My = " 个关注我的人";
	private static final String BE_FRD_STRING_Other = " 个关注Ta的人";
	private static final int USER_HOME_PAGE_BACKGROUND_COLOR = 0xEF393939;

	private MainActivity mContext;

	private List<TickerDTO> mainList = new ArrayList<TickerDTO>();

	private AsyncImageLoader asyncImageLoader;

	private int width;

	public static MusicDTO musicDTO;

	public int playType;

	private int playingTid;

	private OnClickListener mLikeViewListener;

	private OnClickListener mShareViewListener;

	private Handler mHandler;

	private DragRefreshListView mListView;

	private int mServerIndex;

	private PersonalData mPersonalData = new PersonalData();
	private boolean isViewLocked;
	private long mLastRefreshTime;
	private String mOuid;
	private OnClickListener mPlayViewListener;
	private TextView mTimeText;
	private ImageView attention_image;
	
	private HashMap<String, PersonalData> mPersonalDataMap = new HashMap<String, PersonalData>();
	private OnClickListener mAttendListener;

	private boolean isNoNeedSave;
	private boolean isNoNeedSaveP;
	private OnClickListener onPersonalPlayListener;
	private View mHeadView;
	private ImageView playMyRadioBtn;
	private ImageView mMymusicCover;
	private ImageView mMyAvatarImage;
	private View mSwitchView;
	private OnClickListener onGridItemOnClickListener;
	private OnClickListener mChatButtonListener;
	private FrameLayout mBaseView;
	private PersonalTickerAdapter mPersonalTickerAdapter;
	private PersonalRadioAdapter mPersonalRadioAdapter;

	public UserHomePageAdapter(MainActivity context) {
		this.mContext = context;
		mBaseView = new FrameLayout(context);
		mMymusicCover = new ImageView(context);
		mMymusicCover.setScaleType(ScaleType.FIT_XY);
		mMymusicCover.setBackgroundColor(0xFF494949);
		mBaseView.addView(mMymusicCover);
		mMymusicCover.setLayoutParams(new FrameLayout.LayoutParams(JingTools.screenWidth,JingTools.screenWidth));
		mListView = new DragRefreshListView(context);
		mListView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT));
		mBaseView.addView(mListView);
		setupListView(mListView);
		initHandler();
		mPersonalTickerAdapter = new PersonalTickerAdapter();
		mPersonalRadioAdapter = new PersonalRadioAdapter();
		asyncImageLoader = AsyncImageLoader.getInstance();
		mAttendListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (isViewLocked) {
					return;
				}
				if (mContext.isOfflineMode()) {
					mContext.toastOffLine();
					return;
				}
				isViewLocked = true;
				new Thread(){
					public void run() {
						HashMap<Object, Object> params = new HashMap<Object, Object>();
						params.put("uid", mContext.getUserId());
						params.put("frdid", mOuid);
						ResultResponse tempRs;
						if (mPersonalData.attend) {
							tempRs = UserFriendRequestApi.unfollowFrd(params);
						}else{
							tempRs = UserFriendRequestApi.followFrd(params);
						}
						final ResultResponse rs = tempRs;
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								if (rs != null && rs.isSuccess()) {
									Object rsData = rs.getResult();
									if (rsData instanceof UserFrdDTO) {
										Toast.makeText(mContext, "关注成功", 1).show();
										mPersonalData.attend = true;
									}else{
										Toast.makeText(mContext, "取消关注成功", 1).show();
										mPersonalData.attend = false;
									}
									if (mPersonalData.attend) {
										attention_image.setImageResource(R.drawable.profile_right_corner_remove);
									}else{
										attention_image.setImageResource(R.drawable.profile_right_corner_add);
									}
								}else{
									Toast.makeText(mContext, "发送请求失败，请重试", 1).show();
								}
								isViewLocked = false;
							}
						});
					};
				}.start();
			}
		};
		mLikeViewListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mContext.isOfflineMode()) {
					mContext.toastOffLine();
					return;
				}
				final TickerDTO tickerDTO = (TickerDTO) v.getTag();
				ImageView imageView = ((ImageView) v
						.findViewById(R.id.fav_image));
				imageView.setScaleType(ScaleType.CENTER_INSIDE);
				if (!tickerDTO.isLoved()) {
					imageView.setImageResource(R.drawable.liked);
				} else {
					imageView.setImageResource(R.drawable.like);
				}
				switchTickerLove(tickerDTO);
			}
		};
		mShareViewListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				if (mContext.isOfflineMode()) {
					mContext.toastOffLine();
					return;
				}
				TickerDTO tickerDTO = (TickerDTO) v.getTag();
				// TODO shareView onClick
				if (tickerDTO == null) {
					return;
				}
				MusicDTO musicDTO = tickerDTO.toMusicDTO();
				mContext.showShareView(musicDTO);
			}
		};
		mPlayViewListener = new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if (mContext.isOfflineMode()) {
					mContext.toastOffLine();
					return;
				}
				TickerDTO tickerDTO = (TickerDTO) v.getTag();
				// TODO shareView onClick
				if (tickerDTO == null) {
					return;
				}
				if (mContext.isCurrentDtoPlaying(""+tickerDTO.getTid())) {
					if (mContext.isPlaying()) {
						mContext.musicPause();
					}else{
						mContext.musicPlay();
					}
					((ImageView)v).setImageResource(mContext.isPlaying()?R.drawable.ticker_pause:R.drawable.ticker_play);
				}else{
					ArrayList<MusicDTO> list = new ArrayList<MusicDTO>();
					list.add(tickerDTO.toMusicDTO());
					mContext.startNewSubList(list,Constants.UNKNOWN_M_VALUE,Constants.UNKNOWN_CMBT_VALUE, null);
					mContext.setSubListTitle("收听动态");
					((ImageView)v).setImageResource(R.drawable.ticker_pause);
					mContext.changeData(true, new ChangeDataAnimateCallBack() {
						
						@Override
						public void doChangeData() {
							mContext.getmViewManagerCenter().removeAllViewsAddNew(null);
						}
					});
				}
			}
		};
		onPersonalPlayListener = new OnClickListener() {
			
			@Override
			public void onClick(final View v) {
				if (mPersonalData == null || !JingTools.isValidString(mPersonalData.name)) {
					return;
				}
				if(mPersonalData.online){
					if (mContext.isPlayingPersonalRadio(mPersonalData.name)) {
						if (mContext.isPlaying()) {
							mContext.musicPause();
							((ImageView)v).setImageResource(R.drawable.profile_play_radio);
							return;
						}
					}
					if (mContext.isOfflineMode()) {
						mContext.toastOffLine();
						return;
					}
					if (mContext.isBeFollowedMode()) {
						Toast.makeText(mContext, "你的好友正在跟听你", 1).show();
						return;
					}
					if (mContext.isFollowingOther()) {
						Toast.makeText(mContext, "你正在跟听别人", 1).show();
						return;
					}
					if(mPersonalData != null){
						mContext.followListenRequest(mContext.getmLoginData()
									.getUsr().getId(), mPersonalData.uid,
									mPersonalData.name);
						return;
					}
					
				}
				if (mContext.isPlayingPersonalRadio(mPersonalData.name)) {
					if (mContext.isPlaying()) {
						mContext.musicPause();
						((ImageView)v).setImageResource(R.drawable.ticker_play);
					}else{
						mContext.musicPlay();
						((ImageView)v).setImageResource(R.drawable.ticker_pause);
					}
				}else{
					((ImageView)v).setImageResource(R.drawable.ticker_pause);
							mContext.changeData(true,
									new ChangeDataAnimateCallBack() {
										@Override
										public void doChangeData(){
											mContext.getmViewManagerCenter().removeAllViewsAddNew(null);
											mContext.playPersonalRadio(mPersonalData.name,((ImageView)v),R.drawable.ticker_play,R.drawable.ticker_pause);
										}
								});
				}
			}
		};
		mChatButtonListener = new OnClickListener() {

			@Override
			public void onClick(View v) {
				mContext.getmViewManagerCenter().pushChatView(new ChatUserData(mOuid, mPersonalData.name, mPersonalData.avatarUrl));
				String avatar = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_AVATAR,mPersonalData.avatarUrl);
			}
		};
	}
	
	protected void switchTickerLove(final TickerDTO tickerDTO) {
		if (mContext.isCurrentDtoPlaying(""+tickerDTO.getTid())) {
			mContext.musicFav(mContext.getUserId());
		}else{
			new Thread(){
				public void run() {
					HashMap<Object, Object> params = new HashMap<Object, Object>();
					params.put("uid", ""+mContext.getUserId());
					params.put("tid", tickerDTO.getTid());
					UserMusicRequestApi.musicLoveAct(params);
				};
			}.start();
		}
		tickerDTO.setLoved(!tickerDTO.isLoved());
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
				case MSG_REFRESH_TIME:
					removeMessages(MSG_REFRESH_TIME);
					if (mPersonalData == null || mTimeText == null) {
						return;
					}
					if (mPersonalData.onLineTime != 0) {
						mPersonalData.onLineTime++;
						mTimeText.setText(mContext.onlineTimeToText(mPersonalData.onLineTime));
					}
					sendEmptyMessageDelayed(MSG_REFRESH_TIME, 1000);
					break;
				}
			}
		};
	}
	
	@Override
	public void setListView(DragRefreshListView listView) {
	};
	
	public void setupListView(DragRefreshListView listView) {
		mListView = listView;
		if (mListView == null) {
			mHandler.removeMessages(MSG_REFRESH_TIME);
			return;
		}
		mListView.setPullRefreshVisable(false);
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(false);
		mListView.setFastScrollEnabled(true);
		mListView.setSelector(R.drawable.draw_nothing);
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
		if (mPersonalData == null) {
			return 6;
		}else{
			return 6 + (mPersonalData.userUnlockBadgeDTO == null ? 0 : mPersonalData.userUnlockBadgeDTO.size()); 
		}
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
		width = parent.getWidth();
		switch (position) {
		case 0: // head
			if (mHeadView == null) {
				mHeadView = LayoutInflater.from(mContext).inflate(
						R.layout.center_list_profile_head_view, null);
				View bottom_layout = mHeadView.findViewById(R.id.bottom_layout);
				bottom_layout.setBackgroundColor(USER_HOME_PAGE_BACKGROUND_COLOR);
				LayoutParams layoutParams = (FrameLayout.LayoutParams) bottom_layout.getLayoutParams();
				layoutParams.setMargins(0, JingTools.screenWidth*69/100, 0, 0);
				View avatar_layout = mHeadView.findViewById(R.id.avatar_layout);
				LayoutParams layoutParams2 = (FrameLayout.LayoutParams) avatar_layout.getLayoutParams();
				layoutParams2.setMargins(0, layoutParams.topMargin-(layoutParams2.height/2), 0, 0);
			}
			convertView = mHeadView;
			fillHeadView(convertView);
			break;
		case 1: // item1
			convertView = getConvertView(convertView);
			TextView music_num_text = (TextView) convertView
					.findViewById(R.id.main_text);
			((TextView) convertView.findViewById(R.id.sub_text)).setText("查看"+ (isMyHomePage()?"我":"Ta") + "所有喜欢的音乐");
			music_num_text.setText(mPersonalData.FavTk + FAV_TK_STRING);
			ImageView imageView1 = (ImageView) convertView.findViewById(R.id.icon);
			imageView1.setTag(""+R.drawable.user_home_page_fav_song);
			imageView1.setImageResource(R.drawable.user_home_page_fav_song);
			if (isMyHomePage() || mPersonalData.isFrdshp) {
				convertView.findViewById(R.id.deliver_line).setBackgroundColor(
						mContext.getResources().getColor(R.color.item_deliver_line));
			}else{
				convertView.findViewById(R.id.deliver_line).setBackgroundColor(0);
			}
			((ImageView) convertView.findViewById(R.id.sub_icon)).setImageResource(R.drawable.search_select_left_arrow);
			break;
		case 2: // item1
			if (isMyHomePage()) {
				convertView = getConvertView(convertView);
				((TextView) convertView.findViewById(R.id.main_text)).setText("收藏的条件");
				((TextView) convertView.findViewById(R.id.sub_text)).setText("查看我喜欢的搜索条件");
				ImageView imageView2 = (ImageView) convertView.findViewById(R.id.icon);
				imageView2.setTag(""+R.drawable.search_natural_language);
				imageView2.setImageResource(R.drawable.search_natural_language);
				convertView.findViewById(R.id.deliver_line).setBackgroundColor(0);
				((ImageView) convertView.findViewById(R.id.sub_icon)).setImageResource(R.drawable.search_select_left_arrow);
			}else{
				if (mPersonalData.isFrdshp) {
					convertView = getConvertView(convertView);
					((TextView) convertView.findViewById(R.id.main_text)).setText("和Ta聊天");
					((TextView) convertView.findViewById(R.id.sub_text)).setText("和好友聊天");
					ImageView imageView2 = (ImageView) convertView.findViewById(R.id.icon);
					imageView2.setTag(""+R.drawable.user_home_page_chat);
					imageView2.setImageResource(R.drawable.user_home_page_chat);
					convertView.findViewById(R.id.deliver_line).setBackgroundColor(0);
					((ImageView) convertView.findViewById(R.id.sub_icon)).setImageResource(R.drawable.search_select_left_arrow);
				}else{
					convertView = new View(mContext);
					convertView.setMinimumHeight(0);
				}
			}
			break;
		case 3: // item1
			convertView = getConvertView(convertView);
			TextView attention_num_text = (TextView) convertView
					.findViewById(R.id.main_text);
			((TextView) convertView.findViewById(R.id.sub_text)).setText("查看" + (isMyHomePage() ? "我" : "Ta") + "所有关注的人");
			attention_num_text.setText(mPersonalData.Frd + FRD_STRING);
			ImageView imageView3 = (ImageView) convertView.findViewById(R.id.icon);
			imageView3.setTag(""+R.drawable.user_home_page_att);
			imageView3.setImageResource(R.drawable.user_home_page_att);
			convertView.findViewById(R.id.deliver_line).setBackgroundColor(
					mContext.getResources().getColor(R.color.item_deliver_line));
			((ImageView) convertView.findViewById(R.id.sub_icon)).setImageResource(R.drawable.search_select_left_arrow);
			break;
		case 4: // item1
			convertView = getConvertView(convertView);
			TextView attentioned_num_text = (TextView) convertView
					.findViewById(R.id.main_text);
			attentioned_num_text.setText(mPersonalData.Befrd+ (isMyHomePage()?BE_FRD_STRING_My:BE_FRD_STRING_Other));
			((TextView) convertView.findViewById(R.id.sub_text)).setText("查看所有关注" + (isMyHomePage() ? "我" : "Ta") + "的人");
			ImageView imageView4 = (ImageView) convertView.findViewById(R.id.icon);
			imageView4.setTag(""+R.drawable.user_home_page_fans);
			imageView4.setImageResource(R.drawable.user_home_page_fans);
			convertView.findViewById(R.id.deliver_line).setBackgroundColor(
					mContext.getResources().getColor(R.color.item_deliver_line));
			((ImageView) convertView.findViewById(R.id.sub_icon)).setImageResource(R.drawable.search_select_left_arrow);
			break;
		case 5: // item1
			convertView = getConvertView(convertView);
			((TextView) convertView.findViewById(R.id.main_text)).setText((isMyHomePage()?"我":"Ta") + "的动态");
			((TextView) convertView.findViewById(R.id.sub_text)).setText("查看"+ (isMyHomePage()?"我":"Ta") + "的动态");
			ImageView imageView5 = (ImageView) convertView.findViewById(R.id.icon);
			imageView5.setTag(""+R.drawable.user_home_page_ticker);
			imageView5.setImageResource(R.drawable.user_home_page_ticker);
			convertView.findViewById(R.id.deliver_line).setBackgroundColor(0);
			((ImageView) convertView.findViewById(R.id.sub_icon)).setImageResource(R.drawable.search_select_left_arrow);
			break;
		default:
			convertView = getConvertView(convertView);
			UserUnlockBadgeDTO dataDto = mPersonalData.userUnlockBadgeDTO.get(position - 6);
			if (dataDto != null) {
				String subjectname = "Ta的";
				if(isMyHomePage()){
					subjectname = "我的";
				}
				((TextView) convertView.findViewById(R.id.main_text)).setText(subjectname+dataDto.getN());
				((TextView) convertView.findViewById(R.id.main_text)).setTag(dataDto.getN());
				((TextView) convertView.findViewById(R.id.sub_text)).setText(/*JingTools.getDateString(dataDto.getTime())*/"收听"+subjectname+dataDto.getN()+"电台");
				final ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
				((ImageView) convertView.findViewById(R.id.sub_icon)).setImageResource(R.drawable.item_could_play);
				final String drawableUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_ARTIST, dataDto.getFid(), "SM");;
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
			}
			if (position+1 == getCount()) {
				convertView.findViewById(R.id.deliver_line).setBackgroundColor(0);
			}else{
				convertView.findViewById(R.id.deliver_line).setBackgroundColor(
						mContext.getResources().getColor(R.color.item_deliver_line));
			}
			break;
		}
		ImageView iconView = (ImageView) convertView.findViewById(R.id.icon);
		if (iconView != null ) {
			if (position > 5) {
				int padding = JingTools.dip2px(mContext, 3);
				iconView.setPadding(padding, 0, 0, padding);
				iconView.setBackgroundResource(R.drawable.badge_bg);
			}else{
				iconView.setPadding(0, 0, 0, 0);
				iconView.setBackgroundColor(0);
			}
		}
		return convertView;
	}

	private View getConvertView(View convertView) {
		if (convertView == null 
				|| convertView.getTag() == null
				|| R.layout.user_home_page_item != (Integer)convertView.getTag()) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.user_home_page_item, null);
				convertView.setBackgroundColor(USER_HOME_PAGE_BACKGROUND_COLOR);
				convertView.setTag(Integer.valueOf(R.layout.user_home_page_item));
		}
		return convertView;
	}

	private void fillGridItem(int index, View imageview_shell, final ImageView imageview) {
		imageview_shell.setTag(mainList.get(index));
		imageview_shell.setOnClickListener(onGridItemOnClickListener);
		final String url = CustomerImageRule.ID2URL(
				Constants.ID2URL_KEY_WORD_ALBUM, mainList.get(index).getFid(),Constants.ID2URL_DEFAULT_BITRATE_ALBUM);
		if (!url.equals(imageview.getTag())) {
			imageview.setTag(url);
			final ImageView coverImageView = (ImageView) imageview;
			coverImageView.setImageResource(R.drawable.user_home_page_grid_mode_default_cover);
			asyncImageLoader.loadTempBitmapByUrl(url,
					AsyncImageLoader.IMAGE_TYPE_ORIGINAL, new ImageCallback() {
						@Override
						public void imageLoaded(final Bitmap bitmap, final String imageUrl) {
							if (bitmap == null) {
								return;
							}
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									if (imageUrl.startsWith((String) coverImageView.getTag())) {
										try {
											Bitmap tmpBitmap;
											if (imageview.getWidth() > 0 && imageview.getWidth() < bitmap.getWidth()) {
												tmpBitmap = Bitmap.createScaledBitmap(bitmap, imageview.getWidth(), imageview.getWidth(), false);
											}else{
												tmpBitmap = bitmap;
											}
											imageview.setImageBitmap(AsyncImageLoader.toRoundCorner(tmpBitmap,tmpBitmap.getWidth()*3/100));
										} catch (OutOfMemoryError e) {
											imageview.setImageBitmap(bitmap);
										}
									}
								}
							});
						}
					});
		}
	}

	private class ViewHolder{
		TextView music_name;
		TextView name_text;
		TextView time_text;
		TextView ticker_time;
		ImageView headViewImageView;
		ImageView playView;
		ImageView music_cover;
		ImageView shareView;
		ImageView likeView;
	}
	
	private View fillTickerView(View convertView, TickerDTO tickerDTO) {
		ViewHolder viewHolder;
		if (convertView != null 
				&& convertView.getTag() != null
				&& !(convertView.getTag() instanceof Boolean)) {
			viewHolder = (ViewHolder) convertView.getTag();
		}else{
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.center_list_ticker_item, null);
			viewHolder = new ViewHolder();
			viewHolder.music_name = (TextView) convertView.findViewById(R.id.music_name);
			viewHolder.name_text = (TextView) convertView.findViewById(R.id.name_text);
			viewHolder.time_text = (TextView) convertView.findViewById(R.id.time_text);
			viewHolder.ticker_time = (TextView) convertView.findViewById(R.id.ticker_time);
			viewHolder.headViewImageView = (ImageView) convertView
					.findViewById(R.id.headViewImageView);
			viewHolder.headViewImageView.layout(viewHolder.headViewImageView.getLeft(),
					viewHolder.headViewImageView.getTop(), viewHolder.headViewImageView.getRight(),
					viewHolder.headViewImageView.getRight());
			viewHolder.playView = (ImageView) convertView.findViewById(R.id.play_but);
			viewHolder.shareView = (ImageView) convertView.findViewById(R.id.share_image);
			viewHolder.likeView = (ImageView) convertView.findViewById(R.id.fav_image);
			viewHolder.music_cover = (ImageView) convertView.findViewById(R.id.music_cover);
			LayoutParams layoutParams2 = (LayoutParams) viewHolder.music_cover
					.getLayoutParams();
			if (layoutParams2.height != width) {
				layoutParams2.height = width;
				viewHolder.music_cover.setLayoutParams(layoutParams2);
			}
			convertView.setTag(viewHolder);
		}
		viewHolder.music_name.setText(tickerDTO.getAtn() + " - " + tickerDTO.getTit());
		viewHolder.name_text.setText(tickerDTO.getNick());
        Integer seconds = mPersonalData.onLineTime;
		viewHolder.time_text.setText("已经收听了 " + JingTools.formatSeconds(seconds == null ? 0 : seconds));
		Long tickerseconds = Math.abs((System.currentTimeMillis() - tickerDTO.getTs()))  / 1000;
		viewHolder.ticker_time.setText(JingTools.formatSecondsShort(tickerseconds == null ? 0 : tickerseconds));
		final String url = CustomerImageRule.ID2URL(
				Constants.ID2URL_KEY_WORD_AVATAR, tickerDTO.getAvt());
		if (!url.equals(viewHolder.headViewImageView.getTag())) {
			viewHolder.headViewImageView.setTag(url);
			final ImageView headViewImageView = viewHolder.headViewImageView;
			if (mContext.isMyUrlUpdata(url)) {
				Bitmap bitmap = mContext.getAvatarAfterChange();
				headViewImageView.setImageBitmap(AsyncImageLoader.toRoundCorner(bitmap, bitmap.getWidth()*15/100));
			}else{
				headViewImageView.setImageBitmap(null);
				asyncImageLoader.loadTempBitmapByUrl(url,
						AsyncImageLoader.IMAGE_TYPE_ORIGINAL, new ImageCallback() {

							@Override
							public void imageLoaded(final Bitmap bitmap, final String imageUrl) {
								if (bitmap == null) {
									AsyncImageLoader.getInstance().loadTempBakBitmapByUrl(url, AsyncImageLoader.IMAGE_TYPE_ORIGINAL, new ImageCallback() {
										
										@Override
										public void imageLoaded(final Bitmap bitmap,final String imageUrl) {
											mHandler.post(new Runnable() {
												@Override
												public void run() {
													if (imageUrl.startsWith((String) headViewImageView.getTag())) {
//														headViewImageView.setImageBitmap(AsyncImageLoader.toRoundCorner(bitmap, bitmap.getWidth()*15/100));
														headViewImageView.setImageBitmap(bitmap);
													}
												}
											});
										}
									});
								}else{
									mHandler.post(new Runnable() {
										@Override
										public void run() {
											if (imageUrl.startsWith((String) headViewImageView.getTag())) {
												headViewImageView.setImageBitmap(bitmap);
											}
										}
									});
								}
							}
						});
			}
		}
			
		String url2 = CustomerImageRule.ID2URL(
				Constants.ID2URL_KEY_WORD_ALBUM, tickerDTO.getFid(),
				Constants.ID2URL_DEFAULT_BITRATE_ALBUM);
		final ImageView music_cover = viewHolder.music_cover;
		if (!url2.equals(viewHolder.music_cover.getTag())) {
			viewHolder.music_cover.setTag(url2);
			music_cover.setImageResource(R.drawable.draw_color_cover_default);
			asyncImageLoader.loadBitmapByUrl(url2,
					AsyncImageLoader.IMAGE_TYPE_ORIGINAL, new ImageCallback() {
				
				@Override
				public void imageLoaded(final Bitmap bitmap, final String imageUrl) {
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							if (imageUrl.startsWith((String) music_cover.getTag())) {
								music_cover.setImageBitmap(bitmap);
							}
						}
					});
				}
			});
		}
		viewHolder.likeView.setImageResource(tickerDTO.isLoved() ? R.drawable.liked
						: R.drawable.like);
		viewHolder.likeView.setTag(tickerDTO);
		viewHolder.likeView.setOnClickListener(mLikeViewListener);
		viewHolder.playView = (ImageView) convertView.findViewById(R.id.play_but);
		viewHolder.shareView.setTag(tickerDTO);
		viewHolder.shareView.setOnClickListener(mShareViewListener);
		boolean rs = mContext.isCurrentDtoPlaying(""+tickerDTO.getTid()) && mContext.isPlaying();
		viewHolder.playView.setTag(tickerDTO);
		viewHolder.playView.setImageResource(rs?R.drawable.ticker_pause:R.drawable.ticker_play);
		viewHolder.playView.setOnClickListener(mPlayViewListener);
		return convertView;
	}

	private void fillHeadView(View view) {
		if (mPersonalData == null) {
			return;
		}
		TextView name_text = (TextView) view.findViewById(R.id.name_text);
		name_text.setText(mPersonalData.name);
		mContext.setJingTitleText(mPersonalData.name);
		if (mTimeText == null) {
			mTimeText = (TextView) view.findViewById(R.id.time_text);
		}
		mTimeText.setText(mContext.onlineTimeToText(mPersonalData.onLineTime));
		attention_image = (ImageView)view
				.findViewById(R.id.attention_image);
		attention_image.setOnClickListener(mAttendListener);
		View attention_layout = view.findViewById(R.id.attention_layout);
		//attention_layout.setOnClickListener(mAttendListener);
		attention_layout.setVisibility(View.VISIBLE);
		if (isMyHomePage()) {
			attention_image.setVisibility(View.GONE);
		}else{
			attention_image.setVisibility(View.VISIBLE);
			if (mPersonalData.attend) {
				attention_image.setImageResource(R.drawable.profile_right_corner_remove);
			}else{
				attention_image.setImageResource(R.drawable.profile_right_corner_add);
			}
		}
		LayoutParams layoutParams = (LayoutParams) mMymusicCover
				.getLayoutParams();
		layoutParams.height = layoutParams.width;
		mMymusicCover.setLayoutParams(layoutParams);
		mMyAvatarImage = ((ImageView) view
				.findViewById(R.id.avatar_image));
		mMyAvatarImage.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
//				LinkedViewData oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.USER_HOME_PAGE, ""+mOuid, new ArrayList(mainList));
//				LinkedViewData newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.USER_HOME_PAGE_TICKER, ""+mOuid, null);
//				mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData, newLinkedViewData);
				showChangeImageDialog();
			}
		});
		if (mContext.isMyUrlUpdata(mPersonalData.avatarUrl)) {
			Bitmap bitmap = mContext.getAvatarAfterChange();
			mMyAvatarImage.setImageBitmap(AsyncImageLoader.toRound(bitmap));
		}else{
			AsyncImageLoader.getInstance().loadTempBitmapByUrl(mPersonalData.avatarUrl,
					AsyncImageLoader.IMAGE_TYPE_ROUND, new ImageCallback() {

				@Override
				public void imageLoaded(final Bitmap mBitmap, String imageUrl) {
					if (mBitmap == null) {
						AsyncImageLoader.getInstance().loadTempBakBitmapByUrl(mPersonalData.avatarUrl, AsyncImageLoader.IMAGE_TYPE_ROUND, new ImageCallback() {

							@Override
							public void imageLoaded(final Bitmap bitmap, String imageUrl) {
								mHandler.post(new Runnable() {
									@Override
									public void run() {
										mMyAvatarImage.setImageBitmap(bitmap);
									}
								});
							}
						});
					}else{
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								mMyAvatarImage.setImageBitmap(mBitmap);
							}
						});
					}
				}
			});
		}
		
		AsyncImageLoader.getInstance().loadUpdateBitmapByUrl(mPersonalData.coverUrl,
				AsyncImageLoader.IMAGE_TYPE_ORIGINAL, new ImageCallback() {
			
			@Override
			public void imageLoaded(final Bitmap bitmap, String imageUrl) {
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						try {
							Bitmap bp = Bitmap.createScaledBitmap(bitmap, mMymusicCover.getWidth(), mMymusicCover.getWidth(), false);
							mMymusicCover.setImageBitmap(AsyncImageLoader.toTopCorner(bp,bp.getWidth()*10/1000));
						} catch (OutOfMemoryError e) {
							mMymusicCover.setImageBitmap(null);
							e.printStackTrace();
						}
					}
				});
			}
		});
		playMyRadioBtn = ((ImageView) view.findViewById(R.id.play_but));
		playMyRadioBtn.setOnClickListener(onPersonalPlayListener);
		try {
			int num = Integer.parseInt(mPersonalData.FavTk);
			if (num > 0) {
				if (mContext.isPlayingPersonalRadio(mPersonalData.name)) {
					if (mContext.isPlaying()) {
						playMyRadioBtn.setImageResource(R.drawable.profile_play_radio);
					}else{
						playMyRadioBtn.setImageResource(R.drawable.profile_play_radio);
					}
				}else{
					playMyRadioBtn.setImageResource(R.drawable.profile_play_radio);
				}
			}
		} catch (Exception e) {
		}
		
		if (isMyHomePage()) {
			playMyRadioBtn.setVisibility(View.GONE);
		}else{
			playMyRadioBtn.setVisibility(View.VISIBLE);
		}
	}

	private boolean isMyHomePage() {
		return (""+mContext.getUserId()).equals(mOuid);
	}

	public void addOne(TickerDTO dto) {
		this.mainList.add(0, dto);
		notifyDataSetChanged();
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		position--;
		LinkedViewData oldLinkedViewData;
		LinkedViewData newLinkedViewData;
		ArrayList<String> list;
		switch (position) {
		case 0:
			break;
		case 1:
			if (isMyHomePage()) {
				KTC.rep("Homepage", "CheckFavoriteMusic", "self");
			}else{
				KTC.rep("Homepage", "CheckFavoriteMusic", "visitor");
			}
			oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.USER_HOME_PAGE, ""+mOuid, new ArrayList(mainList));
			list = new ArrayList<String>();
			mContext.getmViewManagerCenter().getmMusicViewManager().setOName(mPersonalData.name);
			newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.MUSIC_LIST, ""+mOuid,list);
			mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData, newLinkedViewData);
			break;
		case 2:
			if (isMyHomePage()) {
				KTC.rep("Homepage", "PlayLovedSong", "self");
			}else{
				KTC.rep("Homepage", "PlayLovedSong", "visitor");
			}
			if (isMyHomePage()) {
				oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.USER_HOME_PAGE, ""+mOuid, new ArrayList(mainList));
				newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.RADIO_LIST, ""+mOuid, null);
				mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData, newLinkedViewData);
			}else{
				if (JingTools.isValidString(mOuid)
						|| JingTools.isValidString(mPersonalData.name)) {
					oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.USER_HOME_PAGE, ""+mOuid, new ArrayList(mainList));
					newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.CHAT_VIEW,mOuid, new ChatUserData(mOuid, mPersonalData.name, mPersonalData.avatarUrl),null);
					mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData, newLinkedViewData);
				}
			}
			break;
		case 3:
			if (isMyHomePage()) {
				KTC.rep("Homepage", "CheckFollowers", "self");
			}else{
				KTC.rep("Homepage", "CheckFollowers", "visitor");
			}
			oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.USER_HOME_PAGE, ""+mOuid, new ArrayList(mainList));
			newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.FRIEND_LIST, ""+mOuid+Constants.AC_ATTENTIONDED_KEYWORD, null);
			mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData, newLinkedViewData);
			break;
		case 4:
			if (isMyHomePage()) {
				KTC.rep("Homepage", "CheckFollowings", "self");
			}else{
				KTC.rep("Homepage", "CheckFollowings", "visitor");
			}
			oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.USER_HOME_PAGE, ""+mOuid, new ArrayList(mainList));
			newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.FRIEND_LIST, ""+mOuid+Constants.BE_ATTENTIONDED_KEYWORD, null);
			mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData, newLinkedViewData);
			break;
		case 5:
			if (isMyHomePage()) {
				KTC.rep("Homepage", "CheckTicker", "self");
			}else{
				KTC.rep("Homepage", "CheckTicker", "visitor");
			}
			oldLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.USER_HOME_PAGE, ""+mOuid, new ArrayList(mainList));
			newLinkedViewData = mContext.getmViewManagerCenter().createLinkedViewData(LinkedViewData.USER_HOME_PAGE_TICKER, ""+mOuid, null);
			mContext.getmViewManagerCenter().pushLinkedViews(oldLinkedViewData, newLinkedViewData);
			break;
		default:
			if (isMyHomePage()) {
				KTC.rep("Homepage", "PlayByMood", "self");
			}else{
				KTC.rep("Homepage", "PlayByMood", "visitor");
			}
			final TextView textView = ((TextView) view.findViewById(R.id.main_text));
			if (textView == null) {
				return;
			}
			//final CharSequence str = textView.getText();
			final String str = "@"+mPersonalData.name + " + " +(String)textView.getTag();
			final String showstr = mPersonalData.name + "的" + (String)textView.getTag();
			if (JingTools.isValidString(str)) {
				mPersonalRadioAdapter.showSearchDialog(str,showstr);
			}
			break;
		}
	}

	private void showChangeImageDialog() {
		if(!isMyHomePage()){
			return;
		}
		AlertDialog dialog = new AlertDialog.Builder(mContext).create();
		dialog.setCancelable(true);
		dialog.setMessage("更换头像或封面");
		android.content.DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					mContext.changeAvatar(mMyAvatarImage);
				}else{
					mContext.changeCover(mMymusicCover);
					mMymusicCover.setBackgroundColor(0);
				}
				dialog.dismiss();
			}
		};
		dialog.setButton(AlertDialog.BUTTON_POSITIVE, "更换头像",
				listener);
		dialog.setButton(AlertDialog.BUTTON_NEGATIVE, "更换封面",
				listener);
		dialog.show();
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
			mListView.stopRefresh();
			if (Constants.REFRESH_TIME_LIMIT > System.currentTimeMillis() - mLastRefreshTime ) {
				return;
			}else{
				mLastRefreshTime = System.currentTimeMillis();
				mListView.setRefreshTime(JingTools.getDateString(mLastRefreshTime));
			}
		}
		mPersonalData = null;
		getPersonalData();
	}
	
	@Override
	public void onLoadMore() {
		mListView.stopLoadMore();
	}

	public void showMyHomePage() {
		if (mainList.isEmpty()){
			mOuid = ""+mContext.getUserId();
			try{
				List tmpList = new ArrayList<PersonalData>();
				tmpList = LocalCacheManager.getInstance().loadCacheData(tmpList,PersonalData.class.getName() + mContext.getUserId());
				if (!tmpList.isEmpty()) {
					mPersonalData = (PersonalData) tmpList.get(0);
					mPersonalDataMap.put(mOuid, mPersonalData);
				}else{
					getPersonalData();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				mainList = LocalCacheManager.getInstance().loadCacheData(mainList, UserHomePageAdapter.class.getName() + mContext.getUserId());
			} catch (Exception e) {
				e.printStackTrace();
			}
			mPersonalTickerAdapter.getTickerData();
			if (playMyRadioBtn != null) {
				boolean playingMy = mContext.isPlayingPersonalRadio(mContext.getmLoginData().getUsr().getNick());
				playingMy &= mContext.isPlaying();
				playMyRadioBtn.setImageResource(R.drawable.profile_play_radio);
			}
		}else{
			makeLinkedView(null,""+mContext.getUserId());
		}
		onRefresh();
	}
	
	public View makeLinkedView(List list, String ouid) {
		if (!ouid.equals(mOuid)) {
			this.mOuid = ouid;
			mServerIndex = 0;
			mPersonalData = mPersonalDataMap.get(mOuid);
			if (mPersonalData == null) {
				clearHeadView();
				getPersonalData();
			}
			if (list == null || list.isEmpty()) {
				mainList.clear();
				mPersonalTickerAdapter.getTickerData();
			}else{
				if (mainList != list) {
					mainList.clear();
					for (int i = 0; i < list.size(); i++) {
						mainList.add((TickerDTO) list.get(i));
					}
				}
			}
			notifyDataSetChanged();
		}
		mListView.setTag(mPersonalData.name);
		return mBaseView;
	}
	
	public View makeLinkedViewTicker(List dataList, String ouid) {
		makeLinkedView(dataList, ouid);
		return mPersonalTickerAdapter.getListView();
	}

	private void clearHeadView() {
		if (mHeadView == null) {
			return;
		}
		if (mTimeText == null) {
			mTimeText = (TextView) mHeadView.findViewById(R.id.time_text);
		}
		mTimeText.setText(mContext.onlineTimeToText(0));
		if (mMymusicCover != null) {
			mMymusicCover.setImageBitmap(null);
		}
		if (mMyAvatarImage != null) {
			mMyAvatarImage.setImageBitmap(null);
		}
		mHeadView.findViewById(R.id.attention_layout).setVisibility(View.GONE);
		playMyRadioBtn.setImageResource(R.drawable.profile_play_radio);
		playMyRadioBtn.setVisibility(View.VISIBLE);
		notifyDataSetChanged();
	}

	private void getPersonalData() {
		if (mPersonalData == null) {
			mPersonalData = new PersonalData();
			mPersonalDataMap.put(mOuid, mPersonalData);
		}
		if (mContext.isOfflineMode()) {
			if (mListView != null) {
				mListView.stopRefresh();
				mListView.stopLoadMore();
				mListView.setPullLoadEnable(false);
			}
			return;
		}
		final HashMap<String, String> map = new HashMap<String, String>();
		map.put("uid", "" + mContext.getUserId());
		map.put("ouid", "" + mOuid);
		map.put("st", "0");
		map.put("ps", "" + Constants.DEFAULT_TICKER_NUM_OF_LOAD);
		new Thread() {
			public void run() {
				final ResultResponse<UserPersonalDataDTO> resultResponse = UserPersonalRequestApi
						.userFetchPersonal(map);
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (resultResponse != null
								&& resultResponse.isSuccess()) {
							UserPersonalDataDTO userPersonalDataDTO = resultResponse
									.getResult();
							UserStatDTO data = userPersonalDataDTO.getStat();
							mPersonalData.uid = userPersonalDataDTO.getUser().getUid();
							mPersonalData.userUnlockBadgeDTO = userPersonalDataDTO.getMood();
							mPersonalData.name = userPersonalDataDTO.getUser().getNick();
							mContext.setJingTitleText(mPersonalData.name);
							mPersonalData.attend = userPersonalDataDTO.getUser().isFlwd();
							mPersonalData.isFrdshp = userPersonalDataDTO.getUser().isFrdshp();
							mPersonalData.avatarUrl = CustomerImageRule.ID2URL(
									Constants.ID2URL_KEY_WORD_AVATAR,
									userPersonalDataDTO.getUser().getAvatar(),Constants.ID2URL_DEFAULT_BITRATE_AVATAR);
							mPersonalData.coverUrl = CustomerImageRule.ID2URL(
									Constants.ID2URL_KEY_WORD_COVER,
									userPersonalDataDTO.getCover(),
									Constants.ID2URL_DEFAULT_BITRATE_COVER);
							mPersonalData.FavTk = data.getFavTk();
							mPersonalData.Frd = data.getFrd();
							mPersonalData.Befrd = data.getBefrd();
							mPersonalData.online = userPersonalDataDTO.getUser().isOl();
							try {
								mPersonalData.onLineTime = Integer.parseInt(userPersonalDataDTO.getPt());
								if (isMyHomePage()) {
									mContext.getmViewManagerLeft().getLeftViewAdapter().setUserOnlineTime(mPersonalData.onLineTime);
								}
							} catch (Exception e) {
								// TODO: handle exception
							}
							List personalDataList = new ArrayList<PersonalData>();
							if(!isNoNeedSaveP && isMyHomePage()){
								try {
									personalDataList.add(mPersonalData);
									LocalCacheManager.getInstance().saveCacheData(personalDataList, PersonalData.class.getName()+mOuid);
									isNoNeedSaveP = true;
								} catch (IOException e) {
									e.printStackTrace();
								} catch (ClassNotFoundException e) {
									e.printStackTrace();
								}
							}
							mHandler.sendEmptyMessage(MSG_REFRESH_TIME);
							notifyDataSetChanged();
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
		return mPersonalData.name;
	}

	public View getShowingView() {
		return mBaseView;
	}

	private class PersonalTickerAdapter extends AbstractDragAdapter{
		DragRefreshListView mTickerListView;
		public PersonalTickerAdapter() {
			mTickerListView = new DragRefreshListView(mContext);
			setListView(mTickerListView);
		}
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
		}

		public View getListView() {
			return mTickerListView;
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
			mServerIndex = 0;
			getTickerData();
		}

		@Override
		public void onLoadMore() {
			mServerIndex += Constants.DEFAULT_TICKER_NUM_OF_LOAD;
			getTickerData();
		}

		@Override
		public int getCount() {
			return mainList.size();
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
			convertView = fillTickerView(convertView, mainList.get(position));
			return convertView;
		}

		@Override
		public void setListView(DragRefreshListView listView) {
			mTickerListView = listView;
			mTickerListView.setPullRefreshEnable(true);
			mTickerListView.setPullLoadEnable(true);
			mTickerListView.setFastScrollEnabled(true);
			mTickerListView.setSelector(R.drawable.draw_nothing);
			mTickerListView.setDivider(mContext.getResources().getDrawable(R.drawable.draw_nothing));
			mTickerListView.setDividerHeight(0);
			mTickerListView.setDrawingCacheEnabled(true);
			mTickerListView.setFadingEdgeLength(0);
			mTickerListView.setBackgroundColor(0);
			mTickerListView.setAdapter(this);
			mTickerListView.setOnItemClickListener(this);
			mTickerListView.setOnScrollListener(this);
			mTickerListView.setXListViewListener(this);
			notifyDataSetChanged();
			mTickerListView.invalidate();
		}

		@Override
		public String getTitleText() {
			return mPersonalData.name;
		}
		
		public void getTickerData() {
			if (mContext.isOfflineMode()) {
				if (mTickerListView != null) {
					mTickerListView.stopRefresh();
					mTickerListView.stopLoadMore();
					mTickerListView.setPullLoadEnable(false);
				}
				mContext.toastOffLine();
				return;
			}
			if (isViewLocked) {
				return;
			}
			isViewLocked = true;
			final Map<String, String> params = new HashMap<String, String>();
			params.put("uid", "" + mContext.getUserId());
			params.put("st", "" + mServerIndex);
			params.put("ps", "" + Constants.DEFAULT_TICKER_NUM_OF_LOAD);
			params.put("ouid", "" + mOuid);
			new Thread() {
				public void run() {
					final ResultResponse<ListResult<TickerDTO>> resultResponse = UserTickerRequestApi
							.fetchPersonalLoveTickers(params);
					if (resultResponse != null
							&& resultResponse.isSuccess()
							&& !resultResponse.getResult().getItems().isEmpty()) {
						if(!isNoNeedSave && isMyHomePage()){
							try {
								LocalCacheManager.getInstance().saveCacheData(resultResponse.getResult().getItems(), UserHomePageAdapter.class.getName() + mOuid);
								isNoNeedSave = true;
							} catch (Exception e) {
								e.printStackTrace();
							}
						}
					}
					mHandler.post(new Runnable() {
						@Override
						public void run() {
							List<TickerDTO> listResult = null;
							if (resultResponse != null
									&& resultResponse.isSuccess()) {
								listResult = resultResponse
										.getResult().getItems();
								if (mServerIndex == 0) {
									mainList.clear();
								}
								for (TickerDTO tickerDTO : listResult) {
									mainList.add(tickerDTO);
								}
							} else {
								Toast.makeText(mContext, "加载失败", 0).show();
							}
							if (mTickerListView != null) {
								mTickerListView.stopRefresh();
								mTickerListView.stopLoadMore();
								if (listResult != null) {
									mTickerListView.setPullLoadEnable(!(listResult.size() < Constants.DEFAULT_TICKER_NUM_OF_LOAD));
								}
								mTickerListView.invalidate();
							}
							isViewLocked = false;
						}
					});
				};
			}.start();
		}
	}
	
	private class PersonalRadioAdapter extends AbstractDragAdapter{
		DragRefreshListView mRadioListView;
		private List<FavoritesCmbtDTO> mFavoritesCmbtDTOList;
		private int mRadioIndexInServer;
		public PersonalRadioAdapter() {
			mRadioListView = new DragRefreshListView(mContext);
			int padding = JingTools.dip2px(mContext, 15);
			mRadioListView.setPadding(padding, 0, padding, 0);
			setListView(mRadioListView);
		}
		
		private void fetchRadioList() {
			if (mContext.isOfflineMode()) {
				if (mRadioListView != null) {
					mRadioListView.stopRefresh();
					mRadioListView.stopLoadMore();
					mRadioListView.setPullLoadEnable(false);
				}
				mContext.toastOffLine();
				return;
			}
			if (isViewLocked) {
				return;
			}
			isViewLocked = true;
			new Thread() {
				public void run() {
					Map<Object, Object> params = new HashMap<Object, Object>();
					// params d bid st ps
					params.put("uid", "" + mContext.getUserId());
					params.put("ouid", "" + mContext.getUserId());
					params.put("st", "" + mRadioIndexInServer);
					params.put("ps", "" + Constants.DEFAULT_TICKER_NUM_OF_LOAD);
					final ResultResponse<ListResult<FavoritesCmbtDTO>> rs = UserFavoritesRequestApi
							.fetchFavoritesCmbt(params);
					if (rs != null && rs.isSuccess()) {
						mHandler.post(new Runnable() {
							@Override
							public void run() {
								if (mRadioIndexInServer == 0) {
									mFavoritesCmbtDTOList = rs.getResult().getItems();
								}else{
									List<FavoritesCmbtDTO> favoritesCmbtDTOList = rs.getResult().getItems();
									for (int i = 0; i < favoritesCmbtDTOList.size(); i++) {
										mFavoritesCmbtDTOList.add(favoritesCmbtDTOList.get(i));
									}
								}
								mRadioListView.stopRefresh();
								mRadioListView.stopLoadMore();
								if (mRadioListView != null) {
									if (mFavoritesCmbtDTOList != null) {
										mRadioListView.setPullLoadEnable(!(mFavoritesCmbtDTOList.size() < Constants.DEFAULT_TICKER_NUM_OF_LOAD));
									}
									mRadioListView.invalidate();
								}
								isViewLocked = false;
								notifyDataSetChanged();
							}
						});
					}
				};
			}.start();
		}
		
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			view = view.findViewById(R.id.main_text);
			if (view != null) {
				CharSequence cmbt = ((TextView)view).getText();
				if (JingTools.isValidString(cmbt)) {
					showSearchDialog(""+cmbt, null);
				}
			}
		}
		
		public void showSearchDialog(final String cmbt, final String showstr){
			String message = cmbt;
			if(JingTools.isValidString(showstr)){
				message = showstr;
			}
			AlertDialog alertDialog = new AlertDialog.Builder(mContext)
			.create();
			alertDialog.setCancelable(true);
			alertDialog.setMessage("收听"+message +" ?");
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
		
		public View getListView() {
			onRefresh();
			return mRadioListView;
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
			mServerIndex = 0;
			fetchRadioList();
		}
		
		@Override
		public void onLoadMore() {
			mServerIndex += Constants.DEFAULT_TICKER_NUM_OF_LOAD;
			fetchRadioList();
		}
		
		@Override
		public int getCount() {
			return 1 + (mFavoritesCmbtDTOList == null ? 0 : mFavoritesCmbtDTOList.size());
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
				convertView = new View(mContext);
				convertView.setMinimumHeight(JingTools.dip2px(mContext, 15));
			}else{
				position--;
				convertView = fillRadioView(convertView, mFavoritesCmbtDTOList.get(position));
			}
			return convertView;
		}
		
		private View fillRadioView(View convertView, FavoritesCmbtDTO favoritesCmbtDTO) {
			if (convertView == null 
					|| convertView.getTag() == null
					|| R.layout.right_menu_item != (Integer)convertView.getTag()) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.right_menu_item, null);
				convertView.setTag(Integer.valueOf(R.layout.right_menu_item));
			}
			final TextView textView = ((TextView) convertView.findViewById(R.id.main_text));
			textView.setText(favoritesCmbtDTO.getCmbt());
			final ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
			final String drawableUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_ALBUM,favoritesCmbtDTO.getFid());
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
			}else{
				imageView.setImageResource(R.drawable.tiker_cover);
				imageView.setBackgroundResource(R.drawable.badge_bg);
			}
			return convertView;
		}

		@Override
		public void setListView(DragRefreshListView listView) {
			mRadioListView = listView;
			mRadioListView.setPullRefreshEnable(true);
			mRadioListView.setPullLoadEnable(false);
			mRadioListView.setFastScrollEnabled(true);
			mRadioListView.setSelector(R.drawable.draw_nothing);
			mRadioListView.setDivider(mContext.getResources().getDrawable(R.drawable.draw_nothing));
			mRadioListView.setDividerHeight(0);
			mRadioListView.setDrawingCacheEnabled(true);
			mRadioListView.setFadingEdgeLength(0);
			mRadioListView.setBackgroundColor(0);
			mRadioListView.setAdapter(this);
			mRadioListView.setOnItemClickListener(this);
			mRadioListView.setOnScrollListener(this);
			mRadioListView.setXListViewListener(this);
			notifyDataSetChanged();
			mRadioListView.invalidate();
		}
		
		@Override
		public String getTitleText() {
			return mPersonalData.name;
		}
	}

	public View getRadioListView() {
		return mPersonalRadioAdapter.getListView();
	}
	
	public void updatePersonalDataOLState(String uid,boolean online){
		PersonalData data = mPersonalDataMap.get(uid);
		if(data != null){
			data.online = online;
		}
	}
	
}
