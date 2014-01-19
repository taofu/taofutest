package com.jingfm.api.business;

import java.util.HashMap;
import java.util.Map;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.context.AppContext;
import com.jingfm.api.context.ClientContext;
import com.jingfm.api.context.GuestClientContext;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.MusicDTO;

public class UserClickRequestApi {
	/**
	 * 提交听歌时长
	 * @param params uid d tid
	 * @return
	 */
	public static ResultResponse<String> clickPlayduration(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.Click.Post_Playduration,params,String.class);
	}
	
	/**
	 * 喜欢电台
	 * @param params uid ouid tit t fid(可选)
	 * tit 是搜索条件
	 * 如果不是在ticker上收藏，t传C(大写)，fid不传,ouid传自己ID
	 */
	public static ResultResponse<String> clickFavs(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.Click.Post_Favs,params,String.class);
	}
	
	/**
	 * 提交播放进度
	 * @param params uid cmbt tid ct isclear
	 * @return
	 */
	public static ResultResponse<String> clickPlaydata(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.Click.Post_PlayingData,params,String.class);
	}
	
	/**
	 * 提交渠道LOG
	 * @param params mac channel v d
	 * @return
	 */
	public static ResultResponse<String> clickChanneldata(Map<Object, Object> params){
		params.put("d", ClientContext.device);
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.Click.Post_ChannelData,params,String.class);
	}
	
	/**
	 * 提交GUEST 心跳
	 * @param params unique d
	 * @return
	 */
	public static ResultResponse<String> clickHeartbeat(Map<Object, Object> params){
		params.put("unique", AppContext.getGuestClientContext().getGtoken());
		params.put("d", GuestClientContext.device);
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.Click.Post_Heartbeat,params,String.class);
	}
	/**
	 * 提交GUEST 心跳
	 * @param params unique d
	 * @return
	 */
	public static ResultResponse<String> clickHeartbeat(){
		Map<Object, Object> params = new HashMap<Object, Object>();
		return clickHeartbeat(params);
	}
}
