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

    //登录URL
    String LOGIN_URL = "/doLogin";

    //登出URL
    String LOGOUT_URL= "/doLogout";

    //login_type
    String LOGIN_TYPE = "loginType";

    //商城后台管理系统用户
    String SYS_USER_LOGIN = "sysUserLogin";

    //商城用户购物系统用户
    String MEMBER_LOGIN = "memberLogin";

    //redis中的token有效时间
    Long TOKEN_TIME = 14400L;


}
