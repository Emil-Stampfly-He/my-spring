package com.email.spring.bean.factory.inject;

import com.email.spring.bean.factory.BeanFactory;
import com.email.spring.bean.factory.DefaultBeanFactory;
import com.email.spring.bean.factory.NoSuchBeanDefinitionException;
import com.email.spring.bean.factory.config.BeansException;

import java.lang.reflect.Field;

public class AutowiredFieldElement extends AutowiredElement {

    public AutowiredFieldElement(Field field, boolean required) {
        super(field, required);
    }

    @Override
    protected void inject(Object bean, String beanName, BeanFactory beanFactory) {
        Field field = (Field) this.member;
        Object value; // 要给字段设置的值
        try {
            value = beanFactory.getBean(field.getType());
        } catch (NoSuchBeanDefinitionException ex) {
            if (this.required) {
                throw ex;
            } else {
                return;
            }
        }

        // 反射注入
        field.setAccessible(true);
        try {
            field.set(bean, value);
        } catch (IllegalAccessException e) {
            throw new BeansException("Failed to set field " + field);
        }

        // 登记依赖关系
        if (beanFactory instanceof DefaultBeanFactory df) {
            String dependencyName = df.getBeanNameForType(field.getType());
            df.registerDependentBean(beanName, dependencyName);
        }
    }

}