package com.jingfm.api.model;

import java.io.Serializable;

import com.jingfm.api.helper.StringHelper;

/**
 * 跟听音乐模型
 * 
 */
@SuppressWarnings("serial")
public class MusicFollowDTO implements Serializable{
	
	public static final String PlayAction = "play";
	public static final String PauseAction = "pause";
	
	private MusicDTO musicDto;
	private String action;
	
	public MusicFollowDTO(String message) {
		try {
//			String message = "play,"+m.getTid()+","+m.getMid()+","
//					+m.getFid()+","+m.getN()+","+ m.getD()+","
//					+pst+",1,"+m.getAn()+","
//					+m.getAtn()+",\007play\007"; pausing
			if (message.equals("pause")
					||message.equals("pausing")){
				setAction(MusicFollowDTO.PauseAction);
			}else if(message.equals("play")
					||message.equals("playing")) {
				setAction(MusicFollowDTO.PlayAction);
			}else{
				if (message.startsWith("pau")) {
					setAction(MusicFollowDTO.PauseAction);
				}else if (message.startsWith("pla")) {
					setAction(MusicFollowDTO.PlayAction);
				}
				musicDto = new MusicDTO();
				//不包含标识则是旧版本
				if(message.indexOf("\007") == -1){
					String[] args = message.split(",");
					musicDto.setTid(Integer.parseInt(args[1]));
					musicDto.setMid(args[2]);
					musicDto.setFid(args[3]);
					musicDto.setN(args[4]);
					musicDto.setD(args[5]);
					musicDto.setPst(Integer.parseInt(args[6]));
					musicDto.setAn(args[8]);
					musicDto.setAtn(args[9]);
				}
				//新版本的格式
				else{
					String[] args = message.split("\007");
					musicDto.setTid(Integer.parseInt(args[2]));
					musicDto.setMid(args[3]);
					musicDto.setFid(args[4]);
					musicDto.setN(args[5]);
					musicDto.setD(args[6]);
					musicDto.setPst(Integer.parseInt(args[7]));
					musicDto.setAn(args[9]);
					musicDto.setAtn(args[10]);
				}
			}
			
		} catch (Exception e) {
		}
	}
	public MusicDTO getMusicDto() {
		return musicDto;
	}
	public void setMusicDto(MusicDTO musicDto) {
		this.musicDto = musicDto;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	
	public static String dto2Message(String action2, MusicDTO m, int pst) {
		if (m == null) {
			return null;
		}
		String oldversionmessage = action2 + ","+m.getTid()+","+m.getMid()+","
				+m.getFid()+","+m.getN()+","+ m.getD()+","
				+pst+",1,"+m.getAn()+","
				+m.getAtn() + ",";
		String newversionmessage = "\007" + action2 + "\007"+m.getTid()+"\007"+m.getMid()+"\007"
				+m.getFid()+"\007"+m.getN()+"\007"+ m.getD()+"\007"
				+pst+"\0071\007"+m.getAn()+"\007"
				+m.getAtn();
		return oldversionmessage.concat(newversionmessage);
	}
}
