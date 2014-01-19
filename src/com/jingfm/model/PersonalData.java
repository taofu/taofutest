package com.jingfm.model;

import java.io.Serializable;
import java.util.List;

import com.jingfm.api.model.UserUnlockBadgeDTO;

public class PersonalData implements Serializable {
	private static final long serialVersionUID = -57263868888002L;
	public int uid;
	public String name;
	public String avatarUrl;
	public String coverUrl;
	public String FavTk = "0";
	public String Frd = "0";
	public String Befrd = "0";
	public int onLineTime;
	public boolean attend;
	public boolean isFrdshp;
	public boolean online;
	public List<UserUnlockBadgeDTO> userUnlockBadgeDTO;
}
