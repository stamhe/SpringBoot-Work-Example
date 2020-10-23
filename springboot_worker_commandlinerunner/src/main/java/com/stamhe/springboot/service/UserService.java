package com.stamhe.springboot.service;

import org.springframework.stereotype.Service;

/**
 * @author stamhe
 * @date 2020-08-30 15:06
 */
@Service
public class UserService {
    public String sayHi(String name) {
        return "Hello " + name;
    }
}
