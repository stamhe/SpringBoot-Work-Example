package com.stamhe.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stamhe.springboot.init.MemcachedInit;
import net.spy.memcached.MemcachedClient;

@RestController
@RequestMapping("/hello")
public class HelloController {
	@Autowired
	private MemcachedInit memcachedInit;

	@RequestMapping("/cache")
	public String cacheAction()
	{
		MemcachedClient cacheObj = memcachedInit.getClient();
		String key = "k1";
		// 过期时间单位: ms
		cacheObj.set(key, 60000, "v1");
		String value = cacheObj.get(key).toString();
		
		return value;
	}
}
