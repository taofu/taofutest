package com.jingfm.api.model.sysmessage;

public enum SysMessageType {
	INHS("入驻好友","inhs"), 
	RMND("提醒关注", "rmnd"),
	ACTY("活动", "acty"),
	FLWD("关注","flwd");
	
	String name;
	String cname;
	
	private SysMessageType(String cname,String name)
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
	
	public static SysMessageType fromName(String name){
		SysMessageType[] types = SysMessageType.values();
		for(SysMessageType type:types){
			if(type.getName().equals(name))
				return type;
		}
		return null;
	}
}
