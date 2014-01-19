package com.jingfm.background_model;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.util.Log;

import com.jingfm.Constants.Constants;
import com.jingfm.ViewManager.LoginStateChangeListener;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.business.DeviceRequestApi;
import com.jingfm.api.business.UserAppRequestApi;
import com.jingfm.api.business.UserOAuthRequestApi;
import com.jingfm.api.business.UserRequestApi;
import com.jingfm.api.context.AppContext;
import com.jingfm.api.context.GuestClientContext;
import com.jingfm.api.model.AppLoginDataDTO;
import com.jingfm.api.model.LoginDataDTO;
import com.jingfm.api.model.oauth.OAuthLoginDataDTO;
import com.jingfm.api.model.oauth.OAuthLoginDataNormalDTO;
import com.jingfm.api.model.oauth.OAuthLoginDataNouserDTO;
import com.jingfm.tools.JingTools;

public class LoginRegisterManager implements LoginStateChangeListener{
	private static LoginRegisterManager instance = new LoginRegisterManager();
	LoginDataDTO loginDataDTO;
	private String mMacAddress;
	private String mAppVersionName;
	private String mLastSentUid;

	public static LoginRegisterManager getInstance() {
		return instance;
	}

	private LoginRegisterManager() {
	}

	public LoginDataDTO getLoginDataDTO() {
		return loginDataDTO;
	}
	public LoginDataDTO setLoginDataDTO(LoginDataDTO loginDataDTO) {
		this.loginDataDTO = loginDataDTO;
		return loginDataDTO;
	}

