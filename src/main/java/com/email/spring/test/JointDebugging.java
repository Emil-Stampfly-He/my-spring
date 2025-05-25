package com.email.spring.test;

import com.email.spring.bean.BeanDefinition;
import com.email.spring.bean.factory.DefaultBeanFactory;
import com.email.spring.bean.factory.config.ComponentScanPostProcessor;

import java.lang.reflect.Field;
import java.util.HashMap;

public class JointDebugging {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        DefaultBeanFactory beanFactory = new DefaultBeanFactory();
        ComponentScanPostProcessor postProcessor = new ComponentScanPostProcessor();
        postProcessor.postProcessBeanDefinitionRegistry(beanFactory);

        Field beanDefinitionMap = beanFactory.getClass().getDeclaredField("beanDefinitionMap");
        beanDefinitionMap.setAccessible(true);
        HashMap<String, BeanDefinition> stringBeanDefinitionHashMap = (HashMap<String, BeanDefinition>) beanDefinitionMap.get(beanFactory);

        System.out.println(stringBeanDefinitionHashMap);
    }
}
