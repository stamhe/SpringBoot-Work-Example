package com.stamhe.springboot.controller;

import java.util.Date;

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
		Long id = 0L;

		ArticleModel articleModel = new ArticleModel();

		articleModel.setTitle("article-测试-201908-002");
		articleModel.setSummary("article-测试-201908-002");
		articleModel.setStatus(1);
		articleModel.setType(1);
		articleModel.setUserId(1L);
		articleModel.setCreateTime(new Date());
		articleModel.setUpdateTime(new Date());
		articleModel.setPublicTime(new Date());
        articleMapper.insert(articleModel);
        id = articleModel.getId();
        
		return id;
	}
	
	// http://localhost:8080/article/detail/1
	@RequestMapping(value="/detail/{id}")
	public ArticleModel detailAction(@PathVariable("id")Long id)
	{
		ArticleModel articleModel = articleMapper.getOne(id);
		return articleModel;
	}
}
