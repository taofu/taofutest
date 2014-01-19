package com.jingfm.third_part_api;

import java.io.ByteArrayOutputStream;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.util.Log;
import android.widget.Toast;

import com.jingfm.MainActivity;
import com.jingfm.api.SecureCustomerAudioRule;
import com.jingfm.api.StaticTrackHtmlRule;
import com.jingfm.tools.AsyncImageLoader;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.SendMessageToWX;
import com.tencent.mm.sdk.openapi.WXAPIFactory;
import com.tencent.mm.sdk.openapi.WXMediaMessage;
import com.tencent.mm.sdk.openapi.WXMusicObject;

public class WeixinUtil{

//	public final static String APP_ID = "wxe7158d9712f124d4";
	
//	private final static String AppKey = "c0aed304e462aa5dc0287d82bc26c7d9";
	
	/**
	 * signatured
	 */
	public final static String  APP_ID = "wxebff1c487a10d898";
//	signature = c80968128a9cde4a466056fbca834faa
//	AppKey = c33c8c221f90133591fe14de53025e1a
	
	/**
	 * debug
	 */
//	public final static String  APP_ID = "wx84d96a2382a3f0f5";
//	signature = 111f1kad94006180d25db5fca8bd2e0f
//	signature = c80968128a9cde4a466056fbca834faa
//	AppKey = 901091252240d04e728441677dcd38ba
	
//	  以上是聊天历史记录  
//	  Allen  19:34:57
//	  AppID
//	  wxebff1c487a10d898
//	  AppKey
//	  c33c8c221f90133591fe14de53025e1a
//	  Allen  19:35:04
//	  应用签名 (Android)
//	  423b5c5e13bc9690a145cadff275db25
	
	private static final int THUMB_SIZE = 120;
	
	private IWXAPI api;
	
	private MainActivity activity;
	
	public WeixinUtil(MainActivity activity){
		this.activity = activity;
		regToWx();
	}
	
	private void regToWx(){
		//通过WXAPIFactory 工厂，获取IWXAPI的实例
		api = WXAPIFactory.createWXAPI(this.activity, APP_ID, true);
		
		//将应用的appIdea注册到微信。
		boolean register = api.registerApp(APP_ID);
		Log.e("kid_debug", "register  == "+register);
		
	} 
	/**
	 * @param isFriend  true 分享给好友，，false 分享到朋友圈
	 * 
	 * */
	public void sendMusic(final Integer tid, final String mid, final String name, String imageUrl, final boolean isFriend){
		boolean rs = api.isWXAppInstalled();
		if (!rs) {
			Toast.makeText(activity, "未找到微信客户端", 1).show();
			return;
		}
		Toast.makeText(activity, "正在分享到微信...", 1).show();
		final WXMusicObject music = new WXMusicObject();
		final WXMediaMessage msg = new WXMediaMessage();
		AsyncImageLoader.getInstance().loadBitmapByUrl(imageUrl, AsyncImageLoader.IMAGE_TYPE_ORIGINAL,new AsyncImageLoader.ImageCallback() {
			
			@Override
			public void imageLoaded(Bitmap mBitmap, String imageUrl) {
				music.musicUrl = StaticTrackHtmlRule.ID2URL(""+tid);
				music.musicDataUrl = SecureCustomerAudioRule.ID2SelfNonSecureURL(mid);
				music.musicLowBandUrl = music.musicUrl;
				music.musicLowBandDataUrl = music.musicDataUrl;
				msg.mediaObject = music;
				msg.title = name;
				msg.description = "听无损版本,请下载Jing";
				Bitmap thumbBmp = Bitmap.createScaledBitmap(mBitmap, THUMB_SIZE, THUMB_SIZE, false);
				msg.thumbData = bmpToByteArray(thumbBmp, true);
				
				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.message = msg;
				req.transaction = buildTransaction("music");
				req.scene = isFriend ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req. WXSceneTimeline;
				boolean result = api.sendReq(req);
				Log.e("kid_debug","sendReq result: " + result);
			}
		});
	}
	
	public void sendRadio(final Integer tid, final String mid, final String name, String imageUrl,final String cmbt, final boolean isFriend){
		Log.e("kid_debug","sendRadio imageUrl: " + imageUrl);
		boolean rs = api.isWXAppInstalled();
		if (!rs) {
			Toast.makeText(activity, "未找到微信客户端", 1).show();
			return;
		}
		Toast.makeText(activity, "正在分享到微信...", 1).show();
		final WXMusicObject music = new WXMusicObject();
		final WXMediaMessage msg = new WXMediaMessage();
		AsyncImageLoader.getInstance().loadBitmapByUrl(imageUrl, AsyncImageLoader.IMAGE_TYPE_ORIGINAL,new AsyncImageLoader.ImageCallback() {
			
			@Override
			public void imageLoaded(Bitmap mBitmap, String imageUrl) {
				Log.e("kid_debug","imageLoaded");
				music.musicUrl = "http://jing.fm/share/" + activity.getUserId() + "/"+ cmbt;
				Log.e("kid_debug","music.musicUrl: " + music.musicUrl);
				music.musicDataUrl = SecureCustomerAudioRule.ID2SelfNonSecureURL(mid);
				Log.e("kid_debug","music.musicDataUrl: " + music.musicDataUrl);
				music.musicLowBandUrl = music.musicUrl;
				music.musicLowBandDataUrl = music.musicDataUrl;
				msg.mediaObject = music;
				msg.title = name;
				msg.description = "我正在Jing搜索：" + cmbt;
				Bitmap thumbBmp = Bitmap.createScaledBitmap(mBitmap, THUMB_SIZE, THUMB_SIZE, false);
				msg.thumbData = bmpToByteArray(thumbBmp, true);
				
				SendMessageToWX.Req req = new SendMessageToWX.Req();
				req.message = msg;
				req.transaction = buildTransaction("music");
				req.scene = isFriend ? SendMessageToWX.Req.WXSceneSession : SendMessageToWX.Req. WXSceneTimeline;
				boolean result = api.sendReq(req);
				Log.e("kid_debug","sendReq result: " + result);
			}
		});
	}
	
	private String buildTransaction(final String type) {
		return (type == null) ? String.valueOf(System.currentTimeMillis()) : type + System.currentTimeMillis();
	}
	
	public static byte[] bmpToByteArray(final Bitmap bmp, final boolean needRecycle) {
		ByteArrayOutputStream output = new ByteArrayOutputStream();
		bmp.compress(CompressFormat.JPEG, 100, output);
		if (needRecycle) {
			bmp.recycle();
		}
		
		byte[] result = output.toByteArray();
		try {
			output.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return result;
	}

}
