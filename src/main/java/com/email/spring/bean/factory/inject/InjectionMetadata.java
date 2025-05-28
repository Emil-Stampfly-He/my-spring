package com.email.spring.bean.factory.inject;

import com.email.spring.bean.factory.BeanFactory;

import java.util.List;

public record InjectionMetadata(Class<?> targetClass, List<InjectedElement> injectedElements) {

    // 调用每个InjectedElement的inject方法
    public void inject(Object bean, String beanName, BeanFactory beanFactory) {
        for (InjectedElement element : injectedElements) {
            element.inject(bean, beanName, beanFactory);
        }
    }
}
