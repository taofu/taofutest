package com.jingfm.api;

import com.jingfm.Constants.Constants;
import com.jingfm.api.helper.StringHelper;
import com.jingfm.tools.JingTools;


public class CustomerImageRule {
	public static String Image_Access_EndPoint_Prefix = "http://img.jing.fm/";
	public static String CDN_Image_Access_EndPoint_Prefix = "http://image.jing.fm/";
	
	public static String Default_Image_Access_EndPoint_Prefix = "http://jing.fm/images/defaults/profile/";
	
	//http://img.jing.fm/assets/jing/badges/100@2x/AbsoluteMusicBadge.jpg
	public static String Default_Badge_Image_Access_EndPoint_Prefix = "http://img.jing.fm/assets/jing/badges/";
	
	public final static String NormalOrginalBitrateType = "NO";

	
	private final static String AvatarImageType = "avatar";
	private final static String CoverImageType = "cover";
	public static String ID2URL(String imageType,String id) {
		try{
			return ID2URL(imageType,id,NormalOrginalBitrateType);
//			return ID2URL(imageType,id,"UL");
		}catch(Exception ex){
			return "";
		}
	}
	private final static String Badge_URL_Template = "http://img.jing.fm/assets/jing/badges/%s/%s.jpg";
	private static String ID2BadgeURL(String id){//100@2x
		return String.format(Badge_URL_Template, "100@2x",id);
	}
	
	public static String ID2URL(String imageType,String id, String bitrateType) {
		if (imageType.equals(Constants.ID2URL_KEY_WORD_AVATAR)
				&& !JingTools.isValidString(id)) {
			return "http://image.jing.fm/assets/jing-beta/defaults/avatar/50@2x.jpg";
		}
		try{
			if(StringHelper.isNotEmpty(id)){
				if(id.startsWith("http"))
					return id;
				else{
					int index = id.indexOf('.');
					if(index == -1){//Badge标签
						return ID2BadgeURL(id);
					}
					if("cover".equals(imageType)){
						if(id.length() < 17){//缺省的Cover处理
							StringBuilder sb = new StringBuilder();
							if("CL".equals(bitrateType)){
								
								String prefix = id.substring(0, index);
								String suffix = id.substring(index,id.length());
								sb.append(Default_Image_Access_EndPoint_Prefix).append(prefix).append("@2x").append(suffix);
							}else
								sb.append(Default_Image_Access_EndPoint_Prefix).append(id);
							return sb.toString();
						}
					}
					
					StringBuilder sb = new StringBuilder();
					if(AvatarImageType.equals(imageType) || CoverImageType.equals(imageType))
						sb.append(Image_Access_EndPoint_Prefix).append(imageType).append(ID2FilePath(id,bitrateType));
					else
						sb.append(CDN_Image_Access_EndPoint_Prefix).append(imageType).append(ID2FilePath(id,bitrateType));
					return sb.toString();
				}
			}else{//空头像的情况下
				if("avatar".equals(imageType)){
					if("UY".equals(bitrateType)){
						return Avatar_UY_Default;
					}
					if("UT".equals(bitrateType)){
						return Avatar_UT_Default;
					}
					if("US".equals(bitrateType)){
						return Avatar_US_Default;
					}
					if("U1".equals(bitrateType)){
						return Avatar_U1_Default;
					}
					if("UM".equals(bitrateType)){
						return Avatar_UM_Default;
					}
					return "";
				}else return "";
			}
		}catch(Exception ex){
			return "";
		}
	}
	
	private final static String Avatar_UY_Default = "http://img.jing.fm/assets/jing/defaults/avatar/30@2x.jpg"; 
	private final static String Avatar_UT_Default = "http://img.jing.fm/assets/jing/defaults/avatar/50@2x.jpg";
	private final static String Avatar_US_Default = "http://img.jing.fm/assets/jing/defaults/avatar/64@2x.jpg";
	private final static String Avatar_U1_Default = "http://img.jing.fm/assets/jing/defaults/avatar/80@2x.jpg.png";
	private final static String Avatar_UM_Default = "http://img.jing.fm/assets/jing/defaults/avatar/100@2x.jpg";
	
	
	public static String ID2FilePath(String id,String bitrateType) {
		StringBuilder sb = new StringBuilder();
		sb.append(StringHelper.httpPathGap);
		sb.append(bitrateType).append(StringHelper.httpPathGap);
		sb.append(id.substring(0, 4)).append(StringHelper.httpPathGap);
		sb.append(id.substring(4, 8)).append(StringHelper.httpPathGap);
		sb.append(id.subSequence(8, 10)).append(StringHelper.httpPathGap);
		//取随机字母中的前两位个
		sb.append(id.subSequence(10, 12)).append(StringHelper.httpPathGap);
		sb.append(ID2FileName(id,bitrateType));
		return sb.toString();
	}
	
	public static String ID2FileName(String id,String bitrateType) {
		return bitrateType+id;
		/*if(NormalOrginalBitrateType.equals(bitrateType))
			return id;
		else return bitrateType+id;*/
	}
	
	public static String URL2ID(String url) {
		return FilePath2ID(url);
	}
	private static String FilePath2ID(String path) {
		return FileName2ID(path.substring(path.lastIndexOf("/") + 1));
	}
	
	private static String FileName2ID(String fileName) {
		return fileName.substring(2);
	}
	
	public static void main(String[] argv){
		String fid = "2012020703dNH.jpg";
		System.out.println(CustomerImageRule.ID2URL("album",fid));
		System.out.println(CustomerImageRule.ID2URL("album",fid,"AL"));
		/*String user = "jingfm";
		String passwd = "Jingfm2012";
		//String url = "jingfm.duomi.com/2012/0110/08/Yg/2012011008Ygw.m4a";
		//http://wscp.lxdns.com:8080/wsCP/servlet/contReceiver?username=abc&passwd=0555572ad8c2cfd598d6d43bd1a04d1c&url=www.abc.com/a.html;www.abc.com/b.html
		SecureCustomerAudioRule rule = new SecureCustomerAudioRule();
		StringBuilder sburl =new StringBuilder(RuntimeConfiguration.Media_Access_EndPoint_Prefix.substring(7)+rule.ID2NonSecureURI(fid,BitrateType.NORMALORIGINAL));
		System.out.println(sburl.toString());
		String md5pass = DigestHelper.md5ToHex(user.concat(passwd).concat(sburl.toString()));
		StringBuilder sb =new StringBuilder();
		sb.append("http://wscp.lxdns.com:8080/wsCP/servlet/contReceiver?").append("username=jingfm&").append("passwd=").append(md5pass).append("&url=").append(sburl.toString());
		System.out.println(sb.toString());*/
		
	}
}
