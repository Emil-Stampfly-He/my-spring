package com.email.spring.bean.factory.inject;

import com.email.spring.bean.factory.BeanFactory;
import com.email.spring.bean.factory.DefaultBeanFactory;
import com.email.spring.bean.factory.NoSuchBeanDefinitionException;
import com.email.spring.bean.factory.config.BeansException;

import java.lang.reflect.Member;
import java.lang.reflect.Method;

public class AutowiredMethodElement extends AutowiredElement {

    public AutowiredMethodElement(Member member, boolean required) {
        super(member, required);
    }

    @Override
    protected void inject(Object bean, String beanName, BeanFactory beanFactory) {
        Method method = (Method) this.member;
        Class<?>[] parameterTypes = method.getParameterTypes();
        Object[] args = new Object[parameterTypes.length];

        for (int i = 0; i < parameterTypes.length; i++) {
            try {
                args[i] = beanFactory.getBean(parameterTypes[i]);
            } catch (NoSuchBeanDefinitionException ex) {
                if (this.required) {
                    throw ex;
                }
                args[i] = null;
            }
        }

        method.setAccessible(true);
        try {
            method.invoke(bean, args);
        } catch (Exception e) {
            throw new BeansException("Failed to invoke method " + method);
        }

        // 登记所有参数类型的依赖
        if (beanFactory instanceof DefaultBeanFactory df) {
            for (Class<?> parameterType : parameterTypes) {
                String dependencyName = df.getBeanNameForType(parameterType);
                df.registerDependentBean(dependencyName, beanName);
            }
        }
    }
}