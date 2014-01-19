package com.jingfm.api.model;

import java.io.Serializable;


public class UserPlayingDTO implements Serializable{
	
    private static final long serialVersionUID = -57263802888001L;  
	
	private Integer uid; //用户ID
	private String cmbt; //组合名字
	private Integer tid; //歌曲ID
	private String ct;   //歌曲正在播放时间
	private String fid;  //歌曲专辑FID
	private String mid;  //歌曲媒体MID
	private String n;    //歌曲名字
	private String d;    //歌曲总时长
	private String an;   //歌曲专辑名称
	private String atn;  //艺人名字
	
	public String getCmbt() {
		return cmbt;
	}
	public void setCmbt(String cmbt) {
		this.cmbt = cmbt;
	}
	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public String getCt() {
		return ct;
	}
	public void setCt(String ct) {
		this.ct = ct;
	}
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getN() {
		return n;
	}
	public void setN(String n) {
		this.n = n;
	}
	public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}
	public String getD() {
		return d;
	}
	public void setD(String d) {
		this.d = d;
	}
	public String getAn() {
		return an;
	}
	public void setAn(String an) {
		this.an = an;
	}
	public String getAtn() {
		return atn;
	}
	public void setAtn(String atn) {
		this.atn = atn;
	}
	
	public MusicDTO toMusicDTO(){
		MusicDTO dto = new MusicDTO();
		dto.setAtn(this.getAtn());
		dto.setAn(this.getAn());
		//dto.setAbid(abid);
		//dto.setAid(aid);
		dto.setD(this.getD());
		dto.setFid(this.getFid());
		dto.setMid(this.getMid());
		dto.setN(this.getN());
		dto.setTid(this.getTid());
		return dto;
	}
	
	public static UserPlayingDTO fromMusicDTO(MusicDTO musicDTO){
		UserPlayingDTO dto = new UserPlayingDTO();
		dto.setAtn(musicDTO.getAtn());
		dto.setAn(musicDTO.getAn());
//		dto.setAbid(abid);
//		dto.setAid(aid);
		dto.setD(musicDTO.getD());
		dto.setFid(musicDTO.getFid());
		dto.setMid(musicDTO.getMid());
		dto.setN(musicDTO.getN());
		dto.setTid(musicDTO.getTid());
		return dto;
	}
	
}
