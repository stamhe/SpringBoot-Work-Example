package com.stamhe.springboot.service.impl;

import com.stamhe.springboot.service.UserService;
import org.springframework.stereotype.Service;

/**
 * @author stamhe
 * @date 2020-09-18 15:11
 */
@Service
public class UserServiceImpl implements UserService {
    @Override
    public String sayHi(String name) {
        return "Hi, " + name;
    }
}
