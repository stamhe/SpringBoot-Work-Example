package com.stamhe.springboot.controller;

import com.stamhe.springboot.config.CommonConfig;
import com.stamhe.springboot.config.SiteConfig;
import com.stamhe.springboot.service.HelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/hello")
public class HelloController {
	
	@Autowired
	private SiteConfig siteConfig;
	
	@Autowired
	private CommonConfig commonConfig;
	
	@Autowired
	private HelloService helloService;

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
	
	@RequestMapping("/service/{name}")
	public String serviceAction(@PathVariable("name")String name) {
		return helloService.sayHello(name);
	}
}
