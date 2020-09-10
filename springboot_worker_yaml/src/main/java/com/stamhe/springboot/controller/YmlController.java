package com.stamhe.springboot.controller;

import com.stamhe.springboot.bean.Person;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author stamhe
 * @date 2020-08-31 11:37
 */
@RestController
@RequestMapping("/yml")
public class YmlController {
    @Autowired
    private Person person;
    @RequestMapping("/test")
    public Person testAction() {
        System.out.println(person);
        return person;
    }
}
