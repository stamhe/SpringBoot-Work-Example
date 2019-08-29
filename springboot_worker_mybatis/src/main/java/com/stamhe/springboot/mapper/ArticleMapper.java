package com.stamhe.springboot.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Result;
import org.apache.ibatis.annotations.Results;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.SelectKey;
import org.apache.ibatis.annotations.Update;

import com.stamhe.springboot.model.ArticleModel;

/*
CREATE TABLE `t_article_201908_001` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `title` varchar(255) NOT NULL DEFAULT '',
  `summary` varchar(1024) NOT NULL DEFAULT '',
  `status` int(11) NOT NULL DEFAULT '0',
  `type` int(11) NOT NULL,
  `user_id` bigint(20) NOT NULL DEFAULT '0',
  `create_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `update_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `public_time` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=1 DEFAULT CHARSET=utf8;
 */
public interface ArticleMapper 
{
	@Select("select * from t_article_201908_001 where id=#{id}")
	@Results({
		@Result(property="userId", column="user_id"),
		@Result(property="createTime", column="create_time"),
		@Result(property="updateTime", column="update_time"),
		@Result(property="publicTime", column="public_time"),
	})
	public ArticleModel getDetail(Long id);
	

	@Select("select * from t_article_201908_001")
	@Results({
		@Result(property="userId", column="user_id"),
		@Result(property="createTime", column="create_time"),
		@Result(property="updateTime", column="update_time"),
		@Result(property="publicTime", column="public_time"),
	})
	public List<ArticleModel> getAll();
	
	// articleModel.getId()
	@Insert("insert into t_article_201908_001 (title, summary, status, type, user_id, create_time, update_time, public_time) "
			+ "values (#{title}, #{summary}, #{status}, #{type}, #{userId}, #{createTime}, #{updateTime}, #{publicTime})")
	@SelectKey(statement="select LAST_INSERT_ID()", keyProperty="id", before=false, resultType=long.class)
	public void insert(ArticleModel articleModel);
	
	// 返回受影响的行数
	@Update("update t_article_201908_001 set summary=#{summary} where id=#{id}")
	public Integer update(ArticleModel articleModel);
	
	// 返回受影响的行数
	@Delete("delete from t_article_201908_001 where id=#{id}")
	public Integer delete(@Param("id")Long id);
}
