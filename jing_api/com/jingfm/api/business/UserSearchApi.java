package com.jingfm.api.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.CommonHttpApi;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.CommonBuilder;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.UserFrdDTO;

public class UserSearchApi {
	
	/**
	 * 搜索api
	 * @param params
	 *  params.put("q",	"周杰伦");
		params.put("ps", 	5);
		params.put("st", 	0);
		params.put("u", 	100112);
		params.put("tid", 	0);
		params.put("mt", 	"");
		params.put("ss", 	true);
	 * @return
	 */
	public static ResultResponse<ListResult<MusicDTO>> fetchPls(Map<Object, Object> params){
		//api todo R
		params.put("d", "R");
		return FunctionResultBuilder.requestListResultResponse(ApiUrlDefine.Normal.Search.Fetch_Pls, params, MusicDTO.class);
	}
	
	/**
	 * 根据昵称搜索用户
	 * @param params u q st ps
	 * @return
	 */
	public static ResultResponse<ListResult<UserFrdDTO>> fetchNick(Map<?, ?> params){
		//return FunctionResultBuilder.requestListResultResponse(ApiUrlDefine.Normal.Search.Fetch_Nick, params, UserFrdDTO.class);
		ResultResponse<ListResult<UserFrdDTO>> response = null;
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.Search.Fetch_Nick),params);
		if(jsonstring != null){
			try {
				response = FunctionResultBuilder.buildUserFrdDTO(jsonstring);
				if(response.isSuccess()){
					List<UserFrdDTO> items = response.getResult().getItems();
					if(!items.isEmpty()){
						StringBuilder sbuids = new StringBuilder();
						for(int i=0;i<items.size();i++){
							if(i ==0) sbuids.append(items.get(i).getUid());
							else sbuids.append(',').append(items.get(i).getUid());
						}
						
						Map<Object, Object> params_pt = new HashMap<Object, Object>(); 
						params_pt.put("uid", params.get("u"));
						params_pt.put("uids", sbuids.toString());
						ResultResponse<List<String>> response_pt = UserRequestApi.fetchUsersPlaytime(params_pt);
						List<String> pts = response_pt.getResult();
						if (pts != null && items.size() == pts.size()) {
							for(int i=0;i<items.size();i++){
								items.get(i).setPt(pts.get(i));
							}
						}
					}
				}
				
				return response;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}
	
	/**
	 * 根据昵称搜索好友
	 * @param params u q st ps
	 * @return
	 */
	public static ResultResponse<ListResult<UserFrdDTO>> fetchFrdNick(Map<?, ?> params){
		//return FunctionResultBuilder.requestListResultResponse(ApiUrlDefine.Normal.Search.Fetch_Frd_Nick, params, UserFrdDTO.class);
		ResultResponse<ListResult<UserFrdDTO>> response = null;
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.Search.Fetch_Frd_Nick),params);
		if(jsonstring != null){
			try {
				response = FunctionResultBuilder.buildUserFrdDTO(jsonstring);
				return response;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}
	
}
