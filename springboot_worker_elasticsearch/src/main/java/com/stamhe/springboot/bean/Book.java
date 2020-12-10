package com.stamhe.springboot.bean;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author stamhe
 * @date 2020-10-16 19:01
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Book {
    @JsonIgnore
    private Integer id;
    private String name;
    private String author;
    private Long wordcount;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date onsale;
    private String desc;
}
