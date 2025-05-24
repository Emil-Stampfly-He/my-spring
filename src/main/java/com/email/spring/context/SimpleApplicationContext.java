package com.email.spring.context;

import com.email.spring.bean.BeanDefinition;
import com.email.spring.bean.factory.BeanDefinitionRegistry;
import com.email.spring.bean.factory.DefaultBeanFactory;
import com.email.spring.bean.factory.config.BeanFactoryPostProcessor;
import com.email.spring.core.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class SimpleApplicationContext extends DefaultResourceLoader
        implements ApplicationContext, BeanDefinitionRegistry {

    private final DefaultBeanFactory beanFactory;
    private final String id = this.toString();
    private final List<BeanFactoryPostProcessor> beanFactoryPostProcessors = new ArrayList<>();
    private final ResourcePatternResolver resourcePatternResolver = new PatternMatchingResourcePatternResolver();;
    private AtomicBoolean refreshed = new AtomicBoolean();
    private AtomicBoolean active = new AtomicBoolean();
    private AtomicBoolean closed = new AtomicBoolean();

    public SimpleApplicationContext() {
        this.beanFactory = new DefaultBeanFactory();
    }

    public SimpleApplicationContext(DefaultBeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        this.beanFactory.registerBeanDefinition(beanName, beanDefinition);
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return this.beanFactory.getBeanDefinition(beanName);
    }

    @Override
    public void removeBeanDefinition(String beanName) {
        this.beanFactory.removeBeanDefinition(beanName);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return this.beanFactory.containsBeanDefinition(beanName);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return this.beanFactory.getBeanDefinitionNames();
    }

    @Override
    public int getBeanDefinitionCount() {
        return this.beanFactory.getBeanDefinitionCount();
    }

    @Override
    public Object getBean(String beanName) {
        return this.beanFactory.getBean(beanName);
    }

    @Override
    public boolean containsBean(String beanName) {
        return this.beanFactory.containsBean(beanName);
    }

    @Override
    public boolean isSingleton(String beanName) {
        return this.beanFactory.isSingleton(beanName);
    }

    @Override
    public boolean isPrototype(String beanName) {
        return this.beanFactory.isPrototype(beanName);
    }

    @Override
    public Resource[] getResources(String path) throws IOException {
        return this.resourcePatternResolver.getResources(path);
    }

    @Override
    public String getId() {
        return this.id;
    }

    // TODO: 补全后处理器相关内容
    public void addBeanFactoryPostProcessor(BeanFactoryPostProcessor postProcessor) {

    }

    public List<BeanFactoryPostProcessor> getBeanFactoryPostProcessors() {
        return Collections.emptyList();
    }

    public void refresh() {

    }

    public void close() {
        // 1. 销毁所有Bean
        // 2. 清理BeanFactory
    }
}
