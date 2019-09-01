package com.stamhe.springboot.mapper.article;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;
import org.springframework.context.annotation.Configuration;

import com.stamhe.springboot.model.ArticleModel;

@Configuration
public interface ArticleMapper 
{
	@Select("select * from t_article_x where user_id=#{user_id} and id=#{id}")
	@Results({
		@Result(property="userId", column="user_id"),
		@Result(property="createTime", column="create_time"),
		@Result(property="updateTime", column="update_time"),
		@Result(property="publicTime", column="public_time"),
	})
	public ArticleModel getOne(@Param("user_id")Long user_id, @Param("id")Long id);
	

	@Select("select * from t_article_x where user_id = #{user_id} or user_id != #{user_id}")
	@Results({
		@Result(property="userId", column="user_id"),
		@Result(property="createTime", column="create_time"),
		@Result(property="updateTime", column="update_time"),
		@Result(property="publicTime", column="public_time"),
	})
	public List<ArticleModel> getAll(Long user_id);
	
	// articleModel.getId()
	@Insert("insert into t_article_x (id, title, summary, status, type, user_id, create_time, update_time, public_time) "
			+ "values (#{id}, #{title}, #{summary}, #{status}, #{type}, #{userId}, #{createTime}, #{updateTime}, #{publicTime})")
	@SelectKey(statement="select LAST_INSERT_ID()", keyProperty="id", before=false, resultType=long.class)
	public void insert(ArticleModel articleModel);
	
	// 返回受影响的行数
	@Update("update t_article_x set summary=#{summary} where user_id = #{user_id} and id=#{id}")
	public Integer update(@Param("user_id")Long user_id, @Param("articleModel")ArticleModel articleModel);
	
	// 返回受影响的行数
	@Delete("delete from t_article_x where user_id = #{user_id} and id=#{id}")
	public Integer delete(@Param("user_id")Long user_id, @Param("id")Long id);
}
