package com.jingfm.api.model;

public class BannerDetailDTO {
	//自然语言
	private String cmbt;
	//随机的专辑封面
	private String album_fid;
	
	public BannerDetailDTO(){
		
	}
	
	public BannerDetailDTO(String cmbt, String album_fid){
		this.cmbt = cmbt;
		this.album_fid = album_fid;
	}

	public String getCmbt() {
		return cmbt;
	}

	public void setCmbt(String cmbt) {
		this.cmbt = cmbt;
	}

	public String getAlbum_fid() {
		return album_fid;
	}

	public void setAlbum_fid(String album_fid) {
		this.album_fid = album_fid;
	}
	

}
