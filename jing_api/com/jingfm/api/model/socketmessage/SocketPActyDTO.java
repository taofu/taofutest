package com.jingfm.api.model.socketmessage;


public class SocketPActyDTO extends SocketDTO{
	private String content; //活动内容
	private String title; //活动标题
	private Integer id;
	
	public SocketPActyDTO(){
		super();
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}
	@Override
	public String getT(){
		return SocketMessageType.ACTY.getName();
	}

	
}
