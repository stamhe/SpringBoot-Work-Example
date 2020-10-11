package com.stamhe.springboot.filter;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

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
		
		@Override
		public void init(FilterConfig filterConfig) throws ServletException {
			System.out.println("MyFilter.init...");
		}
		
		@Override
		public void destroy() {
			System.out.println("MyFilter.destroy...");
		}
	}
}
