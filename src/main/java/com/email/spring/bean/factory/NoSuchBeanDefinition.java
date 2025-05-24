package com.email.spring.bean.factory;

public class NoSuchBeanDefinition extends RuntimeException {
    public NoSuchBeanDefinition(String message) {
        super(message);
    }
}
