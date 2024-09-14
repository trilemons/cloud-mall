package com.lemon.constant;

/**
 * 认证授权常量类
 */
public interface AuthConstants {

    //token的key
    String AUTHORIZATION = "Authorization";
    //token前缀
    String BEARER = "bearer";

    //redis中token的key的前缀
    String LOGIN_TOKEN_PREFIX = "login_token";
}
