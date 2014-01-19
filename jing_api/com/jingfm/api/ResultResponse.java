package com.jingfm.api;

public class ResultResponse<T> extends Response{
	/*public static final Response ERROR = new Response(false,"操作失败"){
		
	};*/
	/*public static final Response BUSINESS_ERROR = new Response("操作失败",ResponseErrorCode.COMMON_BUSINESS_ERROR);
	public static final Response SYSTEM_ERROR = new Response("操作失败",ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR);
*/
	
	private T result;

	public T getResult() {
		return result;
	}

	public void setResult(T result) {
		this.result = result;
	}
	
	public static <T> ResultResponse<T> create(){
		return new ResultResponse<T>();
	}
}
