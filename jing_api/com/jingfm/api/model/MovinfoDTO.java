package com.jingfm.api.model;

public class MovinfoDTO {
	//主题曲
	private String theme_song;
	//片头曲
	private String opening_song;
	//片尾曲
	private String ending_song;
	//插曲
	private String in_song;
	//广告歌曲
	private String jingle;
	
	public MovinfoDTO(){}

	public String getTheme_song() {
		return theme_song;
	}

	public void setTheme_song(String theme_song) {
		this.theme_song = theme_song;
	}

	public String getOpening_song() {
		return opening_song;
	}

	public void setOpening_song(String opening_song) {
		this.opening_song = opening_song;
	}

	public String getEnding_song() {
		return ending_song;
	}

	public void setEnding_song(String ending_song) {
		this.ending_song = ending_song;
	}

	public String getIn_song() {
		return in_song;
	}

	public void setIn_song(String in_song) {
		this.in_song = in_song;
	}

	public String getJingle() {
		return jingle;
	}

	public void setJingle(String jingle) {
		this.jingle = jingle;
	}
	


}
