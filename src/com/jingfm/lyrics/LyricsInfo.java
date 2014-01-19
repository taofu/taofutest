package com.jingfm.lyrics;

import java.util.HashMap;

public class LyricsInfo {
	
	private String title;	//	[ti:歌曲的标题]
	private String singer;	//	[ar:歌词作者]
	private String album;	//	[al:这首歌所在的唱片集]
	private String by;		//	[by:本LRC文件的创建者]
	private String re;		//	[re:创建此LRC文件的播放器或编辑器]
	private String ve;		//	[ve:程序的版本]
	private long offset;		//	[offset:+/- 以毫秒为单位整体时间戳调整，+增加，-减小]
	private HashMap<Long, String> lineOfContentMS = new HashMap<Long, String>();

	public String getRe() {
		return re;
	}

	public void setRe(String re) {
		this.re = re;
	}

	public String getVe() {
		return ve;
	}

	public void setVe(String ve) {
		this.ve = ve;
	}

	public String getBy() {
		return by;
	}

	public void setBy(String by) {
		this.by = by;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getSinger() {
		return singer;
	}

	public void setSinger(String singer) {
		this.singer = singer;
	}

	public String getAlbum() {
		return album;
	}

	public void setAlbum(String album) {
		this.album = album;
	}

	public long getOffset() {
		return offset;
	}

	public void setOffset(long offset) {
		this.offset = offset;
	}

	public HashMap<Long, String> getLineOfContentMS() {
		return lineOfContentMS;
	}

	public void setLineOfContentMS(HashMap<Long, String> lineOfContentMS) {
		this.lineOfContentMS = lineOfContentMS;
	}
}
