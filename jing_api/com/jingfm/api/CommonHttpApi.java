package com.jingfm.api;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_INTERNAL_ERROR;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static java.net.HttpURLConnection.HTTP_OK;

import java.io.File;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import com.jingfm.api.builder.CommonBuilder;
import com.jingfm.api.context.AppContext;
import com.jingfm.api.context.ClientContext;
import com.jingfm.api.helper.HttpRequest;
import com.jingfm.api.helper.HttpRequest.HttpRequestException;

public abstract class CommonHttpApi {

	static {
		HttpRequest.keepAlive(false);
	}
	
	/**
	 * 只是用于 用户登录或注册请求是需要从Http head中提取数据
	 * @param url
	 * @param params
	 * @param responseHeader
	 * @return
	 */
	public static String post(String url,Map<?, ?> params,Map<String,String> responseHeader,boolean loginvalidate){
		HttpRequest request = null;
		try{
			request = HttpRequest.post(url);
			if(loginvalidate){
				request.header(ClientContext.JingATokenHeader, AppContext.getClientContext().getAtoken());
				request.header(ClientContext.JingRTokenHeader, AppContext.getClientContext().getRtoken());
			}
			request.form(params);
			int code = request.code();
			if(HTTP_OK == code){
				String body = request.body();
				String atoken = request.header(ClientContext.JingATokenHeader);
				String rtoken = request.header(ClientContext.JingRTokenHeader);
				String stime = request.header(ClientContext.JingSTimeHeader);
				SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
				//得到毫秒数
				long timeStart = System.currentTimeMillis();
				try {
					timeStart=sdf.parse(stime).getTime();
				} catch (Exception e) {
					e.printStackTrace();
				}
				stime = String.valueOf(timeStart);
				responseHeader.put(ClientContext.JingATokenHeader, atoken);
				responseHeader.put(ClientContext.JingRTokenHeader, rtoken);
				responseHeader.put(ClientContext.JingSTimeHeader, stime);
				System.out.println("~~~~~~:"+body);
				
				return body;
			}else{
				switch(code){
					case HTTP_BAD_REQUEST:
						break;
					case HTTP_INTERNAL_ERROR:
						break;	
					case HTTP_NOT_FOUND:
						break;
					default:
						break;
				}
				return  CommonBuilder.builderJsonResultError(code);
				//return JsonHelper.toJSONString(CommonBuilder.buildResultError(code));
			}
		}catch(HttpRequestException ex){
			ex.printStackTrace(System.out);
			return CommonBuilder.builderJsonResultError(ex.getCode());//JsonHelper.toJSONString(CommonBuilder.buildResultError(ex.getCode()));
		}finally{
			if(request != null){
				//request.disconnect();
				//request = null;
			}
		}
	}
	public static String post(String url,Map<?, ?> params){
		HttpRequest request = null;
		try{
			request = HttpRequest.post(url);
			request.header(ClientContext.JingATokenHeader, AppContext.getClientContext().getAtoken());
			request.header(ClientContext.JingRTokenHeader, AppContext.getClientContext().getRtoken());
			request.connectTimeout(10*1000);
			request.readTimeout(10*1000);
			request.form(params);

			int code = request.code();
			if(HTTP_OK == code){
				String body = (request.body());
				System.out.println("~~~~~~:"+body);
				return body;
			}else{
				switch(code){
					case HTTP_BAD_REQUEST:
						break;
					case HTTP_INTERNAL_ERROR:
						break;	
					case HTTP_NOT_FOUND:
						break;
					default:
						break;
				}
				return CommonBuilder.builderJsonResultError(code);//CommonBuilder.builderJsonResultError(code);JsonHelper.toJSONString(CommonBuilder.buildResultError(code));
			}
		}catch(HttpRequestException ex){
			ex.printStackTrace(System.out);
			return CommonBuilder.builderJsonResultError(ex.getCode());//JsonHelper.toJSONString(CommonBuilder.buildResultError(ex.getCode()));
			/*String jsonStr = null;
			switch(ex.getCode()){
				case HttpRequestException.Connection_TimeOut_Code:
					jsonStr = JsonHelper.toJSONString(CommonBuilder.buildResultError(HttpRequestException.Connection_TimeOut_Code));
					System.out.println("~~~~~~~Connection_TimeOut_Code");
					break;
				case HttpRequestException.Read_TimeOut_Code:
					jsonStr = JsonHelper.toJSONString(CommonBuilder.buildResultError(HttpRequestException.Read_TimeOut_Code));
					System.out.println("~~~~~~~Read_TimeOut_Code");
					break;	
				case HttpRequestException.Unknow_Code:
					jsonStr = JsonHelper.toJSONString(CommonBuilder.buildResultError(HttpRequestException.Unknow_Code));
					System.out.println("~~~~~~~Unknow_Code");
					break;
				default:
					break;
			}*/
		}finally{
			if(request != null){
				//request.disconnect();
				//request = null;
			}
		}
	}
	
	
	public static String post(String url,String body){
		HttpRequest request = null;
		try{
			request = HttpRequest.post(url);
			request.header(ClientContext.JingATokenHeader, AppContext.getClientContext().getAtoken());
			request.header(ClientContext.JingRTokenHeader, AppContext.getClientContext().getRtoken());
			request.connectTimeout(10*1000);
			request.readTimeout(10*1000);
			//request..form(params);
			request.send(body);
			int code = request.code();
			if(HTTP_OK == code){
				String responsebody = (request.body());
				System.out.println("~~~~~~:"+responsebody);
				return responsebody;
			}else{
				switch(code){
					case HTTP_BAD_REQUEST:
						break;
					case HTTP_INTERNAL_ERROR:
						break;	
					case HTTP_NOT_FOUND:
						break;
					default:
						break;
				}
				return CommonBuilder.builderJsonResultError(code);//CommonBuilder.builderJsonResultError(code);JsonHelper.toJSONString(CommonBuilder.buildResultError(code));
			}
		}catch(HttpRequestException ex){
			ex.printStackTrace(System.out);
			return CommonBuilder.builderJsonResultError(ex.getCode());//JsonHelper.toJSONString(CommonBuilder.buildResultError(ex.getCode()));
		}finally{
			if(request != null){
			}
		}
	}
	
