package com.jingfm.api.model;

import java.io.Serializable;

public class RecommendAppDTO {
	private String n;//名称
	private String d;//描述
	private String p;//图片地址
	private String l;//连接地址
	
	public String getN() {
		return n;
	}
	public void setN(String n) {
		this.n = n;
	}
	public String getD() {
		return d;
	}
	public void setD(String d) {
		this.d = d;
	}
	public String getP() {
		return p;
	}
	public void setP(String p) {
		this.p = p;
	}
	public String getL() {
		return l;
	}
	public void setL(String l) {
		this.l = l;
	}
	
	
}
