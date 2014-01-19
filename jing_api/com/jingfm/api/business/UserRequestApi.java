package com.jingfm.api.business;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.CommonHttpApi;
import com.jingfm.api.ResponseErrorCode;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.CommonBuilder;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.context.AppContext;
import com.jingfm.api.context.ClientContext;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.LoginDataDTO;
import com.jingfm.api.model.UserAvatarsDTO;
import com.jingfm.api.model.UserDetailDTO;
import com.jingfm.api.model.UserFrequentDTO;
import com.jingfm.api.model.UserRelateDTO;
import com.jingfm.api.model.oauth.OAuthLoginDataNouserDTO;

public class UserRequestApi {
	
	/**
	 * 取得多人的播放时长
	 * @param params uid uids
	 * @return
	 */
	public static ResultResponse<List<String>> fetchUsersPlaytime(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleListResultResponse(
				ApiUrlDefine.Normal.User.FetchUsersPlaytime, params, String.class);
	}

	/**
	 * 用户头像上传
	 * @param params uid file(File)
	 * @return
	 */
	public static ResultResponse<String> userAvatarUpload(Map<?, ?> params){
		if(params == null) return CommonBuilder.buildResultError();
		if(params.containsKey("uid") && params.containsKey("file")){
			String jsonstring = CommonHttpApi.postMultifiles(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.User.AvatarUpload),params);
			if(jsonstring != null){
				try {
					return FunctionResultBuilder.buildResult(jsonstring,String.class);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return CommonBuilder.buildResultError();
		}
		return CommonBuilder.buildResultError(Integer.parseInt(ResponseErrorCode.COMMON_DATA_PARAM_ERROR.code()));
		/*CommonHttpApi.postMultifiles(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.User.AvatarUpload),params,files);
		return null;*/
		/*if(jsonstring != null){
			try {
				ResultResponse<LoginDataDTO> response = FunctionResultBuilder.buildLoginDataDTO(jsonstring);
				String atoken = contexPayload.get(ClientContext.JingATokenHeader);
				String rtoken = contexPayload.get(ClientContext.JingRTokenHeader);
				AppContext.setClientContext(response.getResult().getUsr().getId(), atoken, rtoken);
				return response;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();*/
		
		/*return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.User.AvatarUpload,params,String.class);*/
	}
	
	
	public static ResultResponse<String> userCoverUpload(Map<?, ?> params){
		if(params == null) return CommonBuilder.buildResultError();
		if(params.containsKey("uid") && params.containsKey("file")){
			String jsonstring = CommonHttpApi.postMultifiles(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.User.CoverUpload),params);
			if(jsonstring != null){
				try {
					return FunctionResultBuilder.buildResult(jsonstring,String.class);
				} catch (JSONException e) {
					e.printStackTrace();
				}
			}
			return CommonBuilder.buildResultError();
		}
		return CommonBuilder.buildResultError(Integer.parseInt(ResponseErrorCode.COMMON_DATA_PARAM_ERROR.code()));
		/*CommonHttpApi.postMultifiles(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.User.AvatarUpload),params,files);
		return null;*/
		/*if(jsonstring != null){
			try {
				ResultResponse<LoginDataDTO> response = FunctionResultBuilder.buildLoginDataDTO(jsonstring);
				String atoken = contexPayload.get(ClientContext.JingATokenHeader);
				String rtoken = contexPayload.get(ClientContext.JingRTokenHeader);
				AppContext.setClientContext(response.getResult().getUsr().getId(), atoken, rtoken);
				return response;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();*/
		
		/*return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.User.AvatarUpload,params,String.class);*/
	}
	/**
	 * 注册用户
	 * @param params
	 * @return
	 */
	public static ResultResponse<LoginDataDTO> userCreate(Map<Object, Object> params){
		return userCreateOrLogin(ApiUrlDefine.Normal.User.Create,params,false);
	}
	
	/**
	 * 自动注册用户
	 * @return
	 */
	public static ResultResponse<LoginDataDTO> userAutoCreate(OAuthLoginDataNouserDTO dto){
		Map<Object, Object> params = new HashMap<Object, Object>();
		params.put("d", ClientContext.device);
		params.put("auid", dto.getId());
		params.put("identify", dto.getIdentify());
		params.put("nick", dto.getNick());
		params.put("i", dto.getChannelID());
		return userCreateOrLogin(ApiUrlDefine.Normal.OAuth.Post_Auto_Create,params,false);
	}
	
	/**
	 * 用户 email及密码登录
	 * @param params
	 * @return
	 */
	public static ResultResponse<LoginDataDTO> userLogin(Map<Object, Object> params){
		return userCreateOrLogin(ApiUrlDefine.Normal.User.SessionCreate,params,false);
	}
	
	/**
	 * 用户 Token Validate登录
	 * @param params
	 * @return
	 */
	public static ResultResponse<LoginDataDTO> userValidate(Map<Object, Object> params){
		return userCreateOrLogin(ApiUrlDefine.Normal.User.SessionValidate,params,true);
	}
	
