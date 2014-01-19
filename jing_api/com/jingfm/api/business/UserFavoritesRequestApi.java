package com.jingfm.api.business;

import java.util.Map;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.favorites.FavoritesCmbtDTO;

public class UserFavoritesRequestApi {
	/**
	 * 收藏搜索条件
	 * @param params uid q
	 * @return 
	 */
	public static ResultResponse<String> postFavoritesCmbt(Map<Object, Object> params){
		params.put("d", "R");
		return FunctionResultBuilder.requestSimpleData(ApiUrlDefine.Normal.Favorites.Post_FavoritesCmbt,params,String.class);
	}
	
	/**
	 * 删除收藏搜索条件
	 * @param params uid q
	 * @return 
	 */
	public static ResultResponse<String> removeFavoritesCmbt(Map<Object, Object> params){
		params.put("d", "R");
		return FunctionResultBuilder.requestSimpleData(ApiUrlDefine.Normal.Favorites.Remove_FavoritesCmbt,params,String.class);
	}
	
	
	/**
	 * 查询收藏搜索条件
	 * @param params uid ouid st ps
	 * @return 
	 */
	public static ResultResponse<ListResult<FavoritesCmbtDTO>> fetchFavoritesCmbt(Map<Object, Object> params){
		params.put("d", "R");
		return FunctionResultBuilder.requestListResultResponse(
				ApiUrlDefine.Normal.Favorites.Fetch_FavoritesCmbt, params, FavoritesCmbtDTO.class);
	}
}
