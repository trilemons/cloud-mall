package com.lemon.aspect;

import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.lemon.domain.SysLog;
import com.lemon.service.SysLogService;
import com.lemon.util.AuthUtils;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.sql.SQLException;
import java.util.Date;

/**
 * 记录系统操作日志AOP
 */
@Component
@Aspect
@Slf4j
public class SysLogAspect {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 切入点表达式
     */
    public static final String POINT_CUT = "execution (* com.lemon.controller.*.*(..))";

    @Around(value = POINT_CUT)
    public Object logAround(ProceedingJoinPoint joinPoint) throws SQLException {
        Object result = null;
        // 获取请求对象
        ServletRequestAttributes requestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = requestAttributes.getRequest();
        // 获取请求路径
        String path = request.getRequestURI();
        // 获取ip地址
        String remoteHost = request.getRemoteHost();
        // 获取请求参数
        Object[] args = joinPoint.getArgs();
        // 获取请求方法
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String methodName = method.toString();
        // 获取目标方法上的ApiOperation注解
        ApiOperation apiOperation = method.getDeclaredAnnotation(ApiOperation.class);
        // 判断该注解对象是否为空
        String operation = "";
        if (ObjectUtil.isNotNull(apiOperation)) {
            // 获取apiOperation注解的描述
            operation = apiOperation.value();
        }

        String finalArgs = "";
        // 判断参数类型
        if (ObjectUtil.isNotNull(args) && args.length != 0 && args[0] instanceof MultipartFile) {
            // 说明当前参数为文件对象
            finalArgs = "file";
        } else {
            // 将参数对象转换为json格式的字符串
            finalArgs = JSONObject.toJSONString(apiOperation);
        }

        // 记录开始时间
        long startTime = System.currentTimeMillis();
        try {
            // 执行方法
            result = joinPoint.proceed(args);
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
        // 记录结束时间
        long endTime = System.currentTimeMillis();

        // 方法执行时长
        long execTime = (endTime - startTime);

        SysLog sysLog = new SysLog();
        sysLog.setUserId(AuthUtils.getLoginUserId());
        sysLog.setIp(remoteHost);
        sysLog.setMethod(methodName);
        sysLog.setOperation(operation);
        sysLog.setTime(execTime);
        sysLog.setParams(finalArgs);
        sysLog.setCreateDate(new Date());

        boolean save = sysLogService.save(sysLog);
        if (!save){
            throw new SQLException("日志添加失败");
        }
        return result;
    }
}
