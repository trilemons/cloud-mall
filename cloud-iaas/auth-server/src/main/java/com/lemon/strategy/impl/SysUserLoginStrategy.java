package com.lemon.strategy.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.ObjectUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.lemon.constant.AuthConstants;
import com.lemon.domain.LoginSysUser;
import com.lemon.mapper.LoginSysUserMapper;
import com.lemon.model.SecurityUser;
import com.lemon.strategy.LoginStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 商城后台管理系统登录策略的具体实现
 */
@Service(AuthConstants.SYS_USER_LOGIN)
public class SysUserLoginStrategy implements LoginStrategy {

    @Autowired
    private  LoginSysUserMapper loginSysUserMapper;

    @Override
    public UserDetails realLogin(String username) {
        LambdaQueryWrapper<LoginSysUser> eq = Wrappers.<LoginSysUser>lambdaQuery()
                .eq(LoginSysUser::getUsername, username);
        LoginSysUser loginSysUser = loginSysUserMapper.selectOne(eq);
        if (ObjectUtils.isNotEmpty(loginSysUser)) {
            //根据用户标识查询用户的权限集合
            Set<String> perms = loginSysUserMapper.selectPermsByUserId(loginSysUser.getUserId());
            //创建安全用户对象
            SecurityUser securityUser = new SecurityUser();
            securityUser.setUserId(loginSysUser.getUserId());
            securityUser.setPassword(loginSysUser.getPassword());
            securityUser.setShopId(loginSysUser.getShopId());
            securityUser.setStatus(loginSysUser.getStatus());
            securityUser.setLoginType(AuthConstants.SYS_USER_LOGIN);
            //判断用户权限是否有值
            if (ObjectUtils.isNotEmpty(perms)&&perms.size()!=0) {
                securityUser.setPerms(perms);
            }
            return securityUser;
        }
        return null;
    }

}
