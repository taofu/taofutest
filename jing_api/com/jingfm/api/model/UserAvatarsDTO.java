package com.jingfm.api.model;

import java.util.List;

public class UserAvatarsDTO {
	private Integer id;
	private List<AvatarDTO> items;
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public List<AvatarDTO> getItems() {
		return items;
	}
	public void setItems(List<AvatarDTO> items) {
		this.items = items;
	}
	
	
}
