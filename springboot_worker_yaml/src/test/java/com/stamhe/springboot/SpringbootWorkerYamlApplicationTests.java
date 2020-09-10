package com.stamhe.springboot;

import com.stamhe.springboot.bean.Person;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class SpringbootWorkerYamlApplicationTests {
    @Autowired
    private Person person;
    
    @Test
    void test1() {
        System.out.println(person);
    }
    
}
