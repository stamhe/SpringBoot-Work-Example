package com.stamhe.springboot.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebFilter {
	
	@Bean
	public FilterRegistrationBean<MyFilter> myfilterRegisterBean()
	{
		FilterRegistrationBean<MyFilter> filter = new FilterRegistrationBean<>();
		filter.setFilter(new MyFilter());
		filter.addUrlPatterns("/*");
		filter.addInitParameter("paramName", "paramValue");
		filter.setName("MyFilter");
		filter.setOrder(1);
		
		return filter;
	}

	public class MyFilter implements Filter
	{
		@Override
		public void doFilter(ServletRequest req, ServletResponse rsp, FilterChain chain)
				throws IOException, ServletException {
			long start = System.currentTimeMillis();
			
			HttpServletRequest hsr = (HttpServletRequest)req;
			
			chain.doFilter(req, rsp);
			
			System.out.println("This is my filter. url = " + hsr.getRequestURI() + " cost = " + (System.currentTimeMillis() - start));
		}
	}
}
