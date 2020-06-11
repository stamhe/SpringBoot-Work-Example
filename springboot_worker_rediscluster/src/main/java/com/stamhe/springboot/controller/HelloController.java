package com.stamhe.springboot.controller;

import com.alibaba.fastjson.JSON;
import com.stamhe.springboot.bean.UserBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;


@Controller
@RequestMapping("/hello")
public class HelloController {
    @Autowired
    private RedisTemplate<String, Object> redisTemplate;
    
    @RequestMapping("/hget")
    @ResponseBody
    public String hgetAction() {
        String hashKey = "userInfo";
    
        String userId = "166205";
        UserBean UserBean = (UserBean) redisTemplate.opsForHash().get(hashKey, userId);
        System.out.println(JSON.toJSONString(UserBean));
        
        return "success";
    }
    
    @RequestMapping("/hgetall")
    @ResponseBody
    public String hgetallAction() {
        String hashKey = "userInfo";
        Map<Object, Object> bookMap = redisTemplate.opsForHash().entries(hashKey);
        System.out.println("size " + bookMap.size());
        bookMap.entrySet().forEach(entry -> {
            String k = (String) entry.getKey();
            String v = (String) entry.getValue();
            UserBean UserBean = JSON.parseObject(v, UserBean.class);
            System.out.println(String.format("k = %s,v = %s", k, v));
        });
        
        return "success";
    }
}