	public ResultResponse loginByCache() {
		loginDataDTO = null;
		ResultResponse<LoginDataDTO> rs = new ResultResponse<LoginDataDTO>();
		try {
			List dataList = new ArrayList<LoginDataDTO>();
			String lastUserId = SettingManager.getInstance().getmLastUserId();
			if (JingTools.isValidString(lastUserId)) {
				dataList = LocalCacheManager.getInstance().loadCacheData(dataList, LoginDataDTO.class.getName()+lastUserId);
				if (!dataList.isEmpty()) {
					loginDataDTO = (LoginDataDTO) dataList.get(0);
					rs.setSuccess(true);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		return rs;
	}
	
	public ResultResponse loginByToken(String Atoken,String Rtoken) {
		loginDataDTO = null;
		AppContext.getClientContext().setAtoken(Atoken);
		AppContext.getClientContext().setRtoken(Rtoken);
		ResultResponse<LoginDataDTO> rs = UserRequestApi.userValidate(new HashMap<Object, Object>());
		if (rs.isSuccess()) {
			loginDataDTO = rs.getResult();
			cacheLoginData(loginDataDTO);
			registerDevice(""+loginDataDTO.getUsr().getId(), JingService.sXiaomiPushRegID);
		}
		return rs;
	}

	public ResultResponse loginByUserName(String userName, String pwd) {
		loginDataDTO = null;
		Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("email", userName);
		params.put("pwd", pwd);
		params.put("d", "R");
		ResultResponse<LoginDataDTO> rs = UserRequestApi.userLogin(params);
		if (rs.isSuccess()) {
			loginDataDTO = rs.getResult();
			cacheLoginData(loginDataDTO);
			SettingManager.getInstance().setLastUserId(""+loginDataDTO.getUsr().getId());
			SettingManager.getInstance().setAtoken(AppContext.getClientContext().getAtoken());
			SettingManager.getInstance().setRtoken(AppContext.getClientContext().getRtoken());
			registerDevice(""+loginDataDTO.getUsr().getId(), JingService.sXiaomiPushRegID);
		}
		SettingManager.getInstance().setLastUserName(userName);
		return rs;
	}

	public void registerDevice(String uid,String regid) {
		if (!JingTools.isValidString(uid)
				|| !JingTools.isValidString(regid)
				|| uid.equals(mLastSentUid)) {
			return;
		}
		mLastSentUid = uid;
		Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("uid", uid);
//		"dt": xiaomi Regid,
		params.put("dt", regid);
//		"dm":"4C:B1:99:07:12:57",
		params.put("dm", mMacAddress);
//		"cv":"iPhone OS6.1",
		params.put("cv", android.os.Build.VERSION.RELEASE);
//		"drt":1367251378477,
//		params.put("drt", "" + mPhoneNumber);
		params.put("drt", "");
//		"duuid":"A39EA5EC-1BDB-41F0-9EEF-0E8589A25D09",
//		"pv":"1.0.6",
		params.put("pv", "" + mAppVersionName);
//		"so":true,
//		"ut":"iPad31"; 
		params.put("ut", "" + android.os.Build.MODEL);
		Log.e("kid_debug","registerDevice: uid = " + params.get("uid"));
		Log.e("kid_debug","registerDevice: dt = " + params.get("dt"));
		Log.e("kid_debug","registerDevice: dm = " + params.get("dm"));
		Log.e("kid_debug","registerDevice: cv = " + params.get("cv"));
		Log.e("kid_debug","registerDevice: dn = " + params.get("dn"));
		Log.e("kid_debug","registerDevice: drt = " + params.get("drt"));
		Log.e("kid_debug","registerDevice: pv = " + params.get("pv"));
		Log.e("kid_debug","registerDevice: ut = " + params.get("ut"));
		DeviceRequestApi.register(params);
	}

	public ResultResponse loginByGuest() {
		loginDataDTO = null;
//		ResultResponse<AppLoginDataDTO> rs = UserAppRequestApi.userCreate();
		Map<Object, Object> params = new HashMap<Object,Object>();
		params.put("d", GuestClientContext.device);
		params.put("i", Constants.CHANNELID);
		String guid = SettingManager.getInstance().getGuestId();
		ResultResponse<AppLoginDataDTO> rs;
		if (JingTools.isValidString(guid)) {
			params.put("auid", guid);
			rs = UserAppRequestApi.userValidate(params);
		}else{
			rs = UserAppRequestApi.userCreate(params);
		}
		if (rs.isSuccess()) {
			loginDataDTO = rs.getResult().toLoginDataDTO();
			if (loginDataDTO != null) {
				if (loginDataDTO.getUsr() != null) {
					if (loginDataDTO.getUsr().getId() != null) {
						SettingManager.getInstance().setGuestId(""+loginDataDTO.getUsr().getId());
					}
				}
			}
		}
		return rs;
	}

	public ResultResponse loginBy3rdPart(String jsonString) {
		loginDataDTO = null;
		String responseContent = jsonString;
		ResultResponse rs = UserOAuthRequestApi
				.getOAuthAuthorizeDTO(responseContent);
		if (rs.isSuccess()) {
			final OAuthLoginDataDTO rsDTO = (OAuthLoginDataDTO) rs.getResult();
			if (rsDTO.getAssociation()) {
				loginDataDTO = ((OAuthLoginDataNormalDTO) rsDTO)
						.toLoginDataDTO();
			} else {
				((OAuthLoginDataNouserDTO) rsDTO).setChannelID(Constants.CHANNELID);
				rs = UserRequestApi
						.userAutoCreate((OAuthLoginDataNouserDTO) rsDTO);
				if (rs.isSuccess()) {
					LoginDataDTO rsLoginDataDTO = (LoginDataDTO) rs.getResult();
					final HashMap<String, Object> map = new HashMap<String, Object>();
					map.put("uid", rsLoginDataDTO.getUsr().getId());
					map.put("auid", ((OAuthLoginDataNouserDTO) rsDTO).getId());
					map.put("identify", rsDTO.getIdentify());
					new Thread() {
						public void run() {
							UserOAuthRequestApi.postAssociation(map);
						};
					}.start();
					loginDataDTO = rsLoginDataDTO;
				}
			}
			cacheLoginData(loginDataDTO);
			SettingManager.getInstance().setLastUserId(""+loginDataDTO.getUsr().getId());
			SettingManager.getInstance().setAtoken(AppContext.getClientContext().getAtoken());
			SettingManager.getInstance().setRtoken(AppContext.getClientContext().getRtoken());
			registerDevice(""+loginDataDTO.getUsr().getId(), JingService.sXiaomiPushRegID);
		}
		return rs;
	}
	
	public void cacheLoginData(LoginDataDTO data){
		if (data == null || data.getUsr().isGuest()) {
			return;
		}
		try {
			ArrayList<LoginDataDTO> dataList = new ArrayList<LoginDataDTO>();
			dataList.add(data);
			LocalCacheManager.getInstance().saveCacheData(dataList, LoginDataDTO.class.getName() + data.getUsr().getId());
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
	}

	public void polling() {
		new Thread() {
			public void run() {
				while (loginDataDTO != null) {
					// TODO polling

				}
			};
		}.start();
	}

	@Override
	public void onLogin(LoginDataDTO data) {
		loginDataDTO = data;
	}

	@Override
	public void onLogout() {
		cacheLoginData(loginDataDTO);
		loginDataDTO = null;
		SettingManager.getInstance().setAtoken(null);
		SettingManager.getInstance().setRtoken(null);
		SettingManager.getInstance().setLastUserId(null);
	}

	public void setPhoneInfo(String macAddress, String appVersionName) {
		mMacAddress = macAddress;
		mAppVersionName = appVersionName;
	}
}
