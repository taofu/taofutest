package com.jingfm.api.business;

import java.util.List;
import java.util.Map;

import org.json.JSONException;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.CommonHttpApi;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.builder.CommonBuilder;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.model.ChatRecordDTO;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.SiteMessageDTO;
import com.jingfm.api.model.message.UserOfflineMessageDTO;
import com.jingfm.api.model.sysmessage.SysMessageDTO;

public class UserChatRequestApi {
	
	/**
	 * 请求聊天记录列表
	 * @param params uid fuid st ps
	 * @return
	 */
	public static ResultResponse<ListResult<SiteMessageDTO>> fetchChatctt(Map<?, ?> params){
		/*return FunctionResultBuilder.requestListResultResponse(
				ApiUrlDefine.Normal.Chat.Fetch_Chatctt,params,SiteMessageDTO.class);*/
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.Chat.Fetch_Chatctt),params);
		if(jsonstring != null){
			try {
				ResultResponse<ListResult<SiteMessageDTO>> response = FunctionResultBuilder.buildSiteMessageDTOListResult(jsonstring);
				return response;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
		
	}
	
	/**
	 * 请求聊天会话记录多个
	 * @param params uid frdids
	 * @return
	 */
	public static ResultResponse<List<ChatRecordDTO>> fetchChatsessions(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleListData(
					ApiUrlDefine.Normal.Chat.Fetch_Chatsession, params, ChatRecordDTO.class);
	}
	
	/**
	 * 请求仅自己相关系统消息的列表
	 * @param params uid st ps
	 * @return
	 */
	public static ResultResponse<ListResult<SysMessageDTO>> fetchPersonalSysMessage(Map<?, ?> params){
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.Chat.Fetch_Personal_Sysmessage),params);
		if(jsonstring != null){
			try {
				ResultResponse<ListResult<SysMessageDTO>> response = FunctionResultBuilder.buildSysMessageDTOListResultResponse(jsonstring);
				return response;
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}


    /**
     * 请求仅自己相关离线消息的列表
     * @param params uid
     * @return
     */
    public static ResultResponse<ListResult<UserOfflineMessageDTO>> fetchPersonalOfflineMessage(Map<?, ?> params){
        String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(ApiUrlDefine.Normal.Chat.Fetch_Offline_message),params);
        if(jsonstring != null){
            try {
                ResultResponse<ListResult<UserOfflineMessageDTO>> response = FunctionResultBuilder.buildOfflineMessageDTOListResult(jsonstring);
                return response;
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return CommonBuilder.buildResultError();
    }

	/**
	 * 分享好友歌曲
	 * @param params uid ouid ctt tid
	 * @return
	 */
	public static ResultResponse<String> postShareMusic(Map<?, ?> params){
		return FunctionResultBuilder.requestSimpleData(
				ApiUrlDefine.Normal.Chat.Post_Share_Track,params,String.class);
	}
}
