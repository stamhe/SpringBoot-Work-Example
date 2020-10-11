package com.stamhe.springboot.model.controller;

import com.alibaba.fastjson.JSON;
import com.stamhe.springboot.model.UserModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/redis")
public class RedisController {
	@Autowired
	private RedisTemplate<String, Object> redis1Template;
	
	@Autowired
	private RedisTemplate<String, Object> redis2Template;

	/**
	 * http://127.0.0.1:8080/redis/ops
	 * redis的各种操作演示
	 * @return
	 */
	@RequestMapping("/ops")
	@ResponseBody
	public Map<String, Object> redisAction()
	{
		Map<String, Object> hashMap = new HashMap<String, Object>();
		hashMap.put("hello", "world");
		hashMap.put("k1", "v1");
		
		// kv
		String redisKey = "redis-k1";
		redis1Template.opsForValue().set(redisKey, "redis-v1");
		hashMap.put("redis-k1", redis1Template.opsForValue().get(redisKey));

		// hash
		String redisHashKey = "redis-hash-k1";
		redis1Template.opsForHash().put(redisHashKey, "rk1", "rv1");
		hashMap.put("redis-hash-k1", redis1Template.opsForHash().get(redisHashKey, "rk1"));
		
		// list
		String redisListKey = "redis-list-k1";
		redis1Template.opsForList().rightPush(redisListKey, "redis-list-v1");
		redis1Template.opsForList().rightPush(redisListKey, "redis-list-v2");
		redis1Template.opsForList().rightPush(redisListKey, "redis-list-v3");
		hashMap.put("redis-list-k1", redis1Template.opsForList().leftPop(redisListKey));
		Long redisListLen = redis1Template.opsForList().size(redisListKey);
		hashMap.put("redis-list-len", redisListLen.toString());
		
		// incr
		String redisIncrKey = "redis-incr-k1";
		redis1Template.opsForValue().set(redisIncrKey, 100L);
		Long redisIncrV1 = redis1Template.opsForValue().increment(redisIncrKey, 1L);
		hashMap.put("redis-incr-k1", redisIncrV1.toString());
		
		return hashMap;
	}
	
	/**
	 * http://127.0.0.1:8080/redis/object
	 * 演示了直接存储 POJO 对象的示例
	 * @return
	 */
	@RequestMapping("/object")
	@ResponseBody
	public Map<String, Object> redis2Action()
	{
		Map<String, Object> hashMap = new HashMap<String, Object>();
		
		String key = "user-1";
		redis2Template.opsForValue().set(key, new UserModel(1L, "u1", "pa"));
        final UserModel user = (UserModel) redis2Template.opsForValue().get(key);
        
        System.out.println("user = " + user);
        
        hashMap.put("1", user);
        
        return hashMap;
	}
	
	/**
	 * http://127.0.0.1:8080/redis/getnull
	 * 演示了直接存储 POJO 对象的示例
	 * @return
	 */
	@RequestMapping("/getnull")
	@ResponseBody
	public String getnullAction()
	{
		String data = (String) redis2Template.opsForValue().get("k10000");
		System.out.println(data);
		Object parse = JSON.parse(data);
		System.out.println(parse);
		return data;
	}
	
	@RequestMapping("/listjson")
	@ResponseBody
	public String listjsonAction()
	{
		List<String> data = Arrays.asList("aa", "bb", "cc", "dd");
		String strData = JSON.toJSONString(data);
		System.out.println(strData);
		List<String> newData = JSON.parseArray(strData, String.class);
		for (String t : newData) {
			System.out.println(t);
		}
		
		return strData;
	}
}
