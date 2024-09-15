package com.lemon.config;

import cn.hutool.core.util.ObjectUtil;
import com.lemon.constant.AuthConstants;
import feign.RequestInterceptor;
import feign.RequestTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;

/**
 * feign拦截器
 *  作用：解决服务之间调用没有token的情况
 *
 *  浏览器 -> A服务 -> B服务
 *  定时器 -> A服务
 */
@Component
public class FeignInterceptor implements RequestInterceptor {

    @Override
    public void apply(RequestTemplate requestTemplate) {
        // 获取当前请求的上下文对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        // 判断是否有值
        if (ObjectUtil.isNotNull(requestAttributes)) {
            // 获取请求对象
            HttpServletRequest request = requestAttributes.getRequest();
            // 判断是否有值
            if (ObjectUtil.isNotNull(request)) {
                // 获取当前请求头中的token值，传递到一下一个请求对象的请求头中
                String authorization = request.getHeader(AuthConstants.AUTHORIZATION);
                requestTemplate.header(AuthConstants.AUTHORIZATION,authorization);
                return;
            }
        }
        requestTemplate.header(AuthConstants.AUTHORIZATION,AuthConstants.BEARER+"cf280aeb-e8ac-44aa-8598-eaeeb0cc33d9");

    }
}
