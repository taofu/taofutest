package com.jingfm.api.context;

import com.jingfm.api.helper.StringHelper;

public class ClientContext {
	
	public static final String JingATokenHeader = "Jing-A-Token-Header";
	public static final String JingRTokenHeader = "Jing-R-Token-Header";
	//改为取格式化之后的服务器时间信息
//	public static final String JingSTimeHeader = "Jing-S-Time-Header";
	public static final String JingSTimeHeader = "Jing-S-F-Time-Header";
	
	public static final String JingUidRequestParam = "uid";
	public static final String JingATokenRequestParam = "atoken";
	public static final String JingRTokenRequestParam = "rtoken";
	public static final String JingPwdRequestParam = "pwd";
	
	public static final String device = "R";
	private int uid;
	//private String email;
	private String atoken;
	private String rtoken;
	//private long stime;
	
	public int getUid() {
		return uid;
	}
	public void setUid(int uid) {
		this.uid = uid;
	}
	public String getAtoken() {
		return atoken;
	}
	public void setAtoken(String atoken) {
		this.atoken = atoken;
	}
	public String getRtoken() {
		return rtoken;
	}
	public void setRtoken(String rtoken) {
		this.rtoken = rtoken;
	}
	/*public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}*/
	
	public boolean validate(){
		if(uid >0 && StringHelper.isNotEmpty(getRtoken()) && StringHelper.isNotEmpty(getAtoken()))
			return true;
		return false;
	}
	
	/*public long getStime() {
		return stime;
	}
	public void setStime(long stime) {
		this.stime = stime;
	}*/
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb.append("uid:").append(uid).append(" atoken:").append(atoken).append(" rtoken:").append(rtoken);
		return sb.toString();
	}
}
