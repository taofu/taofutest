package com.jingfm.api.model.socketmessage;

import java.util.List;

import com.jingfm.api.model.UserFrdDTO;

public class SocketPLisnDTO extends SocketDTO{
	private Integer tid;
	private List<UserFrdDTO> items;

	public SocketPLisnDTO(){
		super();
	}

	public Integer getTid() {
		return tid;
	}

	public void setTid(Integer tid) {
		this.tid = tid;
	}

	public List<UserFrdDTO> getItems() {
		return items;
	}

	public void setItems(List<UserFrdDTO> items) {
		this.items = items;
	}

	@Override
	public String getT(){
		return SocketMessageType.LISN.getName();
	}

	
}
