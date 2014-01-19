package com.jingfm.ViewManager;

import java.util.List;

import org.json.JSONException;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.Constants.KTC;
import com.jingfm.api.builder.SocketResultBuilder;
import com.jingfm.api.context.AppContext;
import com.jingfm.api.model.UserFrdDTO;
import com.jingfm.api.model.socketmessage.SocketDTO;
import com.jingfm.api.model.socketmessage.SocketMessageType;
import com.jingfm.api.model.socketmessage.SocketPAtfdDTO;
import com.jingfm.api.model.socketmessage.SocketPChatDTO;
import com.jingfm.api.model.socketmessage.SocketPFlwdDTO;
import com.jingfm.api.model.socketmessage.SocketPLisnDTO;
import com.jingfm.api.model.socketmessage.SocketPRmndDTO;
import com.jingfm.api.model.socketmessage.SocketPUmftDTO;
import com.jingfm.api.model.socketmessage.SocketPUserSignedoffDTO;
import com.jingfm.api.model.socketmessage.SocketPUserSignedonDTO;
import com.jingfm.api.model.socketmessage.SocketTickerDTO;
import com.jingfm.api.model.socketmessage.SocketTickerType;
import com.jingfm.tools.JingTools;

public class WebViewManager {
	private WebView webView;
	private MainActivity mContext;

	String localHTML = "file:///android_asset/www/index.html";
	private Handler mHandler;
	private boolean isSetuping;
	private WebChromeClient mWebChromeClient;
	private WebViewClient mWebViewClient;
	private JavascriptInterface javascriptInterface;
	private long mStartTime;
	protected boolean isConnected;
	private static String sIdCode;
	protected static final int MSG_WEB_LOAD_COMPLETE = 0;
	protected static final int MSG_WEB_CONNECTED = 1;
	protected static final int MSG_WEB_RECEIVED_PRIVATE_MSG = 2;
	protected static final int MSG_WEB_FOLLOW_LISTEN_REQUEST = 3;
	protected static final int MSG_WEB_FOLLOW_LISTEN_REFUSE = 5;
	protected static final int MSG_WEB_FOLLOW_LISTEN_SUCCESS = 6;
	protected static final int MSG_WEB_RECEIVED_PRIVATE_MSG_P_CHAT = 7;
	protected static final int MSG_WEB_RECEIVED_TOAST = 8;
	protected static final int MSG_WEB_RECEIVED_ONE_TICKER_NOTIFY = 9;
	protected static final int MSG_WEB_RECEIVED_Follow_Listener_Join = 11;
	protected static final int MSG_WEB_RECEIVED_Follow_Listener_Leave = 12;
	protected static final int MSG_WEB_SETUP = 16;
	
	@SuppressWarnings(value = { "must init in ui thread" })
	public WebViewManager(MainActivity context) {
		this.mContext = context;
		mStartTime = context.getAppStartTime();
		initHandler();
	}

