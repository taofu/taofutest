package com.jingfm.api.model.sysmessage;
/**
 * 用户提醒对方关注消息
 * @author lawliet
 *
 */
public class RmndSysMessageDTO extends SysMessageDTO{
	private Integer frd_id; //好友ID
	private String frd; //好友nick
	private String avatar;//好友头像
	private boolean frdshp;//双向关系

	public RmndSysMessageDTO(){
		super();
		super.setT(SysMessageType.RMND.getName());
	}

	public Integer getFrd_id() {
		return frd_id;
	}

	public void setFrd_id(Integer frd_id) {
		this.frd_id = frd_id;
	}

	public String getFrd() {
		return frd;
	}

	public void setFrd(String frd) {
		this.frd = frd;
	}

	public String getAvatar() {
		return avatar;
	}

	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}

	public boolean isFrdshp() {
		return frdshp;
	}

	public void setFrdshp(Boolean frdshp) {
		this.frdshp = frdshp;
	}
	
	
}
