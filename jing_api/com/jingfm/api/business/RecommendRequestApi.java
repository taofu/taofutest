package com.jingfm.api.business;

import java.util.Map;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.RecommendAppDTO;

public class RecommendRequestApi {
	
	/**
	 * 请求客户端相关的版本信息
	 * @param params uid st ps
	 * @return
	 */
	public static ResultResponse<ListResult<RecommendAppDTO>> fetch(Map<Object, Object> params){
		params.put("d", "R");
		return FunctionResultBuilder.requestListResultResponse(ApiUrlDefine.Normal.Recommend.Fetch_Recommend, params, RecommendAppDTO.class);
	}
	
	
}
