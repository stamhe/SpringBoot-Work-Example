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
	@Select("select * from t_article_201908_007 where id=#{id}")
	@Results({
		@Result(property="userId", column="user_id"),
		@Result(property="createTime", column="create_time"),
		@Result(property="updateTime", column="update_time"),
		@Result(property="publicTime", column="public_time"),
	})
	public ArticleModel getOne(Long id);
	

	@Select("select * from t_article_201908_007")
	@Results({
		@Result(property="userId", column="user_id"),
		@Result(property="createTime", column="create_time"),
		@Result(property="updateTime", column="update_time"),
		@Result(property="publicTime", column="public_time"),
	})
	public List<ArticleModel> getAll();
	
	// articleModel.getId()
	@Insert("insert into t_article_201908_007 (title, summary, status, type, user_id, create_time, update_time, public_time) "
			+ "values (#{title}, #{summary}, #{status}, #{type}, #{userId}, #{createTime}, #{updateTime}, #{publicTime})")
	@SelectKey(statement="select LAST_INSERT_ID()", keyProperty="id", before=false, resultType=long.class)
	public void insert(ArticleModel articleModel);
	
	// 返回受影响的行数
	@Update("update t_article_201908_007 set summary=#{summary} where id=#{id}")
	public Integer update(ArticleModel articleModel);
	
	// 返回受影响的行数
	@Delete("delete from t_article_201908_007 where id=#{id}")
	public Integer delete(@Param("id")Long id);
}
