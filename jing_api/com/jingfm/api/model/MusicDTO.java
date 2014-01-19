package com.jingfm.api.model;

import java.io.Serializable;

import android.util.Log;

import com.jingfm.api.model.socketmessage.SocketPChatPayloadShareTrackDTO;


@SuppressWarnings("serial")
public class MusicDTO implements Serializable{
	//艺人id
	private Integer aid;//作为艺人交叉排序时使用
	//艺人名字
	private String atn;
	//专辑id
	private Integer abid;
	//专辑名
	private String an;
	//歌曲ID
	private Integer tid;
	//歌曲名称
	private String  n;
	//media fid Media URL，返回对应的媒体文件mid地址
	private String mid;
	//cover fid 返回对应封面专辑图片的url地址。大小为：300x300
	private String fid;
	//歌曲时长，单位：秒
	private String d;
	//码率
	//private String b;
	//文件大小
	private int fs;
	//歌曲距离当前时间相差的年
	private String y;
	
	private int pst;
	//private Integer uid;
	
    //private int docid;
	
	//private float score;
	//private String tagnames;
	//private Integer sorter;
/*	private String media_url;
	private String cover_url;
*/	
	
	/*public String getB() {
		return b;
	}*/
	
	public String getY() {
		return y;
	}

	public int getPst() {
		return pst;
	}

	public void setPst(int pst) {
		this.pst = pst;
	}

	public int getFs() {
		return fs;
	}
	public void setFs(Integer fs) {
		if(fs == null) {
			this.fs = 0;
			return;
		}
		this.fs = fs.intValue();
	}

	public void setY(String y) {
		this.y = y;
	}

	/*public void setB(String b) {
		this.b = b;
	}
*/
	public MusicDTO() {
		super();
	}
	
	public MusicDTO(int tid) {
		super();
		this.tid = tid;
	}
	
	public String getMid() {
		return mid;
	}
	public void setMid(String mid) {
		this.mid = mid;
	}
	public String getFid() {
		return fid;
	}
	public void setFid(String fid) {
		this.fid = fid;
	}
	public Integer getTid() {
		return tid;
	}
	public void setTid(Integer tid) {
		this.tid = tid;
	}
	public String getN() {
		return n;
	}
	public void setN(String n) {
		this.n = n;
	}
	public String getD() {
		return d;
	}
	public void setD(String d) {
		this.d = d;
	}
	public Integer getAid() {
		return aid;
	}
	public void setAid(Integer aid) {
		this.aid = aid;
	}
	
	public Integer getAbid() {
		return abid;
	}

	public void setAbid(Integer abid) {
		this.abid = abid;
	}

	public String getAn() {
		return an;
	}

	public void setAn(String an) {
		this.an = an;
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj instanceof MusicDTO){
			MusicDTO dto = (MusicDTO)obj;
			if(dto.getTid().intValue() == this.tid.intValue()) return true;
			else return false;
		}else return false;
	}
	@Override
	public int hashCode() {
		if(this.getTid() == null) this.setTid(0);
		return this.getTid().hashCode();
		//return new Integer(this.getTid()).hashCode();
	}
	
	/*public Integer getSorter() {
		return sorter;
	}
	public void setSorter(Integer sorter) {
		this.sorter = sorter;
	}*/
	/*public Integer getUid() {
		return uid;
	}
	public void setUid(Integer uid) {
		this.uid = uid;
	}*/
	/*public int getDocid() {
		return docid;
	}
	public void setDocid(int docid) {
		this.docid = docid;
	}
	public float getScore() {
		return score;
	}
	public void setScore(float score) {
		this.score = score;
	}

	public String getTagnames() {
		return tagnames;
	}

	public void setTagnames(String tagnames) {
		this.tagnames = tagnames;
	}*/
	
	public String toString(){
		StringBuilder sb = new StringBuilder();
		//sb.append(" tid:"+this.getTid()+" abid:"+this.getAbid());
		sb.append("aid:"+this.getAid()+" tid:"+this.getTid()+" abid:"+this.getAbid() +" mid:"+this.getMid()+" n:"+this.getN()+" fid:"+this.getFid()+" an:"+this.getAn());
		return sb.toString();
	}

	public String getAtn() {
		return atn;
	}

	public void setAtn(String atn) {
		this.atn = atn;
	}
	
	public SocketPChatPayloadShareTrackDTO toShareMusicDTO(){
		SocketPChatPayloadShareTrackDTO musicDto = new SocketPChatPayloadShareTrackDTO();
		musicDto.setAbid(abid == null?0:abid);
		musicDto.setAid(aid == null?0:aid);
		musicDto.setAn(an == null?"":an);
		musicDto.setAtn(atn == null?"":atn);
		musicDto.setD(d == null?"":d);
		musicDto.setFid(fid == null?"":fid);
		musicDto.setMid(mid == null?"":mid);
		musicDto.setN(n == null?"":n);
		musicDto.setTid(tid == null?0:tid);
		return musicDto;
	}
	
	
	/*public static MusicDTO fromFavMusicDTO(UserFavMusicDTO favDTO){
		if(favDTO == null) return null;
		MusicDTO musicDTO = new MusicDTO();
		musicDTO.setTid(favDTO.getId());
		musicDTO.setN(favDTO.getTit());
		musicDTO.setAtn(favDTO.getAt());
		musicDTO.setAn(favDTO.getAl());
		
		
		musicDTO.setFid(favDTO.getFid());
		musicDTO.setMid(favDTO.getMid());
		return musicDTO;
	}*/
	
//	public static List<MusicDTO> fromFavMusicDTOs(List<UserFavMusicDTO> favDTOs){
//		if(favDTOs == null || favDTOs.isEmpty()) return new ArrayList<MusicDTO>();
//		List<MusicDTO> musicDTOs = new ArrayList<MusicDTO>();
//		//MusicDTO musicDTO = null;
//		for(UserFavMusicDTO favDTO:favDTOs){
//			if(favDTO == null) continue;
//			
//			/*musicDTO = new MusicDTO();
//			musicDTO.setTid(favDTO.getId());
//			musicDTO.setN(favDTO.getTit());
//			musicDTO.setAtn(favDTO.getAt());
//			musicDTO.setAn(favDTO.getAl());
//			
//			musicDTO.setFid(favDTO.getFid());
//			musicDTO.setMid(favDTO.getMid());*/
//			musicDTOs.add(fromFavMusicDTO(favDTO));
//		}
//		
//		return musicDTOs;
//	}
}
