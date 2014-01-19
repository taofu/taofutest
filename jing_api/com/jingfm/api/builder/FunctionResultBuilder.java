
package com.jingfm.api.builder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.CommonHttpApi;
import com.jingfm.api.ResultResponse;
import com.jingfm.api.context.ClientContext;
import com.jingfm.api.helper.JsonHelper;
import com.jingfm.api.helper.StringHelper;
import com.jingfm.api.model.AppLoginDataDTO;
import com.jingfm.api.model.AppUserDTO;
import com.jingfm.api.model.AvatarDTO;
import com.jingfm.api.model.BadgeDTO;
import com.jingfm.api.model.CasualCmbtDTO;
import com.jingfm.api.model.ChartNodeDTO;
import com.jingfm.api.model.ListResult;
import com.jingfm.api.model.LoginDataDTO;
import com.jingfm.api.model.MovinfoDTO;
import com.jingfm.api.model.MusicDTO;
import com.jingfm.api.model.MusicInfoDTO;
import com.jingfm.api.model.PlyinfoDTO;
import com.jingfm.api.model.SiteMessageDTO;
import com.jingfm.api.model.TrackDTO;
import com.jingfm.api.model.UserAvatarsDTO;
import com.jingfm.api.model.UserDTO;
import com.jingfm.api.model.UserFrdDTO;
import com.jingfm.api.model.UserPersonalDataDTO;
import com.jingfm.api.model.UserPersonalSettingDTO;
import com.jingfm.api.model.UserPlayingDTO;
import com.jingfm.api.model.UserRelateDTO;
import com.jingfm.api.model.UserSettingSwitchDTO;
import com.jingfm.api.model.UserStatDTO;
import com.jingfm.api.model.UserUnlockBadgeDTO;
import com.jingfm.api.model.message.OfflineSiteMessageDTO;
import com.jingfm.api.model.message.UserOfflineMessageDTO;
import com.jingfm.api.model.oauth.OAuthLoginDataDTO;
import com.jingfm.api.model.oauth.OAuthLoginDataNormalDTO;
import com.jingfm.api.model.oauth.OAuthLoginDataNouserDTO;
import com.jingfm.api.model.socketmessage.SocketPChatPayloadShareTrackDTO;
import com.jingfm.api.model.sysmessage.ActySysMessageDTO;
import com.jingfm.api.model.sysmessage.FlwdSysMessageDTO;
import com.jingfm.api.model.sysmessage.InhsSysMessageDTO;
import com.jingfm.api.model.sysmessage.RmndSysMessageDTO;
import com.jingfm.api.model.sysmessage.SysMessageDTO;
import com.jingfm.api.model.sysmessage.SysMessageType;

/**
 * 
 * @author Edmond Lee
 */
public class FunctionResultBuilder{
	
