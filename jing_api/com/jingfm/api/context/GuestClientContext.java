package com.jingfm.api.context;


public class GuestClientContext {
	//api todo R
	public static final String device = "AR";
	
	private int uid;
	private String gtoken;//guest token
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getGtoken() {
		return gtoken;
	}
	public void setGtoken(String gtoken) {
		this.gtoken = gtoken;
	}
	
	public boolean validate(){
		return uid >0;
	}
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("uid:").append(uid).append(" gtoken:").append(gtoken);
		return sb.toString();
	}
}
