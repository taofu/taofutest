package com.jingfm.api.model.push;

import java.util.HashMap;
import java.util.Map;

public enum PushMessageType {
	
	CHAT("普通聊天","ct"), 
	SHAREMUSIC("分享好友歌曲","sm"),
	NOTICE("系统活动通知","ne"), 
	BEFOLLOW("被别人关注","bf"), 
	BEREMIND("被别人提醒关注","br"),
	ARTIST("推送艺人","pa"),
	CMBT("推送组合","pc"),
	;
	
	static Map<String, PushMessageType> allPushActionMessageTypes;
	private String name;
	private String prefix;
	private PushMessageType(String name, String prefix){
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

	public static PushMessageType fromPrefix(String prefix){
		return allPushActionMessageTypes.get(prefix);
	}
	
	static {
		allPushActionMessageTypes = new HashMap<String,PushMessageType>();
		PushMessageType[] types = values();
		for (PushMessageType type : types){
			allPushActionMessageTypes.put(type.getPrefix(), type);
		}
	}
}
