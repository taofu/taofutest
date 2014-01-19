package com.jingfm.api.model;


public class TrackDTO {
	private String tid;
	private String n;
	private String fid;
	private String mid;
	private String atst;
	private boolean origin;
	private String feat;
	private String d;
	public String getAtst() {
		return atst;
	}
	public void setAtst(String atst) {
		this.atst = atst;
	}
	public String getN() {
		return n;
	}
	public void setN(String n) {
		this.n = n;
	}
	public String getTid() {
		return tid;
	}
	public boolean isOrigin() {
		return origin;
	}
	public void setOrigin(Boolean origin) {
		if(origin == null) return;
		this.origin = origin.booleanValue();
	}
	public void setTid(String tid) {
		this.tid = tid;
	}
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getFeat() {
		return feat;
	}
	public void setFeat(String feat) {
		this.feat = feat;
	}
	public String getD() {
		return d;
	}
	public void setD(String d) {
		this.d = d;
	}
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof TrackDTO){
			TrackDTO now = (TrackDTO)obj;
			if(now.tid == null || this.tid == null) return false;
			return now.tid.equals(this.tid);
		}else return false;
	}

	@Override
	public int hashCode() {
		if(this.tid == null) return 0;
		return this.tid.hashCode();
	}
	
	
}
