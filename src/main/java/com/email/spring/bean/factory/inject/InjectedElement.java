package com.email.spring.bean.factory.inject;

import com.email.spring.bean.factory.BeanFactory;

import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;

public abstract class InjectedElement {

    protected final Member member;
    protected final boolean isField;

    protected InjectedElement(Member member) {
        this.member = member;
        this.isField = (member instanceof Field);
    }

    public final Member getMember() {
        return this.member;
    }

    protected final Class<?> getResourceType() {
        if (this.isField) {
            return ((Field) this.member).getType();
        } else {
            return ((Method) this.member).getParameterTypes()[0];
        }
    }

    protected abstract void inject(Object target, String requestingBeanName, BeanFactory beanFactory);
}
