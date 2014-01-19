package com.jingfm.api.model.socketmessage;

import java.util.List;
/**
 * 长连接入驻好友消息
 * @author lawliet
 *
 */
public class SocketPInhsDTO extends SocketDTO{
	private List<String> frds; //入驻好友nicknames
	private List<String> snsfrds; //入驻好友sns nicknames
	private List<String> frd_ids; //入驻好友IDs
	private List<String> avatars;//入驻好友头像
	private List<Boolean> frdshps;//入驻好友是否互粉
	private String fromsns;//入驻的第三方
	
	public SocketPInhsDTO(){
		super();
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

	public List<String> getSnsfrds() {
		return snsfrds;
	}

	public void setSnsfrds(List<String> snsfrds) {
		this.snsfrds = snsfrds;
	}

	public String getFromsns() {
		return fromsns;
	}

	public void setFromsns(String fromsns) {
		this.fromsns = fromsns;
	}
	@Override
	public String getT(){
		return SocketMessageType.INHS.getName();
	}


	
}
