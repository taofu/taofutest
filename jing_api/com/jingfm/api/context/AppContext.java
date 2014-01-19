package com.jingfm.api.context;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.jingfm.api.helper.StringHelper;

public class AppContext {
	
	private static long time_gap_with_local = 0;
	private static ClientContext clientContext = null;
	private static GuestClientContext guestClientContext = null;
	
	private static ClientContext defaultClientContext = new ClientContext();
	static{
		defaultClientContext.setUid(100091);
		defaultClientContext.setAtoken("Oh8XVh5RU1pUQkBKTFheB1ZbDVE=");
		defaultClientContext.setRtoken("e1leVxdDDg8e");
	}
	
	public static ClientContext getClientContext(){
		if(clientContext == null){
			return defaultClientContext;
		}
		return clientContext;
	}
	
	public static void setClientContext(ClientContext _clientContext){
		clientContext = _clientContext;
	}
	
	public static void setClientContext(int uid,String atoken,String rtoken,String _stime){
		System.out.println(String.format("Client Context update:%s %s %s", uid,atoken,rtoken));
		if(uid == 0) return;
		if(StringHelper.isEmpty(atoken)) return;
		if(clientContext == null){
			clientContext = new ClientContext();
		}
		clientContext.setUid(uid);
		clientContext.setAtoken(atoken);
		clientContext.setRtoken(rtoken);
		try{
			if(_stime != null && !"".equals(_stime)){
				setServerTime(_stime);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}
	
	private static void setServerTime(String _stime) {
		long stime = Long.parseLong(_stime);
		long now = System.currentTimeMillis();
		time_gap_with_local = stime - now;
		DateFormat df = new SimpleDateFormat(DateFormatPattern,ServerLocale);
		System.out.println("========\n>>time_gap_with_local: " + df.format(new Date(time_gap_with_local)));
		System.out.println("========\n>>stime: " + new Date(stime));
		System.out.println("========\n>>stime: " + df.format(new Date(stime)));
		System.out.println("========\n>>now: " + df.format(new Date(now)));
	}

	public static GuestClientContext getGuestClientContext(){
		return guestClientContext;
	}
	
	public static void setGuestClientContext(GuestClientContext _guestclientContext){
		guestClientContext = _guestclientContext;
	}
	
	public static void setGuestClientContext(int uid,String gtoken,String _stime){
		System.out.println(String.format("Guest Client Context update:%s %s", uid,gtoken));
		if(uid == 0) return;
		if(StringHelper.isEmpty(gtoken)) return;
		if(guestClientContext == null){
			guestClientContext = new GuestClientContext();
		}
		guestClientContext.setUid(uid);
		guestClientContext.setGtoken(gtoken);
		try{
			if(_stime != null && !"".equals(_stime)){
				setServerTime(_stime);
			}
		}catch(Exception ex){
			ex.printStackTrace();
		}
	}

	public static Locale ServerLocale = Locale.CHINA;
	public static final String DateFormatPattern = ("yyyyMMddHHmm");
	public static String currentTime(){
		DateFormat df = new SimpleDateFormat(DateFormatPattern,ServerLocale);
		Calendar calendar = Calendar.getInstance();//获取当前日历对象
//		long unixTime = calendar.getTimeInMillis();//获取当前时区下日期时间对应的时间戳
//		long unixTimeGMT = unixTime - TimeZone.getDefault().getRawOffset();//获取标准格林尼治
		long chinaTime = System.currentTimeMillis();
//		String timeStringGMT = df.format(new Date(unixTimeGMT));
//		String timeStringC = df.format(new Date(chinaTime));
		String timeString = df.format(new Date(chinaTime + time_gap_with_local));
//		System.out.println("=======\n>>timeString: " + timeStringGMT);
//		System.out.println("=======\n>>timeString: " + timeStringC);
		System.out.println("=======\n>>timeString: " + timeString);
		return timeString;
	}
}
