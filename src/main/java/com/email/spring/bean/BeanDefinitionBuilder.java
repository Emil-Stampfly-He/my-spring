package com.email.spring.bean;

public class BeanDefinitionBuilder {

    private final BeanDefinition beanDefinition;

    public BeanDefinitionBuilder(BeanDefinition beanDefinition) {
        this.beanDefinition = beanDefinition;
    }

    public BeanDefinitionBuilder genericBeanDefinition() {
        return new BeanDefinitionBuilder(new SimpleBeanDefinition());
    }

    public BeanDefinitionBuilder genericBeanDefinition(Class<?> beanClass) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(new SimpleBeanDefinition());
        builder.beanDefinition.setBeanClass(beanClass);
        return builder;
    }

    public BeanDefinitionBuilder genericBeanDefinition(Class<?> beanClass, String beanName) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(new SimpleBeanDefinition());
        builder.beanDefinition.setBeanClass(beanClass);
        builder.beanDefinition.setBeanClassName(beanName);
        return builder;
    }

    public BeanDefinitionBuilder setBeanClass(Class<?> beanClass) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(new SimpleBeanDefinition());
        builder.beanDefinition.setBeanClass(beanClass);
        return builder;
    }

    public BeanDefinitionBuilder setBeanClassName(String beanName) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(new SimpleBeanDefinition());
        builder.beanDefinition.setBeanClassName(beanName);
        return builder;
    }

    public BeanDefinitionBuilder setScope(ScopeName scope) {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(new SimpleBeanDefinition());
        builder.beanDefinition.setScope(scope);
        return builder;
    }

    public BeanDefinition getBeanDefinition() {
        return beanDefinition;
    }
}
