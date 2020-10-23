package com.stamhe.springboot;

import com.stamhe.springboot.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootWorkerCommandlinerunnerApplication implements CommandLineRunner {
    @Autowired
    private UserService userService;

    // 启动 springboot
    public static void main(String[] args) {
        SpringApplication.run(SpringbootWorkerCommandlinerunnerApplication.class, args);
    }
    
    // 相当于纯 java 的 main 方法入口
    @Override
    public void run(String... args) throws Exception {
        String ret = userService.sayHi("stamhe");
        System.out.println("CommandLineRunner run result = " + ret);
        while (true) {
            System.out.println("sleep 3s...");
            Thread.sleep(3000L);
        }
    }
}
