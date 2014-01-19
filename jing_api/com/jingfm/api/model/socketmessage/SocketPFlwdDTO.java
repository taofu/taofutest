package com.jingfm.api.model.socketmessage;


public class SocketPFlwdDTO extends SocketDTO{
	private String flwer; //nickname
	private String flwer_id; //用户ID
	private String flw; //被关注nickname
	private String flw_id; //被关注用户ID
	private String flw_avatar; //被关注者头像
	private Boolean me_flw;//自己是否关注过被关注者
	private Boolean frdshp;//是否互粉
	
	public SocketPFlwdDTO(){
		super();
	}

	public String getFlwer() {
		return flwer;
	}

	public void setFlwer(String flwer) {
		this.flwer = flwer;
	}

	public String getFlwer_id() {
		return flwer_id;
	}

	public void setFlwer_id(String flwer_id) {
		this.flwer_id = flwer_id;
	}

	public String getFlw_avatar() {
		return flw_avatar;
	}

	public void setFlw_avatar(String flw_avatar) {
		this.flw_avatar = flw_avatar;
	}

	public String getFlw() {
		return flw;
	}

	public void setFlw(String flw) {
		this.flw = flw;
	}

	public String getFlw_id() {
		return flw_id;
	}

	public void setFlw_id(String flw_id) {
		this.flw_id = flw_id;
	}

	public Boolean getMe_flw() {
		return me_flw;
	}

	public void setMe_flw(Boolean me_flw) {
		this.me_flw = me_flw;
	}

	public Boolean getFrdshp() {
		return frdshp;
	}

	public void setFrdshp(Boolean frdshp) {
		this.frdshp = frdshp;
	}

	@Override
	public String getT(){
		return SocketMessageType.FLWD.getName();
	}
	
}
