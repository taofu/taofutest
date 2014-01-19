package com.jingfm.api.business;

import java.io.StringWriter;

import org.json.JSONObject;

import com.jingfm.api.ApiUrlDefine;
import com.jingfm.api.builder.FunctionResultBuilder;
import com.jingfm.api.helper.JsonHelper;
import com.jingfm.api.model.FeedbackDTO;

public class FeedbackRequestApi {
	/**
	 * 	private Integer uid;//用户ID
		private String p;//平台
		private String c;//错误代码
		private Object ext;//扩展属性
		private String t;//时间GMT
	 * 提交错误报告
	 * @param params
	 * @return
	 */
	public static boolean post_error(int uid, String p,String c,String t,Object ext){
		FeedbackDTO dto = new FeedbackDTO();
		dto.setUid(uid);
		dto.setP(p);
		dto.setC(c);
		dto.setT(t);
		dto.setExt(ext);
		try{
			JSONObject jobject = new JSONObject();
			jobject.put("uid", uid);
			jobject.put("p", p);
			jobject.put("c", c);
			jobject.put("t", t);
			jobject.put("ext", ext);
			/*StringWriter out = new StringWriter();
			jobject.writeJSONString(out);
			String jsonText = out.toString();*/
			FunctionResultBuilder.requestSimpleData(
					ApiUrlDefine.Normal.Feedback.Post_Error,JsonHelper.toJSONString(jobject));
		}catch(Exception ex){
			return false;
		}
		return true;
	}
	
	
}
