package com.email.spring.bean;

public interface BeanDefinition {
    void setBeanClassName(String beanName);
    String getBeanClassName();

    void setBeanClass(Class<?> beanClass);
    Class<?> getBeanClass();

    void setScope(ScopeName scope);
    String getScope();

    boolean isSingleton();
    boolean isPrototype();
}
