package com.jingfm.api.builder;


/**
 * 
 * @author Edmond Lee
 */
public class PlsResultBuilder{

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
