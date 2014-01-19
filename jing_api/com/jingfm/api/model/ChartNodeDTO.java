package com.jingfm.api.model;

import java.io.Serializable;
import java.util.List;

public class ChartNodeDTO implements Serializable{

	private static final long serialVersionUID = -57263898888000L;
	
	//名称
	private String name;
	//下级nodeid
	private String next;
	//榜单封面
	private String fid;
	//标准名称
	private String title;
	//是否是最后一级
	private Boolean last;
	//时间
	private Long newest;
	//总共有多少期
	private Integer lastcount;
	//下一级的node
	private List<ChartNodeDTO> childs;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getNext() {
		return next;
	}
	public void setNext(String next) {
		this.next = next;
	}
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public Boolean isLast() {
		return last;
	}
	public void setLast(Boolean last) {
		this.last = last;
	}
	public Long getNewest() {
		return newest;
	}
	public void setNewest(Long newest) {
		this.newest = newest;
	}
	public Integer getLastcount() {
		return lastcount;
	}
	public void setLastcount(Integer lastcount) {
		this.lastcount = lastcount;
	}
	public List<ChartNodeDTO> getChilds() {
		return childs;
	}
	public void setChilds(List<ChartNodeDTO> childs) {
		this.childs = childs;
	}
	
	
}
