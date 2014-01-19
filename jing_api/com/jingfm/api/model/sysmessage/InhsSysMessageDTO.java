package com.jingfm.api.model.sysmessage;

import java.util.List;
/**
 * 好友入驻消息
 * @author lawliet
 *
 */
public class InhsSysMessageDTO extends SysMessageDTO{
	private List<String> frds; //入驻好友昵称
	private List<String> frd_ids; //入驻好友ID
	private List<String> avatars;//入驻好友头像
	private List<Boolean> frdshps;//入驻好友双向关系
	
	public InhsSysMessageDTO(){
		super();
		super.setT(SysMessageType.INHS.getName());
	}

	public List<String> getFrds() {
		return frds;
	}

	public void setFrds(List<String> frds) {
		this.frds = frds;
	}

	public List<String> getFrd_ids() {
		return frd_ids;
	}

	public void setFrd_ids(List<String> frd_ids) {
		this.frd_ids = frd_ids;
	}

	public List<String> getAvatars() {
		return avatars;
	}

	public void setAvatars(List<String> avatars) {
		this.avatars = avatars;
	}

	public List<Boolean> getFrdshps() {
		return frdshps;
	}

	public void setFrdshps(List<Boolean> frdshps) {
		this.frdshps = frdshps;
	}
	
	
}
