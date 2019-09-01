package com.stamhe.springboot.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stamhe.springboot.mapper.user.UserMapper;
import com.stamhe.springboot.model.UserModel;

@RestController
@RequestMapping("/user")
public class UserController {

	@Autowired
	private UserMapper userMapper;
	
	// http://localhost:8080/user/add
	@RequestMapping("/add")
	public Long addAction()
	{
		Long user_id = 1L;

		UserModel userModel = new UserModel();
		userModel.setUser_id(user_id);
		userModel.setName("user-测试-201908-001");
		userModel.setEmail("stamhe-201908-001@gmail.com");
		
		userMapper.insert(userModel);
		
		return user_id;
	}
	
	// http://localhost:8080/user/detail/1
	@RequestMapping(value="/detail/{user_id}")
	public UserModel detailAction(@PathVariable("user_id")Long user_id)
	{
		UserModel userModel = userMapper.getOne(user_id);
		return userModel;
	}
}
