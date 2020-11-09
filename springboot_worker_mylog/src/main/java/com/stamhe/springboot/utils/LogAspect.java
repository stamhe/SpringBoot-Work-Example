package com.stamhe.springboot.utils;

import com.alibaba.fastjson.JSON;
import com.stamhe.springboot.service.LogService;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author stamhe
 * @date 2020-10-11 11:41
 */
@Component
@Aspect
public class LogAspect {
    @Autowired
    private LogService logService;
    
    @Pointcut("@annotation(com.stamhe.springboot.utils.LogUtils)")
    public void logUtilsPointCut() {}
    
    // 扫描所有 controller 包下的操作
    @Pointcut("execution(* com.stamhe.springboot.controller..*.*(..))")
    public void logExceptionPointCut(){}
    
    /**
     * 返回通知
     */
    @AfterReturning(value = "logUtilsPointCut()", returning = "keys")
    public void returingMethod(JoinPoint joinPoint, Object keys) {
        logService.saveLog("---------------------------------");
        logService.saveLog("LogAspect.returingMethod...start");
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
    
        try {
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取操作
            LogUtils opLog = method.getAnnotation(LogUtils.class);
            if (opLog != null) {
                String operModule = opLog.operModule();
                String operType = opLog.operType();
                String operDesc = opLog.operDesc();
                logService.saveLog("module = " + operModule + ", type = " + operType + ", desc = " + operDesc);
            } else {
                System.out.println("LogAspect.returingMethod...no Annotation");
                return;
            }
            
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName;
        
            logService.saveLog("methodName = " + methodName);
        
            // 请求的参数
            Map<String, String> rtnMap = converMap(request.getParameterMap());
            // 将参数所在的数组转换成json
            String params = JSON.toJSONString(rtnMap);
    
            logService.saveLog("uri = " + request.getRequestURI()); // 请求URI
            logService.saveLog("params = " + params); // 请求参数
            logService.saveLog("result = " + JSON.toJSONString(keys)); // 返回结果
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 异常返回通知，用于拦截异常日志信息 连接点抛出异常后执行
     *
     * @param joinPoint 切入点
     * @param e         异常信息
     */
    @AfterThrowing(pointcut = "logExceptionPointCut()", throwing = "e")
    public void exceptionLog(JoinPoint joinPoint, Throwable e) {
        logService.saveLog("---------------------------------");
        logService.saveLog("LogAspect.exceptionLog...start");
        
        // 获取RequestAttributes
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        // 从获取RequestAttributes中获取HttpServletRequest的信息
        HttpServletRequest request = (HttpServletRequest) requestAttributes
                .resolveReference(RequestAttributes.REFERENCE_REQUEST);
        
        try {
            // 从切面织入点处通过反射机制获取织入点处的方法
            MethodSignature signature = (MethodSignature) joinPoint.getSignature();
            // 获取切入点所在的方法
            Method method = signature.getMethod();
            // 获取请求的类名
            String className = joinPoint.getTarget().getClass().getName();
            // 获取请求的方法名
            String methodName = method.getName();
            methodName = className + "." + methodName;
            logService.saveLog("methodName = " + methodName);
            
            // 请求的参数
            Map<String, String> rtnMap = converMap(request.getParameterMap());
            // 将参数所在的数组转换成json
            String params = JSON.toJSONString(rtnMap);
            logService.saveLog("uri = " + request.getRequestURI()); // 请求URI
            logService.saveLog("params = " + params); // 请求参数
            logService.saveLog("exception name: " + e.getClass().getName()); // 异常名称
            logService.saveLog("stack trace: " + stackTraceToString(e.getClass().getName(), e.getMessage(), e.getStackTrace())); // 异常信息
        } catch (Exception e2) {
            e2.printStackTrace();
        }
        
    }
    
    
    /**
     * 转换request 请求参数
     *
     * @param paramMap request获取的参数数组
     */
    public Map<String, String> converMap(Map<String, String[]> paramMap) {
        Map<String, String> rtnMap = new HashMap<String, String>();
        for (String key : paramMap.keySet()) {
            rtnMap.put(key, paramMap.get(key)[0]);
        }
        return rtnMap;
    }
    
    /**
     * 转换异常信息为字符串
     *
     * @param exceptionName    异常名称
     * @param exceptionMessage 异常信息
     * @param elements         堆栈信息
     */
    public String stackTraceToString(String exceptionName, String exceptionMessage, StackTraceElement[] elements) {
        StringBuffer strbuff = new StringBuffer();
        for (StackTraceElement stet : elements) {
            strbuff.append(stet + "\n");
        }
        String message = exceptionName + ":" + exceptionMessage + "\n\t" + strbuff.toString();
        return message;
    }
}
