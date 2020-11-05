package com.stamhe.springboot.initializer;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * @author stamhe
 * @date 2020-09-21 10:17
 */
public class HelloApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {
    @Override
    public void initialize(ConfigurableApplicationContext ctx) {
        System.out.println("HelloApplicationContextInitializer.initialize..." + ctx);
    }
}
