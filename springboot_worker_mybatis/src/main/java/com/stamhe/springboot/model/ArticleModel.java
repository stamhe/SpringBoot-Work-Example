package com.stamhe.springboot.model;

import lombok.Data;

import java.util.Date;

@Data
public class ArticleModel {
	private Long id;
    private String title;
    private String summary;
    private Integer status;
    private Integer type;
    private Long userId;
    
    private Date createTime;
    private Date publicTime;
    private Date updateTime;
}
