package com.jingfm.api.model;

@SuppressWarnings("serial")
public class BadgeDTO implements java.io.Serializable{
	private String fid;//class_name
	private String n;//name
	private String t;//artist or badge
	private String url;//kid need show url
	
	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
