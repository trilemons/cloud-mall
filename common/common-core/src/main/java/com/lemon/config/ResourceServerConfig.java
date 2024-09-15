package com.lemon.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.lemon.constant.BusinessEnum;
import com.lemon.constant.HttpConstants;
import com.lemon.constant.ResourceConstants;
import com.lemon.filter.TokenTranslationFilter;
import com.lemon.model.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.PrintWriter;

/**
 * Spring Security安全框架的资源服务器配置类
 */
@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ResourceServerConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private TokenTranslationFilter tokenTranslationFilter;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 关闭跨站请求伪造
        http.csrf().disable();
        // 关闭跨域请求
        http.cors().disable();
        // 关闭session策略
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);

        // 编写一个token解析过滤器，将token转换为security安全框架能够认证的用户信息，再存放到当前资源服务器的容器中
        http.addFilterBefore(tokenTranslationFilter, UsernamePasswordAuthenticationFilter.class);

        // 配置处理携带token但权限不足的请求
        http.exceptionHandling()
                .authenticationEntryPoint(authenticationEntryPoint()) // 处理没有携带token的请求
                .accessDeniedHandler(accessDeniedHandler()); // 处理携带token，但是权限不足的请求

        // 配置其它请求
        http.authorizeHttpRequests()
                .antMatchers(ResourceConstants.RESOURCE_ALLOW_URLS)//这里的都需要放行
                .permitAll()
                .anyRequest().authenticated();  // 除了需要放行的请求，都得需要进行身份的认证


    }

    /**
     * 处理：请求没有携带token
     * @return
     */
    @Bean
    public AuthenticationEntryPoint authenticationEntryPoint() {
        return (request, response, authException) -> {
            // 设置响应头信息
            response.setContentType(HttpConstants.APPLICATION_JSON);
            response.setCharacterEncoding(HttpConstants.UTF_8);

            // 创建项目统一响应结果对象
            Result<Object> result = Result.fail(BusinessEnum.UN_AUTHORIZATION);
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(result);
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.flush();
            writer.close();
        };
    }

    /**
     * 处理请求中携带token但是权限不足的情况
     * @return
     */
    @Bean
    public AccessDeniedHandler accessDeniedHandler(){
        return (request, response, accessDeniedException) -> {
            // 设置响应头信息
            response.setContentType(HttpConstants.APPLICATION_JSON);
            response.setCharacterEncoding(HttpConstants.UTF_8);

            // 创建项目统一响应结果对象
            Result<Object> result = Result.fail(BusinessEnum.ACCESS_DENY_FAIL);
            ObjectMapper objectMapper = new ObjectMapper();
            String s = objectMapper.writeValueAsString(result);
            PrintWriter writer = response.getWriter();
            writer.write(s);
            writer.flush();
            writer.close();
        };
    }
}
