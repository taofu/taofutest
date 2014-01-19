package com.jingfm.model;

import java.io.Serializable;

import com.jingfm.Constants.Constants;
import com.jingfm.ViewManager.ChatViewManager.ChatUserData;
import com.jingfm.api.CustomerImageRule;
import com.jingfm.api.model.socketmessage.SocketPChatPayloadDTO;

public class CoversationDTO implements Serializable {
	
	private static final long serialVersionUID = -57263878888002L;
	
	String lastMessage;
	String ouid;
	long messageTime;
	String nick;
	String fid;
	
	public CoversationDTO(ChatUserData chatUserData,String ctt,long messageTime, SocketPChatPayloadDTO socketPChatPayloadDTO) {
		this.ouid = chatUserData.getOuid();
		this.nick = chatUserData.getNick();
		this.fid = chatUserData.getFid();
		this.lastMessage = ctt;
		this.messageTime = messageTime;
	}
	
	public String getLastMessage() {
		return lastMessage;
	}
	public void setLastMessage(String lastMessage) {
		this.lastMessage = lastMessage;
	}
	public String getOuid() {
		return ouid;
	}
	public void setOuid(String ouid) {
		this.ouid = ouid;
	}

	public long getMessageTime() {
		return messageTime;
	}

	public void setMessageTime(long messageTime) {
		this.messageTime = messageTime;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

}
