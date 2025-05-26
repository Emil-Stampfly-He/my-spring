package com.email.spring.test;

import com.email.spring.bean.ScopeName;
import com.email.spring.bean.annotation.Bean;
import com.email.spring.bean.annotation.ComponentScan;
import com.email.spring.bean.annotation.Configuration;
import com.email.spring.bean.annotation.Scope;

@ComponentScan(basePackages = {"com.email.spring"})
@Configuration(name = "goofyConfiguration")
public class MyConfig {
    @Bean
    public Bean1 bean1() {
        return new Bean1();
    }

    @Bean(value = "goofyBean2")
    public Bean2 bean2() {
        return new Bean2();
    }

    @Bean(value = "prototypeBean3")
    @Scope(value = ScopeName.PROTOTYPE)
    public Bean3 bean3() {
        return new Bean3();
    }
    
    public Bean4 bean4() {
        return new Bean4();
    }
}
