package com.jingfm.api.model;import java.io.Serializable;public class UserUnlockBadgeDTO implements Serializable {	private static final long serialVersionUID = -57264868888002L;	private Integer id;	private String category;	public String getCategory() {		return category;	}	public void setCategory(String category) {		this.category = category;	}	/*private String name;	private String alias_name;	private String class_name;*/	private String fid; //勋章CSS或艺人FID	private String n;   //勋章名字	private Long time;  //获取勋章的时间		@Override	public boolean equals(Object obj) {		if(obj == null) return false;		if(obj instanceof UserUnlockBadgeDTO){			UserUnlockBadgeDTO now = (UserUnlockBadgeDTO)obj;			return now.id.equals(this.id);		}else return false;	}	@Override	public int hashCode() {		return this.id.hashCode();	}		public Long getTime() {		return time;	}	public void setTime(Long time) {		this.time = time;	}	public Integer getId() {		return id;	}	public void setId(Integer id) {		this.id = id;	}	public String getFid() {		return fid;	}	public void setFid(String fid) {		this.fid = fid;	}	public String getN() {		return n;	}	public void setN(String n) {		this.n = n;	}}