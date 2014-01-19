package com.jingfm.api.model.push;
/**
 * PUSH消息体DTO
 * @author lawliet
 *
 */
public class PushMessageDTO {
	private String t;//类型
	private String payload;//消息载体
	private long ts;//时间
	
	public PushMessageDTO(){}
	public PushMessageDTO(String t){
		this.t = t;
	}
	
	public String getT() {
		return t;
	}
	public void setT(String t) {
		this.t = t;
	}
	public String getPayload() {
		return payload;
	}
	public void setPayload(String payload) {
		this.payload = payload;
	}
	public long getTs() {
		return ts;
	}
	public void setTs(long ts) {
		this.ts = ts;
	}
	
	
	
}
