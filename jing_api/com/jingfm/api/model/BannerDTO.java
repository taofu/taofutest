package com.jingfm.api.model;

public class BannerDTO {
		//banner id
		private String id;
		//banner image url
		private String fid;
		//banner page url
		private String href;
		
		public BannerDTO(){
			
		}
		
		public BannerDTO(String id, String fid, String href){
			this.id = id;
			this.fid = fid;
			this.href = href;
		}
		
		public String getId() {
			return id;
		}
		public void setId(String id) {
			this.id = id;
		}
		public String getFid() {
			return fid;
		}

		public void setFid(String fid) {
			this.fid = fid;
		}

		public String getHref() {
			return href;
		}
		public void setHref(String href) {
			this.href = href;
		}
		
}
