package com.bryan.staff.Constant;

public class SecurityConstant {

	public static final long EXPIRATION_TIME = 432000000;
	public static final String TOKEN_PREFIX = "Bearer ";
	public static final String JWT_TOKEN_HEADER = "Jwt-Token";
	public static final String TOKEN_CANNOT_BE_VERIFIED = "Token cannot be verified";
	public static final String GET_BRYAN = "Get Bryan";
	public static final String GET_BRYAN_ADMINISTRATION = "USER MANAGEMENT";
	public static final String AUTHORITIES = "Authorities";
	public static final String FORBIDDEN_MESSAGE = "Log in to access this page";
	public static final String ACCESS_DENIED_MESSAGE = "You dont have permission to access this page";
	public static final String OPTIONS_HTTP_METHOD = "OPTIONS";
	public static final String[] PUBLIC_URLS = {"/user/login","/user/image/**"};
	//public static final String[] PUBLIC_URLS = {"**"};
}
