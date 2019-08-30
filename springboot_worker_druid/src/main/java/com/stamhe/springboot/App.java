package com.stamhe.springboot;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

/*
首先要将SpringBoot自带的DataSourceAutoConfiguration禁掉，因为它会读取application.properties文件的spring.datasource.*属性并自动配置单数据源。
在@SpringBootApplication注解中添加exclude属性即可
*/
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
@ComponentScan({"com.stamhe.springboot"})
@MapperScan("com.stamhe.springboot.mapper")
public class App 
{
    public static void main( String[] args )
    {
    	SpringApplication.run(App.class, args);
    }
}
