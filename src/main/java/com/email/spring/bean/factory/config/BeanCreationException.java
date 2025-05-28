package com.email.spring.bean.factory.config;

public class BeanCreationException extends BeansException {

    private final String beanName;

    private final String resourceDescription;

    public BeanCreationException(String msg) {
        super(msg);
        this.beanName = null;
        this.resourceDescription = null;
    }

    public BeanCreationException(String beanName, String msg) {
        super("Error creating bean with name '" + beanName + "': " + msg);
        this.beanName = beanName;
        this.resourceDescription = null;
    }

    public BeanCreationException(String beanName, String msg, Throwable cause) {
        this(beanName, msg);
        initCause(cause);
    }
}
