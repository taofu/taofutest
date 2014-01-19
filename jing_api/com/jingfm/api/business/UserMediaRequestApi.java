package com.jingfm.api.business;

import java.util.Map;

import org.json.JSONException;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.CommonHttpApi;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.CommonBuilder;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.sysmessage.SysMessageDTO;

public class UserMediaRequestApi {
	
	/**
	 * 用户本地缓存歌曲和服务器同步API
	 * @param params u tids
	 * @return
	 */
	public static ResultResponse<ListResult<MusicDTO>> set_offline_Music(Map<?, ?> params){
		/*return FunctionResultBuilder.requestSimpleListResultResponse(
				ApiUrlDefine.Normal.Media.Offline_Music,params,Integer.class);*/
		return FunctionResultBuilder.requestListResultResponse(ApiUrlDefine.Normal.Media.Offline_Music, params, MusicDTO.class);
	}
	
	public static ResultResponse<Map<String, Object>> offline_Music(Map<?, ?> params){
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.Media.Offline_Music),params);
		if(jsonstring != null){
			try {
//				ResultResponse<ListResult<SysMessageDTO>> response = FunctionResultBuilder.buildSysMessageDTOListResultResponse(jsonstring);
				return FunctionResultBuilder.buildOffLineMusicMessageDTOListResult(jsonstring);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}
	
}
