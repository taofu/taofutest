package com.jingfm.api.model.socketmessage;

public enum SocketTickerType {
	LOVE("喜欢","love","L"),//喜欢歌曲通知
	CMBT("组合","cmbt","C"),//新搜索组合通知,好友开始听新的组合
	USD("使用过","usd","D"),//解锁勋章技能通知,单个勋章
	ALIKE("相似","alike","K"),//使用歌曲进行相似搜索
	SAID("说了","said","S"),//使用语意分析搜索
	;
	
	String name;
	String cname;
	String prefix;
	
	private SocketTickerType(String cname,String name,String prefix)
    {
		this.cname = cname;
    	this.name = name;
    	this.prefix = prefix;
    }
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPrefix() {
		return prefix;
	}
	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	
	public static SocketTickerType fromPrefix(String prefix){
		SocketTickerType[] types = SocketTickerType.values();
		for(SocketTickerType type:types){
			if(type.getPrefix().equals(prefix))
				return type;
		}
		return null;
	}
}
