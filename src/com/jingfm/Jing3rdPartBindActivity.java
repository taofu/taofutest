package com.jingfm;

import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.UserOAuthRequestApi;

public class Jing3rdPartBindActivity extends Activity {

	private WebView mainWebView;
	private Handler mHandler;
	private String mUid;
	private String mIdentify;
	private Intent mIntent;
	public static final int RESULT_FAILED = 1000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mainWebView = new WebView(this);
		WebViewClient webViewClient = new WebViewClient() {

			@Override
			public boolean shouldOverrideUrlLoading(WebView view, String url) {
				// return super.shouldOverrideUrlLoading(view, url);
				view.loadUrl(url);
				return true;
			}
		};
		mainWebView.setWebViewClient(webViewClient);
		WebSettings webSettings = mainWebView.getSettings();
		webSettings.setJavaScriptEnabled(true);
		mainWebView.addJavascriptInterface(new runJavaScript(),
				"androidRunJavaScript");
		webSettings.setSaveFormData(false);
		webSettings.setSavePassword(false);
		webSettings.setSupportZoom(false);
		setContentView(mainWebView);
		mHandler = new Handler();
		mIntent = getIntent();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		mIntent = intent;
	}

	@Override
	protected void onStart() {
		super.onStart();
		Jing3rdPartBindActivity.this.setResult(RESULT_CANCELED);
		try {
			Bundle a = mIntent.getExtras();
			String url = a.getString("url");
			mUid = a.getString("uid");
			mIdentify = a.getString("identify");
			Log.i("kid_debug", "Jing3rdPartLoginActivity url: " + url);
			mainWebView.loadUrl(url);
		} catch (Exception e) {
			Jing3rdPartBindActivity.this.setResult(RESULT_FAILED);
			finish();
		}
	}

	@Override
	protected void onStop() {
		super.onStop();
		finish();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		// mainWebView = null;
		// System.gc();
	}

	final class runJavaScript {

		// 这个Java 对象是绑定在另一个线程里的，
		public void bindStateCallback(final String jsonString) {
			try {
				ResultResponse<String> resultResponse = UserOAuthRequestApi
						.getOAuthBindDTO(jsonString);
				if (resultResponse.isSuccess()) {
					HashMap<Object, Object> params = new HashMap<Object, Object>();
					params.put("uid", mUid);
					params.put("identify", mIdentify);
					ResultResponse resultResponse1 = UserOAuthRequestApi
							.postBind(params);
					if (resultResponse1.isSuccess()) {
						Intent intent = new Intent();
						intent.putExtra("uid", mUid);
						intent.putExtra("identify", mIdentify);
						Jing3rdPartBindActivity.this.setResult(RESULT_OK,
								intent);
					} else {
						Jing3rdPartBindActivity.this.setResult(RESULT_FAILED);
					}
				} else {
					Jing3rdPartBindActivity.this.setResult(RESULT_FAILED);
				}
			} catch (Exception e) {
				Jing3rdPartBindActivity.this.setResult(RESULT_FAILED);
			}
			finish();
		}
	}
}
