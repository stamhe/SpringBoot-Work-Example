package com.stamhe.springboot.bean;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @author stamhe
 * @date 2020-08-31 11:31
 */
@Data
@ConfigurationProperties(prefix = "person")
@Component
public class Person {
    private String lastName;
    private Integer age;
    private Boolean boss;
    private Date birth;
    
    private Map<String, Object> maps;
    private List<Object> lists;
    
    private Dog dog;
}
