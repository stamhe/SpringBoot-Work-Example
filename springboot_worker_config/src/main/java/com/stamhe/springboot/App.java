package com.stamhe.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ImportResource;

/**
 * @ImportResource
 * 1. 指定需要额外加载的 spring 配置文件
 * 2. 可以指定多个 spring 配置文件
 */
@SpringBootApplication
@ImportResource(value = {"classpath:spring-other.xml"})
public class App 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(App.class, args);
    }
}
