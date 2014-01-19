package com.jingfm.api.model;

import java.io.Serializable;
import java.util.List;
import java.util.Map;


@SuppressWarnings("serial")
//"lvd":"n","tid":1251909,"vsn":["原版"],"ply_info":"{'timeDot':{'民谣吉他':[{'扫弦':'0'}]}}","cmps_info":{"composers":"Sophie Zelmani","occp":"Sophie Zelmani","arrangers":"Sophie Zelmani","singer":"Sophie Zelmani","songwriters":"Sophie Zelmani"}
public class MusicInfoDTO implements Serializable{
	private String lvd;
	private Integer tid;
	
	private boolean lrc;
	
	private List<String> vsn;
	
	private List<PlyinfoDTO> ply_info;
	
	private List<TrackDTO> orgn;
	//atst,ftw
	private Map<String,String> feat;
	//theme_song,opening_song等
	private MovinfoDTO mov_info;
	//composers,songwriters
	private Map<String,String> cmps_info;
	public String getLvd() {
		return lvd;
	}
	public void setLvd(String lvd) {
		this.lvd = lvd;
	}
	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public List<String> getVsn() {
		return vsn;
	}
	public void setVsn(List<String> vsn) {
		this.vsn = vsn;
	}
	public List<PlyinfoDTO> getPly_info() {
		return ply_info;
	}
	public void setPly_info(List<PlyinfoDTO> ply_info) {
		this.ply_info = ply_info;
	}
	public List<TrackDTO> getOrgn() {
		return orgn;
	}
	public void setOrgn(List<TrackDTO> orgn) {
		this.orgn = orgn;
	}
	public Map<String, String> getFeat() {
		return feat;
	}
	public void setFeat(Map<String, String> feat) {
		this.feat = feat;
	}
	public MovinfoDTO getMov_info() {
		return mov_info;
	}
	public void setMov_info(MovinfoDTO mov_info) {
		this.mov_info = mov_info;
	}
	public Map<String, String> getCmps_info() {
		return cmps_info;
	}
	public void setCmps_info(Map<String, String> cmps_info) {
		this.cmps_info = cmps_info;
	}
	public boolean isLrc() {
		return lrc;
	}
	public void setLrc(boolean lrc) {
		this.lrc = lrc;
	}
	
}
