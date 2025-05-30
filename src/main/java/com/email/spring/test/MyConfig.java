package com.email.spring.test;

import com.email.spring.bean.ScopeName;
import com.email.spring.bean.annotation.*;

@ComponentScan(basePackages = {"com.email.spring"})
@Configuration
public class MyConfig {
    @Bean(initMethod = "init")
    public Bean1 bean1() {
        return new Bean1();
    }

    @Bean(value = "goofyBean2")
    public Bean2 bean2() {
        return new Bean2();
    }

    @Bean(value = "prototypeBean3", initMethod = "init")
    @Scope(value = ScopeName.PROTOTYPE)
    public Bean3 bean3() {
        return new Bean3();
    }

    public Bean4 bean4() {
        return new Bean4();
    }
}
