package com.jingfm.api.model;

import java.util.List;


public class UserPersonalDataDTO {
	private UserRelateDTO user;
	private String pt;
	
	/*FRD_FIELD = "frd";
	private static final String BEFRD_FIELD = "befrd";
	private static final String FAVORTRACKFRD_FIELD = "favTk";
	private static final String BADGEFRD_FIELD = "badge";*/
	private String cover;
	private List<UserUnlockBadgeDTO> mood;
	private UserStatDTO stat;
	private List<String> favoralbumfids;
	private List<TickerDTO> tickers;
	public String getPt() {
		return pt;
	}

	public void setPt(String pt) {
		this.pt = pt;
	}

	public List<String> getFavoralbumfids() {
		return favoralbumfids;
	}

	public void setFavoralbumfids(List<String> favoralbumfids) {
		this.favoralbumfids = favoralbumfids;
	}

	public UserStatDTO getStat() {
		return stat;
	}

	public void setStat(UserStatDTO stat) {
		this.stat = stat;
	}

	public List<UserUnlockBadgeDTO> getMood() {
		return mood;
	}

	public void setMood(List<UserUnlockBadgeDTO> mood) {
		this.mood = mood;
	}

	public String getCover() {
		return cover;
	}

	public void setCover(String cover) {
		this.cover = cover;
	}

	public UserRelateDTO getUser() {
		return user;
	}

	public void setUser(UserRelateDTO user) {
		this.user = user;
	}

	public List<TickerDTO> getTickers() {
		return tickers;
	}

	public void setTickers(List<TickerDTO> tickers) {
		this.tickers = tickers;
	}
	
}
