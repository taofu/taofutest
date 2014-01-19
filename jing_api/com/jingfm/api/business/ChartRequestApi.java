package com.jingfm.api.business;

import java.util.Map;

import org.json.JSONException;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.CommonHttpApi;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.CommonBuilder;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.model.ChartNodeDTO;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.MusicDTO;

public class ChartRequestApi {
	
	/**
	 * 请求榜单级层数据
	 * @param params uid nodeids
	 * @return
	 */
	public static ResultResponse<Map<String,ListResult<ChartNodeDTO>>> fetchCharts(Map<?, ?> params){
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.Chart.Fetch_Charts), params);
		if(jsonstring != null){
			try {
				ResultResponse<Map<String,ListResult<ChartNodeDTO>>> response = FunctionResultBuilder.buildChartNodeListResultResponse(jsonstring);
				return response;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}
	
	/**
	 * 请求榜单歌曲列表(支持多个)
	 * @param params uid nodeids
	 * @return
	 */
	public static ResultResponse<Map<String,ListResult<MusicDTO>>> fetchChartMusics(Map<?, ?> params){
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.Chart.Fetch_Charts), params);
		if(jsonstring != null){
			try {
				ResultResponse<Map<String,ListResult<MusicDTO>>> response = FunctionResultBuilder.buildChartMusicListResultResponse(jsonstring);
				return response;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}
	
	/**
	 * 请求榜单歌曲列表(只支持1个)
	 * @param params uid nodeids
	 * @return
	 */
	public static ResultResponse<ListResult<MusicDTO>> fetchChartMusic(Map<?, ?> params){
		ResultResponse<Map<String,ListResult<MusicDTO>>> responseMap = fetchChartMusics(params);
		if(responseMap.isSuccess()){
			Object nodeid = params.get("nodeids");
			if(nodeid != null){
				ListResult<MusicDTO> result = responseMap.getResult().get(nodeid);
				ResultResponse<ListResult<MusicDTO>> responselist = ResultResponse.create();
				responselist.setCode(responseMap.getCode());
				responselist.setCodemsg(responseMap.getCodemsg());
				responselist.setSuccess(responseMap.isSuccess());
				//打乱items顺序
				result.shuffleitems();
				responselist.setResult(result);
				return responselist;
			}
		}
		return CommonBuilder.buildResultError();
	}
}
