package com.stamhe.springboot.controller;

import com.stamhe.springboot.utils.LogUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author stamhe
 * @date 2020-10-11 11:37
 */
@RestController
@RequestMapping("/hello")
public class HelloController {
    @RequestMapping("/world")
    @LogUtils(operModule = "用户", operType = "controller", operDesc = "用户相关功能")
    public String worldAction() {
        return "success";
    }
    
    @RequestMapping("/normal")
    public String normalAction() {
        return "normal";
    }
    
    @RequestMapping("/exception")
    public String exceptionAction() {
        int i = 1 / 0;
        return "exception";
    }
}
