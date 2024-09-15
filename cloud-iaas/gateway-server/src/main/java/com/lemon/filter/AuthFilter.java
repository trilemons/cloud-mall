package com.lemon.filter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.config.WhiteUrlsConfig;
import com.lemon.constant.AuthConstants;
import com.lemon.constant.BusinessEnum;
import com.lemon.constant.HttpConstants;
import com.lemon.model.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Date;

/**
 *
 */
@Component
@Slf4j
public class AuthFilter implements GlobalFilter, Ordered {

    @Autowired
    private WhiteUrlsConfig whiteUrlsConfig;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 校验token
     *
     * 1.获取请求路径
     * 2.判断请求路径是否可以放行
     *   放行：则不需要验证身份
     *   不放行：需要验证身份
     * @return
     */

    @Override
    public int getOrder() {
        return 0;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {

        //获取请求对象
        ServerHttpRequest request = exchange.getRequest();
        //获取请求路径
        String path = request.getPath().toString();
        //判断当前路径是否需要放行
        if (whiteUrlsConfig.getAllowUrls().contains(path)) {
            //包含在白名单中
            return chain.filter(exchange);
        }
        //不放行则需要校验
        String authorizationValue = request.getHeaders().getFirst(AuthConstants.AUTHORIZATION);
        if (StringUtils.hasText(authorizationValue)) {
            //获取没有前缀的token
            String tokenValue = authorizationValue.replaceFirst(AuthConstants.BEARER, "").replaceFirst("^\\s+", "");
            //判断token是否有值且是否在redis中存在
            if (StringUtils.hasText(tokenValue) && stringRedisTemplate.hasKey(AuthConstants.LOGIN_TOKEN_PREFIX+tokenValue)) {
                //身份验证通过
                return chain.filter(exchange);
            }
        }
        //请求不合法
        log.error("拦截非法请求,时间：{},请求API路径：{}", new Date(), path);
        //获取响应对象
        ServerHttpResponse response = exchange.getResponse();
        //设置响应头消息
        response.getHeaders().set(HttpConstants.CONTENT_TYPE, HttpConstants.APPLICATION_JSON);
        //设置响应消息
        Result<Object> result = Result.fail(BusinessEnum.UN_AUTHORIZATION);
        //创建一个objectMapper对象,将result转成一个byte数组
        ObjectMapper objectMapper = new ObjectMapper();
        byte[] byteResult = new byte[0];
        try {
            byteResult = objectMapper.writeValueAsBytes(result);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
        DataBuffer dataBuffer = response.bufferFactory().wrap(byteResult);


        return response.writeWith(Mono.just(dataBuffer));
    }
}
