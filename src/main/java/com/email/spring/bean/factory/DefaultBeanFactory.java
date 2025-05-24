package com.email.spring.bean.factory;

import com.email.spring.bean.BeanDefinition;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.*;

public class DefaultBeanFactory implements BeanFactory, BeanDefinitionRegistry {

    private static final Log log = LogFactory.getLog(DefaultBeanFactory.class);

    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private final Map<String, Object> singletonBeanMap = new HashMap<>();

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        // 检查是否存在相同的beanName
        Set<String> beanNames = this.beanDefinitionMap.keySet();
        Set<String> singletonBeanNames = this.singletonBeanMap.keySet();
        if (beanNames.contains(beanName)) {
            log.info("Bean" + beanName + "已存在。现被覆盖。");
            this.beanDefinitionMap.remove(beanName);
            if (singletonBeanNames.contains(beanName)) {
                this.singletonBeanMap.remove(beanName);
            }

            this.beanDefinitionMap.put(beanName, beanDefinition);
            if (beanDefinition.isSingleton()) {
                this.singletonBeanMap.put(beanName, beanDefinition.getBeanClass());
            }
        }

        this.beanDefinitionMap.put(beanName, beanDefinition);
        if (beanDefinition.isSingleton()) {
            this.singletonBeanMap.put(beanName, beanDefinition.getBeanClass());
        }
    }

    @Override
    public BeanDefinition getBeanDefinition(String beanName) {
        return this.beanDefinitionMap.get(beanName);
    }

    @Override
    public void removeBeanDefinition(String beanName) {
        this.beanDefinitionMap.remove(beanName);
    }

    @Override
    public boolean containsBeanDefinition(String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public String[] getBeanDefinitionNames() {
        return this.beanDefinitionMap.keySet().toArray(new String[0]);
    }

    @Override
    public int getBeanDefinitionCount() {
        return this.beanDefinitionMap.size();
    }

    // TODO: getBean方法以后可能需要返回代理对象

    @Override
    public Object getBean(String beanName) {
        return this.beanDefinitionMap.get(beanName).getBeanClass();
    }

    @Override
    public boolean containsBean(String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public boolean isSingleton(String beanName) {
        return this.singletonBeanMap.containsKey(beanName);
    }

    @Override
    public boolean isPrototype(String beanName) {
        return !this.singletonBeanMap.containsKey(beanName);
    }
}
