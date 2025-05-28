package com.email.spring.bean.factory.config;

import com.email.spring.bean.annotation.Autowired;
import com.email.spring.bean.factory.BeanFactory;
import com.email.spring.bean.factory.DefaultBeanFactory;
import com.email.spring.bean.factory.inject.AutowiredFieldElement;
import com.email.spring.bean.factory.inject.AutowiredMethodElement;
import com.email.spring.bean.factory.inject.InjectedElement;
import com.email.spring.bean.factory.inject.InjectionMetadata;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AutowiredAnnotationPostProcessor implements BeanPostProcessor {

    private final Set<Class<? extends Annotation>> autowiredAnnotationTypes = new HashSet<>();
    private DefaultBeanFactory beanFactory = new DefaultBeanFactory();

    public AutowiredAnnotationPostProcessor() {
        this.autowiredAnnotationTypes.add(Autowired.class);
    }

    public AutowiredAnnotationPostProcessor(DefaultBeanFactory beanFactory) {
        this.autowiredAnnotationTypes.add(Autowired.class);
        this.beanFactory = beanFactory;
    }

    // 用户自定义用于依赖注入的注解，替代@Autowired
    public void setAutowiredAnnotationType(Class<? extends Annotation> autowiredAnnotationTypes) {
        assert autowiredAnnotationTypes != null;
        this.autowiredAnnotationTypes.clear();
        this.autowiredAnnotationTypes.add(autowiredAnnotationTypes);
    }

    // 用户批量自定义用于依赖注入的注解，替代@Autowired
    public void setAutowiredAnnotationTypes(Set<Class<? extends Annotation>> autowiredAnnotationTypes) {
        assert autowiredAnnotationTypes != null;
        this.autowiredAnnotationTypes.clear();
        this.autowiredAnnotationTypes.addAll(autowiredAnnotationTypes);
    }

    // 用户增加自定义依赖注入的注解，@Autowired仍能生效
    public void addAutowiredAnnotationType(Class<? extends Annotation> autowiredAnnotationTypes) {
        assert autowiredAnnotationTypes != null;
        this.autowiredAnnotationTypes.add(autowiredAnnotationTypes);
    }

    // 用户批量增加自定义依赖注入的注解，@Autowired仍能生效
    public void addAutowiredAnnotationTypes(Set<Class<? extends Annotation>> autowiredAnnotationTypes) {
        assert autowiredAnnotationTypes != null;
        this.autowiredAnnotationTypes.addAll(autowiredAnnotationTypes);
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        if (!(beanFactory instanceof DefaultBeanFactory dbf)) {
            throw new IllegalArgumentException("beanFactory must be an instance of DefaultBeanFactory");
        }
        this.beanFactory = dbf;
    }

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        InjectionMetadata injectionMetadata = findAutowiringMetadata(bean.getClass());
        try {
            injectionMetadata.inject(bean, beanName, this.beanFactory);
        } catch (BeanCreationException ex) {
            throw ex;
        } catch (Throwable ex) {
            throw new BeanCreationException(beanName, "Injection of autowired dependencies failed", ex);
        }

        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }

    // TODO: 可增加缓存，避免每次都build
    private InjectionMetadata findAutowiringMetadata(Class<?> beanClass) {
        return buildAutowiringMetadata(beanClass);
    }

    private InjectionMetadata buildAutowiringMetadata(Class<?> beanClass) {
        List<InjectedElement> injectedElements = new ArrayList<>();

        for (Field field : beanClass.getDeclaredFields()) {
            Autowired autowired = field.getAnnotation(Autowired.class);
            if (autowired != null) {
                injectedElements.add(new AutowiredFieldElement(field, autowired.required()));
            }
        }

        for (Method method : beanClass.getDeclaredMethods()) {
            Autowired autowired = method.getAnnotation(Autowired.class);
            if (autowired != null) {
                injectedElements.add(new AutowiredMethodElement(method, autowired.required()));
            }
        }

        return new InjectionMetadata(beanClass, injectedElements);
    }
}
