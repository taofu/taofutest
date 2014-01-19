package com.jingfm.api.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserMtknownFrdDTO implements Serializable{
	
	private Integer uid;
	private String nick;
	private String avatar;
	private String pt;
	
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
	public String getPt() {
		return pt;
	}
	public void setPt(String pt) {
		this.pt = pt;
	}
	public UserFrdDTO toUserFrdDTO(){
		UserFrdDTO dto = new UserFrdDTO();
		dto.setUid(uid.toString());
		dto.setNick(nick);
		dto.setAvatar(avatar);
		dto.setPt(pt);
		return dto;
	}
}
