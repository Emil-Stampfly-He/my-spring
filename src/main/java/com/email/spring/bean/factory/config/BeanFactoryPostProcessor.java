package com.email.spring.bean.factory.config;

import com.email.spring.bean.factory.BeanDefinitionRegistry;

public interface BeanFactoryPostProcessor {
    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;
}
