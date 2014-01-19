package com.jingfm.api.model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class UserFavMusicDTO extends MusicDTO implements Serializable{
	
    private static final long serialVersionUID = -57263828888000L;  
	
	private Long ts;//喜欢的时间
	private String feat;//合唱
	
	public UserFavMusicDTO() {
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
