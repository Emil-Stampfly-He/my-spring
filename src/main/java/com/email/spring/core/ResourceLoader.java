package com.email.spring.core;

public interface ResourceLoader {

    public static final String CLASSPATH_URL_PREFIX = "classpath:";

    Resource getResource(String path);
    ClassLoader getClassLoader();
}
