package com.jingfm.api.model.socketmessage;


public class SocketPAtfdDTO extends SocketDTO{
	private String frd_id; //好友ID
	private String frd; //好友nick
	private String cmbt;//组合内容

	public SocketPAtfdDTO(){
		super();
	}

	public String getFrd_id() {
		return frd_id;
	}

	public void setFrd_id(String frd_id) {
		this.frd_id = frd_id;
	}

	public String getFrd() {
		return frd;
	}

	public void setFrd(String frd) {
		this.frd = frd;
	}

	public String getCmbt() {
		return cmbt;
	}

	public void setCmbt(String cmbt) {
		this.cmbt = cmbt;
	}

	@Override
	public String getT(){
		return SocketMessageType.ATFD.getName();
	}

	
}
