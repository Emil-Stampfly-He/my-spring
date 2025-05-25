package com.email.spring.bean.annotation;

import com.email.spring.bean.ScopeName;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Scope {
    // 用于指定作用域
    // 默认是单例
    ScopeName value() default ScopeName.SINGLETON;
}