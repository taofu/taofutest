package com.jingfm.api.model.oauth;

/**
 * 通过第三方账号登陆,没有绑定的用户情况下的DTO
 * @author lawliet
 *
 */
public class OAuthLoginDataNouserDTO extends OAuthLoginDataDTO{
	private String id;
	private String nick;
	private String avatar;
	private String channelID;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getNick() {
		return nick;
	}
	public void setNick(String nick) {
		this.nick = nick;
	}
	public String getAvatar() {
		return avatar;
	}
	public void setAvatar(String avatar) {
		this.avatar = avatar;
	}
	public void setChannelID(String channelID) {
		this.channelID = channelID;
	}
	public String getChannelID() {
		return channelID;
	}
	
}
