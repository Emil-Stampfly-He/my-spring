package com.email.spring.bean;

public record BeanDefinitionBuilder(BeanDefinition beanDefinition) {

    public static BeanDefinitionBuilder genericBeanDefinition() {
        return new BeanDefinitionBuilder(new SimpleBeanDefinition());
    }

    public static BeanDefinitionBuilder genericBeanDefinition(Class<?> beanClass) {
        return genericBeanDefinition()
                .setBeanClass(beanClass);
    }

    public static BeanDefinitionBuilder genericBeanDefinition(Class<?> beanClass, String beanName) {
        return genericBeanDefinition()
                .setBeanClass(beanClass)
                .setBeanClassName(beanName);
    }

    public BeanDefinitionBuilder setBeanClass(Class<?> beanClass) {
        this.beanDefinition.setBeanClass(beanClass);
        return this;
    }

    public BeanDefinitionBuilder setBeanClassName(String beanName) {
        this.beanDefinition.setBeanClassName(beanName);
        return this;
    }

    public BeanDefinitionBuilder setScope(ScopeName scope) {
        this.beanDefinition.setScope(scope);
        return this;
    }

    public BeanDefinitionBuilder setInitMethod(String initMethod) {
        this.beanDefinition.setInitMethodName(initMethod);
        return this;
    }

    public BeanDefinitionBuilder setDestroyMethod(String destroyMethod) {
        this.beanDefinition.setDestroyMethodName(destroyMethod);
        return this;
    }
}
