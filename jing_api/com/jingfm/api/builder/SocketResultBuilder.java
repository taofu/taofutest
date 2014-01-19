package com.jingfm.api.builder;

import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jingfm.api.helper.JsonHelper;
import com.jingfm.api.helper.StringHelper;
import com.jingfm.api.model.UserFrdDTO;
import com.jingfm.api.model.socketmessage.SocketDTO;
import com.jingfm.api.model.socketmessage.SocketMessageType;
import com.jingfm.api.model.socketmessage.SocketPActyDTO;
import com.jingfm.api.model.socketmessage.SocketPAtfdDTO;
import com.jingfm.api.model.socketmessage.SocketPChatDTO;
import com.jingfm.api.model.socketmessage.SocketPChatPayloadDTO;
import com.jingfm.api.model.socketmessage.SocketPChatPayloadShareTrackDTO;
import com.jingfm.api.model.socketmessage.SocketPChatPayloadType;
import com.jingfm.api.model.socketmessage.SocketPFlwdDTO;
import com.jingfm.api.model.socketmessage.SocketPInhsDTO;
import com.jingfm.api.model.socketmessage.SocketPLisnDTO;
import com.jingfm.api.model.socketmessage.SocketPRmndDTO;
import com.jingfm.api.model.socketmessage.SocketPUmftDTO;
import com.jingfm.api.model.socketmessage.SocketPUserSignedoffDTO;
import com.jingfm.api.model.socketmessage.SocketPUserSignedonDTO;
import com.jingfm.api.model.socketmessage.SocketPUtrcDTO;
import com.jingfm.api.model.socketmessage.SocketTickerDTO;

/**
 * 长连接消息处理
 * @author lawliet
 *
 */
public class SocketResultBuilder {

	public static  SocketDTO buildPrivateMessageDTO(String responseContent) throws JSONException {
		JSONObject jsonObject = new JSONObject(responseContent);
		return buildPrivateMessageDTOByType(jsonObject);
	}
	

	
	public static SocketDTO buildPrivateMessageDTOByType(JSONObject jsonObject) throws JSONException{
		String t = jsonObject.getString("t");
		if(StringHelper.isEmpty(t)) return null;
		//聊天
		if(SocketMessageType.CHAT.getName().equals(t)){
			SocketPChatDTO dto = new SocketPChatDTO();
			dto.setFuid(jsonObject.getString("fuid"));
			dto.setNick(jsonObject.getString("nick"));
			dto.setFid(jsonObject.getString("fid"));
			dto.setCtt(jsonObject.getString("ctt"));
			if(!jsonObject.isNull("ts"))
				dto.setTs(jsonObject.getLong("ts"));
			String payload = jsonObject.getString("payload");
			if(StringHelper.isNotEmpty(payload)&&!("null").equals(payload)){
				//JSONObject jsonChatPayloadObject = jsonObject.getJSONObject("payload");
				JSONObject jsonChatPayloadObject = new JSONObject(payload);
				SocketPChatPayloadDTO socketPChatPayloadDto = buildPrivateMessageChatPayloadDTOByType(jsonChatPayloadObject);
				dto.setPayload(socketPChatPayloadDto);
			}
			return dto;
		}
		//入驻好友
		else if(SocketMessageType.INHS.getName().equals(t)){
			return JsonHelper.getDTO(jsonObject, SocketPInhsDTO.class);
		}
		//关注
		else if(SocketMessageType.FLWD.getName().equals(t)){
			return JsonHelper.getDTO(jsonObject, SocketPFlwdDTO.class);
		}
		//好友@你
		else if(SocketMessageType.ATFD.getName().equals(t)){
			return JsonHelper.getDTO(jsonObject, SocketPAtfdDTO.class);
		}
		//正在听同一首歌
		else if(SocketMessageType.LISN.getName().equals(t)){
			SocketPLisnDTO dto = new SocketPLisnDTO();
			dto.setTid(jsonObject.getInt("tid"));
			if(!jsonObject.isNull("ts"))
				dto.setTs(jsonObject.getLong("ts"));
			if(!jsonObject.isNull("items")){
				JSONArray jsonPLisnItemsArray = jsonObject.getJSONArray("items");
				List<UserFrdDTO> itemDtos = JsonHelper.getListDTO(jsonPLisnItemsArray, UserFrdDTO.class);
				dto.setItems(itemDtos);
			}
			return dto;
		}
		//提醒
		else if(SocketMessageType.RMND.getName().equals(t)){
			return JsonHelper.getDTO(jsonObject, SocketPRmndDTO.class);
		}
		//活动
		else if(SocketMessageType.ACTY.getName().equals(t)){
			return JsonHelper.getDTO(jsonObject, SocketPActyDTO.class);
		}
		//收藏ticker
		else if(SocketMessageType.UTRC.getName().equals(t)){
			return JsonHelper.getDTO(jsonObject, SocketPUtrcDTO.class);
		}
		//在ticker上喜欢同样的歌曲
		else if(SocketMessageType.UMFT.getName().equals(t)){
			return JsonHelper.getDTO(jsonObject, SocketPUmftDTO.class);
		}
		//其他用户上线
		else if(SocketMessageType.NSON.getName().equals(t)){
			return JsonHelper.getDTO(jsonObject, SocketPUserSignedonDTO.class);
		}
		//其他用户离线
		else if(SocketMessageType.NSOF.getName().equals(t)){
			return JsonHelper.getDTO(jsonObject, SocketPUserSignedoffDTO.class);
		}
		return null;
	}
	
	public static SocketPChatPayloadDTO buildPrivateMessageChatPayloadDTOByType(JSONObject jsonChatPayloadObject) throws JSONException{
		String t = jsonChatPayloadObject.getString("t");
		if(StringHelper.isEmpty(t)) return null;

		if(String.valueOf(SocketPChatPayloadType.SHARETRACK.getPrefix()).equals(t)){
			return JsonHelper.getDTO(jsonChatPayloadObject, SocketPChatPayloadShareTrackDTO.class);
		}
		return null;
	}
	
	
	public static  SocketTickerDTO buildTickerMessageDTO(String responseContent) throws JSONException {
		JSONObject jsonObject = new JSONObject(responseContent);
		return JsonHelper.getDTO(jsonObject, SocketTickerDTO.class);
	}

	
}
