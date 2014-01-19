package com.jingfm.api.model.sysmessage;
/**
 * 个人系统消息基类
 * @author lawliet
 *
 */
public class SysMessageDTO {
	private String t;
	private Long ts;
	
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	public Long getTs() {
		return ts;
	}
	public void setTs(Long ts) {
		this.ts = ts;
	}
	
	
}
