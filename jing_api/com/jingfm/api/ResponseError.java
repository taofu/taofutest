package com.jingfm.api;



public class ResponseError extends Response{
	public static final Response ERROR = new ResponseError("操作失败");
	public static final Response BUSINESS_ERROR = new ResponseError("操作失败",ResponseErrorCode.COMMON_BUSINESS_ERROR);
	public static final Response SYSTEM_ERROR = new ResponseError("操作失败",ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR);
	private String code;
    private String codemsg;
    private String stack;
    
    public ResponseError() {
    	this.setSuccess(false);
    }
    public ResponseError(String message) {
    	this();
    	this.setCodemsg(message);
    	//this.setMsg(message);
        //this.codemsg = LocalI18NMessageSource.getInstance().getMessage(responseErrorCode.i18n());
    }
    public ResponseError(String message, ResponseErrorCode responseErrorCode) {
    	this();
    	//this.setCodemsg(message);
    	//this.setMsg(message);
        this.code = responseErrorCode.code();
        this.setCodemsg(responseErrorCode.i18n());
        //Assert.notNull(this.code, "msgcode must be set!");
        //this.codemsg = LocalI18NMessageSource.getInstance().getMessage(responseErrorCode.i18n());
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

	public String getStack() {
		return stack;
	}
	public void setStack(String stack) {
		this.stack = stack;
	}
	
	
	/*public static ResponseError embedSystemError(){
		ResponseError re = new ResponseError(Response.ERROR.getMsg(),ResponseErrorCode.COMMON_SYSTEM_UNKOWN_ERROR);
		return re;
	}*/
	
	/*public static ResponseError embed(String msg,ResponseErrorCode code){
		ResponseError re = new ResponseError(msg,code);
		return re;
	}
	
	public static ResponseError embed(ResponseErrorCode code){
		ResponseError re = new ResponseError(ResponseError.ERROR.getMsg(),code);
		return re;
	}
	public static ResponseError embed(ResponseErrorCode code,String[] txts){
		ResponseError re = new ResponseError(ResponseError.ERROR.getMsg(),code,txts);
		return re;
	}*/
}
