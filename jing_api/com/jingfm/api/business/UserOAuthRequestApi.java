package com.jingfm.api.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.CommonBuilder;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.context.AppContext;
import com.jingfm.api.helper.StringHelper;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.UserSnsFrdDTO;
import com.jingfm.api.model.oauth.OAuthLoginDataDTO;
import com.jingfm.api.model.oauth.OAuthLoginDataNormalDTO;

public class UserOAuthRequestApi {
	
	public static final String OAuth_Sina_WEIBO_Identify = "sina_weibo";
	public static final String OAuth_Qq_WEIBO_Identify = "qq_weibo";
	public static final String OAuth_Renren_Identify = "renren";
	public static final String OAuth_Douban_Identify = "douban";
	
	public static enum OAuthAction{
		LOGIN, BIND
	}
	
	/*public static String getSinaLoginAuthorizeUrl(){
		return getLoginAuthorizeUrl(OAuth_Sina_Identify);
	}
	
	public static String getRenrenLoginAuthorizeUrl(){
		return getLoginAuthorizeUrl(OAuth_Renren_Identify);
	}*/
	
	
	
	
	/*public static String getSinaBindAuthorizeUrl(String uid){
		return getBindAuthorizeUrl(uid,OAuth_Sina_Identify);
	}
	
	public static String getRenrenBindAuthorizeUrl(String uid){
		return getBindAuthorizeUrl(uid,OAuth_Renren_Identify);
	}
	
	public static String getQqBindAuthorizeUrl(String uid){
		return getBindAuthorizeUrl(uid,OAuth_Qq_Identify);
	}*/
	
	
	
	
	/*public static ResultResponse<ListResult<UserSnsFrdDTO>> fetchSinaFriends(Map<Object, Object> params){
		params.put("identify", OAuth_Sina_Identify);
		return fetchFriends(params);
	}
	
	public static ResultResponse<ListResult<UserSnsFrdDTO>> fetchRenrenFriends(Map<Object, Object> params){
		params.put("identify", OAuth_Renren_Identify);
		return fetchFriends(params);
	}
	
	public static ResultResponse<ListResult<UserSnsFrdDTO>> fetchQqFriends(Map<Object, Object> params){
		params.put("identify", OAuth_Qq_Identify);
		return fetchFriends(params);
	}*/
	
	
	
	
	/*protected static ResultResponse<String> postSinaShareMusic(Map<Object, Object> params){
		params.put("identify", OAuth_Sina_Identify);
		return postShareMusic(params);
	}
	protected static ResultResponse<String> postRenrenShareMusic(Map<Object, Object> params){
		params.put("identify", OAuth_Renren_Identify);
		return postShareMusic(params);
	}*/
	
	/**
	 * 关联用户
	 * @param params uid auid identify
	 * @return 
	 */
	@SuppressWarnings("rawtypes")
	public static ResultResponse<HashMap> postAssociation(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(ApiUrlDefine.Normal.OAuth.Post_Association,params,HashMap.class);
	}
	
	
	/**
	 * 获取用户的第三方绑定列表
	 * @param params uid
	 * @return
	 */
	public static ResultResponse<List<String>> fetchIdentifies(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleListData(
				ApiUrlDefine.Normal.OAuth.Fetch_Identifies, params, String.class);
	}
	
	
	/**
	 * 解除绑定用户
	 * @param params uid identify
	 * @return 
	 */
	public static ResultResponse<String> postRemoveBind(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(ApiUrlDefine.Normal.OAuth.Post_RemoveBind,params,String.class);
	}
	
	/**
	 * 绑定用户
	 * @param params uid identify
	 * @return 
	 */
	public static ResultResponse<String> postBind(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(ApiUrlDefine.Normal.OAuth.Post_Bind,params,String.class);
	}
	
	/**
	 * 邀请第三方好友
	 * @param params uid identify suid nick
	 * @return 
	 */
	public static ResultResponse<String> postInviteFriend(Map<Object, Object> params){
		params.put("d", "R");
		return FunctionResultBuilder.requestSimpleData(ApiUrlDefine.Normal.OAuth.Post_Invite_Friend,params,String.class);
	}
	
	/**
	 * 分享第三方歌曲
	 * @param params uid c tid identify
	 * @return
	 */
	public static ResultResponse<String> postShareMusic(Map<Object, Object> params){
		params.put("d", "R");
		return FunctionResultBuilder.requestSimpleData(ApiUrlDefine.Normal.OAuth.Post_ShareMusic,params,String.class);
	}
	
	
	/**
	 * 分享第三方搜索条件
	 * @param params uid tid q cmbt identify
	 * @return
	 */
	public static ResultResponse<String> postShareCmbt(Map<Object, Object> params){
		params.put("d", "R");
		return FunctionResultBuilder.requestSimpleData(ApiUrlDefine.Normal.OAuth.Post_ShareCmbt,params,String.class);
	}
	
	
	/**
	 * responseContent 登陆 授权成功之后通过服务器返回的json
	 * @return OAuthLoginDataDTO
	 */
	public static ResultResponse<OAuthLoginDataDTO> getOAuthAuthorizeDTO(String responseContent){
		if(responseContent != null){
			try{
				ResultResponse<OAuthLoginDataDTO> response = FunctionResultBuilder.buildOAuthDTO(responseContent);
				OAuthLoginDataDTO result = response.getResult();
				if(result.getAssociation()){
					OAuthLoginDataNormalDTO dto = (OAuthLoginDataNormalDTO)result;
					String atoken = dto.getJingAToken();
					String rtoken = dto.getJingRToken();
					String stime = dto.getJingSTime();
					AppContext.setClientContext(dto.getUsr().getId(), atoken, rtoken,stime);
				}
				return response;
			}catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}
	
	/**
	 * responseContent BIND授权成功之后通过服务器返回的json
	 * @return OAuthLoginDataDTO
	 */
	public static ResultResponse<String> getOAuthBindDTO(String responseContent){
		try{
			return FunctionResultBuilder.buildResult(responseContent, String.class);
		}catch (JSONException e) {
			e.printStackTrace();
		}
		return CommonBuilder.buildResultError();
	}
	/**
	 * 获取第三方好友列表
	 * @param params uid identify st ps
	 * @return
	 */
	public static ResultResponse<ListResult<UserSnsFrdDTO>> fetchFriends(Map<?, ?> params){
		return FunctionResultBuilder.requestListResultResponse(
				ApiUrlDefine.Normal.OAuth.Fetch_Friends, params, UserSnsFrdDTO.class);
	}
	
	public static String getLoginAuthorizeUrl(String identify){
		return getAuthorizeUrl(null,identify,OAuthAction.LOGIN);
	}
	
	public static String getBindAuthorizeUrl(String uid, String identify){
		return getAuthorizeUrl(uid,identify,OAuthAction.BIND);
	}
	
	protected static String getAuthorizeUrl(String uid, String identify, OAuthAction action){
		String url = ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.OAuth.Authorize);
		url = url.concat("?").concat("identify").concat("=").concat(identify);
		url = url.concat("&").concat("action").concat("=").concat(action.toString());
		url = url.concat("&").concat("method").concat("=").concat("OUTPUTHANDLE");
		if(StringHelper.isNotEmpty(uid)){
			url = url.concat("&").concat("uid").concat("=").concat(uid);
		}
		url = url.concat("&").concat("d").concat("=").concat("R");
		
		return url;
	}
	

}
