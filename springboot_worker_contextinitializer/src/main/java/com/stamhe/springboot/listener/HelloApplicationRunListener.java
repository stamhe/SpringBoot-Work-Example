package com.stamhe.springboot.listener;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * @author stamhe
 * @date 2020-09-21 10:19
 */
public class HelloApplicationRunListener implements SpringApplicationRunListener {
    
    public HelloApplicationRunListener(SpringApplication application, String[] args){
    
    }
    
    @Override
    public void starting() {
        System.out.println("HelloApplicationRunListener.starting...");
    }
    
    @Override
    public void environmentPrepared(ConfigurableEnvironment environment) {
        Object o = environment.getSystemProperties().get("os.name");
        System.out.println("HelloApplicationRunListener.environmentPrepared..." + o);
    }
    
    @Override
    public void contextPrepared(ConfigurableApplicationContext context) {
        System.out.println("HelloApplicationRunListener.contextPrepared...");
    }
    
    @Override
    public void contextLoaded(ConfigurableApplicationContext context) {
        System.out.println("HelloApplicationRunListener.contextLoaded...");
    }
    
    @Override
    public void started(ConfigurableApplicationContext context) {
        System.out.println("HelloApplicationRunListener.started...");
    }
    
    
}
