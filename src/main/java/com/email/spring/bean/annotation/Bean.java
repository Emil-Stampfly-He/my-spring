package com.email.spring.bean.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
    // 用于指定Bean的名字
    // @Bean("customBeanName")
    String value() default "";

    // TODO: 未来可以添加 initMethod 和 destroyMethod
    // TODO: 包括和 @Primary 一起作用
}
