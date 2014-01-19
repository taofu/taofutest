package com.jingfm.api.model.socketmessage;

/**
 * 在ticker上喜欢同样的歌曲
 * @author lawliet
 *
 */
public class SocketPUmftDTO extends SocketDTO{
	private Integer uid; //用户ID
	private String nick; //用户nick
	private Integer tid;//歌曲的ID
	private String n;//歌曲名字
	
	public SocketPUmftDTO(){
		super();
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}
	
	@Override
	public String getT(){
		return SocketMessageType.UMFT.getName();
	}
}
