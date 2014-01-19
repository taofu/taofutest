package com.jingfm.adapter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.os.RecoverySystem.ProgressListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.MainActivity.ChangeDataAnimateCallBack;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.Constants.KTC;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.UserMediaRequestApi;
import com.jingfm.api.business.UserMusicRequestApi;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.UserFavMusicDTO;
import com.jingfm.background_model.LocalCacheManager;
import com.jingfm.background_model.PlayerManager.PlayListOverListener;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.customer_views.JingDownloadingView;
import com.jingfm.db.DataBaseOperator;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.AsyncImageLoader.ImageCallback;
import com.jingfm.tools.HttpDownloader.SizeObtainer;
import com.jingfm.tools.JingTools;

public class FavLocalMusicAdapter extends AbstractDragAdapter implements OnItemLongClickListener{

	protected static final int MSG_SET_NOTIFY = 110;

	private List<UserFavMusicDTO> mainFavList = new ArrayList<UserFavMusicDTO>();
	private List<MusicDTO> mainLocalList = new ArrayList<MusicDTO>();
	private List<MusicDTO> mainDateBaseList = new ArrayList<MusicDTO>();
	private Map<MusicDTO,DownloadProgressListener> progressMap = new HashMap<MusicDTO,DownloadProgressListener>();

	private boolean isLocalMusicListMode;

	private boolean isFavLoadMore;
	
	private Handler mHandler;

	private DragRefreshListView mListView;

	private AsyncImageLoader mAsyncImageLoader;

	private MainActivity mContext;

	private DataBaseOperator mDataBaseOperator;

	private int mIndexOnServer;

	private long mLastRefreshTime;

	private View mBaseView;

	private TextView head_text;

	private TextView what_i_have;

	private OnClickListener mDownloadButtonListener;

	private int mNumTotalFavSong;

	private PlayListOverListener mFavPlayListOverListener;

	private String mOuid;

	private OnClickListener mDownloadPlayListener;

	private boolean isNoNeedSave;

	private AlertDialog mDeleteDialog;

	private OnClickListener mDownloadShareListener;

	private String mCurrentNick;

