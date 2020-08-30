package com.stamhe.springboot.springboot_worker_closespringlogo;

import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class SpringbootWorkerClosespringlogoApplication {
    
    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(SpringbootWorkerClosespringlogoApplication.class);
        springApplication.setBannerMode(Banner.Mode.OFF);
        springApplication.run(args);
    }
    
}
