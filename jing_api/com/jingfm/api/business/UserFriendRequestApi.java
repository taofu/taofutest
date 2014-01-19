package com.jingfm.api.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.context.ClientContext;
import com.jingfm.api.model.BannerDTO;
import com.jingfm.api.model.BannerDetailDTO;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.UserFrdDTO;
import com.jingfm.api.model.UserMtknownFrdDTO;

public class UserFriendRequestApi {
	
	public static ResultResponse<String> checkFrdshp(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(ApiUrlDefine.Normal.UserFriend.CheckFrdshp,params,String.class);
	}
	
	/*
	private static ResultResponse<String> simpleDataRequest(String url,Map<?, ?> params){
		return CommonHttpApi.function_common_post(ApiUrlDefine.getApiUrl(url), params, String.class);
	}*/
	
	
	/**
	 * 获取可能认识的好友
	 * @param params uid ps
	 * @return
	 */
	public static ResultResponse<ListResult<UserMtknownFrdDTO>> fetchMtknownFriends(Map<?, ?> params){
		/*return FunctionResultBuilder.requestListResultResponse(
				ApiUrlDefine.Normal.UserFriend.FetchMtknownFriends, params, UserMtknownFrdDTO.class);*/
		ResultResponse<ListResult<UserMtknownFrdDTO>> response = FunctionResultBuilder.requestListResultResponse(
				ApiUrlDefine.Normal.UserFriend.FetchMtknownFriends, params, UserMtknownFrdDTO.class);
		
		if(response.isSuccess()){
			List<UserMtknownFrdDTO> items = response.getResult().getItems();
			if(!items.isEmpty()){
				StringBuilder sbuids = new StringBuilder();
				for(int i=0;i<items.size();i++){
					if(i ==0) sbuids.append(items.get(i).getUid());
					else sbuids.append(',').append(items.get(i).getUid());
				}
				
				Map<Object, Object> params_pt = new HashMap<Object, Object>(); 
				params_pt.put("uid", params.get("uid"));
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
		/*String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.UserFriend.FetchMtknownFriends),params);
		if(jsonstring != null){
			try {
				return FunctionResultBuilder.buildListResult(jsonstring, UserDTO.class);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();*/
	}
	
	/**
	 * 提交某uid为自己不认识的人
	 * @param params uid fid
	 * @return
	 */
	public static ResultResponse<String> postNotknownFriends(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.UserFriend.PostNotknownFriends,params,String.class);
	}
	
	/**
	 * 获取用户好友
	 * @param params
	 * @return uid ouid st ps
	 */
	public static ResultResponse<ListResult<UserFrdDTO>> fetchFriends(Map<?, ?> params){
		return FunctionResultBuilder.requestListResultResponse(
				ApiUrlDefine.Normal.UserFriend.FetchFriends, params, UserFrdDTO.class);
		/*String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.UserFriend.FetchFriends),params);
		if(jsonstring != null){
			try {
				return FunctionResultBuilder.buildListResult(jsonstring, UserFrdDTO.class);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();*/
	}
	
	/**
	 * 获取用户好友 排序
	 * @param params uid ouid st ps index
	 * @return 
	 */
	public static ResultResponse<ListResult<UserFrdDTO>> fetchFriendsOrder(Map<?, ?> params){
		return fetchUserFrdDtoList(params,ApiUrlDefine.Normal.UserFriend.FetchFriendsOrder);
	}
	
	/**
	 * 获取关注用户 
	 * @param params uid ouid st ps
	 * @return 
	 */
	public static ResultResponse<ListResult<UserFrdDTO>> fetchFollowings(Map<?, ?> params){
		return fetchUserFrdDtoList(params,ApiUrlDefine.Normal.UserFriend.FetchFollowings);
	}
	
	/**
	 * 获取被关注用户 
	 * @param params uid ouid st ps
	 * @return 
	 */
	public static ResultResponse<ListResult<UserFrdDTO>> fetchFolloweds(Map<?, ?> params){
		return fetchUserFrdDtoList(params,ApiUrlDefine.Normal.UserFriend.FetchFolloweds);
	}
	
	/**
	 * 获取Users_Pt 
	 * @param params uid uids
	 * @return 
	 */
	public static ResultResponse<List<String>> fetchUsersPt(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleListResultResponse(ApiUrlDefine.Normal.UserFriend.FetchUsersPt, params, String.class);
	}
	
	/**
	 * 获取Banner 
	 * @param params d
	 * @return 
	 */
	public static ResultResponse<List<BannerDTO>> fetchBanners(){
		HashMap<String, String> params = new HashMap<String, String>();
		params.put("d", ClientContext.device);
		return FunctionResultBuilder.requestSimpleListResultResponse(ApiUrlDefine.Normal.UserFriend.FetchBanners, params, BannerDTO.class);
	}
	
	/**
	 * 获取BannerDTO 
	 * @param params d bid st ps 
	 * @return 
	 */
	public static ResultResponse<ListResult<BannerDetailDTO>> fetchBannersDTO(Map params){
		params.put("d", ClientContext.device);
		return FunctionResultBuilder.requestListResultResponse(ApiUrlDefine.Normal.UserFriend.FetchBannersDetail, params, BannerDetailDTO.class);
	}
	
	public static ResultResponse<ListResult<UserFrdDTO>> fetchUserFrdDtoList(Map<?, ?> params, String uri){
		ResultResponse<ListResult<UserFrdDTO>> response = FunctionResultBuilder.requestListResultResponse(
				uri, params, UserFrdDTO.class);
		
		if(response.isSuccess()){
			List<UserFrdDTO> items = response.getResult().getItems();
			if(!items.isEmpty()){
				StringBuilder sbuids = new StringBuilder();
				for(int i=0;i<items.size();i++){
					if(i ==0) sbuids.append(items.get(i).getUid());
					else sbuids.append(',').append(items.get(i).getUid());
				}
				
				Map<Object, Object> params_pt = new HashMap<Object, Object>(); 
				params_pt.put("uid", params.get("uid"));
				params_pt.put("uids", sbuids.toString());
				ResultResponse<List<String>> response_pt = UserRequestApi.fetchUsersPlaytime(params_pt);
				List<String> pts = response_pt.getResult();
				if (pts != null && items.size() == pts.size()) {
					for(int i=0;i<items.size();i++){
						items.get(i).setPt(pts.get(i));
						//if(i ==0) sbuids.append(items.get(0).getUid());
						//else sbuids.append(',').append(items.get(0).getUid());
					}
				}
			}
		}
		
		return response;
	}
	
	/**
	 * 获取用户在线好友
	 * @param params uid st ps
	 * @return
	 */
	public static ResultResponse<ListResult<UserFrdDTO>> fetchOnlineFriendsOrder(Map<?, ?> params){
		return FunctionResultBuilder.requestListResultResponse(
				ApiUrlDefine.Normal.UserFriend.FetchFriendsOrder, params, UserFrdDTO.class);
		/*String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.UserFriend.FetchOnlineFriends),params);
		if(jsonstring != null){
			try {
				return FunctionResultBuilder.buildListResult(jsonstring, UserFrdDTO.class);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();*/
	}
	
	/**
	 * 获取用户好友黑名单
	 * @param params uid st ps
	 * @return
	 */
	public static ResultResponse<ListResult<UserFrdDTO>> fetchBlockFriends(Map<?, ?> params){
		return FunctionResultBuilder.requestListResultResponse(
				ApiUrlDefine.Normal.UserFriend.FetchBlockFriends, params, UserFrdDTO.class);
		/*String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.UserFriend.FetchBlockFriends),params);
		if(jsonstring != null){
			try {
				return FunctionResultBuilder.buildListResult(jsonstring, UserFrdDTO.class);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();*/
	}
	
	/**
	 * {"msg":"操作成功","result":{"frdshp":false},"success":true}
	 * 关注好友
	 * @param params uid frdid
	 * @return
	 */
	public static ResultResponse<UserFrdDTO> followFrd(Map<?, ?> params){
		return FunctionResultBuilder.requestResultResponse(
				ApiUrlDefine.Normal.UserFriend.FollowFrd, params, UserFrdDTO.class);
	}
	/**
	 * 取消关注
	 * @param params uid frdid
	 * @return
	 */
	public static ResultResponse<String> unfollowFrd(Map<?, ?> params){
		return FunctionResultBuilder.requestResultResponse(
				ApiUrlDefine.Normal.UserFriend.UnfollowFrd, params, String.class);
	}
	
	/**
	 * 提醒好友关注
	 * @param params uid frdid
	 * @return
	 */
	public static ResultResponse<String> remindFrd(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.UserFriend.RemindFrd,params,String.class);
	}
	
	
}
