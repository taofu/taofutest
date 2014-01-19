package com.jingfm.api.model;

import java.io.Serializable;

/**
 * 	sina_weibo("false") //新浪微博同步消息
   ,qq_weibo("false")  //腾讯微博同步消息
   ,renren("false")    //人人网同步消息
   ,douban("false")    //豆瓣网同步消息
   ,timedot("true")  //歌曲演奏信息
   ,rltd("true")     //歌曲周边信息
   ,frdlvd("true")    //好友也喜欢这个歌
   ,rcmd("true")     //推荐歌曲 U
   ,tckNtf("true")   //ticker自动提醒
   ,rmdTone("true")  //消息提醒声音
   ,frdCntNtf("true") //自动提醒在线好友
   ,tipNtf("true")    //提醒菜单tips A
   ,thm("dflt")      //主题设置
   ,release("")      //最新版本提示
   ,lgA("false")      //显示大字体
   ,rtCv("true")      //封面旋转
   ,autoSnc("false")      //自动分享,是否弹出自定义文本
   ,h5("true")        //是否开启HTML5播放器
   ,hbr("true")       //高品质
   ,ssch("true")       //智能引导
 * @author lawliet
 *
 */
public class UserSettingSwitchDTO implements Serializable{

    private static final long serialVersionUID = -57263803888000L;  
	
	private boolean sina_weibo;
	private boolean qq_weibo;
	private boolean renren;
	private boolean douban;
	private boolean timedot;
	private boolean rltd;
	private boolean frdlvd;
	private boolean rcmd;
	private boolean tckNtf;
	private boolean rmdTone;
	private boolean frdCntNtf;
	private boolean tipNtf;
	private String thm;
	private String release;
	private boolean lgA;
	private boolean rtCv;
	private boolean autoSnc;
	private boolean h5;
	private boolean hbr;
	private boolean ssch;
	
	public boolean isSina_weibo() {
		return sina_weibo;
	}
	public void setSina_weibo(String sina_weibo) {
		this.sina_weibo = toBoolean(sina_weibo);
	}
	public boolean isQq_weibo() {
		return qq_weibo;
	}
	public void setQq_weibo(String qq_weibo) {
		this.qq_weibo = toBoolean(qq_weibo);
	}
	public boolean isRenren() {
		return renren;
	}
	public void setRenren(String renren) {
		this.renren = toBoolean(renren);
	}
	public boolean isDouban() {
		return douban;
	}
	public void setDouban(String douban) {
		this.douban = toBoolean(douban);
	}
	public boolean isTimedot() {
		return timedot;
	}
	public void setTimedot(String timedot) {
		this.timedot = toBoolean(timedot);
	}
	public boolean isRltd() {
		return rltd;
	}
	public void setRltd(String rltd) {
		this.rltd = toBoolean(rltd);
	}
	public boolean isFrdlvd() {
		return frdlvd;
	}
	public void setFrdlvd(String frdlvd) {
		this.frdlvd = toBoolean(frdlvd);
	}
	public boolean isRcmd() {
		return rcmd;
	}
	public void setRcmd(String rcmd) {
		this.rcmd = toBoolean(rcmd);
	}
	public boolean isTckNtf() {
		return tckNtf;
	}
	public void setTckNtf(String tckNtf) {
		this.tckNtf = toBoolean(tckNtf);
	}
	public boolean isRmdTone() {
		return rmdTone;
	}
	public void setRmdTone(String rmdTone) {
		this.rmdTone = toBoolean(rmdTone);
	}
	public boolean isFrdCntNtf() {
		return frdCntNtf;
	}
	public void setFrdCntNtf(String frdCntNtf) {
		this.frdCntNtf = toBoolean(frdCntNtf);
	}
	public boolean isTipNtf() {
		return tipNtf;
	}
	public void setTipNtf(String tipNtf) {
		this.tipNtf = toBoolean(tipNtf);
	}
	public String getThm() {
		return thm;
	}
	public void setThm(String thm) {
		this.thm = thm;
	}
	public String getRelease() {
		return release;
	}
	public void setRelease(String release) {
		this.release = release;
	}
	public boolean isLgA() {
		return lgA;
	}
	public void setLgA(String lgA) {
		this.lgA = toBoolean(lgA);
	}
	public boolean isRtCv() {
		return rtCv;
	}
	public void setRtCv(String rtCv) {
		this.rtCv = toBoolean(rtCv);
	}
	public boolean isAutoSnc() {
		return autoSnc;
	}
	public void setAutoSnc(String autoSnc) {
		this.autoSnc = toBoolean(autoSnc);
	}
	public boolean isH5() {
		return h5;
	}
	public void setH5(String h5) {
		this.h5 = toBoolean(h5);
	}
	public boolean isHbr() {
		return hbr;
	}
	public void setHbr(String hbr) {
		this.hbr = toBoolean(hbr);
	}
	public boolean isSsch() {
		return ssch;
	}
	public void setSsch(String ssch) {
		this.ssch = toBoolean(ssch);
	}
	
	public boolean toBoolean(String value){
		return Boolean.valueOf(value);
	}
	
}
