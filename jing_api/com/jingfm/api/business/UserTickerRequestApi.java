package com.jingfm.api.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.RecommendCmbtDTO;
import com.jingfm.api.model.TickerDTO;

public class UserTickerRequestApi {
	
	/**
	 * 
	 * @param params uid,content
	 * @return
	 */
	public static ResultResponse<RecommendCmbtDTO> postCmbt(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.Ticker.Post_Cmbt,params,RecommendCmbtDTO.class);
	}
	
	/**
	 * 请求所有类型的列表
	 * @param params uid st ps
	 * @return
	 */
	public static ResultResponse<ListResult<TickerDTO>> fetchRecents(Map<?, ?> params){
		return FunctionResultBuilder.requestListResultResponse(
				ApiUrlDefine.Normal.Ticker.Fetch_Recents,params,TickerDTO.class);
	}
	
	
	/**
	 * 请求个人的喜欢歌的ticker列表
	 * @param params uid ouid st ps
	 * @return
	 */
	public static ResultResponse<ListResult<TickerDTO>> fetchPersonalLoveTickers(Map<?, ?> params){	
		/*return FunctionResultBuilder.requestListResultResponse(
				ApiUrlDefine.Normal.UserPersonal.FetchLoveTickers,params,TickerDTO.class);*/
		ResultResponse<ListResult<TickerDTO>> response =  FunctionResultBuilder.requestListResultResponse(
				ApiUrlDefine.Normal.Ticker.Fetch_Personal_LoveTickers,params,TickerDTO.class);
		if(response.isSuccess()){
			List<TickerDTO> items = response.getResult().getItems();
			if(!items.isEmpty()){
				addValidateFavoriteTickers(params.get("uid"), items);
			}
			
		}
		
		return response;
	}
	
	/**
	 * 请求只有喜欢歌曲类型的列表
	 * @param params uid st ps
	 * @return
	 */
	public static ResultResponse<ListResult<TickerDTO>> fetchLoveRecents(Map<?, ?> params){
		ResultResponse<ListResult<TickerDTO>> response =  FunctionResultBuilder.requestListResultResponse(
				ApiUrlDefine.Normal.Ticker.Fetch_Love_Recents,params,TickerDTO.class);
		if(response.isSuccess()){
			List<TickerDTO> items = response.getResult().getItems();
			if(!items.isEmpty()){
				addValidateFavoriteTickers(params.get("uid"), items);
			}
			
		}
		
		return response;
	}
	/**
	 * 通用方法,用于自动填充ticker中喜欢的歌对于用户是否喜欢
	 * @param uid
	 * @param items
	 */
	protected static void addValidateFavoriteTickers(Object uid, List<TickerDTO> items){
		try{
			StringBuilder sbtids = new StringBuilder();
			Integer tid = null;
			for(int i=0;i<items.size();i++){
				tid = items.get(i).getTid();
				if(tid != null){
					if(i ==0) sbtids.append(tid);
					else sbtids.append(',').append(tid);
				}
			}
			
			Map<Object, Object> params_pt = new HashMap<Object, Object>(); 
			params_pt.put("uid", uid);
			params_pt.put("tids", sbtids.toString());
			ResultResponse<List<Integer>> response_pt = UserMusicRequestApi.fetchValidateFavorites(params_pt);
			List<Integer> pts = response_pt.getResult();
			
			for(int i=0;i<items.size();i++){
				tid = items.get(i).getTid();
				if(tid != null){
					if(pts.contains(tid)){
						items.get(i).setLoved(true);
					}
				}
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
}
