package com.stamhe.springboot.controller;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.stamhe.springboot.mapper.ArticleMapper;
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
		ArticleModel articleModel = new ArticleModel();
		articleModel.setTitle("测试标题-201908-006-003");
		articleModel.setSummary("测试摘要-201908-006-003");
		articleModel.setStatus(1);
		articleModel.setType(1);
		articleModel.setUserId(201908006002L);
		articleModel.setCreateTime(new Date());
		articleModel.setUpdateTime(new Date());
		articleModel.setPublicTime(new Date());
        articleMapper.insert(articleModel);
        Long id = articleModel.getId();
        return id;
	}
	
	// http://localhost:8080/article/detail/1
	@RequestMapping(value="/detail/{id}")
	public ArticleModel detailAction(@PathVariable("id")Long id)
	{
		ArticleModel articleModel = articleMapper.getDetail(id);
		return articleModel;
	}
}
