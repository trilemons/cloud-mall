package com.lemon.constant;

/**
 * 系统业务模块常量类
 */
public interface ManagerConstants {

    /**
     * 系统所有角色数据存放到redis中的KEY
     */
    String SYS_ALL_ROLE_KEY = "'roles'";

    /**
     * 系统所有权限数据存放到redis中的KEY
     */
    String SYS_ALL_MENU_KEY = "'menus'";
}
