package com.stamhe.springboot.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author stamhe
 * @date 2020-08-30 14:37
 */
@RestController
public class UserController {
    @RequestMapping("/user/login")
    public String loginAction() {
        return "/user/login success";
    }
    
    @RequestMapping("/user/info")
    public String infoAction() {
        return "/user/info success";
    }
}
