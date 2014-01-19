package com.jingfm.api.model;


public class ConfigDTO {
	private String start;
	private String start_url;
	private String version;
	private String app_url;
	private String permit;
	private String cdn_url;
	private String cdn_start;
	private String update_message;
	public String getStart() {
		return start;
	}
	public void setStart(String start) {
		this.start = start;
	}
	public String getStart_url() {
		return start_url;
	}
	public void setStart_url(String start_url) {
		this.start_url = start_url;
	}
	public String getVersion() {
		return version;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public String getApp_url() {
		return app_url;
	}
	public void setApp_url(String app_url) {
		this.app_url = app_url;
	}
	public String getPermit() {
		return permit;
	}
	public void setPermit(String permit) {
		this.permit = permit;
	}
	public String getCdn_url() {
		return cdn_url;
	}
	public void setCdn_url(String cdn_url) {
		this.cdn_url = cdn_url;
	}
	public String getCdn_start() {
		return cdn_start;
	}
	public void setCdn_start(String cdn_start) {
		this.cdn_start = cdn_start;
	}
	public String getUpdate_message() {
		return update_message;
	}
	public void setUpdate_message(String update_message) {
		this.update_message = update_message;
	}
}