	public FavLocalMusicAdapter(MainActivity context,View baseView) {
		this.mContext = context;
		this.mBaseView = baseView;
		initView();
		initHandler();
		initDataBaseData();
		mAsyncImageLoader = AsyncImageLoader.getInstance();
		mDownloadPlayListener =new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MusicDTO data = (MusicDTO) v.getTag();
				if (data != null) {
					if (mContext.isCurrentDtoPlaying(""+data.getTid())) {
						if (mContext.isPlaying()) {
							mContext.musicPause();
						}else{
							mContext.musicPlay();
						}
						((ImageView)v).setImageResource(mContext.isPlaying()?R.drawable.download_pause:R.drawable.download_play);
					}else{
						KTC.rep("CachedMusic", "PlaySingle", "");
						ArrayList<MusicDTO> list = new ArrayList<MusicDTO>();
						list.add(data);
						mContext.startNewSubList(list,Constants.UNKNOWN_M_VALUE,Constants.UNKNOWN_CMBT_VALUE, null);
						if (isLocalMusicListMode) {
							mContext.setSubListTitle("收听本地缓存的音乐");
						}else{
							mContext.setSubListTitle("收听喜欢的歌");
						}
						((ImageView)v).setImageResource(R.drawable.download_pause);
						mContext.changeData(true, new ChangeDataAnimateCallBack() {
							
							@Override
							public void doChangeData() {
								mContext.getmViewManagerCenter().removeAllViewsAddNew(null);
								mContext.getmViewManagerCenter().refreshRightButtonState();
							}
						});
					}
				}
			}
		};
		mDownloadShareListener =new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				MusicDTO data = (MusicDTO) v.getTag();
				if (data != null) {
					mContext.showShareView(data);
				}
			}
		};
		mDownloadButtonListener =new OnClickListener() {
			@Override
			public void onClick(View v) {
				MusicDTO data = (MusicDTO) v.getTag();
				if (data == null) {
					return;
				}
				if(((JingDownloadingView)v).getmProgress() >= 100){
					deleteMusic(data);
				}else{
					addNewMusicDownload(data,mContext.getUserId(),((JingDownloadingView)v));
				}
			}
		};
	}
	
	protected void deleteMusic(final MusicDTO data) {
		if (data == null) {
			return;
		}
		if (mDeleteDialog == null) {
			mDeleteDialog = new AlertDialog.Builder(mContext).create();
			mDeleteDialog.setTitle("删除缓存");
			mDeleteDialog.setIcon(android.R.drawable.ic_dialog_info);
			mDeleteDialog.setCancelable(true);
		}
		mDeleteDialog.setMessage("确定要删除<"+data.getN()+">吗");
		android.content.DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				if (which == DialogInterface.BUTTON_POSITIVE) {
					doDeleteMusic(data);
				}
			}
		};
		mDeleteDialog.setButton(AlertDialog.BUTTON_POSITIVE, "删除",
				listener);
		mDeleteDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "保留",
				listener);
		mDeleteDialog.show();
	}

	private void doDeleteMusic(MusicDTO data) {
		if (data == null) {
			return;
		}
		deleteFileDownload(data);
		DownloadProgressListener listener = progressMap.get(data);
		if (listener != null) {
			listener.onProgress(-1);
		}
		mainLocalList.remove(data);
		new Thread() {
			public void run() {
				sendLocalListToServer();
			}
		}.start();
		notifyDataSetChanged();
	}

	private void obtainServerList() {
		new Thread(){
			public void run() {
				HashMap<Object, Object> params = new HashMap<Object, Object>();
				int uid = mContext.getUserId();
				params.put("u", ""+uid);
				ResultResponse<Map<String, Object>> rs = UserMediaRequestApi.offline_Music(params);
				if (rs != null && rs.isSuccess()) {
					List<MusicDTO> serverList = ((ListResult<MusicDTO>)rs.getResult().get("items")).getItems();
					Integer cm = (Integer) rs.getResult().get("cm");
					if (cm != null) {
						mContext.getmLoginData().setCm(cm);
					}
					if (serverList==null) {
						return;
					}
					if (!JingTools.checkListEqual(serverList,mainLocalList)) {
						if (serverList.isEmpty()) {
							mDataBaseOperator.deleteALLFileDownloadInDatabase(""+uid);
						}else{
							for (int i = 0; i < mainLocalList.size(); i++) {
								if (!serverList.contains(mainLocalList.get(i))) {
									mainDateBaseList.remove(mainLocalList.get(i));
									mDataBaseOperator.deleteFileDownload(mainLocalList.get(i).getTid(),""+uid);
								}
							}
							for (int i = 0; i < serverList.size(); i++) {
								serverList.get(i).setFs(0);
								mDataBaseOperator.addNewDownloadTask(serverList.get(i), uid, i);
							}
						}
						mainLocalList = serverList;
					}
				}
				refreshTime();
				startDownloadAllMusic();
			};
		}.start();
	}

	protected void startDownloadAllMusic() {
		for (int i = 0; i < mainLocalList.size(); i++) {
			addNewMusicDownload(mainLocalList.get(i),mContext.getUserId(),null);
		}
	}

	private void initView() {
		head_text = (TextView) mBaseView
				.findViewById(R.id.head_text);
		what_i_have = (TextView) mBaseView
				.findViewById(R.id.what_i_have);
	}

	@Override
	public void setListView(DragRefreshListView listView) {
		mListView = listView;
		if (mListView == null) {
			return;
		}
		mListView.setPullRefreshEnable(true);
		mListView.setPullLoadEnable(false);
		mListView.setSelector(R.drawable.draw_nothing);
		mListView.setAdapter(this);
		mListView.setOnItemLongClickListener(this);
		mListView.setDividerHeight(0);
		mListView.setFadingEdgeLength(0);
		mListView.setOnScrollListener(this);
		mListView.setXListViewListener(this);
		mListView.setCacheColorHint(0);
	};
	
	private void initDataBaseData() {
		mDataBaseOperator = DataBaseOperator.getInstants(mContext);
		mainDateBaseList = mDataBaseOperator.qryOfflineMusicByUid(mContext.getUserId());
		mainLocalList.addAll(mainDateBaseList);
		for (int i = 0; i < mainLocalList.size(); i++) {
			MusicDTO musicDTO = mainLocalList.get(i);
			long downLoadedsize = LocalCacheManager.getInstance().getDownloadedFileSize(Constants.DOWNLOAD_HIGH_QUALITIY,""+musicDTO.getTid(), "");
			int totalSize = musicDTO.getFs();
			DownloadProgressListener downloadProgressListener = new DownloadProgressListener();
			downloadProgressListener.onProgress((int) (totalSize == 0?0:downLoadedsize * 100 / totalSize));
			progressMap.put(musicDTO, downloadProgressListener);
		}
		refreshTime();
		if (mContext.isOfflineMode()) {
			playLocalMusic(null,0,0);
		}else{
			obtainServerList();
		}
	}
	
	public void addNewMusicDownload(final MusicDTO musicDTO,int uid, ProgressBar progressBar){
		if (!LocalCacheManager.getInstance().isReady()) {
			return;
		}
		if (!mainLocalList.contains(musicDTO)) {
			if (!getLocalMusicCanDownload()) {
				Toast.makeText(mContext, "最多只能缓存" + mContext.getmLoginData().getCm() + "分钟", 1).show();
				return;
			}
			mainLocalList.add(musicDTO);
			new Thread(){
				public void run() {
					sendLocalListToServer();
				}
			}.start();
			if (!mainDateBaseList.contains(musicDTO)) {
				mDataBaseOperator.addNewDownloadTask(musicDTO, uid, getCount());
			}
			Toast.makeText(mContext, musicDTO.getN() + " 已经被添加到下载队列", 0).show();
			refreshTime();
		}
		DownloadProgressListener downloadProgressListener = progressMap.get(musicDTO);
		if (downloadProgressListener == null) {
			downloadProgressListener = new DownloadProgressListener();
			progressMap.put(musicDTO, downloadProgressListener);
		}
		if (progressBar != null) {
			progressBar.setProgress(0);
			downloadProgressListener.setProgressBar(progressBar);
			progressBar.invalidate();
		}
		LocalCacheManager.getInstance().downMusicFile(musicDTO, downloadProgressListener,new SizeObtainer() {
			
			@Override
			public void onSizeObtain(long size) {
				musicDTO.setFs((int) size);
				mDataBaseOperator.updateFS(musicDTO);
			}
		});
	}
	
	private synchronized void sendLocalListToServer() {
		if (mainLocalList == null) {
			return;
		}
		HashMap<Object, Object> params = new HashMap<Object, Object>();
		int uid = mContext.getUserId();
		params.put("u", ""+uid);
		String tids = "";
		for (int i = 0; i < mainLocalList.size(); i++) {
			if (i != 0 ) {
				tids += ",";
			}
			tids += mainLocalList.get(i).getTid();
		}
		params.put("tids", tids);
		ResultResponse<ListResult<MusicDTO>> rs = UserMediaRequestApi.set_offline_Music(params);
//		if (rs != null && rs.isSuccess()) {
//			List<MusicDTO> serverList = rs.getResult().getItems();
//			if (!JingTools.checkListEqual(serverList,mainLocalList)) {
//				mDataBaseOperator.deleteALLFileDownloadInDatabase(""+uid);
//				for (int i = 0; i < serverList.size(); i++) {
//					mDataBaseOperator.addNewDownloadTask(serverList.get(i), uid, i);
//				}
//				mainLocalList = serverList;
//			}
//		}
	}

	public void deleteFileDownload(MusicDTO musicDTO){
		boolean hasReference = mDataBaseOperator.deleteFileDownload(musicDTO.getTid(),""+mContext.getUserId());
		if (!hasReference) {
			LocalCacheManager.getInstance().delDownloadedFile(""+musicDTO.getTid());
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
	public int getCount() {
		return isLocalMusicListMode?mainLocalList.size():mainFavList.size();
	}

	@Override
	public Object getItem(int arg0) {
		return isLocalMusicListMode?mainLocalList.get(arg0):mainFavList.get(arg0);
	}

	@Override
	public long getItemId(int arg0) {
		return arg0;
	}

	@Override
	public void notifyDataSetChanged() {
		if (isLocalMusicListMode) {
			refreshTime();
		}else{
			what_i_have.setText(""+mNumTotalFavSong);
		}
		super.notifyDataSetChanged();
	}

	private void refreshTime() {
		if (JingTools.isUiThread()) {
			Integer timeIcanHave = 0;
			try {
				timeIcanHave = mContext.getmLoginData().getCm();
			} catch (Exception e) {
				return;
			}
			if (timeIcanHave == null || timeIcanHave <= 0) {
				return;
			}
			int localTotalTime = getMusicTimeDownLoaded();
			if (isLocalMusicListMode) {
				what_i_have.setText(Math.min(localTotalTime/60,60) + "分钟");
				head_text.setText(String.format("你总共可以缓存%2d分钟，还剩%2d分钟",timeIcanHave,Math.max(timeIcanHave - getTotalDuration()/60,0)));
			}
		}
	}

	private int getMusicTimeDownLoaded(){
		int localTotalTime = 0;
		for (int i = 0; i < mainLocalList.size(); i++) {
			localTotalTime += Integer.parseInt(mainLocalList.get(i).getD());
		}
		return localTotalTime;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(mContext).inflate(
				R.layout.center_list_fav_music_item, null);
		TextView music_name = (TextView) convertView.findViewById(R.id.music_name);
		TextView artist_name = (TextView) convertView
				.findViewById(R.id.artist_name);
		ImageView icon = (ImageView) convertView
				.findViewById(R.id.music_cover);
		ImageView download_play = (ImageView) convertView
				.findViewById(R.id.download_play);
		ImageView download_share = (ImageView) convertView
				.findViewById(R.id.download_share);
		JingDownloadingView downloadProgess = (JingDownloadingView) convertView
				.findViewById(R.id.download_image);
		MusicDTO data;
		if (isLocalMusicListMode) {
			data = mainLocalList.get(position);
			artist_name.setText(data.getAn());
//			download_play.setVisibility(View.GONE);
		}else{
//			download_play.setVisibility(View.VISIBLE);
			data = mainFavList.get(position);
			artist_name.setText(((UserFavMusicDTO) data).getAtn());
		}
		download_play.setTag(data);
		download_play.setImageResource(mContext.isCurrentDtoPlaying(""+data.getTid())&&mContext.isPlaying()?R.drawable.download_pause:R.drawable.download_play);
		download_play.setOnClickListener(mDownloadPlayListener);
		download_share.setTag(data);
		download_share.setOnClickListener(mDownloadShareListener);
		downloadProgess.setTag(data);
		downloadProgess.setOnClickListener(mDownloadButtonListener);
		if (mainLocalList.contains(data)) {
			downloadProgess.setVisibility(View.VISIBLE);
			DownloadProgressListener listener = progressMap.get(data);
			if (listener !=null) {
				listener.setProgressBar(downloadProgess);
				downloadProgess.setProgress(listener.getDownloadedProgress());
			}
		}
		music_name.setText(data.getN());
		String url = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_ALBUM,
				data.getFid(),"AM");
		final ImageView tmpIconView = icon;
		mAsyncImageLoader.loadBitmapByUrl(url, AsyncImageLoader.IMAGE_TYPE_ORIGINAL,
				new ImageCallback() {

					@Override
					public void imageLoaded(final Bitmap mBitmap,
							String imageUrl) {
						mHandler.post(new Runnable() {
							
							@Override
							public void run() {
								tmpIconView.setImageBitmap(mBitmap);
							}
						});
					}
				});
		if (position %2 == 0) {
			convertView.setBackgroundColor(0xFF393939);
		}else{
			convertView.setBackgroundColor(0xFF333333);
		}
		return convertView;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		final View layout = view.findViewById(R.id.download_layout);
		if (layout.getVisibility() == View.VISIBLE) {
			return;
		}
		layout.setVisibility(View.VISIBLE);
		Animation animationIn = AnimationUtils.loadAnimation(mContext,
				R.anim.push_right_in);
		layout.startAnimation(animationIn);
		mHandler.postDelayed(new Runnable() {
			
			@Override
			public void run() {
				Animation animationOut = AnimationUtils.loadAnimation(mContext,
						R.anim.push_right_out);
				animationOut.setAnimationListener(new AnimationListener() {
					@Override
					public void onAnimationStart(Animation animation) {}
					@Override
					public void onAnimationRepeat(Animation animation) {}
					@Override
					public void onAnimationEnd(Animation animation) {
						layout.setVisibility(View.GONE);
					}
				});
				layout.startAnimation(animationOut);
			}
		}, 2000);
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
		if (isLocalMusicListMode) {
			if (mListView != null) {
				mListView.setPullRefreshEnable(false);
				mListView.stopRefresh();
			}
			return;
		}
		if (mListView != null) {
			if (Constants.REFRESH_TIME_LIMIT > System.currentTimeMillis() - mLastRefreshTime ) {
				mListView.stopRefresh();
				return;
			}else{
				mLastRefreshTime = System.currentTimeMillis();
				mListView.setRefreshTime(JingTools.getDateString(mLastRefreshTime));
			}
		}
		mIndexOnServer = 0;
		getData();
	}

	@Override
	public void onLoadMore() {
		getData();
	}

	public View initData(List list,String ouid, String name, DragRefreshListView listView) {
		mCurrentNick = name;
		if (mainFavList.isEmpty() && (""+mContext.getUserId()).equals(ouid)) {
			try {
				mainFavList = LocalCacheManager.getInstance().loadCacheData(mainFavList, FavLocalMusicAdapter.class.getName() + ouid);
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			}
		}
		if (listView == null) {
			setListView(new DragRefreshListView(mContext));
		}
		if (!ouid.equals(mOuid)) {
			mOuid = ouid;
			if (list == null || list.isEmpty()) {
				mainFavList.clear();
				notifyDataSetChanged();
				getData();
			}else{
				if (mainFavList != list) {
					mainFavList.clear();
					for (int i = 0; i < list.size(); i++) {
						mainFavList.add((UserFavMusicDTO) list.get(i));
					}
				}
				notifyDataSetChanged();
			}
		}
		return mListView; 
	}

	public void getData() {
		if (mContext.isOfflineMode()) {
			if (mListView != null) {
				mListView.stopRefresh();
				mListView.stopLoadMore();
			}
			return;
		}
		new Thread() {
			public void run() {
				// uid ouid st ps index
				final HashMap<String, String> map = new HashMap<String, String>();
				map.put("uid", "" + mContext.getUserId());
				map.put("ouid", "" + mOuid);
				map.put("st", "" + mIndexOnServer);
				map.put("ps", "" + Constants.DEFAULT_NUM_OF_LOAD);
				final ResultResponse<ListResult<UserFavMusicDTO>> resultResponse = UserMusicRequestApi
						.fetchFavMusics(map);
				if (resultResponse != null
						&& resultResponse.isSuccess() && !resultResponse
						.getResult().getItems().isEmpty()) {
					if(!isNoNeedSave && (""+mContext.getUserId()).equals(mOuid)){
						try {
							LocalCacheManager.getInstance().saveCacheData(resultResponse
									.getResult().getItems(), FavLocalMusicAdapter.class.getName() + mOuid);
							isNoNeedSave = true;
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						if (resultResponse != null
								&& resultResponse.isSuccess()) {
							List<UserFavMusicDTO> listResult = resultResponse
									.getResult().getItems();
							mNumTotalFavSong = resultResponse.getResult().getTotal();
							if (mIndexOnServer <= 0) {
								mainFavList.clear();
							}
							for (UserFavMusicDTO userFavMusicDTO : listResult) {
								mainFavList.add(userFavMusicDTO);
							}
							isFavLoadMore = !(listResult.size() < Constants.DEFAULT_NUM_OF_LOAD);
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
						if (mListView != null) {
							mListView.stopRefresh();
							mListView.stopLoadMore();
							mListView.setPullLoadEnable(isFavLoadMore &&!isLocalMusicListMode);
						}
					}
				});
			};
		}.start();
	}
	
	public boolean isLocalMusicListMode() {
		return isLocalMusicListMode;
	}

	public void setLocalMusicListMode(boolean isLocalMusicListMode) {
		this.isLocalMusicListMode = isLocalMusicListMode;
		if (mListView != null) {
			mListView.setPullRefreshEnable(!isLocalMusicListMode);
			mListView.setPullLoadEnable(isFavLoadMore && !isLocalMusicListMode && !mContext.isOfflineMode());
			mListView.invalidate();
			notifyDataSetChanged();
		}
		if (isLocalMusicListMode) {
			int 	timeIcanHave = 0;
			try {
				timeIcanHave = mContext.getmLoginData().getCm();
			} catch (Exception e) {
				return;
			}
			head_text.setText(String.format("你总共可以缓存%2d分钟，还剩%2d分钟",timeIcanHave,Math.max(timeIcanHave - getTotalDuration()/60,0)));
			refreshTime();
		}else{
			head_text.setText("我喜欢的音乐");
		}
	}
	
	public class DownloadProgressListener implements ProgressListener{
		int mDownloadedProgress = 0;
		private ProgressBar mProgressBar;
		@Override
		public void onProgress(int progress) {
			if(mDownloadedProgress != progress){
				mDownloadedProgress = progress;
				onProgressChanger();
			}
		}
		
		private void onProgressChanger() {
			if (mProgressBar != null) {
				mProgressBar.setProgress(mDownloadedProgress);
			}
		}

		public int getDownloadedProgress(){
			return mDownloadedProgress;
		}
		
		public void setProgressBar(ProgressBar progressBar){
			mProgressBar = progressBar;
			mProgressBar.setProgress(mDownloadedProgress);
		}
	}
	
	public int getTotalDuration() {
		int time = 0;
		for (int i = 0; i < mainLocalList.size(); i++) {
			try {
				time += Integer.parseInt(mainLocalList.get(i).getD());
			} catch (Exception e) {
			}
		}
		return time;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view,
			final int position, long id) {
		if (isLocalMusicListMode) {
			try {
				deleteMusic(mainLocalList.get(position-1));
			} catch (Exception e) {
			}
		}
		return false;
	}

	public boolean downloadMusicContains(MusicDTO musicDTO) {
		return mainLocalList.contains(musicDTO);
	}

	public List<MusicDTO> getLocalMusicList(){
		return mainLocalList;
	}
	
	public void playList(final ImageView imageButton,final int ridPlay,final int ridPause) {
		if (isLocalMusicListMode) {
			KTC.rep("CachedMusic", "PlayAll", "");
			playLocalMusic(imageButton,ridPlay,ridPause);
		}else{
			KTC.rep("FavoriteMusic", "PlayAll", "");
			mContext.changeData(true,
					new ChangeDataAnimateCallBack() {
						@Override
						public void doChangeData(){
							mContext.getmViewManagerCenter().removeAllViewsAddNew(null);
							mContext.playPersonalRadio(mCurrentNick,imageButton,ridPlay,ridPause);
						}
					});
		}
	}

	@Override
	public String getTitleText() {
		return null;
	}

	public void playLocalMusic(ImageView imageButton, int ridPlay, int ridPause) {
		if (mainLocalList == null) {
			return;
		}
		List<MusicDTO> list = new ArrayList<MusicDTO>();
		for (int i = 0; i < mainLocalList.size(); i++) {
			MusicDTO dto = mainLocalList.get(i);
			DownloadProgressListener progressListener = progressMap.get(dto);
			if (progressListener != null && progressListener.getDownloadedProgress() >= 100) {
				list.add(dto);
			}
		}
		if (list.isEmpty()) {
			if (imageButton != null) {
				imageButton.setImageResource(ridPlay);
			}
			Toast.makeText(mContext, "无本地缓存歌曲", 0).show();
		}else{
			Collections.shuffle(list);
			mContext.startNewSubList(list,Constants.UNKNOWN_M_VALUE,Constants.UNKNOWN_CMBT_VALUE,null);
			mContext.setSubListTitle("收听本地缓存的音乐");
			mContext.setJingTitleText("收听本地缓存的音乐");
			mContext.setLocalMusicMode(true);
			if (imageButton != null) {
				imageButton.setImageResource(ridPause);
			}
		}
	}

	public boolean getLocalMusicCanDownload() {
		Integer cm = mContext.getmLoginData().getCm();
		if (cm == null || cm ==0) {
			return true;
		}
		return getMusicTimeDownLoaded()/60 < cm;
	}	
}
