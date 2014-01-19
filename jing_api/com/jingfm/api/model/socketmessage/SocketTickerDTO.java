package com.jingfm.api.model.socketmessage;

/**
 * ticker dto
 * @author lawliet
 *
 */
public class SocketTickerDTO{
	
	private String t;
	/**
	 * 用户id
	 */
	private Integer uid;
	
	/**
	 * 用户昵称
	 */
	private String nick;
	
	/**
	 * 用户头像
	 */
	private String avt;
	
	/**
	 * 时间
	 */
	private Long ts;
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
	 * 喜欢歌曲的搜索条件
	 */
	private String q;
	
	
	public SocketTickerDTO(){
		super();
	}

	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public Integer getUid() {
		return uid;
	}

	public void setUid(Integer uid) {
		this.uid = uid;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getAvt() {
		return avt;
	}

	public void setAvt(String avt) {
		this.avt = avt;
	}

	public Long getTs() {
		return ts;
	}

	public void setTs(Long ts) {
		this.ts = ts;
	}

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

	public String getQ() {
		return q;
	}

	public void setQ(String q) {
		this.q = q;
	}
	
}
