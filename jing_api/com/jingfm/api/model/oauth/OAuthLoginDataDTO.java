package com.jingfm.api.model.oauth;



public class OAuthLoginDataDTO {
	private Boolean association;
	private String identify;
	private String JingAToken;
	private String JingRToken;
	private String JingSTime;
	public Boolean getAssociation() {
		return association;
	}
	public void setAssociation(Boolean association) {
		this.association = association;
	}
	public String getIdentify() {
		return identify;
	}
	public void setIdentify(String identify) {
		this.identify = identify;
	}
	public String getJingAToken() {
		return JingAToken;
	}
	public void setJingAToken(String jingAToken) {
		JingAToken = jingAToken;
	}
	public String getJingRToken() {
		return JingRToken;
	}
	public void setJingRToken(String jingRToken) {
		JingRToken = jingRToken;
	}
	public String getJingSTime() {
		return JingSTime;
	}
	public void setJingSTime(String jingSTime) {
		JingSTime = jingSTime;
	}
}
