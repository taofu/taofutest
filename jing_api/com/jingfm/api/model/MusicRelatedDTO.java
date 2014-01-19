package com.jingfm.api.model;

public class MusicRelatedDTO{
	private Integer tid;
	private String n;
	private String fid;
	private String desc;
	
	private String realtedtype;

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getDesc() {
		return desc;
	}

	public void setDesc(String desc) {
		this.desc = desc;
	}

	public String getRealtedtype() {
		return realtedtype;
	}

	public void setRealtedtype(String realtedtype) {
		this.realtedtype = realtedtype;
	}

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}
	
}
