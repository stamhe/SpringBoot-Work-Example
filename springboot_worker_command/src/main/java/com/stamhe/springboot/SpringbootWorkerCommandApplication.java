package com.stamhe.springboot;

import com.stamhe.springboot.service.UserService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SpringbootWorkerCommandApplication {
    
    public static void main(String[] args) {
        ConfigurableApplicationContext ctx = SpringApplication.run(SpringbootWorkerCommandApplication.class, args);
        UserService userService = ctx.getBean(UserService.class);
        String result = userService.sayHi("全仔");
        System.out.println("result = " + result);
    }
}
