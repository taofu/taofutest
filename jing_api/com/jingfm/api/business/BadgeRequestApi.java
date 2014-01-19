package com.jingfm.api.business;

import java.util.List;
import java.util.Map;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.context.ClientContext;
import com.jingfm.api.model.BadgeDTO;

public class BadgeRequestApi {
	
	/**
	 * 请求所有类型的列表
	 * @param params t ps uid d
	 * @return
	 */
	public static ResultResponse<List<BadgeDTO>> fetchBadges(Map<Object, Object> params){
		params.put("d", ClientContext.device);
		return FunctionResultBuilder.requestSimpleListResultResponse(ApiUrlDefine.Normal.Badge.Fetch_Badges, params, BadgeDTO.class);
	}
	
	
	/**
	 * 请求所有类型的列表(去除过滤)
	 * @param params t ps uid d
	 * @return
	 */
	public static ResultResponse<List<BadgeDTO>> fetchBadgesByfilter(Map<Object, Object> params){
		params.put("d", ClientContext.device);
		return FunctionResultBuilder.requestSimpleListResultResponse(ApiUrlDefine.Normal.Badge.Fetch_Badges_byfilter, params, BadgeDTO.class);
	}
}
