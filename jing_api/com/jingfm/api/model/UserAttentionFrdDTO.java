package com.jingfm.api.model;

public class UserAttentionFrdDTO extends UserFrdDTO{
	private String type;
	private String sub_text;

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getSub_text() {
		return sub_text;
	}

	public void setSub_text(String sub_text) {
		this.sub_text = sub_text;
	}
	
}
