package com.stamhe.springboot.mapper;

import com.stamhe.springboot.model.ArticleModel;

/**
 * @author stamhe
 * @date 2020-09-20 11:07
 * xml 配置版本
 */
public interface Article2Mapper {
    public ArticleModel getDetail(Long id);
}
