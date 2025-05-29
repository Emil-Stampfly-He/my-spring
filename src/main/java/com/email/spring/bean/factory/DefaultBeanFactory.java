package com.email.spring.bean.factory;

import com.email.spring.bean.BeanDefinition;
import com.email.spring.bean.factory.config.BeanPostProcessor;
import com.email.spring.bean.factory.config.BeansException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class DefaultBeanFactory implements BeanFactory, BeanDefinitionRegistry {

    private static final Log log = LogFactory.getLog(DefaultBeanFactory.class);

    private final Map<String, BeanDefinition> beanDefinitionMap = new HashMap<>();
    private final Map<String, Object> singletonObject = new HashMap<>();

    // Bean依赖图
    private final Map<String, Set<String>> dependenciesForBeanMap = new HashMap<>();
    private final Map<String, Set<String>> dependentBeanMap = new HashMap<>();

    // BeanPostProcessor
    private final Set<BeanPostProcessor> beanPostProcessors = new HashSet<>();

    @Override
    public void registerBeanDefinition(String beanName, BeanDefinition beanDefinition) {
        // 检查是否存在相同的beanName
        Set<String> beanNames = this.beanDefinitionMap.keySet();
        Set<String> singletonBeanNames = this.singletonObject.keySet();
        if (beanNames.contains(beanName)) {
            log.info("Bean" + beanName + "exists, now is covered");
            this.beanDefinitionMap.remove(beanName);
            if (singletonBeanNames.contains(beanName)) {
                this.singletonObject.remove(beanName);
            }

            this.beanDefinitionMap.put(beanName, beanDefinition);
            putSingletonBeanMap(beanName, beanDefinition);
        }

        this.beanDefinitionMap.put(beanName, beanDefinition);
        if (beanDefinition.isSingleton()) {
            putSingletonBeanMap(beanName, beanDefinition);
        }
    }

    private void putSingletonBeanMap(String beanName, BeanDefinition beanDefinition) {
        if (beanDefinition.isSingleton()) {
            Class<?> beanClass = beanDefinition.getBeanClass();
            Object beanInstance;
            try {
                beanInstance = beanClass.getDeclaredConstructor().newInstance();
            } catch (Exception e) {
                throw new BeansException("Failed to instantiate " + beanClass);
            }
            this.singletonObject.put(beanName, beanInstance);
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
        if (this.singletonObject.containsKey(beanName)) {
            return this.singletonObject.get(beanName);
        }

        BeanDefinition bd = this.beanDefinitionMap.get(beanName);
        if (bd == null) {
            throw new NoSuchBeanDefinitionException(beanName);
        }

        Class<?> beanClass = bd.getBeanClass();
        Object bean;
        try {
            bean = beanClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BeansException("Failed to instantiate bean: " + beanName);
        }

        return bean;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getBean(Class<T> requiredType) {
        // 1. 在单例缓存中寻找
        for (Object obj : this.singletonObject.values()) {
            if (requiredType.isInstance(obj)) {
                return (T) obj;
            }
        }

        // 2. beanDefinitionMap里寻找唯一匹配
        String name = this.getBeanNameForType(requiredType);
        return (T) this.getBean(name);
    }

    public String getBeanNameForType(Class<?> requiredType) {
        String found = null;
        for (Map.Entry<String, BeanDefinition> entry : this.beanDefinitionMap.entrySet()) {
            if (requiredType.isAssignableFrom(entry.getValue().getBeanClass())) {
                if (found != null) {
                    throw new NoSuchBeanDefinitionException("Expected single matching bean but found two: " + requiredType);
                }
                found = entry.getKey();
            }
        }
        if (found == null) {
            throw new NoSuchBeanDefinitionException("No such bean: " + requiredType);
        }
        return found;
    }

    public void registerDependentBean(String beanName, String dependentBeanName) {
        this.dependenciesForBeanMap
                .computeIfAbsent(beanName, k -> new HashSet<>())
                .add(dependentBeanName);
        this.dependentBeanMap
                .computeIfAbsent(beanName, k -> new HashSet<>())
                .add(beanName);
    }

    @Override
    public boolean containsBean(String beanName) {
        return this.beanDefinitionMap.containsKey(beanName);
    }

    @Override
    public boolean isSingleton(String beanName) {
        return this.singletonObject.containsKey(beanName);
    }

    @Override
    public boolean isPrototype(String beanName) {
        return !this.singletonObject.containsKey(beanName);
    }

    // 用于将beanFactory中的所有Bean都经过Bean后处理器进行处理
    public void addBeanPostProcessor(BeanPostProcessor beanPostProcessor) {
        this.beanPostProcessors.add(beanPostProcessor);
        for (BeanPostProcessor bp : this.beanPostProcessors) {
            this.beanDefinitionMap.forEach(
                    (beanName, beanDefinition)
                            -> bp.postProcessBeforeInitialization(this.getBean(beanName), beanDefinition.getBeanClassName()));

        }
    }

    // TODO: 可能还得改一下initMethodNames数组的问题
    // 调用初始化方法
    public void initializeBean() {
        // 1. 拿到所有BeanDefinition，从而拿到所有beanName和相应的initMethodName
        // 2. 根据beanName拿到bean，使用initMethodName反射地调用相应的初始化方法
        Map<String, String> beanInitMap = new HashMap<>();
        for (Map.Entry<String, BeanDefinition> entry : this.beanDefinitionMap.entrySet()) {
            if (entry.getValue().getInitMethodName() == null) continue;
            beanInitMap.put(entry.getKey(), entry.getValue().getInitMethodName());
        }

        try {
            for (Map.Entry<String, String> entry : beanInitMap.entrySet()) {
                Object bean = this.getBean(entry.getKey());
                String initMethodName = entry.getValue();
                if (initMethodName.isEmpty()) continue;

                Method initMethod = bean.getClass().getDeclaredMethod(initMethodName);
                initMethod.setAccessible(true);
                initMethod.invoke(bean);
            }
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No such init method");
        } catch (InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("Failed to invoke init method");
        }
    }
}
