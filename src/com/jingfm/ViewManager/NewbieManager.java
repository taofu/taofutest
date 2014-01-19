package com.jingfm.ViewManager;

import java.util.Random;

import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.jingfm.MainActivity;
import com.jingfm.R;
import com.jingfm.Constants.Constants;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.tools.AsyncImageLoader;
import com.jingfm.tools.JingTools;

public class NewbieManager {
	
	private MainActivity mContext;
	private ListView view;
	private Handler mHandler;

	public NewbieManager(MainActivity context) {
		this.mContext = context;
		initHandler();
		initView();
	}

	private void initHandler() {
		mHandler = new Handler(){
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
			}
		};
	}

	private void initView(){
//		mLoginViewContainer.setBackgroundColor(0xFF000000);
//		mLoginViewContainer.getBackground().setAlpha(153);
		view = new ListView(mContext);
		view.setVerticalScrollBarEnabled(false);
		view.setFadingEdgeLength(0);
		view.setItemsCanFocus(false);
		FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(     
				FrameLayout.LayoutParams.WRAP_CONTENT,     
				FrameLayout.LayoutParams.FILL_PARENT); 
		layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		view.setLayoutParams(layoutParams);
		view.setSelector(R.drawable.draw_nothing);
		view.setDivider(null);
		view.setDrawingCacheBackgroundColor(0);
//		view.setPullLoadEnable(false);
//		view.setPullRefreshEnable(false);
		NewbieAdapter newbieAdapter = new NewbieAdapter();
		view.setAdapter(newbieAdapter);
		view.setOnItemClickListener(newbieAdapter);
		FrameLayout.LayoutParams layoutParams2 = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
		layoutParams2.setMargins(JingTools.screenWidth *5/100, 0, JingTools.screenWidth *5/100, 0);
		view.setLayoutParams(layoutParams2);
		view.bringToFront();
		newbieAdapter.notifyDataSetChanged();
	}

	public ListView getView() {
		return view;
	}
	public void quitNewBie() {
		view = null;
		mContext.firstSearchDone();
	}
	
	public class NewbieAdapter extends BaseAdapter implements OnItemClickListener{
		private String[] strs;
		protected boolean isSearching;
		private String[][] imageFids;
		public NewbieAdapter() {
			strs = new String[]{
					"选择感兴趣的搜索条件",
					"我想听一些舒缓的，安静的纯音乐",
					"替我播发当下比较流行的华语新歌",
					"我现在心情不是很好",
					"他走了，就这么走了，再见",
					"天气不错，阳光明媚",
					"我要听有钢琴和吉他的英文歌",
					"来点节奏轻快的木吉他",
					"给自己的情歌",
					"替我播放一些能帮助睡眠的歌曲",
					"我想听有小鸟和森林感觉的音乐",
					"小提琴和木吉他一起奏响的歌曲",
					"她离开我了，永别了我的爱人",
					"80年代的经典英文老歌",
					"让人兴奋的音乐"
			};
			imageFids = new String[][]{
					new String[]{"2012072007mls.jpg", "2012080711wdG.jpg", "2012080718kwY.jpg", "2012080914Gcq.jpg", "2012070609orZ.jpg", "2012080119hnf.jpg", "2012080616qcD.jpg", "2012080913imA.jpg", "2012073007OEs.jpg", "2012073105aiX.jpg"},
					new String[]{"2013101718bcd.jpg", "2013080514PAy.jpg", "2013092211ema.jpg", "2013101810ani.jpg", "2013080610BtX.jpg", "2013101813Udt.jpg", "2013101815rQY.jpg", "2013101813GyK.jpg", "2013101810Qen.jpg", "2013102113JRc.jpg"},
					new String[]{"2013101816TZF.jpg", "2013101815NWD.jpg", "2013102115eQv.jpg", "2013101413nGA.jpg", "2013102110Bkb.jpg", "2013101810ani.jpg", "2011123010Cuv.jpg", "2012030712aFn.jpg", "2012102215HOs.jpg", "2012102410pXw.jpg"},
					new String[]{"2012072007mls.jpg", "2012080712WKo.jpg", "2012072010OKk.jpg", "2012071808XnN.jpg", "2012080816wMx.jpg", "2012080113ZxC.jpg", "2012071907xGj.jpg", "2012080114fiv.jpg", "2012072010RUl.jpg", "2012080715Fzj.jpg"},
					new String[]{"2013092617pVc.jpg", "2012080714haM.jpg", "2012011605qqG.jpg", "2012081717YCW.jpg", "2013102210gpK.jpg", "2013102117cxh.jpg", "2012081317bDD.jpg", "2013091016oMx.jpg", "2013101413UTd.jpg", "2012102410pXw.jpg"},
					new String[]{"2012080214wyl.jpg", "2012082014IWT.jpg", "2012080614UMW.jpg", "2012072503pwF.jpg", "2012071210YXp.jpg", "2011122210iRR.jpg", "2012010504Alf.jpg", "2011122709ulf.jpg", "2012090518mYz.bmp", "2012080718FvF.jpg"},
					new String[]{"2013092617pVc.jpg", "2012081717YCW.jpg", "2012080912bOU.jpg", "2012020702JlN.jpg", "2012022205uIs.jpg", "2012072310pjN.jpg", "2012042019Tgo.jpg", "2012080114ccI.jpg", "2012072703jVE.jpg", "2012081010OPH.jpg"},
					new String[]{"2013101815NWD.jpg", "2013102115eQv.jpg", "2012080113ZxC.jpg", "2013101413nGA.jpg", "2013102110Bkb.jpg", "2013101810ani.jpg", "2012071907xGj.jpg", "2011123010Cuv.jpg", "2012082215aqX.jpg", "2011123105WSB.jpg"},
					new String[]{"2012082419xyM.jpg", "2013101817VfV.jpg", "2012080711wdG.jpg", "2013031110ooj.jpg", "2012102716vvp.jpg", "2012080311nJe.jpg", "2012070409pgW.jpg", "2012062715eKP.jpg", "2013031112glD.jpg", "2012042613vCP.jpg"},
					new String[]{"2012040910bTe.jpg", "2012122115ipj.jpg", "2012122114BdN.jpg", "2013061415WXf.jpg", "2012071608CPh.jpg", "2012122114Xie.jpg", "2013061414fee.jpg", "2012102214tUt.jpg"},
					new String[]{"2012080614UMW.jpg", "2012011605qqG.jpg", "2012080912bOU.jpg", "2013091714KEn.jpg", "2013092417STU.jpg", "2012072507EpP.jpg", "2012080712rSD.jpg", "2013092411NxG.jpg", "2012072609wTo.jpg", "2012072507VZZ.jpg"},
					new String[]{"2012011207Lrc.jpg", "2012110711pED.jpg", "2012011203TCV.jpg", "2012011106XRU.jpg", "2012011110qNe.jpg", "2011122907WrV.jpg", "2011122306GmP.jpg", "2012080716uwn.jpg", "2012021612ymV.jpg", "2012111910QxE.jpg"},
					new String[]{"2012071308Tbj.jpg", "2012100811uYv.jpg", "2012022121EIP.jpg", "2012070402Wbn.jpg", "2012042811jWH.jpg", "2012010408nPm.jpg", "2012072707gRR.jpg", "2012121220DgH.jpg", "2012020408ncn.jpg", "2013052813mWG.jpg"},
					new String[]{"2012080318Aua.jpg", "2012080113sFy.jpg", "2012042404lJa.jpg", "2012082015KBA.jpg", "2012071102Shn.jpg", "2012071308Ehf.jpg", "2012070308xbm.jpg", "2013041517qLz.jpg", "2012072306nPU.jpg", "2012011410InY.jpg"}
			};
		}

		@Override
		public int getCount() {
			return strs.length;
		}

		@Override
		public Object getItem(int position) {
			return position > strs.length ? null: strs[position];
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			if (position == 0) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.right_menu_item, null);
				final TextView textView = ((TextView) convertView.findViewById(R.id.main_text));
				final TextView subTextView = ((TextView) convertView.findViewById(R.id.sub_text));
				final ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
				imageView.setImageResource(R.drawable.right_item_title_npersonal_music);
				final ImageView imageView1 = (ImageView) convertView.findViewById(R.id.sub_icon);
				imageView1.setVisibility(View.GONE);
				textView.setText("  " + strs[position] + "  ");
				subTextView.setText("  选择感兴趣的搜索条件开始Jing的奇幻旅程 ");
				FrameLayout frameLayout = new FrameLayout(mContext);
				frameLayout.addView(convertView);
				frameLayout.setPadding(0, JingTools.dip2px(mContext, 15), 0, JingTools.dip2px(mContext, 10));
				convertView = frameLayout;
			}else{
				if (convertView == null 
						|| convertView.getTag() == null
						|| R.layout.right_menu_item != (Integer)convertView.getTag()) {
					convertView = LayoutInflater.from(mContext).inflate(
							R.layout.right_menu_item, null);
					convertView.setTag(Integer.valueOf(R.layout.right_menu_item));
				}
				final TextView textView = ((TextView) convertView.findViewById(R.id.main_text));
				final TextView subTextView = ((TextView) convertView.findViewById(R.id.sub_text));
				final ImageView imageView = (ImageView) convertView.findViewById(R.id.icon);
				convertView.findViewById(R.id.sub_icon).setVisibility(View.GONE);
				textView.setGravity(Gravity.CENTER_VERTICAL);
				subTextView.setVisibility(View.GONE);
				textView.setText("  " + strs[position] + "  ");
				position--;
				final String drawableUrl = CustomerImageRule.ID2URL(Constants.ID2URL_KEY_WORD_ALBUM, imageFids[position][new Random(System.currentTimeMillis()).nextInt(imageFids[position].length)],Constants.ID2URL_DEFAULT_BITRATE_ALBUM);
				if (JingTools.isValidString(drawableUrl)
						&& !drawableUrl.equals(imageView.getTag())) {
					imageView.setTag(drawableUrl);
					imageView.setImageBitmap(null);
					imageView.setBackgroundResource(R.drawable.badge_bg);
					int padding = JingTools.dip2px(mContext, 3);
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
				subTextView.setText("");
			}
			return convertView;
		}

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			if (position == 0) {
				return;
			}
			mContext.searchMainListByCmbt(strs[position], 0, true,null);
			quitNewBie();
		}
	}
}
