package com.jingfm.ViewManager;

import com.jingfm.api.model.LoginDataDTO;

public interface LoginStateChangeListener {
	public void onLogin(LoginDataDTO data);
	public void onLogout();
}
