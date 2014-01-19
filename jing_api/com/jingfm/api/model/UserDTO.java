package com.jingfm.api.model;

import java.io.Serializable;

//"usr":{"id":100112,"sid":"54cd264c_0e5a_4830_b4b5_fce77c101d45","newbie":0,"regip":"221.122.120.226","nick":"鷹擊長空","c":1,"avatar":"2012033009kxz.jpg"},
public class UserDTO implements Serializable{

    private static final long serialVersionUID = -57263802888000L; 
    
	private Integer id;
	private String sid;
	private Integer newbie;
	private String regip;
	private String nick = "未知";
	private Integer c;
	private String avatar;
	private boolean guest;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getSid() {
		return sid;
	}
	public void setSid(String sid) {
		this.sid = sid;
	}
	public Integer getNewbie() {
		return newbie;
	}
	public void setNewbie(Integer newbie) {
		this.newbie = newbie;
	}
	public String getRegip() {
		return regip;
	}
	public void setRegip(String regip) {
		this.regip = regip;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		if (nick == null) {
			this.nick = "未知";
		}else{
			this.nick = nick;
		}
	}
	public Integer getC() {
		return c;
	}
	public void setC(Integer c) {
		this.c = c;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public boolean isGuest() {
		return guest;
	}
	public void setGuest(boolean guest) {
		this.guest = guest;
	}
	
	public UserFrdDTO toUserFrdDTO(){
		UserFrdDTO userFrdDTO = new UserFrdDTO();
		userFrdDTO.setUid(""+id);
		userFrdDTO.setNick(nick);
		userFrdDTO.setAvatar(avatar);
		return userFrdDTO;
	}
	
}