	public static void postWithoutAuthorityToken(String url,Map<Object, Object> params){
		HttpRequest request = HttpRequest.post(url);
		params.put(ClientContext.JingATokenHeader, AppContext.getClientContext().getAtoken());
		params.put(ClientContext.JingRTokenHeader, AppContext.getClientContext().getRtoken());
		int code = request.form(params).code();
		if(HTTP_OK == code){
			
		}else{
			
		}
	}
	
	
	
	public static String postMultifiles(String url,Map<?, ?> params){
		//if(files == null || files.length == 0) return;
		/*HttpRequest request = HttpRequest.post(url);
		Map<Object, Object> params = new HashMap<Object,Object>();
		params.put(ClientContext.JingATokenHeader, AppContext.getClientContext().getAtoken());
		params.put(ClientContext.JingRTokenHeader, AppContext.getClientContext().getRtoken());
		for(String file:files){
			File filetmp = new File(file);
			if(filetmp.exists() && filetmp.isFile()){
				int code = request.form(params).send(filetmp).code();
				if(HTTP_OK == code){
					
				}else{
					
				}
			}
		}*/
		
		HttpRequest request = null;
		try{
			String uid = String.class.cast(params.get("uid"));
			File filetmp = (File) params.get("file");
			request = HttpRequest.post(url);
			request.header(ClientContext.JingATokenHeader, AppContext.getClientContext().getAtoken());
			request.header(ClientContext.JingRTokenHeader, AppContext.getClientContext().getRtoken());
			//request.contentType(HttpRequest.CONTENT_TYPE_MULTIPART);
			request.connectTimeout(10*1000);
			request.readTimeout(10*1000);
			request.part("uid", uid);
			request.part("file", "temp.jpg", filetmp);
			int code = request.code();
			if(HTTP_OK == code){
				String body = request.body();
				System.out.println("~~~~~~:"+body);
				return body;
			}else{
				switch(code){
					case HTTP_BAD_REQUEST:
						break;
					case HTTP_INTERNAL_ERROR:
						break;	
					case HTTP_NOT_FOUND:
						break;
					default:
						break;
				}
				return CommonBuilder.builderJsonResultError(code);//JsonHelper.toJSONString(CommonBuilder.buildResultError(code));
			}
			//request.form(params);
			/*for(String file:files){
				File filetmp = new File(file);
				if(filetmp.exists() && filetmp.isFile()){
					request.part("uid", String.class.cast(params.get("uid")));
					request.part("file", "temp.jpg", new File(files[0]));
					int code = request.code();
					if(HTTP_OK == code){
						String body = (request.body());
						System.out.println("body:"+body);
					}else{
						switch(code){
							case HTTP_BAD_REQUEST:
								break;
							case HTTP_INTERNAL_ERROR:
								break;	
							case HTTP_NOT_FOUND:
								break;
							default:
								break;
						}
						System.out.println("Error body:"+CommonBuilder.buildResultError(code));
					}
				}
			}*/
		}catch(HttpRequestException ex){
			ex.printStackTrace(System.out);
			return CommonBuilder.builderJsonResultError(ex.getCode());//JsonHelper.toJSONString(CommonBuilder.buildResultError(ex.getCode()));
		}finally{
			if(request != null){
			}
		}
	}
}
