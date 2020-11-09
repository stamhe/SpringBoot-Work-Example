package com.stamhe.springboot.service;

import org.springframework.stereotype.Component;

/**
 * @author stamhe
 * @date 2020-10-11 11:42
 */
@Component
public class LogService {
    public void saveLog(String msg) {
        System.out.println("LogService.saveLog..." + msg);
    }
}