	private static ResultResponse<LoginDataDTO> userCreateOrLogin(String url,Map<Object, Object> params,boolean loginvalidate){
		params.put("d", ClientContext.device);
		Map<String,String> contexPayload = new HashMap<String,String>();
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(url),params,contexPayload,loginvalidate);
		if(jsonstring != null){
			try {
				ResultResponse<LoginDataDTO> response = FunctionResultBuilder.buildLoginDataDTO(jsonstring);
				if(response.isSuccess()){
					String atoken = contexPayload.get(ClientContext.JingATokenHeader);
					String rtoken = contexPayload.get(ClientContext.JingRTokenHeader);
					String stime = contexPayload.get(ClientContext.JingSTimeHeader);
					AppContext.setClientContext(response.getResult().getUsr().getId(), atoken, rtoken,stime);
				}
				return response;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}
	
	
	/**
	 * 用户昵称重复检查
	 * @param params nick oldnick
	 * @return
	 */
	public static ResultResponse<String> checkNick(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.User.CheckNick,params,String.class);
	}
	
	public static ResultResponse<String> checkEmail(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.User.CheckEmail,params,String.class);
	}
	
	public static ResultResponse<String> checkPermalink(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.User.CheckPermalink,params,String.class);
	}
	
	
	/**
	 * 忘记密码API
	 * @param params email
	 * @return
	 */
	public static ResultResponse<String> forgotPassword(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.User.ForgotPassword,params,String.class);
	}
	
	/**
	 * 邀请码验证
	 * @param params invt
	 * @return
	 */
	public static ResultResponse<String> verifyInvitation(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.User.VerifyInvitation,params,String.class);
	}
	
	/**
	 * 通知后台注册流程结束请求
	 * @param params uid
	 * @return
	 */
	public static ResultResponse<String> registerCompleted(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.User.RegisterCompleted,params,String.class);
	}
	
	/**
	 * 注册用户 密码修改请求
	 * @param params
	 * @return
	 */
	public static ResultResponse<String> changePwd(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.User.ChangePwd,params,String.class);
	}
	
	
	/*
	private static ResultResponse<String> simpleDataRequest(String url,Map<?, ?> params){
		return CommonHttpApi.function_common_post(ApiUrlDefine.getApiUrl(url), params, String.class);
	}*/
	
	/**
	 * 用户信息修改请求
	 * @param params uid nick bday permailink sex bio isreg
	 *  
	 * @return
	 */
	public static ResultResponse<UserDetailDTO> updateProfile(Map<?, ?> params){
		return FunctionResultBuilder.requestResultResponse(
				ApiUrlDefine.Normal.User.UpdateProfile, params, UserDetailDTO.class);
		/*String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.User.UpdateProfile),params);
		if(jsonstring != null){
			try {
				return FunctionResultBuilder.build(jsonstring,UserDetailDTO.class);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();*/
	}
	
	/**
	 * 用户信息请求
	 * @param params uid
	 * @return
	 */
	public static ResultResponse<UserDetailDTO> fetchProfile(Map<?, ?> params){
		return FunctionResultBuilder.requestResultResponse(
				ApiUrlDefine.Normal.User.FetchProfile, params, UserDetailDTO.class);
		/*String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.User.FetchProfile),params);
		if(jsonstring != null){
			try {
				return FunctionResultBuilder.build(jsonstring,UserDetailDTO.class);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();*/
	}
	
	/**
	 * 获取用户头像信息 所有的包括sns
	 * @param params uid
	 * @return
	 */
	public static ResultResponse<UserAvatarsDTO> fetchAvatar(Map<?, ?> params){
		return FunctionResultBuilder.requestResultResponse(
				ApiUrlDefine.Normal.User.FetchAvatar, params, UserAvatarsDTO.class);
		/*String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.User.FetchAvatar),params);
		if(jsonstring != null){
			try {
				return FunctionResultBuilder.buildUserAvatarsDTO(jsonstring);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();*/
	}
	
	/**
	 * 最优听者 
	 * @param params uid ps
	 * @return
	 */
	public static ResultResponse<ListResult<UserFrequentDTO>> fetchFrequent(Map<?, ?> params){
		return FunctionResultBuilder.requestListResultResponse(
				ApiUrlDefine.Normal.User.Frequent, params, UserFrequentDTO.class);
		/*String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.User.Frequent),params);
		if(jsonstring != null){
			try {
				return FunctionResultBuilder.buildListResult(jsonstring,UserFrequentDTO.class);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();*/
	}
	
	
	/**
	 * 关注某人 
	 * @param params uid frdid
	 * @return
	 */
	public static ResultResponse<UserRelateDTO> followUser(Map<?, ?> params){
		return FunctionResultBuilder.requestResultResponse(
				ApiUrlDefine.Normal.User.followUser, params, UserRelateDTO.class);
	}
	
	/**
	 * 放入垃圾箱 
	 * @param params uid frdid
	 * @return
	 */
	public static ResultResponse<String> blockUser(Map<?, ?> params){
		return FunctionResultBuilder.requestResultResponse(
				ApiUrlDefine.Normal.User.blockUser,params,String.class);
	}
	
	
}
