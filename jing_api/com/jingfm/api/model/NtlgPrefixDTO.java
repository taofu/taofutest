package com.jingfm.api.model;

import java.io.Serializable;

public class NtlgPrefixDTO implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -8402762199762665663L;
//	Artist("艺人搜索","ats"),
//	Tag("标签搜索","tag"),
//	SoundTrack("原声专辑搜索","sta"),
//	Alike("相似歌曲","alike"),
//	Cmbt("自然语言","cmbt"),
//	Friend("好友语言","frd");
	private String t;//instantsearch type
	private String n;//所有关于名字的
	private String fid;//所有封面、图片
	private String an;//艺人名字
	private String abn;//专辑名字
	private String tt;//标签tag类别
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
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
	public String getAn() {
		return an;
	}
	public void setAn(String an) {
		this.an = an;
	}
	public String getAbn() {
		return abn;
	}
	public void setAbn(String abn) {
		this.abn = abn;
	}
	public String getTt() {
		return tt;
	}
	public void setTt(String tt) {
		this.tt = tt;
	}
	public static enum IsearchType {
		Artist("艺人搜索","ats","收听这位艺人的音乐电台"),
		Tag("标签搜索","tag","收听有关这个标签下的电台"),
		SoundTrack("原声专辑搜索","sta","收听这部剧作的原声电台"),
		Alike("相似歌曲","alike","收听与这首歌曲类似的音乐电台"),
		Cmbt("自然语言","cmbt","好玩的搜索条件产生的音乐电台"),
		Friend("好友语言","friend","");
		;
		private String name;
		private String cname;
		private String subtitle;
		
		private IsearchType(String cname, String name,String subtitle){
			this.name = name;
			this.cname = cname;
			this.subtitle = subtitle;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getCname() {
			return cname;
		}

		public void setCname(String cname) {
			this.cname = cname;
		}

		public String getSubtitle() {
			return subtitle;
		}

		public void setSubtitle(String subtitle) {
			this.subtitle = subtitle;
		}
	}
	
}
