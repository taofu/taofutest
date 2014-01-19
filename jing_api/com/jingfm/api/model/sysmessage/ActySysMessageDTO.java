package com.jingfm.api.model.sysmessage;
/**
 * 系统活动或公告消息
 * @author lawliet
 *
 */
public class ActySysMessageDTO extends SysMessageDTO{
	
	private String content; //活动内容
	private String title; //活动标题
	private int id; //活动ID
	
	public ActySysMessageDTO(){
		super();
		super.setT(SysMessageType.ACTY.getName());
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

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	
}
