package com.stamhe.springboot.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stamhe.springboot.config.CommonConfig;
import com.stamhe.springboot.config.SiteConfig;

@RestController
@RequestMapping("/hello")
public class HelloController {
	
	@Autowired
	private SiteConfig siteConfig;
	
	@Autowired
	private CommonConfig commonConfig;

	@RequestMapping("/world")
	public String worldAction()
	{
		return commonConfig.getUrl();
	}
	
	@RequestMapping("/list")
	public Map<String, String> listAction()
	{
		HashMap<String, String> map = new HashMap<>();
		map.put("url1", siteConfig.getUrl1());
		map.put("url2", siteConfig.getUrl2());
		
		return map;
	}
}
