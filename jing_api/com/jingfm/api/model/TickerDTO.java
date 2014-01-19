package com.jingfm.api.model;

import java.io.Serializable;


public class TickerDTO extends AbstractTicker{

    private static final long serialVersionUID = -57263808888000L;  
	
	/**
	 * tick名称
	 */
	private String tit;
	
	/**
	 * tick fid
	 */
	private String fid;
	/**
	 * 歌曲ID
	 */
	private Integer tid;
	/**
	 * 艺人名字
	 */
	private String atn;
	/**
	 * 专辑名字
	 */
	private String an;
	/**
	 * 歌曲时长
	 */
	private String d;
	/**
	 * 歌曲mid
	 */
	private String mid;
	
	/**
	 * 是否喜欢这首歌
	 */
	private boolean loved;
	
	public String getTit() {
		return tit;
	}
	public void setTit(String tit) {
		this.tit = tit;
	}
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public String getAtn() {
		return atn;
	}
	public void setAtn(String atn) {
		this.atn = atn;
	}
	public String getAn() {
		return an;
	}
	public void setAn(String an) {
		this.an = an;
	}
	public String getD() {
		return d;
	}
	public void setD(String d) {
		this.d = d;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public boolean isLoved() {
		return loved;
	}
	public void setLoved(Boolean loved) {
		if(loved == null) return;
		this.loved = loved.booleanValue();
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof TickerDTO){
			TickerDTO now = (TickerDTO)obj;
			return now.getUid() == this.getUid() && now.getTit().equals(this.getTit());
		}else return false;
	}
	
	public MusicDTO toMusicDTO(){
		MusicDTO musicDto = new MusicDTO();
		musicDto.setAn(an);
		musicDto.setAtn(atn);
		musicDto.setTid(tid);
		musicDto.setD(d);
		musicDto.setFid(fid);
		musicDto.setMid(mid);
		musicDto.setN(tit);
		musicDto.setAbid(0);
		musicDto.setAid(0);
		return musicDto;
	}
	
}
