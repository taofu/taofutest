package com.jingfm.api.model;

import java.util.HashMap;
import java.util.Map;

public enum BadgeType {
	Artist("ats", "艺人"),
	Time("time", "时间"),
	Weather("weather", "天气"),
	Locations("locations", "地点"),
	Status("status", "状态"),
	Language("lang", "语言"),
	OtherGenre("ognr", "风格流派"),
	Tonality("toy", "调性"),
	MusicalInstruments("mits","演奏乐器"),
	Style("sty", "曲风"),
	Mood("mood", "情绪"),
	GEND("gend", "形式"),
	Others("oth", "其他"),
	;
	
	private String name;
	private String cname;
	
	private BadgeType(String name, String cname){
		this.name = name;
		this.cname = cname;
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

	private static Map<String, BadgeType> mapKeyName;
	
	static {
		mapKeyName = new HashMap<String, BadgeType>();
		BadgeType[] types = values();
		for (BadgeType type : types){
			mapKeyName.put(type.name, type);
		}
	}
	
	public static BadgeType fetchByNameKey(String name){
		return mapKeyName.get(name);
	}
	
	public static boolean supported(String key){
		return mapKeyName.get(key) != null;
	}
	
 }