	public static ResultResponse<OAuthLoginDataDTO> buildOAuthDTO(String responseContent) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildResultError(jsonObject);
		}
		if(jsonObject.isNull("result")){
			return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		ResultResponse<OAuthLoginDataDTO> response= ResultResponse.create();
		String msg = jsonObject.getString("msg");
		response.setSuccess(true);
		response.setCodemsg(msg);
		
		OAuthLoginDataDTO dto = new OAuthLoginDataDTO();
		JSONObject result = jsonObject.getJSONObject("result");
		Map<String,String> mapdto = null;
		if(result != null){
			mapdto = JsonHelper.getMap(result);
			String association = mapdto.get("association");
			if(StringHelper.isNotEmpty(association)){
				//正常登陆
				if(Boolean.valueOf(association)){
					//dto = JsonHelper.getDTO(result, OAuthLoginDataNormalDTO.class);
					dto = buildOAuthLoginDataNormalDTO(result);
				}else{
					dto = JsonHelper.getDTO(result, OAuthLoginDataNouserDTO.class);
				}
			}
			if(mapdto.get(ClientContext.JingATokenHeader) != null) dto.setJingAToken(mapdto.get(ClientContext.JingATokenHeader));
			if(mapdto.get(ClientContext.JingRTokenHeader) != null) dto.setJingRToken(mapdto.get(ClientContext.JingRTokenHeader));
			if(mapdto.get(ClientContext.JingSTimeHeader) != null) dto.setJingSTime(mapdto.get(ClientContext.JingSTimeHeader));
		}
		response.setResult(dto);
		return response;
	}
	
	/**
	 * 第三方登陆成功DTO
	 * @param result
	 * @return
	 * @throws JSONException
	 */
	public static OAuthLoginDataNormalDTO buildOAuthLoginDataNormalDTO(JSONObject result) throws JSONException {
		OAuthLoginDataNormalDTO dto = new OAuthLoginDataNormalDTO();
		
		if(!result.isNull("usr")){
			dto.setUsr(JsonHelper.getDTO(result.getJSONObject("usr"), UserDTO.class));
		}
		
		if(!result.isNull("pld")){
			dto.setPld(JsonHelper.getDTO(result.getJSONObject("pld"), UserPlayingDTO.class));
		}
		
		if(!result.isNull("avbF")){
			dto.setAvbF(result.getString("avbF"));
		}
		if(!result.isNull("cm")){
			dto.setCm(result.getInt("cm"));
		}
		
		if(!result.isNull("sts")){
			dto.setSetswitch(JsonHelper.getDTO(result.getJSONObject("sts"), UserSettingSwitchDTO.class));
		}
		
		if(!result.isNull("snstokens")){
			dto.setSnstokens(JsonHelper.getMap(result.getJSONObject("snstokens")));
		}
		
		if(!result.isNull("association")){
			dto.setAssociation(result.getBoolean("association"));
		}
		
		if(!result.isNull("identify")){
			dto.setIdentify(result.getString("association"));
		}
		
		return dto;
	}
	
	public static ResultResponse<CasualCmbtDTO> buildCasualCmbtDTO(String responseContent) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildResultError(jsonObject);
		}
		if(jsonObject.isNull("result")){
			return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		ResultResponse<CasualCmbtDTO> response= ResultResponse.create();
		String msg = jsonObject.getString("msg");
		response.setSuccess(true);
		response.setCodemsg(msg);
		CasualCmbtDTO dto = new CasualCmbtDTO();
		JSONObject result = jsonObject.getJSONObject("result");
		
		if(!result.isNull("keywords")){
			dto.setKeywords(result.getString("keywords"));
		}
		
		if(!result.isNull("badges")){
			JSONArray items = result.getJSONArray("badges");
			dto.setBadges(JsonHelper.getListDTO(items, BadgeDTO.class));
		}
		
		response.setResult(dto);
		return response;
	}
	
	
	public static ResultResponse<UserAvatarsDTO> buildUserAvatarsDTO(String responseContent) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildResultError();
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildResultError(jsonObject);
		}
		if(jsonObject.isNull("result")){
			return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		ResultResponse<UserAvatarsDTO> response= ResultResponse.create();
		String msg = jsonObject.getString("msg");
		response.setSuccess(true);
		response.setCodemsg(msg);
		JSONObject result = jsonObject.getJSONObject("result");
		UserAvatarsDTO dto = new UserAvatarsDTO();
		if(!result.isNull("id")){
			dto.setId(result.getInt("id"));
		}
		
		if(!result.isNull("items")){
			JSONArray items = result.getJSONArray("items");
			dto.setItems(JsonHelper.getListDTO(items, AvatarDTO.class));
		}
		response.setResult(dto);
		return response;
	}
	
	public static ResultResponse<UserPersonalDataDTO> buildUserPersonalDataDTO(String responseContent) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildResultError(jsonObject);
		}
		if(jsonObject.isNull("result")){
			return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		ResultResponse<UserPersonalDataDTO> response= ResultResponse.create();
		String msg = jsonObject.getString("msg");
		response.setSuccess(true);
		response.setCodemsg(msg);
		UserPersonalDataDTO dto = new UserPersonalDataDTO();
		JSONObject result = jsonObject.getJSONObject("result");
		
		
		if(!result.isNull("cover")){
			dto.setCover(result.getString("cover"));
		}
		
		if(!result.isNull("pt")){
			dto.setPt(result.getString("pt"));
		}
		
		if(!result.isNull("user")){
			dto.setUser(JsonHelper.getDTO(result.getJSONObject("user"), UserRelateDTO.class));
		}
		
		if(!result.isNull("favoralbums")){
			dto.setFavoralbumfids(JsonHelper.getListDTO(result.getJSONArray("favoralbums"), String.class));
		}
		
		if(!result.isNull("stat")){
			dto.setStat(JsonHelper.getDTO(result.getJSONObject("stat"), UserStatDTO.class));
		}
		
		if(!result.isNull("mood")){
			dto.setMood(JsonHelper.getListDTO(result.getJSONArray("mood"), UserUnlockBadgeDTO.class));
		}
		
		
		/*if(!result.isNull("pld")){
			dto.setPld(JsonHelper.getDTO(result.getJSONObject("pld"), UserPlayingDTO.class));
			//dto.setTid(result.getInt("tid"));
		}
		
		if(!result.isNull("avbF")){
			dto.setAvbF(result.getString("avbF"));
		}
		
		
		if(!result.isNull("sts")){
			dto.setSts(JsonHelper.getMap(result.getJSONObject("sts")));
		}
		
		if(!result.isNull("snstokens")){
			dto.setSnstokens(JsonHelper.getMap(result.getJSONObject("snstokens")));
		}*/
		
		response.setResult(dto);
		return response;
	}
	
	
	public static ResultResponse<UserPersonalSettingDTO> buildUserPersonalSettingDTO(String responseContent) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildResultError(jsonObject);
		}
		if(jsonObject.isNull("result")){
			return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		ResultResponse<UserPersonalSettingDTO> response= ResultResponse.create();
		String msg = jsonObject.getString("msg");
		response.setSuccess(true);
		response.setCodemsg(msg);
		UserPersonalSettingDTO dto = new UserPersonalSettingDTO();
		JSONObject result = jsonObject.getJSONObject("result");
		
		
		if(!result.isNull("setting")){
			dto.setUserswitch(JsonHelper.getDTO(result.getJSONObject("setting"), UserSettingSwitchDTO.class));
		}
		
		if(!result.isNull("links")){
			dto.setUserlinks(JsonHelper.getListDTO(result.getJSONArray("links"), String.class));
		}
		
		if(!result.isNull("userinfo")){
			dto.setUserinfo(JsonHelper.getDTO(result.getJSONObject("userinfo"), UserDTO.class));
		}
		
		if(!result.isNull("identifys")){
			dto.setIdentifys(JsonHelper.getListDTO(result.getJSONArray("identifys"), String.class));
		}
		
		response.setResult(dto);
		return response;
	}
	
	/**
	 * 新建用户 登录 数据解析
	 * @param responseContent
	 * @return
	 * @throws JSONException
	 */
	public static ResultResponse<LoginDataDTO> buildLoginDataDTO(String responseContent) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildResultError(jsonObject);
		}
		if(jsonObject.isNull("result")){
			return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		ResultResponse<LoginDataDTO> response= ResultResponse.create();
		String msg = jsonObject.getString("msg");
		response.setSuccess(true);
		response.setCodemsg(msg);
		LoginDataDTO dto = new LoginDataDTO();
		JSONObject result = jsonObject.getJSONObject("result");
		
		if(!result.isNull("usr")){
			dto.setUsr(JsonHelper.getDTO(result.getJSONObject("usr"), UserDTO.class));
		}
		
		if(!result.isNull("pld")){
			dto.setPld(JsonHelper.getDTO(result.getJSONObject("pld"), UserPlayingDTO.class));
			//dto.setTid(result.getInt("tid"));
		}
		
		if(!result.isNull("avbF")){
			dto.setAvbF(result.getString("avbF"));
		}
		
		
		if(!result.isNull("sts")){
			dto.setSetswitch(JsonHelper.getDTO(result.getJSONObject("sts"), UserSettingSwitchDTO.class));
		}
		
		if(!result.isNull("cm")){
			dto.setCm(result.getInt("cm"));
		}
		
		if(!result.isNull("snstokens")){
			dto.setSnstokens(JsonHelper.getMap(result.getJSONObject("snstokens")));
		}
		
		response.setResult(dto);
		return response;
	}
	
	/**
	 * 返回搜索好友的列表
	 * @param responseContent
	 * @return
	 * @throws JSONException
	 */
	public static ResultResponse<ListResult<UserFrdDTO>> buildUserFrdDTO(String responseContent) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildResultError(jsonObject);
		}
		if(jsonObject.isNull("result")){
			return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		ResultResponse<ListResult<UserFrdDTO>> response= ResultResponse.create();
		String msg = jsonObject.getString("msg");
		response.setSuccess(true);
		response.setCodemsg(msg);
		
		ListResult<UserFrdDTO> result = new ListResult<UserFrdDTO>();
		
		JSONObject jsonObjectResult = jsonObject.getJSONObject("result");
		if(!jsonObjectResult.isNull("total")){
			result.setTotal(jsonObjectResult.getInt("total"));
		}
		if(!jsonObjectResult.isNull("st")){
			result.setSt(jsonObjectResult.getInt("st"));
		}
		
		if(!jsonObjectResult.isNull("ps")){
			result.setPs(jsonObjectResult.getInt("ps"));
		}
		
		List<UserFrdDTO> items = new ArrayList<UserFrdDTO>();
		JSONArray array = jsonObjectResult.getJSONArray("items");
		for (int i=0; i < array.length(); i++) {
			JSONObject jsonFrdObject = array.getJSONObject(i);
			UserFrdDTO dto = new UserFrdDTO();
			if(!jsonFrdObject.isNull("uid")){
				dto.setUid(String.valueOf(jsonFrdObject.getInt("uid")));
			}
			
			if(!jsonFrdObject.isNull("flwd")){
				dto.setFlwd(jsonFrdObject.getBoolean("flwd"));
			}
			
			if(!jsonFrdObject.isNull("frdshp")){
				dto.setFrdshp(jsonFrdObject.getBoolean("frdshp"));
			}
			
			
			if(!jsonFrdObject.isNull("from")){
				dto.setFrom(jsonFrdObject.getString("from"));
			}
			
			if(!jsonFrdObject.isNull("nick")){
				dto.setNick(jsonFrdObject.getString("nick"));
			}
			
			if(!jsonFrdObject.isNull("avatar")){
				dto.setAvatar(jsonFrdObject.getString("avatar"));
			}
			
			if(!jsonFrdObject.isNull("device")){
				dto.setDevice(jsonFrdObject.getString("device"));
			}
			
			if(!jsonFrdObject.isNull("ol")){
				dto.setOl(jsonFrdObject.getBoolean("ol"));
			}
			items.add(dto);
		}
		result.setItems(items);
		response.setResult(result);
		return response;
	}
	
	/**
	 * app用户 登录 数据解析
	 * @param responseContent
	 * @return
	 * @throws JSONException
	 */
	public static ResultResponse<AppLoginDataDTO> buildAppLoginDataDTO(String responseContent) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildResultError(jsonObject);
		}
		if(jsonObject.isNull("result")){
			return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		ResultResponse<AppLoginDataDTO> response= ResultResponse.create();
		String msg = jsonObject.getString("msg");
		response.setSuccess(true);
		response.setCodemsg(msg);
		AppLoginDataDTO dto = new AppLoginDataDTO();
		JSONObject result = jsonObject.getJSONObject("result");
		
		if(!result.isNull("usr")){
			dto.setAppusr(JsonHelper.getDTO(result.getJSONObject("usr"), AppUserDTO.class));
		}
		
		response.setResult(dto);
		return response;
	}
	
	/**
	 * 榜单层级数据专用解析
	 * @param responseContent
	 * @return
	 * @throws JSONException
	 */
	@SuppressWarnings("rawtypes")
	public static ResultResponse<Map<String,ListResult<ChartNodeDTO>>> buildChartNodeListResultResponse(String responseContent) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildResultError(jsonObject);
		}
		if(jsonObject.isNull("result")){
			return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		ResultResponse<Map<String,ListResult<ChartNodeDTO>>> response= ResultResponse.create();
		
		String msg = jsonObject.getString("msg");
		response.setSuccess(true);
		response.setCodemsg(msg);
		
		Map<String,ListResult<ChartNodeDTO>> map = new HashMap<String,ListResult<ChartNodeDTO>>();
		
		JSONObject result = jsonObject.getJSONObject("result");
		Iterator iterator = result.keys();
		while(iterator.hasNext()){
			String key = String.class.cast(iterator.next());
			if(!"m".equals(key)){
				JSONObject value = result.getJSONObject(key);
				map.put(key, buildChartNodeDTOListResult(value));
			}
		}
		response.setResult(map);
		return response;
	}
	
	private static ListResult<ChartNodeDTO> buildChartNodeDTOListResult(JSONObject jsonObjectResult) throws JSONException {
		ListResult<ChartNodeDTO> result = new ListResult<ChartNodeDTO>();
		if(!jsonObjectResult.isNull("total")){
			result.setTotal(jsonObjectResult.getInt("total"));
		}
		if(!jsonObjectResult.isNull("st")){
			result.setSt(jsonObjectResult.getInt("st"));
		}
		
		if(!jsonObjectResult.isNull("ps")){
			result.setPs(jsonObjectResult.getInt("ps"));
		}
		
		List<ChartNodeDTO> items = new ArrayList<ChartNodeDTO>();
		
		JSONArray array = jsonObjectResult.getJSONArray("items");
		for (int i=0; i < array.length(); i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			ChartNodeDTO item = JsonHelper.getDTO(jsonObject, ChartNodeDTO.class);
			if(!jsonObject.isNull("childs")){
				List<ChartNodeDTO> childitems = new ArrayList<ChartNodeDTO>();
				JSONArray childarray = jsonObject.getJSONArray("childs");
				for(int k=0;k<childarray.length();k++){
					JSONObject jsonChildObject = childarray.getJSONObject(k);
					ChartNodeDTO childitem = JsonHelper.getDTO(jsonChildObject, ChartNodeDTO.class);
					childitems.add(childitem);
				}
				item.setChilds(childitems);
			}
			items.add(item);
		}
		result.setItems(items);
		return result;
	}
	
	/**
	 * 榜单歌曲数据专用解析
	 * @param responseContent
	 * @return
	 * @throws JSONException
	 */
	@SuppressWarnings("rawtypes")
	public static ResultResponse<Map<String,ListResult<MusicDTO>>> buildChartMusicListResultResponse(String responseContent) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildResultError(jsonObject);
		}
		if(jsonObject.isNull("result")){
			return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		ResultResponse<Map<String,ListResult<MusicDTO>>> response= ResultResponse.create();
		
		String msg = jsonObject.getString("msg");
		response.setSuccess(true);
		response.setCodemsg(msg);
		
		Map<String,ListResult<MusicDTO>> map = new HashMap<String,ListResult<MusicDTO>>();
		
		JSONObject result = jsonObject.getJSONObject("result");
		Iterator iterator = result.keys();
		
		Integer m = null;
		while(iterator.hasNext()){
			String key = String.class.cast(iterator.next());
			if("m".equals(key)){
				m = result.getInt(key);
			}else{
				JSONObject value = result.getJSONObject(key);
				map.put(key, buildChartMusicDTOListResult(value));
			}
		}
		
		if(m != null){
			Collection<ListResult<MusicDTO>> values = map.values();
			for(ListResult<MusicDTO> listResult : values){
				listResult.setM(m);
			}
		}
		response.setResult(map);
		return response;
	}
	
	
	private static ListResult<MusicDTO> buildChartMusicDTOListResult(JSONObject jsonObjectResult) throws JSONException {
		ListResult<MusicDTO> result = new ListResult<MusicDTO>();
		if(!jsonObjectResult.isNull("total")){
			result.setTotal(jsonObjectResult.getInt("total"));
		}
		if(!jsonObjectResult.isNull("st")){
			result.setSt(jsonObjectResult.getInt("st"));
		}
		
		if(!jsonObjectResult.isNull("ps")){
			result.setPs(jsonObjectResult.getInt("ps"));
		}
		
		JSONArray array = jsonObjectResult.getJSONArray("items");
		List<MusicDTO> items = JsonHelper.getListDTO(array, MusicDTO.class);
		result.setItems(items);
		return result;
	}
	
	/**
	 * 系统消息专用解析
	 * @param responseContent
	 * @return
	 * @throws JSONException
	 */
	public static ResultResponse<ListResult<SysMessageDTO>> buildSysMessageDTOListResultResponse(String responseContent) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildResultError(jsonObject);
		}
		if(jsonObject.isNull("result")){
			return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		ResultResponse<ListResult<SysMessageDTO>> response= ResultResponse.create();
		
		String msg = jsonObject.getString("msg");
		response.setSuccess(true);
		response.setCodemsg(msg);
		
		JSONObject result = jsonObject.getJSONObject("result");
		ListResult<SysMessageDTO> listResult = buildSysMessageDTOListResult(result);
		
		response.setResult(listResult);
		return response;
	}
	
	private static ListResult<SysMessageDTO> buildSysMessageDTOListResult(JSONObject jsonObjectResult) throws JSONException {
		ListResult<SysMessageDTO> result = new ListResult<SysMessageDTO>();
		if(!jsonObjectResult.isNull("total")){
			result.setTotal(jsonObjectResult.getInt("total"));
		}
		if(!jsonObjectResult.isNull("st")){
			result.setSt(jsonObjectResult.getInt("st"));
		}
		
		if(!jsonObjectResult.isNull("ps")){
			result.setPs(jsonObjectResult.getInt("ps"));
		}
		
		List<SysMessageDTO> items = new ArrayList<SysMessageDTO>();
		
		JSONArray array = jsonObjectResult.getJSONArray("items");
		for (int i=0; i < array.length(); i++) {
			JSONObject jsonObject = array.getJSONObject(i);
			String t = jsonObject.getString("t");
			SysMessageDTO item = null;
			if(SysMessageType.RMND.getName().equals(t)){
				item = JsonHelper.getDTO(jsonObject, RmndSysMessageDTO.class);
			}else if(SysMessageType.INHS.getName().equals(t)){
				item = JsonHelper.getDTO(jsonObject, InhsSysMessageDTO.class);
			}else if(SysMessageType.FLWD.getName().equals(t)){
				item = JsonHelper.getDTO(jsonObject, FlwdSysMessageDTO.class);
			}else if(SysMessageType.ACTY.getName().equals(t)){
				item = JsonHelper.getDTO(jsonObject, ActySysMessageDTO.class);
			}
			
			if(item != null)
				items.add(item);
		}
		result.setItems(items);
		return result;
	}
	
	public static ResultResponse<MusicInfoDTO> buildMusicInfoDTO(String responseContent) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildResultError(jsonObject);
		}
		if(jsonObject.isNull("result")){
			return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		ResultResponse<MusicInfoDTO> response= ResultResponse.create();
		String msg = jsonObject.getString("msg");
		response.setSuccess(true);
		response.setCodemsg(msg);
		MusicInfoDTO dto = new MusicInfoDTO();
		JSONObject result = jsonObject.getJSONObject("result");
		if(!result.isNull("lvd")){
			dto.setLvd(result.getString("lvd"));
		}
		if(!result.isNull("lrc")){
			dto.setLrc(result.getBoolean("lrc"));
		}
		
		if(!result.isNull("tid")){
			dto.setTid(result.getInt("tid"));
		}
		
		if(!result.isNull("ply_info")){
			List<PlyinfoDTO> plyinfoDtos = new ArrayList<PlyinfoDTO>();
			//dto.setPly_info(result.getString("ply_info"));
			String plyinfoString = result.getString("ply_info");
			if (plyinfoString != null && plyinfoString.length() > 0) {
				JSONObject plyinfoObject = new JSONObject(plyinfoString);
				if(!plyinfoObject.isNull("timeDot")){
					JSONObject timeDotObject = plyinfoObject.getJSONObject("timeDot");
					Iterator<String> keys = timeDotObject.keys();
					while(keys.hasNext()){
						String key = keys.next();
						JSONArray values = timeDotObject.getJSONArray(key);
						for (int i=0; i < values.length(); i++) {
							try{
								//{'民谣吉他':[{'扫弦':'14'}] 这种情况
								JSONObject valueObject = values.getJSONObject(i);
								Iterator<String> valuekeys = valueObject.keys();
								while(valuekeys.hasNext()){
									String valuekey = valuekeys.next();
									int valuekeyvalue = valueObject.getInt(valuekey);
									if (StringHelper.isEmpty(valuekey)) {
										if (StringHelper.isEmpty(key)) {
											continue;
										}else{
											valuekey = key;
										}
									}
									plyinfoDtos.add(new PlyinfoDTO(valuekey, valuekeyvalue));
								}
							}catch(Exception ex){
								//'电吉他':['14','20']
								String value = values.getString(i);
								plyinfoDtos.add(new PlyinfoDTO(key, Integer.parseInt(value)));
							}

						}
					}
					
					if(!plyinfoDtos.isEmpty()){
						Collections.sort(plyinfoDtos, new Comparator<PlyinfoDTO>() {
					           public int compare(PlyinfoDTO a, PlyinfoDTO b) {
					           	int flag = 0;
					           	if(a.getTime()>b.getTime()) flag = 1;
					           	else if(a.getTime()<b.getTime()) flag = -1;
					           	else flag = 0;
					           	
					       		return flag; 
					           }
					       });
						
						dto.setPly_info(plyinfoDtos);
					}
				}
			}
		}
		
		if(!result.isNull("vsn")){
			/*JSONArray array = result.getJSONArray("vsn");
			List<String> vsn = new ArrayList<String>();
			for(int i = 0;i<array.length();i++){
				String value = array.getString(i);
				vsn.add(value);
			}
			dto.setVsn(vsn);*/
			dto.setVsn(JsonHelper.getListDTO(result.getJSONArray("vsn"), String.class));
		}

        if(!result.isNull("orgn")){
            dto.setOrgn(JsonHelper.getListDTO(result.getJSONArray("orgn"), TrackDTO.class));
        }

		if(!result.isNull("feat")){
			dto.setFeat(JsonHelper.getMap(result.getJSONObject("feat")));
		}
		
		if(!result.isNull("mov_info")){
			dto.setMov_info(JsonHelper.getDTO(result.getJSONObject("mov_info"), MovinfoDTO.class));
		}
		
		if(!result.isNull("cmps_info")){
			dto.setCmps_info(JsonHelper.getMap(result.getJSONObject("cmps_info")));
		}
		
		response.setResult(dto);
		return response;
		//return null;
	}
	
	public static ResultResponse<Map<String, Object>> buildOffLineMusicMessageDTOListResult(String responseContent) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildResultError(jsonObject);
		}
		if(jsonObject.isNull("result")){
			return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		
		ResultResponse<Map<String, Object>> response= ResultResponse.create();
		String msg = jsonObject.getString("msg");
		response.setSuccess(true);
		response.setCodemsg(msg);
		
		JSONObject result = jsonObject.getJSONObject("result");
		ListResult<MusicDTO> items = CommonBuilder.buildListResult(result, MusicDTO.class);
		Integer cm = result.getInt("cm");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("cm",cm);
		map.put("items",items);
		response.setResult(map);
		return response;
	}
	
	
	public static ResultResponse<ListResult<SiteMessageDTO>> buildSiteMessageDTOListResult(String responseContent) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildResultError(jsonObject);
		}
		if(jsonObject.isNull("result")){
			return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		
		ResultResponse<ListResult<SiteMessageDTO>> response= ResultResponse.create();
		String msg = jsonObject.getString("msg");
		response.setSuccess(true);
		response.setCodemsg(msg);
		
		JSONObject result = jsonObject.getJSONObject("result");
		ListResult<SiteMessageDTO> listresult = new ListResult<SiteMessageDTO>();
		List<SiteMessageDTO> items = new ArrayList<SiteMessageDTO>();
		JSONArray array = result.getJSONArray("items");
		for (int i=0; i < array.length(); i++) {
			JSONObject jsonItemObject = array.getJSONObject(i);
			SiteMessageDTO dto = new SiteMessageDTO();
			if(!jsonItemObject.isNull("ctt")){
				dto.setCtt(jsonItemObject.getString("ctt"));
			}
			if(!jsonItemObject.isNull("ts")){
				dto.setTs(jsonItemObject.getString("ts"));
			}
			if(!jsonItemObject.isNull("sf")){
				dto.setSf(jsonItemObject.getBoolean("sf"));
			}
			if(!jsonItemObject.isNull("payload")){
				dto.setPayload(JsonHelper.getDTO(jsonItemObject.getJSONObject("payload"), SocketPChatPayloadShareTrackDTO.class));
			}
			items.add(dto);
		}
		listresult.setItems(items);
		response.setResult(listresult);
		return response;
	}


    public static ResultResponse<ListResult<UserOfflineMessageDTO>> buildOfflineMessageDTOListResult(String responseContent) throws JSONException {
        if(responseContent == null){
            return CommonBuilder.buildResultError(null);
        }
        System.out.println("+++++++++:"+responseContent);
        JSONObject jsonObject = new JSONObject(responseContent);
        boolean success = jsonObject.getBoolean("success");
        if(!success){
            return CommonBuilder.buildResultError(jsonObject);
        }
        if(jsonObject.isNull("result")){
            return CommonBuilder.buildSimpleResultSuccess(jsonObject);
        }

        ResultResponse<ListResult<UserOfflineMessageDTO>> response= ResultResponse.create();
        String msg = jsonObject.getString("msg");
        response.setSuccess(true);
        response.setCodemsg(msg);

        JSONObject result = jsonObject.getJSONObject("result");
        ListResult<UserOfflineMessageDTO> list_result = new ListResult<UserOfflineMessageDTO>();
        list_result.setItems(CommonBuilder.buildSimpleListResult(result.getJSONArray("items"),UserOfflineMessageDTO.class));
        list_result.setChats(CommonBuilder.buildSimpleListResult(result.getJSONArray("chats"),OfflineSiteMessageDTO.class));
        response.setResult(list_result);
        return response;
    }

	public static <T> ResultResponse<T> buildResult(String responseContent,Class<T> classz) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildResultError(jsonObject);
		}
		if(jsonObject.isNull("result")){
			return CommonBuilder.buildSimpleResultSuccess(jsonObject);
		}
		
		ResultResponse<T> response= ResultResponse.create();
		
		String msg = jsonObject.getString("msg");
		
		if(classz == String.class){
			String result = jsonObject.getString("result");
			response.setResult(classz.cast(result));
		}/*else if(classz == List.class){
			//response.setResult(JsonHelper.getDTO(result, classz));
			JSONArray result = jsonObject.getJSONArray("result");
			response.setResult(JsonHelper.getListDTO(result, String.class));
			//dto.setVsn(JsonHelper.getListDTO(result.getJSONArray("vsn"), String.class));
		}*/else{
			JSONObject result = jsonObject.getJSONObject("result");
			response.setResult(JsonHelper.getDTO(result, classz));//CommonBuilder.buildResult(result, classz));
		}
		response.setSuccess(true);
		response.setCodemsg(msg);
		return response;
	}

	
	private static <T> ResultResponse<ListResult<T>> buildListResult(String responseContent,Class<T> classz) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildListResultError(null);
		}
		if (classz.equals(MusicInfoDTO.class)) {
			System.out.println("+++++++++:"+MusicInfoDTO.class);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildListResultError(jsonObject);
		}
		
		ResultResponse<ListResult<T>> response= ResultResponse.create();
		
		String msg = jsonObject.getString("msg");

		JSONObject result = jsonObject.getJSONObject("result");
		response.setResult(CommonBuilder.buildListResult(result, classz));
		response.setSuccess(true);
		response.setCodemsg(msg);
		return response;
	}
	
	
	public static <T> ResultResponse<List<T>> buildSimpleListResult(String responseContent,Class<T> classz) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildSimpleListResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildSimpleListResultError(jsonObject);
		}
		
		ResultResponse<List<T>> response= ResultResponse.create();
		
		String msg = jsonObject.getString("msg");

		JSONArray result = jsonObject.getJSONArray("result");
		response.setResult(CommonBuilder.buildSimpleListResult(result, classz));
		response.setSuccess(true);
		response.setCodemsg(msg);
		return response;
	}
	
	public static ResultResponse<MusicInfoDTO> fetch_trackinfo_post(String url,Map<?, ?> params){
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(url),params);
		if(jsonstring != null){
			try {
				return FunctionResultBuilder.buildMusicInfoDTO(jsonstring);
			} catch (JSONException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}
	
	public static <T> ResultResponse<T> requestResultResponse(String url,Map<?, ?> params,Class<T> classz){
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(url),params);
		if(jsonstring != null){
			try {
				return FunctionResultBuilder.buildResult(jsonstring,classz);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}
	
	public static void requestResultResponse(String url,String body){
		CommonHttpApi.post(ApiUrlDefine.getBaseUrl(url),body);
	}
	
	public static <T> ResultResponse<T> requestSimpleData(String url,Map<?, ?> params,Class<T> classz){
		return requestResultResponse(url, params, classz);
	}
	
	public static void requestSimpleData(String url, String body){
		requestResultResponse(url, body);
	}
	
	
	public static <T> ResultResponse<List<T>> requestSimpleListResultResponse(String url,Map<?, ?> params,Class<T> classz){
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(url),params);
		if(jsonstring != null){
			try {
				return FunctionResultBuilder.buildSimpleListResult(jsonstring,classz);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}
	
	public static <T> ResultResponse<List<T>> requestSimpleListData(String url,Map<?, ?> params,Class<T> classz){
		return requestSimpleListResultResponse(ApiUrlDefine.getApiUrl(url), params, classz);
	}
	
	public static <T> ResultResponse<ListResult<T>> requestListResultResponse(String url,Map<?, ?> params,Class<T> classz){
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(url),params);
		//Log.e(Contants.TAG, "jsonstring = "+jsonstring);
		if(jsonstring != null){
			try {
				System.out.println("json:"+jsonstring);
				return FunctionResultBuilder.buildListResult(jsonstring,classz);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}
	
	/*public static ResultResponse<ListResult<MusicDTO>> pls_post(String url,Map<?, ?> params){
		String jsonstring = CommonHttpApi.post(ApiUrlDefine.getApiUrl(url),params);
		Log.e(Contants.TAG, "jsonstring = "+jsonstring);
		if(jsonstring != null){
			try {
				System.out.println("json:"+jsonstring);
				return FunctionResultBuilder.buildListResult(jsonstring,MusicDTO.class);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return CommonBuilder.buildResultError();
	}*/
	
	
	/*public static <T> ResultResponse<ListResult<T>> build(String responseContent,Class<T> classz) throws JSONException {
		if(responseContent == null){
			return CommonBuilder.buildListResultError(null);
		}
		//System.out.println("+++++++++:"+responseContent);
		JSONObject jsonObject = new JSONObject(responseContent);
		boolean success = jsonObject.getBoolean("success");
		if(!success){
			return CommonBuilder.buildListResultError(jsonObject);
		}
		
		ResultResponse<ListResult<T>> response= ResultResponse.create();
		
		String msg = jsonObject.getString("msg");

		JSONObject result = jsonObject.getJSONObject("result");
		response.setResult(CommonBuilder.buildListResult(result, classz));
		response.setSuccess(true);
		response.setCodemsg(msg);
		return response;
	}*/
	
}
