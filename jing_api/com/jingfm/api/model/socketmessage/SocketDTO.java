package com.jingfm.api.model.socketmessage;

public class SocketDTO {
	private String t;
	private Long ts;
	
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	public Long getTs() {
		return ts == null?0:ts;
	}
	public void setTs(Long ts) {
		this.ts = ts;
	}
	
}
