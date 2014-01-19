package com.jingfm.api.model.message;

public class OfflineSiteMessageDTO {
	private Integer uid;
	private String nick; 
	private String avatar; 
	private Integer count;
	private boolean ol;
	
	public OfflineSiteMessageDTO(){
		super();
	}
	
	public OfflineSiteMessageDTO(Integer uid, String nick, String avatar, Integer count){
		super();
		this.uid = uid;
		this.nick = nick;
		this.avatar = avatar;
		this.count = count;
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

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public boolean isOl() {
		return ol;
	}

	public void setOl(Boolean ol) {
        if(ol == null) return;
        this.ol = ol.booleanValue();
	}
	

	
}
