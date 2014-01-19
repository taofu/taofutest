package com.jingfm.api.model;

public class PlyinfoDTO {
	private int time;
	private String action;
	
	public PlyinfoDTO(){}
	public PlyinfoDTO(String action, int time){
		this.action = action;
		this.time = time;
	}
	
	public int getTime() {
		return time;
	}
	public void setTime(int time) {
		this.time = time;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

}
