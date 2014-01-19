package com.jingfm.api.business;

import java.util.Map;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.FunctionResultBuilder;

public class UserSettingRequestApi {
	
	/**
	 * 修改个人开关设置
	 * @param params uid n v
	 * @return
	 */
	public static ResultResponse<String> postSettings(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.Setting.Post_Settings,params,String.class);
	}
}
