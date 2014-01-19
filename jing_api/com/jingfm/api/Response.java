package com.jingfm.api;

public class Response {
	
	//public static final Response ERROR = new Response(false,"操作失败");
	//public static final Response BUSINESS_ERROR = new Response("操作失败",ResponseErrorCode.COMMON_BUSINESS_ERROR);
	//public static final Response SYSTEM_ERROR = new Response("操作失败",ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR);

	private boolean success;
	//private boolean msg;
	private String code;
    private String codemsg;
	
	//private T result;

	public boolean isSuccess() {
		return success;
	}
	public Response(){
		
	}
	public Response(boolean success, String codemsg) {
		super();
		this.success = success;
		this.codemsg = codemsg;
	}

	public void setSuccess(boolean success) {
		this.success = success;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getCodemsg() {
		return codemsg;
	}

	public void setCodemsg(String codemsg) {
		this.codemsg = codemsg;
	}

	/*public boolean isMsg() {
		return msg;
	}

	public void setMsg(boolean msg) {
		this.msg = msg;
	}*/

	//public abstract <T> T getResult();
	/*
	public void setResult(T result) {
		this.result = result;
	}*/

	
}
