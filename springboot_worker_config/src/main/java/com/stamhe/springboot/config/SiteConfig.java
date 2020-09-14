package com.stamhe.springboot.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

/**
 * @PropertySource
 * 1. value 支持数组形式
 * 2. 只支持 .properties 配置文件， .yaml 不支持
 * 3. 可以使用 encoding 参数指定配置文件的编码格式
 */
@Configuration
@PropertySource(value = {"classpath:/site.properties"}, encoding = "utf-8")
@ConfigurationProperties(prefix="siteinfo")
public class SiteConfig {
	private String url1;
	private String url2;
	public String getUrl1() {
		return url1;
	}
	public void setUrl1(String url1) {
		this.url1 = url1;
	}
	public String getUrl2() {
		return url2;
	}
	public void setUrl2(String url2) {
		this.url2 = url2;
	}
}
