package com.stamhe.springboot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

@SpringBootApplication
@EnableAspectJAutoProxy
public class SpringbootWorkerMylogApplication {
    
    public static void main(String[] args) {
        SpringApplication.run(SpringbootWorkerMylogApplication.class, args);
    }
}
