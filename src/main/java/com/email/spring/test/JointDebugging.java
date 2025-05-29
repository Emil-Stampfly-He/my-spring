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
        DefaultBeanFactory beanFactory = getDefaultBeanFactory();

        MyController myController = beanFactory.getBean(MyController.class);
        System.out.println(myController.getBean1());
        System.out.println(myController.getBean2());
        System.out.println(myController.getBean4()); // null
        System.out.println();

        Field beanDefinitionMap = beanFactory.getClass().getDeclaredField("beanDefinitionMap");
        beanDefinitionMap.setAccessible(true);
        HashMap<String, BeanDefinition> stringBeanDefinitionHashMap = (HashMap<String, BeanDefinition>) beanDefinitionMap.get(beanFactory);
        stringBeanDefinitionHashMap.forEach((k, v) -> System.out.println(k + " = " + v + ": " + v.getScope()));
    }

    private static DefaultBeanFactory getDefaultBeanFactory() {
        DefaultBeanFactory beanFactory = new DefaultBeanFactory();

        // BeanFactoryPostProcessor
        ComponentScanPostProcessor componentScanPostProcessor = new ComponentScanPostProcessor();
        componentScanPostProcessor.postProcessBeanDefinitionRegistry(beanFactory);

        ConfigurationClassPostProcessor configurationClassPostProcessor = new ConfigurationClassPostProcessor();
        configurationClassPostProcessor.postProcessBeanDefinitionRegistry(beanFactory);

        // BeanPostProcessor前置环节
        AutowiredAnnotationPostProcessor autowiredAnnotationPostProcessor = new AutowiredAnnotationPostProcessor();
        autowiredAnnotationPostProcessor.setBeanFactory(beanFactory);
        // 注册BeanPostProcessor，让BeanPostProcessor生效
        beanFactory.addBeanPostProcessor(autowiredAnnotationPostProcessor);

        // 初始化Bean
        beanFactory.initializeBean();

        return beanFactory;
    }
}
