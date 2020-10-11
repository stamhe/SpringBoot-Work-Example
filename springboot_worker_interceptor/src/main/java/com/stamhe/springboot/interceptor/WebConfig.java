package com.stamhe.springboot.interceptor;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author stamhe
 * @date 2020-08-30 14:31
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        String[] addPathPatterns = {
                "/user/**", // /user 下的全部拦截
                "/order/**",
        };
        
        // 不拦截 uri 优先.
        String[] excludePathPatterns = {
                "/user/login",
                "/index/index"
        };
        
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns(addPathPatterns)  // 需要拦截的路径
                .excludePathPatterns(excludePathPatterns); // 不需要拦截的路径
    }
}
