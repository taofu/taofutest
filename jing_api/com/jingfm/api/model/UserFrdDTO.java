package com.jingfm.api.model;

import java.io.Serializable;

//"avatar":"2012051415lgl.jpg?9","flwd":true,"frdshp":false,"from":"","id":"100434","nick":"麦太太3","ol":true,"uid":"100434"
public class UserFrdDTO implements Serializable{
	
    private static final long serialVersionUID = -57263878888000L;  
	
	private String uid;
	private boolean flwd;
	private boolean frdshp;
	private String from;
	private String nick;
	private String avatar;
	private String device;
	private boolean ol;
	//个人总播放时长
	private String pt;
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}

	public boolean isFlwd() {
		return flwd;
	}
	public void setFlwd(Boolean flwd) {
		if(flwd == null) return; 
		this.flwd = flwd.booleanValue();
	}
	public boolean isFrdshp() {
		return frdshp;
	}
	public void setFrdshp(Boolean frdshp) {
		if(frdshp == null) return; 
		this.frdshp = frdshp.booleanValue();
	}
	public String getFrom() {
		return from;
	}
	public void setFrom(String from) {
		this.from = from;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public boolean isOl() {
		return ol;
	}
	public void setOl(Boolean ol) {
		if(ol == null) return; 
		this.ol = ol.booleanValue();
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
	public String getDevice() {
		return device;
	}
	public void setDevice(String device) {
		this.device = device;
	}
	
}
