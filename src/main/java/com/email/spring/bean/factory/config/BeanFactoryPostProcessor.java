package com.email.spring.bean.factory.config;

import com.email.spring.bean.factory.BeanDefinitionRegistry;
import com.email.spring.core.PatternMatchingResourcePatternResolver;

public interface BeanFactoryPostProcessor {

    String DEFAULT_PATH = "classpath*:com/email/spring/**/*.class";
    String CLASSPATH_URL_PREFIX = "classpath*:";
    String ALL_CLASS_RESOURCES_SUFFIX = "/**/*.class";
    String ANNOTATION_COMPONENT_SCAN = "com.email.spring.bean.annotation.ComponentScan";
    String ANNOTATION_COMPONENT = "com.email.spring.bean.annotation.Component";
    String ANNOTATION_CONFIGURATION = "com.email.spring.bean.annotation.Configuration";

    PatternMatchingResourcePatternResolver resourceResolver = new PatternMatchingResourcePatternResolver();

    void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry registry) throws BeansException;
}
