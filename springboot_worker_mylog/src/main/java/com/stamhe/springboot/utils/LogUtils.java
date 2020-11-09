package com.stamhe.springboot.utils;

import java.lang.annotation.*;

/**
 * @author stamhe
 * @date 2020-10-11 11:39
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface LogUtils {
    String operModule() default "";
    String operType() default "";
    String operDesc() default "";
}
