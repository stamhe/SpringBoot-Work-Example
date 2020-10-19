package com.stamhe.springboot.bean;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author stamhe
 * @date 2020-10-16 19:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Article {
    private String name;
    private String author;
    private Long wordcount;
    private String onsale;
    private String desc;
}
