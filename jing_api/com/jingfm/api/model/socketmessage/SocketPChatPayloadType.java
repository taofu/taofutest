package com.jingfm.api.model.socketmessage;
/**
 * 聊天扩展属性的类型
 * @author lawliet
 *
 */
public enum SocketPChatPayloadType {
	SHARETRACK("分享歌曲","sharetrack",'S')//分享歌曲类型
	;
	String name;
	String cname;
	char prefix;
	
	private SocketPChatPayloadType(String cname,String name,char prefix)
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
	public char getPrefix() {
		return prefix;
	}
	public void setPrefix(char prefix) {
		this.prefix = prefix;
	}
	public String getCname() {
		return cname;
	}
	public void setCname(String cname) {
		this.cname = cname;
	}
	
	public static SocketPChatPayloadType fromPrefix(char prefix){
		SocketPChatPayloadType[] types = SocketPChatPayloadType.values();
		for(SocketPChatPayloadType type:types){
			if(type.getPrefix() == prefix)
				return type;
		}
		return null;
	}
	
}
