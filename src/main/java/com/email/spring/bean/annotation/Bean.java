package com.email.spring.bean.annotation;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Bean {
    // 用于指定Bean的名字
    // @Bean("customBeanName")
    String value() default "";

    // 用于指定Bean初始化方法的名字
    String initMethod() default "";

    String destroyMethod() default "";

    // TODO: 未来可以和 @Primary 一起作用
}
