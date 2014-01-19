package com.jingfm.api.business;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.CommonHttpApi;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.CommonBuilder;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.context.AppContext;
import com.jingfm.api.context.ClientContext;
import com.jingfm.api.context.GuestClientContext;
import com.jingfm.api.model.AppLoginDataDTO;
import com.jingfm.api.model.BadgeDTO;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.PrefixDTO;
import com.jingfm.api.model.SearchwordDTO;
import com.jingfm.api.model.UserFrequentDTO;
import com.jingfm.api.model.UserPersonalDataDTO;
/**
 * GUEST操作API
 * @author lawliet
 *
 */
public class UserAppRequestApi {
	
	
	protected static Map<Object, Object> getAppMap(){
		Map<Object, Object> params = new HashMap<Object,Object>();
		params.put("d", GuestClientContext.device);
		return params;
	}
	
	protected static void addAppDefaultMap(Map<Object, Object> params){
		params.put("d", GuestClientContext.device);
		params.put("token", AppContext.getGuestClientContext().getGtoken());
	}
	
	protected static Map<Object, Object> getAppUidMap(){
		Map<Object, Object> params = getAppMap();
		params.put("auid", AppContext.getGuestClientContext().getUid());
		return params;
	}
	
	
	/**
	 * 注册用户
	 * @param params d i
	 * @return
	 */
	public static ResultResponse<AppLoginDataDTO> userCreate(Map<Object, Object> params){
		params.put("d", GuestClientContext.device);
		return userCreateOrLogin(ApiUrlDefine.Normal.Guest.Create,params);
	}
	
	/*public static ResultResponse<AppLoginDataDTO> userCreate(Map<Object, Object> params){
		return userCreateOrLogin(ApiUrlDefine.Normal.Guest.Create,params);
	}*/
	
	/**
	 * 
	 * @param auid d i
	 * @return
	 */
	public static ResultResponse<AppLoginDataDTO> userLogin(Map<Object, Object> params){
		params.put("d", GuestClientContext.device);
		params.put("auid", AppContext.getGuestClientContext().getUid());
		return userCreateOrLogin(ApiUrlDefine.Normal.Guest.SessionCreate,params);
	}
	
