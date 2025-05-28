package com.email.spring.bean.annotation;

import java.lang.annotation.*;

@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Autowired {
    // 默认被注解依赖必须存在
    boolean required() default true;
}
