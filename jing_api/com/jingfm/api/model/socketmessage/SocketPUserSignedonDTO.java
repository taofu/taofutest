package com.jingfm.api.model.socketmessage;

/**
 * 好友上线通知
 * @author lawliet
 *
 */
public class SocketPUserSignedonDTO extends SocketDTO{
	private String uid; //用户ID
	private String nick; //昵称
	private String avatar; //头像
	
	public SocketPUserSignedonDTO(){
		super();
	}

	public String getUid() {
		return uid;
	}

	public void setUid(String uid) {
		this.uid = uid;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	@Override
	public String getT(){
		return SocketMessageType.NSON.getName();
	}

	
}
