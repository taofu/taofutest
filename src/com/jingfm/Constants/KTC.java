package com.jingfm.Constants;

import java.util.HashMap;

import android.app.Activity;
import android.text.format.Time;

import com.jingfm.background_model.PlayerManager;
import com.tendcloud.tenddata.TCAgent;

public class KTC {
	public static Activity activity;
	public static void rep(String EVENT_ID, String EVENT_LABEL, String EVENT_KEY){
		HashMap<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put(EVENT_KEY, Integer.valueOf(1));
		TCAgent.onEvent(activity, EVENT_ID, EVENT_LABEL,  hashMap);
	}
	
	public static String getTimeKeyString(){
		Time t=new Time(); // or Time t=new Time("GMT+8"); 加上Time Zone资料。  
		t.setToNow(); // 取得系统时间。  
//		int year = t.year;  
//		int month = t.month;  
//		int date = t.monthDay;  
		int hour = t.hour; // 0-23  
//		int minute = t.minute;  
//		int second = t.second;
		String timeKey = "";
		if (hour<5) {
			timeKey = "1-5";
		}else if (hour<8) {
			timeKey = "5-8";
		}else if (hour<12) {
			timeKey = "8-12";
		}else if (hour<14) {
			timeKey = "12-14";
		}else if (hour<18) {
			timeKey = "14-18";
		}else if (hour<21) {
			timeKey = "18-21";
		}else {
			timeKey = "21-1";
		}
		return timeKey;
	}
	
	public static String getProgress(int progress){
		String timeKey = "";
		if (progress < 25) {
			timeKey = "0-25";
		}else if (progress < 50) {
			timeKey = "25-50";
		}else if (progress < 75) {
			timeKey = "50-75";
		}else{
			timeKey = "75-100";
		}
		return timeKey;
	}
	public static String getMusicProgress(){
		return getProgress(PlayerManager.getInstance().getProgress());
	}
}
