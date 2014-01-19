package com.jingfm.api.model;

public class AppLoginDataDTO {
	private AppUserDTO appusr;

	public AppUserDTO getAppusr() {
		return appusr;
	}

	public void setAppusr(AppUserDTO appusr) {
		this.appusr = appusr;
	}

	public LoginDataDTO toLoginDataDTO(){
		LoginDataDTO dto = new LoginDataDTO();
		dto.setAvbF(null);
		dto.setPld(null);
		dto.setSetswitch(null);
		dto.setSnstokens(null);
		UserDTO usr = new UserDTO();
		usr.setId(appusr.getId());
		usr.setGuest(true);
		dto.setUsr(usr);
		return dto;
	}
}
