package com.jingfm.api.model.socketmessage;


public class SocketPRmndDTO extends SocketDTO{
	private Integer frd_id; //好友ID
	private String frd; //好友nick
	private String avatar;//好友头像
	private Boolean frdshp;//是否互粉

	public SocketPRmndDTO(){
		super();
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
	
	public Boolean getFrdshp() {
		return frdshp;
	}

	public void setFrdshp(Boolean frdshp) {
		this.frdshp = frdshp;
	}

	@Override
	public String getT(){
		return SocketMessageType.RMND.getName();
	}

}
