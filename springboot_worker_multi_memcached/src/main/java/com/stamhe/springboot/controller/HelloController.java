package com.stamhe.springboot.controller;

import java.util.ArrayList;
import java.util.List;

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
		long start_time = System.nanoTime();
		String value = cacheObj.get(key).toString();
		long end_time = System.nanoTime();
		System.out.println("start_time = " + start_time + " end_time = " + end_time);
		
		return value;
	}
	
	@RequestMapping("/obj")
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
	

	@RequestMapping("/objlist")
	public List<UserModel> objlistAction()
	{
		MemcachedClient cacheObj = memcachedInit.getClient();
		
		List<UserModel> list = new ArrayList<>();
		
		UserModel userModel1 = new UserModel();
		userModel1.setId(1001L);
		userModel1.setUsername("hequan-1");
		userModel1.setPassword("123456-1");
		

		UserModel userModel2 = new UserModel();
		userModel2.setId(1002L);
		userModel2.setUsername("hequan-2");
		userModel2.setPassword("123456-2");
		
		list.add(userModel1);
		list.add(userModel2);
		
		String key = "k2-objlist";
		// 过期时间单位: s
		cacheObj.set(key, 60, list);
		
		List<UserModel> list2 = (List<UserModel>)cacheObj.get(key);
		
		return list2;
	}
}
