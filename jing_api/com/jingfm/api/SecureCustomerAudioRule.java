package com.jingfm.api;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import com.jingfm.api.context.AppContext;
import com.jingfm.api.helper.DigestHelper;
import com.jingfm.api.helper.StringHelper;

/**
 * 蓝讯CDN url加密
 * @author edmond
 *
 */
public class SecureCustomerAudioRule {
	public static String Self_Media_Access_EndPoint_Prefix = "http://pmedia.jing.fm";
	public static String Self_Lyrics_Access_EndPoint_Prefix = "http://lrc.jing.fm";
	public static String Media_Access_EndPoint_Prefix = "http://cc.cdn.jing.fm";
	private static final String CDNSecureKey = "Zwm8JCTa6x3YhVzL";
	/**
	 	加密串key=KupKVv)#4ktKufaT3&XmpV8dDENib)cq 
		有效时间expiredtime=20min 
		time = 当前时间的yyyyMMddHHmm格式
		md5值=md5(key+time+uri) 
		Wskey的格式为Time+/+md5(KEY+Time+URL)
	 */
	//http://Media_Access_EndPoint/Time/md5(KEY+Time+URI)/URI
	public static String ID2URL(String id) {
		return ID2URL(id,"NO");
	}
	public static String ID2URL(String id, String bitrateType) {
		StringBuilder sb = new StringBuilder();
		//.append(generateWskey(ID2NonSecureURL(id,bitrateType)))
		//
		sb.append(Media_Access_EndPoint_Prefix).append(StringHelper.httpPathGap).append(generateWskey(ID2NonSecureURI(id,bitrateType))).append(SuperID2URL(id,bitrateType));
		//sb.append("/").append(generateWskey(ID2NonSecureURI(id,bitrateType))).append(super.ID2URL(id,bitrateType));
		return sb.toString();
	}
	public static String ID2Lyrics(String id) {
		if (StringHelper.isEmpty(id))
			return "";
		if (id.toLowerCase(AppContext.ServerLocale).startsWith("http://"))
			return id;
		StringBuilder sb = new StringBuilder(Self_Lyrics_Access_EndPoint_Prefix);
		sb.append(ID2FilePath(id, "NO")+".lrc");
		return sb.toString();
	}
	private static String generateWskey(String uri){
		String time = AppContext.currentTime();
		StringBuilder sb = new StringBuilder();
		sb.append(CDNSecureKey).append(time).append(uri);
		//System.out.println("~~"+uri);
		//System.out.println("~~"+sb.toString());
		////System.out.println("~~"+DigestHelper.md5ToHex(sb.toString()).concat("/").concat(timeHex));
		//System.out.println("~~"+time.concat("/").concat(DigestHelper.md5ToHex(sb.toString())));
		return time.concat("/").concat(DigestHelper.md5ToHex(sb.toString()));//DigestHelper.md5ToHex(sb.toString()).concat("/").concat(timeHex);
	}
	/*private String ID2NonSecureURL(String id,BitrateType bitrateType){
		StringBuilder sb = new StringBuilder();
		sb.append(CDNPrefixDomain).append(super.ID2URL(id,bitrateType));
		return sb.toString();
	}*/
	
	/**
	 * 返回本地媒体服务器url 限制为MM
	 * @param id
	 * @return
	 */
	public static String ID2SelfNonSecureURL(String id) {
		StringBuilder sb = new StringBuilder();
		sb.append(Self_Media_Access_EndPoint_Prefix).append(ID2NonSecureURI(id,"MM"));
		return sb.toString();
	}
	
	//pmedia.jing.fm/2011/1111/15/eb/MM20111111115ebs.m4a
	public static String ID2NonSecureURI(String id,String bitrateType){
		/*StringBuilder sb = new StringBuilder();
		sb.append(CDNPrefixDomain).append(super.ID2URL(id,bitrateType));
		return sb.toString();*/
		return SuperID2URL(id,bitrateType);
	}
	
	public static String SuperID2URL(String id, String bitrateType) {
		if (StringHelper.isEmpty(id))
			return "";
		if (id.toLowerCase(AppContext.ServerLocale).startsWith("http://"))
			return id;
		return ID2FilePath(id, bitrateType);
	}
	
	public static String ID2FilePath(String id,String bitrateType) {
		StringBuilder sb = new StringBuilder();
		sb.append('/');
		//sb.append(bitrateType.getDisplay()).append('/');
		sb.append(id.substring(0, 4)).append('/');
		sb.append(id.substring(4, 8)).append('/');
		sb.append(id.subSequence(8, 10)).append('/');
		//取随机字母中的前两位个
		sb.append(id.subSequence(10, 12)).append('/');
		sb.append(ID2FileName(id,bitrateType));
		return sb.toString();
	}
	
	public static String ID2FileName(String id,String bitrateType) {
		if("NO".equals(bitrateType))
			return id;
		else return bitrateType+id;
	}
	
	/*private String currentTime2Hex(){
		long t = System.currentTimeMillis()/1000;
		return Long.toHexString(t);
	}*/
	//jingfm / Final2012
	//推送并预取（只针对url）
	//调用方式：http://wscp.lxdns.com:8080/wsCP/servlet/contReceiver?username=name&passwd=md5&url=url1&isfetch=Y
	public static void main(String[] argv){
		String fid = "2012011008Ygw.m4a";
		//SecureCustomerAudioRule rule = new SecureCustomerAudioRule();
		System.out.println(SecureCustomerAudioRule.ID2URL(fid));
		System.out.println(SecureCustomerAudioRule.ID2URL(fid,"SM"));
		System.out.println(SecureCustomerAudioRule.ID2URL(fid,"MM"));
		/*String user = "jingfm";
		String passwd = "Jingfm2012";
		//String url = "jingfm.duomi.com/2012/0110/08/Yg/2012011008Ygw.m4a";
		//http://wscp.lxdns.com:8080/wsCP/servlet/contReceiver?username=abc&passwd=0555572ad8c2cfd598d6d43bd1a04d1c&url=www.abc.com/a.html;www.abc.com/b.html
		SecureCustomerAudioRule rule = new SecureCustomerAudioRule();
		StringBuilder sburl =new StringBuilder(RuntimeConfiguration.Media_Access_EndPoint_Prefix.substring(7)+rule.ID2NonSecureURI(fid,BitrateType.NORMALORIGINAL));
		System.out.println(sburl.toString());
		String md5pass = DigestHelper.md5ToHex(user.concat(passwd).concat(sburl.toString()));
		StringBuilder sb =new StringBuilder();
		sb.append("http://wscp.lxdns.com:8080/wsCP/servlet/contReceiver?").append("username=jingfm&").append("passwd=").append(md5pass).append("&url=").append(sburl.toString());
		System.out.println(sb.toString());*/
		
	}
}