	/**
	 * 用户 Token Validate登录
	 * @param auid d i
	 * @return
	 */
	public static ResultResponse<AppLoginDataDTO> userValidate(Map<Object, Object> params){
//		params.put("d", GuestClientContext.device);
//		params.put("auid", AppContext.getGuestClientContext().getUid());
		return userCreateOrLogin(ApiUrlDefine.Normal.Guest.SessionValidate,params);
	}
	
	
	private static ResultResponse<AppLoginDataDTO> userCreateOrLogin(String url,Map<Object, Object> params){	
		Map<String,String> contexPayload = new HashMap<String,String>();
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(url),params,contexPayload,false);
		if(jsonstring != null){
			try {
				ResultResponse<AppLoginDataDTO> response = FunctionResultBuilder.buildAppLoginDataDTO(jsonstring);
				if(response.isSuccess()){
					AppLoginDataDTO dto = response.getResult();
					String stime = contexPayload.get(ClientContext.JingSTimeHeader);
					AppContext.setGuestClientContext(dto.getAppusr().getId(), dto.getAppusr().getGuesttoken(),stime);
				}
				return response;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}
	
	/**
	 * GUEST最有听者
	 * @param params ps
	 * @return
	 */
	public static ResultResponse<ListResult<UserFrequentDTO>> fetchFrequent(Map<Object, Object> params){
		addAppDefaultMap(params);
		return FunctionResultBuilder.requestListResultResponse(
				ApiUrlDefine.Normal.Guest.Frequent, params, UserFrequentDTO.class);
	}
	
	/**
	 * GUEST访问个人数据
	 * @param params ouid 
	 * @return
	 */
	public static ResultResponse<UserPersonalDataDTO> userFetchPersonal(Map<Object, Object> params){
		addAppDefaultMap(params);
		ResultResponse<UserPersonalDataDTO> response = null;
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.Guest.FetchPersonal),params);
		if(jsonstring != null){
			try {
				response = FunctionResultBuilder.buildUserPersonalDataDTO(jsonstring);
				return response;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}
	
	/**
	 * GUEST搜索api
	 * @param params
	 *  params.put("q",	"周杰伦");
		params.put("ps", 	5);
		params.put("st", 	0);
		params.put("tid", 	0);
		params.put("mt", 	"");
	 * @return
	 */
	public static ResultResponse<ListResult<MusicDTO>> fetchPls(Map<Object, Object> params){
		addAppDefaultMap(params);
		params.put("d", "P");
		return FunctionResultBuilder.requestListResultResponse(ApiUrlDefine.Normal.Guest.Fetch_Pls, params, MusicDTO.class);
	}
	
	/**
	 * GUEST auto搜索api
	 * @param params 
	 *  params.put("q",	"周杰伦");
		params.put("ps", 	5);
		params.put("st", 	0);
	 * @return
	 */
	public static ResultResponse<List<PrefixDTO>> fetchAuto(Map<Object, Object> params,boolean ispop){
		addAppDefaultMap(params);
		if(ispop)
			return FunctionResultBuilder.requestSimpleListResultResponse(ApiUrlDefine.Normal.Guest.Fetch_Pop, params, PrefixDTO.class);
		return FunctionResultBuilder.requestSimpleListResultResponse(ApiUrlDefine.Normal.Guest.Fetch_Auto, params, PrefixDTO.class);
	}
	/**
	 * GUEST 提取优秀搜索
	 * @param params st ps
	 * @return
	 */
	public static ResultResponse<ListResult<PrefixDTO>> fetchNtlg(Map<Object, Object> params){
		addAppDefaultMap(params);
		ResultResponse<ListResult<SearchwordDTO>> response = FunctionResultBuilder.requestListResultResponse(ApiUrlDefine.Normal.Guest.Fetch_Ntlg, params, SearchwordDTO.class);
		if(response.isSuccess()){
			ResultResponse<ListResult<PrefixDTO>> curesponse = ResultResponse.create();
			curesponse.setSuccess(response.isSuccess());
			curesponse.setCode(response.getCode());
			curesponse.setCodemsg(response.getCodemsg());
			ListResult<PrefixDTO> result = new ListResult<PrefixDTO>();
			result.setSt(response.getResult().getSt());
			result.setPs(response.getResult().getPs());
			result.setTotal(response.getResult().getTotal());
			List<PrefixDTO> items = new ArrayList<PrefixDTO>();
			PrefixDTO dto = null;
			for(SearchwordDTO sdto:response.getResult().getItems()){
				dto = new PrefixDTO();
				dto.setN(sdto.getSw());
				dto.setFid(sdto.getFid());
				items.add(dto);
			}
			result.setItems(items);
			curesponse.setResult(result);
			//curesponse.getResult().setItems(items);
			return curesponse;
		}else{
			return CommonBuilder.buildResultError();
		}
	}
	
	
	/**
	 * ntlg_auto搜索api
	 * @param params 
	 *  params.put("q",	"z");
		params.put("ps", 	5);
		params.put("st", 	0);
	 * @return
	 */
	public static ResultResponse<List<PrefixDTO>> fetchNtlgAuto(Map<Object, Object> params){
		addAppDefaultMap(params);
		return FunctionResultBuilder.requestSimpleListResultResponse(ApiUrlDefine.Normal.Guest.Fetch_Ntlg_Auto, params, PrefixDTO.class);
	}
	
	/**
	 * 请求所有类型的列表
	 * @param params t ps d
	 * @return
	 */
	public static ResultResponse<List<BadgeDTO>> fetchBadges(Map<Object, Object> params){
		addAppDefaultMap(params);
		return FunctionResultBuilder.requestSimpleListResultResponse(ApiUrlDefine.Normal.Guest.Fetch_Badges, params, BadgeDTO.class);
	}
	
}
