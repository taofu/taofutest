package com.jingfm.api.model;

@SuppressWarnings("serial")
public class UserHateMusicDTO extends MusicDTO{
	private Long ts;//讨厌的时间
	private String feat;//合唱
	
	public UserHateMusicDTO() {
		super();
	}

	public Long getTs() {
		return ts;
	}

	public void setTs(Long ts) {
		this.ts = ts;
	}

	public String getFeat() {
		return feat;
	}

	public void setFeat(String feat) {
		this.feat = feat;
	}
	
	

}
