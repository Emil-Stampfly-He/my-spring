package com.email.spring.bean.factory;

import com.email.spring.bean.factory.config.BeansException;

public class NoSuchBeanDefinitionException extends BeansException {
    public NoSuchBeanDefinitionException(String message) {
        super(message);
    }
}
