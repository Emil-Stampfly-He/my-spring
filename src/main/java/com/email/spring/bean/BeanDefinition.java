package com.email.spring.bean;

public interface BeanDefinition {
    void setBeanClassName(String beanName);
    String getBeanClassName();

    void setBeanClass(Class<?> beanClass);
    Class<?> getBeanClass();

    void setScope(ScopeName scope);
    String getScope();

    void setInitMethodName(String initMethodName);
    String getInitMethodName();

    void setDestroyMethodName(String destroyMethodName);
    String getDestroyMethodName();

    boolean isSingleton();
    boolean isPrototype();
}
