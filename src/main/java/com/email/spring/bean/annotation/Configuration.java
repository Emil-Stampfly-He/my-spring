package com.email.spring.bean.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Component
public @interface Configuration {
    // 用于指定@Configuration类下的Bean统一命名
    String name() default "";
}
