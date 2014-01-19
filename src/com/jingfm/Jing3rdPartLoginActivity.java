package com.jingfm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.jingfm.api.ResultResponse;
import com.jingfm.background_model.LoginRegisterManager;

public class Jing3rdPartLoginActivity extends Activity {

	private WebView mainWebView;
	private Handler mHandler;
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
		Jing3rdPartLoginActivity.this.setResult(RESULT_CANCELED);
//		new Thread(){
//			public void run() {
//				TrackerManger.getInstance().sendEvent(TrackerManger.Category_SnsLogin, TrackerManger.Action_Start);
//			};
//		}.start();
		try {
			Bundle a = mIntent.getExtras();
			String url = a.getString("url");
			Log.i("kid_debug", "Jing3rdPartLoginActivity url: " + url);
			mainWebView.loadUrl(url);
		} catch (Exception e) {
			Jing3rdPartLoginActivity.this.setResult(RESULT_FAILED);
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
				ResultResponse resultResponse = LoginRegisterManager
						.getInstance().loginBy3rdPart(jsonString);
				if (resultResponse.isSuccess()) {
					Jing3rdPartLoginActivity.this.setResult(RESULT_OK);
				} else {
					Jing3rdPartLoginActivity.this.setResult(RESULT_FAILED);
				}
			} catch (Exception e) {
				Jing3rdPartLoginActivity.this.setResult(RESULT_FAILED);
			}
			finish();
		}
	}
}
