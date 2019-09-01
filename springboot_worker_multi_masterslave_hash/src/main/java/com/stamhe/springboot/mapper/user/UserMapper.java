package com.stamhe.springboot.mapper.user;

import java.util.List;

import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.springframework.context.annotation.Configuration;

import com.stamhe.springboot.model.UserModel;


@Configuration
public interface UserMapper 
{
	@Select("select * from t_user_x where user_id=#{user_id}")
	@Results({
		@Result(property="user_id", column="user_id"),
		@Result(property="name", column="name"),
		@Result(property="email", column="email"),
	})
	public UserModel getOne(Long user_id);
	

	@Select("select * from t_user_x")
	@Results({
		@Result(property="user_id", column="user_id"),
		@Result(property="name", column="name"),
		@Result(property="email", column="email"),
	})
	public List<UserModel> getAll();
	

	@Insert("insert into t_user_x (user_id, name, email) "
			+ "values (#{user_id}, #{name}, #{email})")
	@SelectKey(statement="select LAST_INSERT_ID()", keyProperty="user_id", before=false, resultType=long.class)
	public void insert(UserModel userModel);	
}
