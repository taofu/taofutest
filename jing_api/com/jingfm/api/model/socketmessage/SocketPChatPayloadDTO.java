package com.jingfm.api.model.socketmessage;
/**
 * 用于聊天的时候的扩展字段 接口
 * @author lawliet
 *
 */
public class SocketPChatPayloadDTO {
	//扩展聊天类型(分享给好友歌曲等类型)
	private char t;

	public char getT() {
		return t;
	}

	public void setT(char t) {
		this.t = t;
	}
	
}
