package com.email.spring.bean.factory;

public interface BeanFactory {
    Object getBean(String beanName);
    boolean containsBean(String beanName);
    boolean isSingleton(String beanName);
    boolean isPrototype(String beanName);
}
