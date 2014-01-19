package com.jingfm.api.model;

import java.io.Serializable;

public abstract class AbstractTicker  implements Serializable{

    private static final long serialVersionUID = -57263608888000L;  
	/**
	 * ticker type 存储 ticker的prefix char
	 */
	private String t;
	/**
	 * 用户id
	 */
	private Integer uid;
	
	/**
	 * 用户昵称
	 */
	private String nick;
	
	/**
	 * 用户头像
	 */
	private String avt;
	
	/**
	 * 时间
	 */
	private Long ts;
	
	public Integer getUid() {
		return uid;
	}
	
	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getAvt() {
		return avt;
	}
	public void setAvt(String avt) {
		this.avt = avt;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public Long getTs() {
		return ts;
	}
	public void setTs(Long ts) {
		this.ts = ts;
	}
}
