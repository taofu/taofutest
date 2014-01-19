package com.jingfm.api.model.feedback;


/**
 * 歌曲相关错误
 * @author lawliet
 *
 */
public class FeedbackTrackErrorDTO {
	private String d;//描述
	private Integer tid;//歌曲ID
	
	public String getD() {
		return d;
	}
	public void setD(String d) {
		this.d = d;
	}
	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	
}
