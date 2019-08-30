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
		Long id = 0L;

		UserModel userModel = new UserModel();
		userModel.setName("user-测试-201908-006");
		userModel.setEmail("stamhe-201908-006@gmail.com");
		
		userMapper.insert(userModel);
		
		id = userModel.getId();
		
		return id;
	}
	
	// http://localhost:8080/user/detail/1
	@RequestMapping(value="/detail/{id}")
	public UserModel detailAction(@PathVariable("id")Long id)
	{
		UserModel userModel = userMapper.getOne(id);
		return userModel;
	}
}
