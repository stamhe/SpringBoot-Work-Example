package com.stamhe.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.stamhe.springboot.init.MemcachedInit;
import com.stamhe.springboot.model.UserModel;

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
		// 过期时间单位: s
		cacheObj.set(key, 60, "v1");
		String value = cacheObj.get(key).toString();
		
		return value;
	}
	
	@RequestMapping("obj")
	public UserModel objAction()
	{
		MemcachedClient cacheObj = memcachedInit.getClient();
		
		UserModel userModel = new UserModel();
		userModel.setId(1000L);
		userModel.setUsername("hequan");
		userModel.setPassword("123456");
		
		String key = "k2-obj";
		// 过期时间单位: s
		cacheObj.set(key, 60, userModel);
		UserModel objModel = (UserModel)cacheObj.get(key);
		
		return objModel;
	}
}
