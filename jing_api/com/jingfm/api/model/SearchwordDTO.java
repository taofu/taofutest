package com.jingfm.api.model;

public class SearchwordDTO{
	//search_words
	private String sw;
	private String fid;
	
	public SearchwordDTO(){}
	public SearchwordDTO(String sw, String fid){
		this.sw = sw;
		this.fid = fid;
	}
	
	public String getSw() {
		return sw;
	}

	public void setSw(String sw) {
		this.sw = sw;
	}
	
	public String getFid() {
		return fid;
	}
	
	public void setFid(String fid) {
		this.fid = fid;
	}
	
}
