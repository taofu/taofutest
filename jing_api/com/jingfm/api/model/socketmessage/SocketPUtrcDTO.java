package com.jingfm.api.model.socketmessage;

/**
 * 收藏ticker
 * @author lawliet
 *
 */
public class SocketPUtrcDTO extends SocketDTO{
	private Integer uid; //用户ID
	private String nick; //用户nick
	private String tit;//收藏的ticker内容

	public SocketPUtrcDTO(){
		super();
	}

	public int getUid() {
		return uid;
	}

	public void setUid(int uid) {
		this.uid = uid;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getTit() {
		return tit;
	}

	public void setTit(String tit) {
		this.tit = tit;
	}
	
	@Override
	public String getT(){
		return SocketMessageType.UTRC.getName();
	}
	
	
}
