package com.jingfm.api.model.socketmessage;

public enum SocketMessageType {
	//private message 
	FLWD("关注","flwd"), 
	INHS("入驻好友","inhs"), 
	ATFD("at","atfd"),
	LISN("正在听","lisn"),
	CHAT("聊天","chat"),
	RMND("提醒", "rmnd"),
	ACTY("活动", "acty"),
	UTRC("收藏ticker", "utrc"),
	UMFT("在ticker上喜欢同样的歌曲", "umft"),
	NSON("好友上线","nson"),
	NSOF("好友下线","nsof"),
	;
	
	String name;
	String cname;
	
	private SocketMessageType(String cname,String name)
    {
		this.cname = cname;
    	this.name = name;
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	
	public static SocketMessageType fromName(String name){
		SocketMessageType[] types = SocketMessageType.values();
		for(SocketMessageType type:types){
			if(type.getName().equals(name))
				return type;
		}
		return null;
	}
}
