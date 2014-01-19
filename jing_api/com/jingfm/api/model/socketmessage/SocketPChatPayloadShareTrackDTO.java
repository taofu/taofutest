package com.jingfm.api.model.socketmessage;

import com.jingfm.api.model.MusicDTO;


/**
 * 用于聊天的时候的扩展字段
 * 分享歌曲类型
 * @author lawliet
 *
 */
public class SocketPChatPayloadShareTrackDTO extends SocketPChatPayloadDTO{
	
	private Integer aid;//艺人id
	private String atn;//艺人名字
	private Integer abid;//专辑id
	private String an;//专辑名
	private Integer tid;//歌曲ID
	private String n;//歌曲名称
	private String mid;//media fid Media URL，返回对应的媒体文件mid地址
	private String fid;//cover fid 返回对应封面专辑图片的url地址。大小为：300x300
	private String d;//歌曲时长，单位：秒
	
	@Override
	public char getT(){
		return SocketPChatPayloadType.SHARETRACK.getPrefix();
	}

	public MusicDTO toMusicDTO(){
		MusicDTO musicDto = new MusicDTO();
		musicDto.setAbid(abid);
		musicDto.setAid(aid);
		musicDto.setAn(an);
		musicDto.setAtn(atn);
		musicDto.setD(d);
		musicDto.setFid(fid);
		musicDto.setMid(mid);
		musicDto.setN(an);
		musicDto.setTid(tid);
		return musicDto;
	}
	
	public int getAid() {
		return aid;
	}

	public void setAid(int aid) {
		this.aid = aid;
	}

	public String getAtn() {
		return atn;
	}

	public void setAtn(String atn) {
		this.atn = atn;
	}

	public int getAbid() {
		return abid;
	}

	public void setAbid(int abid) {
		this.abid = abid;
	}

	public String getAn() {
		return an;
	}

	public void setAn(String an) {
		this.an = an;
	}

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public String getMid() {
		return mid;
	}

	public void setMid(String mid) {
		this.mid = mid;
	}

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	public String getD() {
		return d;
	}

	public void setD(String d) {
		this.d = d;
	}
	
	
}
