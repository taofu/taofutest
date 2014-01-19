package com.jingfm.api.model;

import java.io.Serializable;
import java.util.Map;

//{"msg":"操作成功","result":{
//"newview":{"t":[],"m":[],"k":[]},
//"avbF":"stct,at,smlMsc,psnRd",
//"cm":"10",
//"sts":{"rltd":"true","thm":"rlwd","rtCv":"true","frdlvd":"true","frdCntNtf":"true","tckNtf":"true","hbr":"true","rcmd":"true","rmdTone":"true","h5":"true","timedot":"true","tipNtf":"true","lgA":"true","autoSnc":"false"},
//"snstokens":{"sina_weibo":"2.008X7axCBxva8C1dc93e92ebO6BFZE"},
//"usr":{"id":100112,"sid":"54cd264c_0e5a_4830_b4b5_fce77c101d45","newbie":0,"regip":"221.122.120.226","nick":"鷹擊長空","c":1,"avatar":"2012033009kxz.jpg"},
//"release":null,
//"pld":{"an":"Nordland","atn":"Bathory","cmbt":"异教民谣","ct":"23","d":"335","fid":"2012072606CkD.jpg","mid":"2012072606IGe.m4a","n":"Ring Of Gold","tid":1276208,"uid":100112}},"success":true}
public class LoginDataDTO  implements Serializable{
	
    private static final long serialVersionUID = -57263801888000L;  
	
	private UserDTO usr;
	private UserPlayingDTO pld;
	private String avbF;
	//private Map<String,String> sts;
	private UserSettingSwitchDTO setswitch;
	private Map<String,String> snstokens;
	private Integer cm;
	public UserDTO getUsr() {
		return usr;
	}
	public void setUsr(UserDTO usr) {
		this.usr = usr;
	}
	public UserPlayingDTO getPld() {
		return pld;
	}
	public void setPld(UserPlayingDTO pld) {
		this.pld = pld;
	}
	public String getAvbF() {
		return avbF;
	}
	public void setAvbF(String avbF) {
		this.avbF = avbF;
	}
//	public Map<String, String> getSts() {
//		return sts;
//	}
//	public void setSts(Map<String, String> sts) {
//		this.sts = sts;
//	}
	public UserSettingSwitchDTO getSetswitch() {
		return setswitch;
	}
	public void setSetswitch(UserSettingSwitchDTO setswitch) {
		this.setswitch = setswitch;
	}
	public Map<String, String> getSnstokens() {
		return snstokens;
	}
	public void setSnstokens(Map<String, String> snstokens) {
		this.snstokens = snstokens;
	}
	public Integer getCm() {
		return cm;
	}
	public void setCm(Integer cm) {
		this.cm = cm;
	}
	
}