	private void initAllPlugin(WebView newWebView) {
		javascriptInterface = new JavascriptInterface() {
			private WebView mWebView;

			public void connectedSuccess() {
				if (webView == null || !webView.equals(mWebView)) {
					return;
				}
				Log.e("kid_debug","connectedSuccess webView: " + webView);
				isConnected = true;
				mContext.connectedSuccess();
			}

			public void receivePrivateMessage(String message) {
				if (webView == null || !webView.equals(mWebView)) {
					return;
				}
				Log.e("kid_debug","WebViewManager" + "message: " + message);
				try {
					SocketDTO socketDTO = SocketResultBuilder.buildPrivateMessageDTO(message);
					if (socketDTO == null) {
						return;
					}
					String socketDTOgetT = socketDTO.getT();
					if (socketDTOgetT.equals(SocketMessageType.CHAT.getName())) {
						SocketPChatDTO sDTO = (SocketPChatDTO) socketDTO;
							Message msg = new Message();
							msg.what = MSG_WEB_RECEIVED_PRIVATE_MSG_P_CHAT;
							msg.obj = sDTO;
							mHandler.sendMessage(msg);
//						}
					}else if (socketDTOgetT.equals(SocketMessageType.FLWD.getName())){
						SocketPFlwdDTO sDTO = (SocketPFlwdDTO) socketDTO;
						Message msg = new Message();
						msg.what = MSG_WEB_RECEIVED_TOAST;
						String user1 = sDTO.getFlwer().equals(mContext.getmLoginData().getUsr().getNick())?"你":sDTO.getFlwer();
						String user2 = sDTO.getFlw().equals(mContext.getmLoginData().getUsr().getNick())?"你":sDTO.getFlw();
						msg.obj =  user1 + " 关注了 " + user2;
						if ("你".equals(user2)) {
							mHandler.post(new Runnable() {
								@Override
								public void run() {
									mContext.getmViewManagerCenter().hasNewAttend2u();
								}
							});
						}
						mHandler.sendMessage(msg);
					}else if (socketDTOgetT.equals(SocketMessageType.INHS.getName())){
						//TODO INHS
					}else if (socketDTOgetT.equals(SocketMessageType.ATFD.getName())){
						SocketPAtfdDTO sDTO = (SocketPAtfdDTO) socketDTO;
						Message msg = new Message();
						msg.what = MSG_WEB_RECEIVED_TOAST;
						String user1 = sDTO.getFrd();
						String cmbt = sDTO.getCmbt();
						if (JingTools.isValidString(cmbt)) {
							msg.obj =  user1 + " 正在收听你喜欢的 " + cmbt + " 的歌曲";
						}else{
							msg.obj =  user1 + " 正在收听你喜欢的歌曲";
						}
						mHandler.sendMessage(msg);
					}else if (socketDTOgetT.equals(SocketMessageType.LISN.getName())){
						SocketPLisnDTO sDTO = (SocketPLisnDTO) socketDTO;
						Message msg = new Message();
						msg.what = MSG_WEB_RECEIVED_TOAST;
						List<UserFrdDTO> users = sDTO.getItems();
						String user = "";
						for (int i = 0; i < users.size(); i++) {
							if ( i != 0) {
								user += "、";
							}
							user += users.get(i).getNick();
						}
						msg.obj =  user + " 正在听这首歌";
						mHandler.sendMessage(msg);
					}else if (socketDTOgetT.equals(SocketMessageType.RMND.getName())){
						SocketPRmndDTO sDTO = (SocketPRmndDTO) socketDTO;
						Message msg = new Message();
						msg.what = MSG_WEB_RECEIVED_TOAST;
						String user = sDTO.getFrd();
						msg.obj =  user + " 想让你关注Ta";
						mHandler.sendMessage(msg);
					}else if (socketDTOgetT.equals(SocketMessageType.ACTY.getName())){
						//TODO ACTY
					}else if (socketDTOgetT.equals(SocketMessageType.UTRC.getName())){
//						SocketPUtrcDTO sDTO = (SocketPUtrcDTO) socketDTO;
//						Message msg = new Message();
//						msg.what = MSG_WEB_RECEIVED_TOAST;
//						String user = sDTO.getNick();
//						String ticker = sDTO.getTit();
//						msg.obj =  user + " 想让你关注Ta" ;
//						mHandler.sendMessage(msg);
					}else if (socketDTOgetT.equals(SocketMessageType.UMFT.getName())){
						SocketPUmftDTO sDTO = (SocketPUmftDTO) socketDTO;
						Message msg = new Message();
						msg.what = MSG_WEB_RECEIVED_TOAST;
						String user = sDTO.getNick();
						String song = sDTO.getN();
						msg.obj =  user + " 也喜欢了 " + song;
						mHandler.sendMessage(msg);
					}else if (socketDTOgetT.equals(SocketMessageType.NSON.getName())){
						SocketPUserSignedonDTO sDTO = (SocketPUserSignedonDTO) socketDTO;
						Log.e("kid_debug","NSON sDTO: " + sDTO.getNick());
						mContext.frdOnline(sDTO);
					}else if (socketDTOgetT.equals(SocketMessageType.NSOF.getName())){
						SocketPUserSignedoffDTO sDTO = (SocketPUserSignedoffDTO) socketDTO;
						Log.e("kid_debug","NSOF sDTO: " + sDTO.getNick());
						mContext.frdOff(sDTO);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
				Message msg = new Message();
				msg.arg1 = MSG_WEB_RECEIVED_PRIVATE_MSG;
				msg.obj = message;
				mHandler.sendMessage(msg);
				// TODO receivePrivateMessage
			}

			public void receiveTickerMessage(String message) {
				if (webView == null || !webView.equals(mWebView)) {
					return;
				}
				try {
					SocketTickerDTO socketDTO = SocketResultBuilder.buildTickerMessageDTO(message);
					if (socketDTO == null) {
						return;
					}
					if (socketDTO.getT().equals(SocketTickerType.LOVE.getPrefix())){
						mHandler.sendEmptyMessage(MSG_WEB_RECEIVED_ONE_TICKER_NOTIFY);
					}
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}

			// 客户端收到服务器拒绝跟听消息
			public void followListenResponseAuthorizeRefuse() {
				KTC.rep("FollowListening", "PostRequest", "Disagree");
				if (webView == null || !webView.equals(mWebView)) {
					return;
				}
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						mContext.followListenResponseAuthorizeRefuse();
					}
				});
			}

			// 客户端收到服务器同意跟听消息
			public void followListenResponseAuthorizeSuccess(String message) {
				KTC.rep("FollowListening", "PostRequest", "Agree");
				if (webView == null || !webView.equals(mWebView)) {
					return;
				}
				Message msg = new Message();
				msg.what = MSG_WEB_FOLLOW_LISTEN_SUCCESS;
				msg.arg1 = 100;
				msg.obj = message;
				mHandler.sendMessage(msg);
			}
			
			//  被主机踢掉
			public void followListenKickLeave(String hostid) {
				if (webView == null || !webView.equals(mWebView)) {
					return;
				}
				mContext.followListenKickLeave(hostid);
			}
			
			// 接收歌曲模型
			// 很多类型，需要判断
			/**
			 * if (isPlaying == undefined || isPlaying == true) str = "play,";
			 * else str = "pausing,"; return str+music.tid+","+
			 * music.mid+","+music.fid+","+ music.name+","+music.duration+","+
			 * seconds+","+Player.volume+","+ music.an+","+music.atn;
			 * */
			public void receiveFollowListenMessage(String id, String message) {
				if (webView == null || !webView.equals(mWebView)) {
					return;
				}
				Message msg = new Message();
				msg.what = MSG_WEB_FOLLOW_LISTEN_SUCCESS;
				msg.obj = message;
				mHandler.sendMessage(msg);
			}

			// 服务器跟听请求接受
			// 弹出同意，拒绝功能对话框，15秒默认接受
			public void followListenRequestAuthorize(String id, String nick,String avatar) {
				if (webView == null || !webView.equals(mWebView)) {
					return;
				}
				Message msg = new Message();
				msg.what = MSG_WEB_FOLLOW_LISTEN_REQUEST;
				msg.obj = new String[]{id,nick,avatar};
				mHandler.sendMessage(msg);
			}

			public void followListenJoin(String id, String nick,String avatar) {
				//TODO FollowListen join 
				if (webView == null || !webView.equals(mWebView)) {
					return;
				}
				if ((""+mContext.getUserId()).equals(id)) {
					return;
				}
				Message msg = new Message();
				msg.what = MSG_WEB_RECEIVED_Follow_Listener_Join;
				msg.obj = new String[]{id,nick,avatar};
				mHandler.sendMessage(msg);
			}
			
			// 服务器告诉客户端，谁离开了跟听。
			public void followListenLeave(String id, String nick) {
				if (webView == null || !webView.equals(mWebView)) {
					return;
				}
				Message msg = new Message();
				msg.what = MSG_WEB_RECEIVED_Follow_Listener_Leave;
				msg.obj = new String[]{id,nick};
				mHandler.sendMessage(msg);
			}

			// 请求跟听失败。
			public void followListenResponseFailOther(final String id, final String nick) {
				if (webView == null || !webView.equals(mWebView)) {
					return;
				}
				mHandler.post(new Runnable() {
					
					@Override
					public void run() {
						mContext.followListenRequestOther(id, nick);
					}
				});
			}

			public void disconnect(String clientid) {
				if (webView == null || !webView.equals(mWebView)) {
					return;
				}
				Log.e("kid_debug","server disconnect");
				mHandler.post(new Runnable() {
					@Override
					public void run() {
						isConnected = false;
						mContext.disconnect();
					}
				});
			}

			public void socketOnDisconnect() {
				if (webView == null || !webView.equals(mWebView)) {
					return;
				}
				Log.e("kid_debug","socketOnDisconnect");
				isConnected = false;
				mContext.socketOnDisconnect();
			}

			@Override
			public void setWebView(WebView webView) {
				this.mWebView = webView;
			}
		};
		
		javascriptInterface.setWebView(newWebView);
		
		mWebChromeClient = new WebChromeClient() {
			public void onProgressChanged(WebView view, int progress) {
				if (isConnected || webView == null || view == null || !view.equals(webView)) {
					return;
				}
				Log.v("kid_debug", "WebView onProgressChanged ->" + progress);
				if (progress >= 100 && view.getOriginalUrl().equals(localHTML)) {
					if (!JingTools.isValidString(sIdCode)) {
						String mac = mContext.getLocalMacAddress();
						if (!JingTools.isValidString(mac)) {
							try {
								mac = mContext.getmLoginData().getUsr().getSid();
							} catch (Exception e) {
								mac = ""+mStartTime;
							}
						}
						sIdCode = mac;
					}
					String param = "javascript:initNow("
							+ mContext.getUserId()+","
							+ "'" + mContext.getmLoginData().getUsr().getNick()+ "'" + ","
							+ "'Android"+mStartTime+"'" + ","
							+ "'" + AppContext.getClientContext().getAtoken() + "'" + ","
							+ "'" + sIdCode + "')";
					Log.e("kid_debug", "param = " + param);
					webView.loadUrl(param);
				}
			}
		};
		mWebViewClient = new WebViewClient() {
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				view.loadUrl(url);
				return false;
			}
		};
	}

	private void initHandler() {
		mHandler = new Handler() {
			@Override
			public void handleMessage(Message msg) {
				super.handleMessage(msg);
				switch (msg.what) {
				case MSG_WEB_LOAD_COMPLETE:
					break;
				case MSG_WEB_RECEIVED_PRIVATE_MSG_P_CHAT:
					mContext.getmViewManagerCenter().hasNewMessage((SocketPChatDTO) msg.obj);					
					break;
				case MSG_WEB_RECEIVED_TOAST:
					Toast.makeText(mContext, ""+msg.obj, 1).show();
					break;
				case MSG_WEB_RECEIVED_ONE_TICKER_NOTIFY:
					mContext.tickerNotifyChange(1);
					break;
				case MSG_WEB_FOLLOW_LISTEN_REQUEST:
					String[] strs = (String[]) msg.obj;
					mContext.followListenRequest(strs);
					break;
				case MSG_WEB_FOLLOW_LISTEN_SUCCESS:
					String message = (String) msg.obj;
					mContext.doPlayFollowerMusic(msg.arg1 != 0,message);
					break;
				case MSG_WEB_RECEIVED_Follow_Listener_Join:
					String[] args1 = (String[]) msg.obj;
					Toast.makeText(mContext, ""+ args1[1] +"加入了跟听", 1).show();
					mContext.addFollower(args1[0], args1[1], args1[2]);
					break;
				case MSG_WEB_RECEIVED_Follow_Listener_Leave:
					String[] args2 = (String[]) msg.obj;
					mContext.removeFollower(args2[0],args2[1]);
					break;
				case MSG_WEB_SETUP:
					isSetuping = false;
					if (webView != null) {
						return;
					}
					webView = new WebView(mContext.getApplicationContext());
					isConnected = false;
					Log.e("kid_debug", "init webView = " + webView);
					setWebView();
					removeMessages(MSG_WEB_SETUP);
					break;
				}
			}
		};
	}
	public void sendMessage(String id,String uid,String message){
		webView.loadUrl("javascript:now.postChat("+id+","+uid+",'"+message+"')");
	}
	
	public void sendMessageToFollower(String id,String message){
		webView.loadUrl("javascript:now.sendFollowListenMessage("+id+",'"+message+"')");
	}
	
	public void notifyFollowListenLeave(String id){
		if (webView == null) {
			return;
		}
		webView.loadUrl("javascript:now.notifyFollowListenLeave("+id+")");
	}
	
	public void followListenResponseAuthorize(boolean authorized,int unique,int tounique,String message){
		if (authorized) {
			webView.loadUrl("javascript:	now.followListenResponseAuthorize("+authorized+","+unique+","+tounique+",'"+ message +"')");
		}else{
			webView.loadUrl("javascript:	now.followListenResponseAuthorize("+authorized+","+unique+","+tounique+")");
		}
	}

	public void followListenRequest(int unique,String tounique){
		webView.loadUrl("javascript:	now.followListenRequest("+unique+","+tounique+")");
	}
	public void notifyFollowListenKickLeave(int uid,String touniques){
		webView.loadUrl("javascript:now.notifyFollowListenKickLeave("+uid+","+"'" + touniques + "')");
	}
	

	public WebView getWebView() {
		return webView;
	}
	
	public void justToastMessage(String message){
		Message msg = new Message();
		msg.what = MSG_WEB_RECEIVED_TOAST;
		msg.obj =  message;
		mHandler.sendMessage(msg);
	}
	
	private void setWebView() {
		initAllPlugin(webView);
		mContext.getWebviewContainer().addView(webView);
		webView.getSettings().setJavaScriptEnabled(true);
		webView.setWebChromeClient(mWebChromeClient);
		webView.setWebViewClient(mWebViewClient);
		webView.loadUrl(localHTML);
		webView.addJavascriptInterface(javascriptInterface, "jingfm_android");
	}

	public void destory() {
		if (webView == null) {
			return;
		}
		try {
			Log.e("kid_debug", "destory webView = " + webView);
			mContext.getWebviewContainer().removeView(webView);
			WebView tempWebView = webView;
			webView = null;
			tempWebView.removeAllViews();
			tempWebView.destroy();
			tempWebView = null;
			System.gc();
		} catch (Exception e) {
			webView = null;
		}
	}

	public synchronized void setupWebView() {
		if (isSetuping || webView != null) {
			return;
		}
		isSetuping = true;
		mHandler.sendEmptyMessage(MSG_WEB_SETUP);
	}

	interface JavascriptInterface{
		public void setWebView(WebView webView);
	}
}
