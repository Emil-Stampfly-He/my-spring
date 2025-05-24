package com.email.spring.bean;

public class SimpleBeanDefinition implements BeanDefinition {

    private Class<?> beanClass;
    private String beanName;
    private String scope;

    @Override
    public void setBeanClassName(String beanName) {
        this.beanName = beanName;
    }

    @Override
    public String getBeanClassName() {
        return this.beanName;
    }

    @Override
    public void setBeanClass(Class<?> beanClass) {
        this.beanClass = beanClass;
    }

    @Override
    public Class<?> getBeanClass() {
        return this.beanClass;
    }

    @Override
    public void setScope(ScopeName scope) {
        this.scope = scope.getString();
    }

    @Override
    public String getScope() {
        return this.scope;
    }

    @Override
    public boolean isSingleton() {
        return this.scope.equals("singleton");
    }

    @Override
    public boolean isPrototype() {
        return this.scope.equals("prototype");
    }
}
