package com.lemon.factory;

import com.lemon.strategy.LoginStrategy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 登录策略工厂类
 */
@Component
public class LoginStrategyFactory {

    /**
     * 当spring发现一个被自动注入的对象是一个Map<String,T>或者是List<T>时
     * 对于前者，当T是某个接口或者类时，spring会将所有继承或实现了T的Bean找出来，并且将Bean的名称作为key，Bean实例作为value放入Map中
     * 对于后者，spring会将所有匹配的Bean放入List
     */
    @Autowired
    private Map<String,LoginStrategy> loginStrategyMap = new HashMap<>();

    /**
     * 根据用户登录类型获取具体的登录策略
     * @param loginType
     * @return
     */
    public LoginStrategy getInstance(String loginType) {
        return loginStrategyMap.get(loginType);
    }
}
