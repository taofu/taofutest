package com.jingfm;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.jingfm.third_part_api.WeixinUtil;
import com.tencent.mm.sdk.openapi.BaseReq;
import com.tencent.mm.sdk.openapi.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

	private IWXAPI api;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.e("kid_debug", "WXEntryActivity onCreate");
		api = WXAPIFactory.createWXAPI(this, WeixinUtil.APP_ID, false);
		api.handleIntent(getIntent(), this);
	}

	@Override
	public void onReq(BaseReq arg0) {
		Log.e("kid_debug", "onReq  --- arg0 = " + arg0);
		finish();
	}

	@Override
	public void onResp(BaseResp resp) {
		Log.e("kid_debug", "onResp  --- arg0 = " + resp);
		int result = 0;
		switch (resp.errCode) {
		case BaseResp.ErrCode.ERR_OK:
			result = R.string.errcode_success;
			break;
		case BaseResp.ErrCode.ERR_USER_CANCEL:
			result = R.string.errcode_cancel;
			break;
		case BaseResp.ErrCode.ERR_AUTH_DENIED:
			result = R.string.errcode_deny;
			break;
		default:
			result = R.string.errcode_unknown;
			break;
		}
		Toast.makeText(this, result, Toast.LENGTH_LONG).show();
		finish();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.e("kid_debug", "##############requestCode = " + requestCode);
		Log.e("kid_debug", "resultCode = " + resultCode);
		Log.e("kid_debug", "data = " + data);
		finish();
	}
}
