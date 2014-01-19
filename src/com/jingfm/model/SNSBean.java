package com.jingfm.model;

public class SNSBean {

	private int id;

	private String name;

	private String content;

	private int drawableId;

	public SNSBean() {
		
	}

	public SNSBean(int id, String name, String content, int drawableId) {
		this.id = id;
		this.name = name;
		this.content = content;
		this.drawableId = drawableId;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public int getDrawableId() {
		return drawableId;
	}

	public void setDrawableId(int drawableId) {
		this.drawableId = drawableId;
	}

}
