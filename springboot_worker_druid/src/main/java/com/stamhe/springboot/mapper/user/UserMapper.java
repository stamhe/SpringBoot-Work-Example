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
	@Select("select * from users_201908_007 where id=#{id}")
	@Results({
		@Result(property="id", column="id"),
		@Result(property="name", column="name"),
		@Result(property="email", column="email"),
	})
	public UserModel getOne(Long id);
	

	@Select("select * from users_201908_007")
	@Results({
		@Result(property="id", column="id"),
		@Result(property="name", column="name"),
		@Result(property="email", column="email"),
	})
	public List<UserModel> getAll();
	

	// userModel.getId()
	@Insert("insert into users_201908_007 (name, email) "
			+ "values (#{name}, #{email})")
	@SelectKey(statement="select LAST_INSERT_ID()", keyProperty="id", before=false, resultType=long.class)
	public void insert(UserModel userModel);	
}
