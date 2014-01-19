package com.jingfm.api;

import java.util.HashMap;
import java.util.Map;

public enum ResponseErrorCode {    
	/*
	validate.Code.notEquals = 验证码不匹配
	validate.userOrpwd.error = 用户名密码错误
	validate.request.post.method = 只支持post请求*/
	
	VALIDATE_CODE_NOTEQUALS("1001","validate.code.notequals"),
	VALIDATE_USERORPWD_ERROR("1002","validate.userorpwd.error"),
	VALIDATE_REQUEST_POST_METHOD("1003","validate.request.post.method"),
	
	COMMON_SYSTEM_UNKOWN_ERROR("1000", "common.system.unknow.error"),
	COMMON_BUSINESS_ERROR("999", "common.business.error"),
	//COMMON_UNKOWN("999", "common.unkown"),
	COMMON_LOGIN("998", "common.login"),
	COMMON_DATA_NOTEXIST("997", "common.data.notexist"),
	
	COMMON_DATA_VALIDATE_EMPTY("996", "common.data.validate.empty"),
	COMMON_DATA_VALIDATE_ILEGAL("995", "common.data.validate.ilegal"),
	COMMON_DATA_PARAM_ERROR("994", "common.data.param.error"),
	COMMON_CONNECT_TIMEOUT_ERROR("993", "common.connect.timeout"),
	COMMON_DATA_ALREADYEXIST("992", "common.data.alreadyexist"),
	
	SNS_FIND_NOT_TOKEN("801", "sns.token.not"), 
	SNS_EXPIRED_TOKEN("802", "sns.token.expired"), 
	SNS_GET_TOKEN_FAIL("803", "sns.token.getfail"),
	SNS_API_TIMEOUT("804", "sns.api.timeout"), 
	SNS_FIND_NOT_USER("805", "sns.user.not"), 
	///SNS-TRY_AGAIN("try_again","905"),
	SNS_SAME_AUID_BIND("806", "sns.bind.duplicateauid"),
	
	SEARCH_FAVSONG_KEYWORDS_PARSER_ATTARGET_EMPTY("799","search.friendfavsong.keywords.parser.attarget.empty"),
	SEARCH_COMMON_KEYWORDS_PARSER_ILLEGAL("798","search.keywords.parser.illegal"),
	SEARCH_NATURALWORD_RESPONSE("797","search.naturalword.reponse"),
	
	TASK_ALREADY_ACCEPTED_OR_COMPLETED("699","task.already.acceptedorcompleted"),
	TASK_UNDEFINED("698","task.undefined"),
	TASK_VALIDATE_PRETASKUNCOMPLETED("697","task.validate.pretaskuncompleted"),
	
	
	FUNC_UNLOCK("650","func.unlock"),
	
	FILTER_TAG_NOTEXIST("701", "filter.tag.notexist"),
	FILTER_TAG_EXIST("702", "filter.tag.exist"),
	FILTER_TAG_ARTIST("703", "filter.tag.artist"),
	
    LOGIN_UNAME_OR_PWD_INVALID("201","login.usernameOrpwd.invalid"),
    LOGIN_PWD_INVALID("202","login.pwd.invalid"),
    LOGIN_COOKIE_TOKEN_EXPIRED("203","login.cookie.token.expired"),
    LOGIN_COOKIE_VALIDATE_ACTION_EXCEPTION("204","login.cookie.token.validate.exception"),
    LOGIN_USER_DATA_NOTEXIST("205","login.user.data.notexist"),
    
    AUTH_TOKEN_INVALID("250","auth.token.invalid"),
    AUTH_EMAIL_INVALID_FORMAT("251","auth.email.format.invalid"),
    AUTH_NICKNAME_INVALID_FORMAT("252","auth.nickname.format.invalid"),
	AUTH_NICKNAME_DATA_EXIST("253","auth.nickname.data.exist"),
	AUTH_EMAIL_DATA_EXIST("254","auth.email.data.exist"),
	AUTH_REMOTECOOKIES_EMPTY("255","auth.remotecookies.empty"),
	AUTH_REMOTECOOKIES_INVALID("256","auth.remotecookies.invalid"),
	AUTH_PARAM_SYSTEM_WORD_RESERVED("257","auth.param.system.wordreserved"),
	AUTH_PARAM_SYSTEM_WORD_FORBIDDEN("258","auth.param.system.wordforbidden"),
	AUTH_TOKEN_REMOTEUID_NOTMATCH("259","auth.token.remoteuid.notmatch"),
	AUTH_TOKEN_TIMEOUT("260","auth.token.timeout"),
	AUTH_EMAIL_EMPTY("261","auth.email.empty"),
	AUTH_NICKNAME_INVALID_LENGTH("261","auth.nickname.length.invalid"),
	AUTH_PERMALINK_INVALID_FORMAT("262","auth.permalink.format.invalid"),
	AUTH_PERMALINK_DATA_EXIST("263","auth.permalink.data.exist"),
	AUTH_PERMALINK_INVALID_LENGTH("264","auth.permalink.length.invalid"),
	
	USER_DATA_NOT_EXIST("301","user.data.notexist"),
	USER_TOKENS_GEN_ALREADY_FULL("310","user.tokens.gen.already.full"),
	USER_TOKENS_INVALID("311","user.tokens.invalid"),
	USER_AVATAR_UPLOAD_IMAGE_FILEFORMAT_INVALID("312","user.avatar.upload.image.fileformat.invalid"),
	USER_OPERATION_UPDPWD_NOTMATCH("313","user.operation.updpwd.notmatch"),
	USER_NICK_ALREADY_BEUSED("314","user.nick.already.beused"),
	USER_FRIEND_COUNT_OVER_MAX("315","user.friend.count.over.max"),
	USER_PERSONAL_INVALID("316","user.personal.invalid"),
	USER_FRIEND_PERSONAL_INVALID("317","user.friend.personal.invalid"),
	USER_FORGOTPWD_TIMES_REACHLIMIT("318","user.forgotpwd.times.reachlimit"),
	USER_TICKER_COLLECT_EXIST("319","user.ticker.collect.exist"),
	
	
	
	DEVICE_TYPE_NOT_SUPPORTED("350","device.type.notsupported"),
	
	REQUEST_TIMEOUT_ERROR("400","400.pagerequest.timeout.error"),
	
	REQUEST_404_ERROR("404","404.pagerequest.notfound.error"),
	REQUEST_403_ERROR("403","403.pagerequest.forbidden.error"),
	REQUEST_500_ERROR("500","500.pagerequest.server.error"),
	REQUEST_UNKNOW_ERROR("599","unknow.pagerequest.error"),
	REQUEST_NULL_ERROR("598","Empty error");
    private String code;
    private String i18n;

    ResponseErrorCode(String code, String i18n) {
        this.code = code;
        this.i18n = i18n;
    }

    public String code() {
        return code;
    }

    public String i18n() {
        return this.i18n;
    }

    
    private static Map<String, ResponseErrorCode> mapCode;
	
	static {
		mapCode = new HashMap<String, ResponseErrorCode>();
		ResponseErrorCode[] types = values();//new ThumbType[] {SMALL, MIDDLE, LARGE, ORIGINAL};
		for (ResponseErrorCode type : types){
			mapCode.put(type.code, type);
		}
	}
	public static ResponseErrorCode getByCode(String code) {
		ResponseErrorCode ret = mapCode.get(code);
		if(ret == null) return REQUEST_NULL_ERROR;
		return ret;
	}
}
