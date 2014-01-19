package com.jingfm.api.business;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.CommonHttpApi;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.CommonBuilder;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.model.CasualCmbtDTO;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.NtlgPrefixDTO;
import com.jingfm.api.model.PrefixDTO;
import com.jingfm.api.model.SearchwordDTO;

public class UserAutoSearchApi {
	
	/**
	 * 隨便聽聽返回結果
	 * @param params uid
	 * @param ispop
	 * @return
	 */
	public static ResultResponse<CasualCmbtDTO> fetchCasualCmbt(Map<?, ?> params){
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.Auto.Fetch_Casual_Cmbt),params);
		if(jsonstring != null){
			try {
				ResultResponse<CasualCmbtDTO> response = FunctionResultBuilder.buildCasualCmbtDTO(jsonstring);
				return response;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
		/*if(ispop)
			return FunctionResultBuilder.requestSimpleListResultResponse(ApiUrlDefine.Normal.Auto.Fetch_Pop, params, PrefixDTO.class);
		return FunctionResultBuilder.requestSimpleListResultResponse(ApiUrlDefine.Normal.Auto.Fetch_Auto, params, PrefixDTO.class);*/
	}
	
	/**
	 * auto搜索api
	 * @param params 
	 *  params.put("q",	"周杰伦");
		params.put("ps", 	5);
		params.put("st", 	0);
		params.put("u", 	100112);
	 * @return
	 */
	public static ResultResponse<List<PrefixDTO>> fetchAuto(Map<?, ?> params,boolean ispop){
		if(ispop)
			return FunctionResultBuilder.requestSimpleListResultResponse(ApiUrlDefine.Normal.Auto.Fetch_Pop, params, PrefixDTO.class);
		return FunctionResultBuilder.requestSimpleListResultResponse(ApiUrlDefine.Normal.Auto.Fetch_Auto, params, PrefixDTO.class);
	}
	
	
	/**
	 * ntlg_auto搜索api
	 * @param params 
	 *  params.put("q",	"z");
		params.put("ps", 	5);
		params.put("st", 	0);
		params.put("u", 	100112);
	 * @return
	 */
	public static ResultResponse<List<PrefixDTO>> fetchNtlgAuto(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleListResultResponse(ApiUrlDefine.Normal.Auto.Fetch_Ntlg_Auto, params, PrefixDTO.class);
	}
//	public static ResultResponse<List<NtlgPrefixDTO>> fetchNtlgSauto(Map<?, ?> params){
//		return FunctionResultBuilder.requestSimpleListResultResponse(ApiUrlDefine.Normal.Auto.Fetch_Ntlg_Sauto, params, NtlgPrefixDTO.class);
//	}
	public static ResultResponse<List<NtlgPrefixDTO>> fetchNtlgSauto(Map<?, ?> params){
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.Auto.Fetch_Ntlg_Sauto),params);
		if(jsonstring != null){
			try {
				if(jsonstring == null){
					return CommonBuilder.buildSimpleListResultError(null);
				}
				//System.out.println("+++++++++:"+responseContent);
				JSONObject jsonObject = new JSONObject(jsonstring);
				boolean success = jsonObject.getBoolean("success");
				if(!success){
					return CommonBuilder.buildSimpleListResultError(jsonObject);
				}
				
				ResultResponse<List<NtlgPrefixDTO>> response= ResultResponse.create();
				
				String msg = jsonObject.getString("msg");

				JSONObject result = jsonObject.getJSONObject("result");
				JSONArray items = result.getJSONArray("items");
				response.setResult(CommonBuilder.buildSimpleListResult(items, NtlgPrefixDTO.class));
				response.setSuccess(true);
				response.setCodemsg(msg);
				return response;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}
	
	public static ResultResponse<ListResult<PrefixDTO>> fetchNtlg(Map<?, ?> params){
		ResultResponse<ListResult<SearchwordDTO>> response = FunctionResultBuilder.requestListResultResponse(ApiUrlDefine.Normal.Auto.Fetch_Ntlg, params, SearchwordDTO.class);
		if(response.isSuccess()){
			ResultResponse<ListResult<PrefixDTO>> curesponse = ResultResponse.create();
			curesponse.setSuccess(response.isSuccess());
			curesponse.setCode(response.getCode());
			curesponse.setCodemsg(response.getCodemsg());
			ListResult<PrefixDTO> result = new ListResult<PrefixDTO>();
			result.setSt(response.getResult().getSt());
			result.setPs(response.getResult().getPs());
			result.setTotal(response.getResult().getTotal());
			//curesponse.getResult().setSt(response.getResult().getSt());
			//curesponse.getResult().setPs(response.getResult().getPs());
			//curesponse.getResult().setTotal(response.getResult().getTotal());
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
}
