package com.jingfm.api.model;

import java.io.Serializable;

/**
 * 前缀信息类 包括 词频，值，显示值，类型
 * @author Hevin
 *
 */
public class PrefixDTO implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 635100864908266971L;
	//private int count;
	//private String value;
	private String t;
	private String n;
	private String fid;
	private String id;//父id
	//private boolean hasChildren;//是否有子节点
	
	private String frdid;//好友id
	private String c; // friends favor songs count
	

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/*public boolean isHasChildren() {
		return hasChildren;
	}

	public void setHasChildren(boolean hasChildren) {
		this.hasChildren = hasChildren;
	}*/

	public String getFid() {
		return fid;
	}

	public void setFid(String fid) {
		this.fid = fid;
	}

	/*public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}*/

	/*public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}*/

	
	public String getT() {
		return t;
	}

	public void setT(String t) {
		this.t = t;
	}

	public String getN() {
		return n;
	}

	public void setN(String n) {
		this.n = n;
	}

	public String getFrdid() {
		return frdid;
	}

	public void setFrdid(String frdid) {
		this.frdid = frdid;
	}

	public String getC() {
		return c;
	}

	public void setC(String c) {
		this.c = c;
	}

}
