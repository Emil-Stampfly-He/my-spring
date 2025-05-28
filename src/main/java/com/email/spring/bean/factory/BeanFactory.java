package com.email.spring.bean.factory;

public interface BeanFactory {
    Object getBean(String beanName);
    <T> T getBean(Class<T> requiredType);
    boolean containsBean(String beanName);
    boolean isSingleton(String beanName);
    boolean isPrototype(String beanName);
}
