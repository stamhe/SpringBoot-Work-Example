package com.stamhe.springboot.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import com.stamhe.springboot.model.UserModel;

@Controller
@RequestMapping("/hello")
public class HelloController {

	// http://localhost:8080/hello/world.html
	@RequestMapping("/world.html")
	public String worldAction(HttpServletRequest request)
	{
		UserModel u1 = new UserModel();
		u1.setId(1L);
		u1.setName("u1");
		

		UserModel u2 = new UserModel();
		u2.setId(2L);
		u2.setName("u2");
		
		List<UserModel> list = new ArrayList<UserModel>();
		list.add(u1);
		list.add(u2);
		
		request.setAttribute("listUser", list);
		
		// 返回的 world 默认映射到 src/main/resources/templates/world.html
		return "world";
	}

	// http://localhost:8080/hello/world2.html
	@RequestMapping("/world2.html")
	public ModelAndView world2Action()
	{
		UserModel u1 = new UserModel();
		u1.setId(1L);
		u1.setName("u1");
		
		UserModel u2 = new UserModel();
		u2.setId(2L);
		u2.setName("u2");
		
		List<UserModel> list = new ArrayList<UserModel>();
		list.add(u1);
		list.add(u2);
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("HE", "QUAN");
		map.put("STAM", "HE");
		
		ModelAndView view = new ModelAndView();
        // 设置跳转的视图 默认映射到 src/main/resources/templates/world2.html
        view.setViewName("world2");
        // 设置属性
        view.addObject("listUser", list);
        view.addObject("mapUser", map);
        
        return view;
	}
}
