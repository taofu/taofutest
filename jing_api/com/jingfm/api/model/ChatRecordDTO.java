package com.jingfm.api.model;

import com.jingfm.api.model.socketmessage.SocketPChatPayloadShareTrackDTO;



public class ChatRecordDTO {
	//聊天记录流水ID
	private int nid;
	//聊天会话ID
	private String sid;
	//聊天时间
	private long ts;
	//聊天发送者ID
	private int sender;
	//聊天接受者ID
	private int receive;
	//聊天内容
	private String ctt;
	//扩展业务字段
	private SocketPChatPayloadShareTrackDTO payload;
	
	public String getCtt() {
		return ctt;
	}
	public void setCtt(String ctt) {
		this.ctt = ctt;
	}
	public int getNid() {
		return nid;
	}
	public void setNid(int nid) {
		this.nid = nid;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	public int getSender() {
		return sender;
	}
	public void setSender(int sender) {
		this.sender = sender;
	}
	public int getReceive() {
		return receive;
	}
	public void setReceive(int receive) {
		this.receive = receive;
	}
	public SocketPChatPayloadShareTrackDTO getPayload() {
		return payload;
	}
	public void setPayload(SocketPChatPayloadShareTrackDTO payload) {
		this.payload = payload;
	}

	
	
}
