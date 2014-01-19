package com.jingfm.api.business;

import java.util.Map;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.model.ConfigDTO;

public class ConfigRequestApi {
	
	/**
	 * 请求客户端相关的版本信息
	 * @param params cid
	 * @return
	 */
	public static ResultResponse<ConfigDTO> fetch(Map<Object, Object> params){
		params.put("secretkey", "Ia3LSbC!Taaa4HPmT74qOpZemr<RR*lZ");
		return FunctionResultBuilder.requestSimpleData(ApiUrlDefine.Normal.Config.Fetch_Android, params, ConfigDTO.class);
	}
	
	
}
