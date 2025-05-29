package com.email.spring.bean;

public class SimpleBeanDefinition implements BeanDefinition {

    private Class<?> beanClass;
    private String beanName;
    private String scope;
    private String[] initMethodNames = new String[0];
    private String[] destroyMethodNames = new String[0];

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
    public void setInitMethodName(String initMethodName) {
        this.initMethodNames = initMethodName!= null ? new String[]{initMethodName} : null;
    }

    @Override
    public String getInitMethodName() {
        return this.initMethodNames.length != 0 ? this.initMethodNames[0] : null;
    }

    @Override
    public void setDestroyMethodName(String destroyMethodName) {
        this.destroyMethodNames = destroyMethodName!= null ? new String[]{destroyMethodName} : null;
    }

    @Override
    public String getDestroyMethodName() {
        return this.destroyMethodNames.length != 0 ? this.destroyMethodNames[0] : null;
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
