package com.jingfm.api.model.oauth;

import java.util.Map;

import com.jingfm.api.model.LoginDataDTO;
import com.jingfm.api.model.UserDTO;
import com.jingfm.api.model.UserPlayingDTO;
import com.jingfm.api.model.UserSettingSwitchDTO;
/**
 * 通过第三方账号正常登陆的DTO
 * @author lawliet
 *
 */
public class OAuthLoginDataNormalDTO extends OAuthLoginDataDTO{
	private UserDTO usr;
	private UserPlayingDTO pld;
	private String avbF;
	private UserSettingSwitchDTO setswitch;
	private Map<String,String> snstokens;
	private Integer cm;
	public Integer getCm() {
		return cm;
	}
	public void setCm(Integer cm) {
		this.cm = cm;
	}
	public UserDTO getUsr() {
		return usr;
	}
	public void setUsr(UserDTO usr) {
		this.usr = usr;
	}
	public UserPlayingDTO getPld() {
		return pld;
	}
	public void setPld(UserPlayingDTO pld) {
		this.pld = pld;
	}
	public String getAvbF() {
		return avbF;
	}
	public void setAvbF(String avbF) {
		this.avbF = avbF;
	}
	public UserSettingSwitchDTO getSetswitch() {
		return setswitch;
	}
	public void setSetswitch(UserSettingSwitchDTO setswitch) {
		this.setswitch = setswitch;
	}
	public Map<String, String> getSnstokens() {
		return snstokens;
	}
	public void setSnstokens(Map<String, String> snstokens) {
		this.snstokens = snstokens;
	}
	
	public LoginDataDTO toLoginDataDTO(){
		LoginDataDTO dto = new LoginDataDTO();
		dto.setAvbF(this.avbF);
		dto.setPld(this.pld);
		dto.setSetswitch(this.setswitch);
		dto.setSnstokens(this.snstokens);
		dto.setUsr(this.usr);
		dto.setCm(this.cm);
		return dto;
	}
}
