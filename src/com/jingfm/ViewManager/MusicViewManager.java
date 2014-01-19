package com.jingfm.ViewManager;

import java.util.List;

import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;

import com.jingfm.MainActivity;
import com.jingfm.R;
import com.jingfm.adapter.FavLocalMusicAdapter;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.background_model.SettingManager;
import com.jingfm.customer_views.DragRefreshListView;
import com.jingfm.tools.JingTools;


public class MusicViewManager {
	protected static final int MSG_REFRESH_VIEW = 0;
	protected static final int MSG_REFRESH_COVER = 1;
	private MainActivity mContext;
	private View mMusicView;
	private DragRefreshListView listView;
	private Handler mHandler;
	private FavLocalMusicAdapter mFavLocalMusicAdapter;
	private Button imageButton;
	private String mOname;

	public MusicViewManager(MainActivity context) {
		this.mContext = context;
		initHandler();
		initLocalMusicView();
		mFavLocalMusicAdapter = new FavLocalMusicAdapter(mContext,mMusicView);
		mFavLocalMusicAdapter.initData(null,""+mContext.getUserId(),""+mContext.getmLoginData().getUsr().getNick(),listView);
	}

	private void initLocalMusicView() {
		mMusicView = LayoutInflater.from(mContext).inflate(R.layout.center_view_list_music, null);
		listView = (DragRefreshListView) mMusicView.findViewById(R.id.list_view_music);
		imageButton = (Button) mMusicView.findViewById(R.id.play_but);
		imageButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if ((mContext.isLocalMusicMode() && mFavLocalMusicAdapter.isLocalMusicListMode())
						|| (mContext.isPlayingPersonalRadio(mContext.getmLoginData().getUsr().getNick()) && !mFavLocalMusicAdapter.isLocalMusicListMode())) {
					if (mContext.isPlaying()) {
						mContext.musicPause();
					}else{
						mContext.musicPlay();
					}
				}else{
					mFavLocalMusicAdapter.playList(null,0,R.drawable.ticker_pause);
				}
			}
		});
	}
	public List<MusicDTO> getLocalMusicList(){
		return mFavLocalMusicAdapter.getLocalMusicList();
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

	public View getView() {
		mFavLocalMusicAdapter.onRefresh();
		return mMusicView;
	}

	public void onShowingFavMusic() {
		mFavLocalMusicAdapter.setLocalMusicListMode(false);
		mFavLocalMusicAdapter.setListView(listView);
		mFavLocalMusicAdapter.initData(null,""+mContext.getUserId(),"" + mContext.getmLoginData().getUsr().getNick(), listView);
		mFavLocalMusicAdapter.onRefresh();
		onShowing(false);
		showDownloadMusicGuide();
	}
	
	private void showDownloadMusicGuide() {
		if (SettingManager.getInstance().isNoNeedShowDownloadMusicGuide()) {
			return;
		}
		SettingManager.getInstance().setNoNeedShowDownloadMusicGuide(true);
		final FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.RIGHT | Gravity.TOP;
		params.setMargins(0,  JingTools.dip2px(mContext, 46+58+12), JingTools.dip2px(mContext, 7), 0);
//		mContext.showGuide(R.drawable.guide_download_music, params,null);
	}

	public void onShowingLocalMusic() {
		onShowing(true);
		listView.stopRefresh();
		listView.stopLoadMore();
		listView.setPullRefreshEnable(false);
		listView.setPullLoadEnable(false);
	}
	
	public void onShowing(boolean isLocalMode){
		listView.invalidate();
		listView.setAdapter(mFavLocalMusicAdapter);
		listView.setXListViewListener(mFavLocalMusicAdapter);
		listView.setOnItemClickListener(mFavLocalMusicAdapter);
		listView.setOnScrollListener(mFavLocalMusicAdapter);
		mFavLocalMusicAdapter.setListView(listView);
		mFavLocalMusicAdapter.setLocalMusicListMode(isLocalMode);
		mFavLocalMusicAdapter.initData(null, ""+mContext.getUserId(),"" + mContext.getmLoginData().getUsr().getNick(), listView);
		mFavLocalMusicAdapter.notifyDataSetChanged();
	}

	public void startDownloadMusic(MusicDTO mucisDTO) {
		mFavLocalMusicAdapter.addNewMusicDownload(mucisDTO, mContext.getUserId(),null);
	}

	public boolean downloadMusicContains(MusicDTO musicDTO) {
		return mFavLocalMusicAdapter.downloadMusicContains(musicDTO);
	}

	public void setOName(String oname){
		mOname = oname;
	}
	
	public View makeLinkedView(String ouid, List dataList) {
		 mFavLocalMusicAdapter.setLocalMusicListMode(false);
		 mFavLocalMusicAdapter.setListView(listView);
		 mFavLocalMusicAdapter.initData(dataList,ouid,mOname, listView);
		 mFavLocalMusicAdapter.onRefresh();
		 return getView();
	}

	public void playLocalMusic() {
		mFavLocalMusicAdapter.playLocalMusic(null,0,0);
	}
	
	public boolean getLocalMusicCanDownload() {
		return mFavLocalMusicAdapter.getLocalMusicCanDownload();
	}
}
