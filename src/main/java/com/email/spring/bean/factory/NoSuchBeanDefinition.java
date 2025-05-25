package com.email.spring.bean.factory;

import com.email.spring.bean.factory.config.BeansException;

public class NoSuchBeanDefinition extends BeansException {
    public NoSuchBeanDefinition(String message) {
        super(message);
    }
}
