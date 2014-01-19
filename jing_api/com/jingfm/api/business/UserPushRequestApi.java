package com.jingfm.api.business;

import java.util.List;
import java.util.Map;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.model.push.PushMessageDTO;

public class UserPushRequestApi {
	/**
	 * 查询push消息
	 * @param params dt
	 * @return
	 */
	public static ResultResponse<List<PushMessageDTO>> fetchPushData(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleListResultResponse(ApiUrlDefine.Normal.Push.Fetch_Push, params, PushMessageDTO.class);
	}
	
}
