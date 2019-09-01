package com.stamhe.springboot.controller;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stamhe.springboot.mapper.article.ArticleMapper;
import com.stamhe.springboot.model.ArticleModel;

@RestController
@RequestMapping("/article")
public class ArticleController {

	@Autowired
	private ArticleMapper articleMapper;
	
	// http://localhost:8080/article/add
	@RequestMapping("/add")
	public Long addAction()
	{
		Long id = 1L;

		ArticleModel articleModel = new ArticleModel();
		
		articleModel.setId(id);
		articleModel.setTitle("article-测试-201908-001");
		articleModel.setSummary("article-测试-201908-001");
		articleModel.setStatus(1);
		articleModel.setType(1);
		articleModel.setUserId(1L);
		articleModel.setCreateTime(new Date());
		articleModel.setUpdateTime(new Date());
		articleModel.setPublicTime(new Date());
        articleMapper.insert(articleModel);
        
		return id;
	}

	// http://localhost:8080/article/all/1
	@RequestMapping(value = "/all/{user_id}")
	public List<ArticleModel> allAction(@PathVariable("user_id")Long user_id)
	{
		List<ArticleModel> list = articleMapper.getAll(user_id);
		return list;
	}
	
	// http://localhost:8080/article/detail/1/1
	@RequestMapping(value="/detail/{user_id}/{id}")
	public ArticleModel detailAction(@PathVariable("user_id") Long user_id, @PathVariable("id")Long id)
	{
		ArticleModel articleModel = articleMapper.getOne(user_id, id);
		return articleModel;
	}
}
