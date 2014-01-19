package com.jingfm.api.model;

public class UserSnsFrdDTO extends UserFrdDTO{
	private boolean inhs;
	private String auid;
	private String identify;
	
	public boolean isInhs() {
		return inhs;
	}

	public void setInhs(boolean inhs) {
		this.inhs = inhs;
	}
	
	public String getAuid() {
		return auid;
	}

	public void setAuid(String auid) {
		this.auid = auid;
	}

	public String getIdentify() {
		return identify;
	}

	public void setIdentify(String identify) {
		this.identify = identify;
	}
}
