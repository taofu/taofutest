package com.jingfm.api.model;

import java.util.Collections;
import java.util.List;
import com.jingfm.api.model.message.OfflineSiteMessageDTO;

public class ListResult<T>{
	
	private int index;//只适用 user friend fetch order
	private String fid;//只适用 chatctt fetch
	
	private String cmbt;
	private int total;//":117,"atmode":true
	private boolean atmode;
	
	//"moods":["悲伤"] "moodids":"189"
	
	/*
	public static final String Tune_Mode_Key = "m";
	public static final int M_Tune_Normal_Mode = 0;// 正常模式
	public static final int M_Tune_At_Mode = 1;// 收聽某個用戶（包括自己）
	public static final int M_Tune_Similar_Mode = 2;// 收聽相似歌曲
	public static final int M_Tune_PersonalRadio_Mode = 3;// 收聽個人電台
	public static final int M_Tune_BillboardCharts_Mode = 4;// 收聽榜單
	public static final int M_Tune_TopCharts_Mode = 5; // 收聽Top榜單
	public static final int M_Tune_LoveHateSong_Mode = 6; // 收聽喜歡、討厭的單曲
	public static final int M_Tune_NaturalLanguage_Mode = 7; // 收聽自然語言 此种模式只能客户端自己处理
	public static final int M_Tune_EmptyConditions_Mode = 97;
	public static final int M_Tune_DirtyWords_Mode = 98;
	public static final int M_Tune_Other_Mode = 99;*/
	private int m;
	private boolean choose;
	private List<T> items;
	private List<ChooseAlikeDTO> chooseitems;
	private List<String> moods;
    private List<OfflineSiteMessageDTO> chats;
	private String moodids;
	//相似歌曲艺人名称
	private String an;
	//相似歌曲名称
	private String tn;
	//相似歌曲id
	private int tid;
	
	private String hint;
	
	private int st;
	private int ps;
	public int getTotal() {
		return total;
	}
	public void setTotal(int total) {
		this.total = total;
	}
	public boolean isAtmode() {
		return atmode;
	}
	public void setAtmode(boolean atmode) {
		this.atmode = atmode;
	}
	public List<T> getItems() {
		return items;
	}
	public void setItems(List<T> items) {
		this.items = items;
	}
	public String getHint() {
		return hint;
	}
	public void setHint(String hint) {
		this.hint = hint;
	}
	public int getSt() {
		return st;
	}
	public void setSt(int st) {
		this.st = st;
	}
	public int getPs() {
		return ps;
	}
	public void setPs(int ps) {
		this.ps = ps;
	}
	public List<String> getMoods() {
		return moods;
	}
	public void setMoods(List<String> moods) {
		this.moods = moods;
	}
	public String getMoodids() {
		return moodids;
	}
	public void setMoodids(String moodids) {
		this.moodids = moodids;
	}
	public int getIndex() {
		return index;
	}
	public void setIndex(int index) {
		this.index = index;
	}
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public void shuffleitems(){
		if(this.items != null && !this.items.isEmpty()){
			Collections.shuffle(items);
		}
	}
	public String getCmbt() {
		return cmbt;
	}
	public void setCmbt(String cmbt) {
		this.cmbt = cmbt;
	}
	public int getM() {
		return m;
	}
	public void setM(int m) {
		this.m = m;
	}
	public boolean isChoose() {
		return choose;
	}
	public void setChoose(boolean choose) {
		this.choose = choose;
	}
	public List<ChooseAlikeDTO> getChooseitems() {
		return chooseitems;
	}
	public void setChooseitems(List<ChooseAlikeDTO> chooseitems) {
		this.chooseitems = chooseitems;
	}
	public String getAn() {
		return an;
	}
	public void setAn(String an) {
		this.an = an;
	}
	public String getTn() {
		return tn;
	}
	public void setTn(String tn) {
		this.tn = tn;
	}
	public int getTid() {
		return tid;
	}
	public void setTid(int tid) {
		this.tid = tid;
	}

    public List<OfflineSiteMessageDTO> getChats() {
        return chats;
    }

    public void setChats(List<OfflineSiteMessageDTO> chats) {
        this.chats = chats;
    }
}
