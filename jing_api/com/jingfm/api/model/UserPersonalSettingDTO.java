package com.jingfm.api.model;

import java.util.List;

public class UserPersonalSettingDTO {
	/************************** 访问自己时候使用 ****************************/
	//开关设置
	private UserSettingSwitchDTO userswitch;
	//个人网址收藏设置
	private List<String> userlinks;
	
	
	/************************** 访问别人时候使用 ****************************/
	//个人信息
	private UserDTO userinfo;
	//个人SNS状态
	private List<String> identifys;
	
	
	public UserSettingSwitchDTO getUserswitch() {
		return userswitch;
	}
	public void setUserswitch(UserSettingSwitchDTO userswitch) {
		this.userswitch = userswitch;
	}
	public List<String> getUserlinks() {
		return userlinks;
	}
	public void setUserlinks(List<String> userlinks) {
		this.userlinks = userlinks;
	}
	public UserDTO getUserinfo() {
		return userinfo;
	}
	public void setUserinfo(UserDTO userinfo) {
		this.userinfo = userinfo;
	}
	public List<String> getIdentifys() {
		return identifys;
	}
	public void setIdentifys(List<String> identifys) {
		this.identifys = identifys;
	}
	
	
	
}
