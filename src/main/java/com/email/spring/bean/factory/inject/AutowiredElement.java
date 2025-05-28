package com.email.spring.bean.factory.inject;

import java.lang.reflect.Member;

public abstract class AutowiredElement extends InjectedElement {
    protected final boolean required;

    protected AutowiredElement(Member member, boolean required) {
        super(member);
        this.required = required;
    }
}