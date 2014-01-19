package com.jingfm.api.business;

import java.util.Map;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.FunctionResultBuilder;

public class DeviceRequestApi {
	/**
	 * 注册设备
	 * @param params uid dt dm cv pv ut dn duuid
	 * @return
	 */
	public static ResultResponse<String> register(Map<Object, Object> params){
		params.put("d", "R");
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.Device.Post_Register,params,String.class);
	}
	
	/**
	 * 销毁设备
	 * @param params uid 
	 * @return
	 */
	public static ResultResponse<String> destory(Map<Object, Object> params){
		params.put("d", "R");
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.Device.Post_Destory,params,String.class);
	}
}
