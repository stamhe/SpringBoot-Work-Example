package com.stamhe.springboot.interceptor;

import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @author stamhe
 * @date 2020-08-30 14:28
 * https://mp.weixin.qq.com/s/UVy1if4LOQSTS9sM5EM1Jw
 * 拦截器.
 * 需要实现 HandlerInterceptor 类，并且重写三个方法：
 *
 * preHandle：在 Controoler 处理请求之前被调用，返回值是 boolean类型，如果是true就进行下一步操作；若返回false，则证明不符合拦截条件，在失败的时候不会包含任何响应，此时需要调用对应的response返回对应响应。
 * postHandler：在 Controoler 处理请求执行完成后、生成视图前执行，可以通过ModelAndView对视图进行处理，当然ModelAndView也可以设置为 null。
 * afterCompletion：在 DispatcherServlet 完全处理请求后被调用，通常用于记录消耗时间，也可以对一些资源进行处理。
 */
public class LoginInterceptor implements HandlerInterceptor {
    
    /**
     *
     * @param request
     * @param response
     * @param handler
     * @return 返回 false 标识认证中断， true 标识继续执行.
     * @throws Exception
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        System.out.println("LoginInterceptor.preHandle..." + request.getRequestURI());
        return true;
    }
    
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        System.out.println("LoginInterceptor.postHandle...");
    }
    
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        System.out.println("LoginInterceptor.afterCompletion...");
    }
}
