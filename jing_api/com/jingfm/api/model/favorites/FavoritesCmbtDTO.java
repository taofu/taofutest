package com.jingfm.api.model.favorites;

public class FavoritesCmbtDTO extends FavoritesDTO{
	//收藏的搜索条件
	private String cmbt;
	//随机专辑ID
	private String fid;
	
	public String getCmbt() {
		return cmbt;
	}
	public void setCmbt(String cmbt) {
		this.cmbt = cmbt;
	}
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	
	
}

