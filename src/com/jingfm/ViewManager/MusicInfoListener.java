package com.jingfm.ViewManager;

import java.util.List;

import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.MusicInfoDTO;
import com.jingfm.api.model.UserFrdDTO;

public interface MusicInfoListener {
	public void refreshMusicInfoDTO(MusicDTO musicDto,MusicInfoDTO dto, List<UserFrdDTO> mUserFrdDTOList);
	//public void clearMusicInfoDTO();
}
