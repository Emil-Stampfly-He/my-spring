package com.email.spring.test;

import com.email.spring.bean.BeanDefinition;
import com.email.spring.bean.factory.DefaultBeanFactory;
import com.email.spring.bean.factory.config.AutowiredAnnotationPostProcessor;
import com.email.spring.bean.factory.config.ComponentScanPostProcessor;
import com.email.spring.bean.factory.config.ConfigurationClassPostProcessor;

import java.lang.reflect.Field;
import java.util.HashMap;

@SuppressWarnings("unchecked")
public class JointDebugging {
    public static void main(String[] args) throws NoSuchFieldException, IllegalAccessException {
        DefaultBeanFactory beanFactory = new DefaultBeanFactory();

        // BeanFactoryPostProcessor
        ComponentScanPostProcessor componentScanPostProcessor = new ComponentScanPostProcessor();
        componentScanPostProcessor.postProcessBeanDefinitionRegistry(beanFactory);

        ConfigurationClassPostProcessor configurationClassPostProcessor = new ConfigurationClassPostProcessor();
        configurationClassPostProcessor.postProcessBeanDefinitionRegistry(beanFactory);

        // BeanPostProcessor
        MyController myController = new MyController();
        AutowiredAnnotationPostProcessor autowiredAnnotationPostProcessor = new AutowiredAnnotationPostProcessor();
        autowiredAnnotationPostProcessor.setBeanFactory(beanFactory);
        autowiredAnnotationPostProcessor.postProcessBeforeInitialization(myController, "myController");

        System.out.println(myController.getBean1());
        System.out.println();

        Field beanDefinitionMap = beanFactory.getClass().getDeclaredField("beanDefinitionMap");
        beanDefinitionMap.setAccessible(true);
        HashMap<String, BeanDefinition> stringBeanDefinitionHashMap = (HashMap<String, BeanDefinition>) beanDefinitionMap.get(beanFactory);
        stringBeanDefinitionHashMap.forEach((k, v) -> System.out.println(k + " = " + v + ": " + v.getScope()));
    }
}
