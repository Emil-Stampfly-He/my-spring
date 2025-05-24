package com.emil.spring.bean;

import com.email.spring.bean.BeanDefinitionBuilder;
import com.email.spring.bean.ScopeName;
import com.email.spring.bean.SimpleBeanDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BeanDefinitionBuilderTest {
    @Test
    void constructor_and_getBeanDefinition() {
        SimpleBeanDefinition original = new SimpleBeanDefinition();
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(original);
        assertSame(original, builder.getBeanDefinition(),
                "getBeanDefinition() 应返回构造时传入的实例");
    }

    @Test
    void genericBeanDefinition_noArgs() {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(null);
        BeanDefinitionBuilder result = builder.genericBeanDefinition();
        assertNotNull(result, "genericBeanDefinition() 不应返回 null");
        assertNotSame(builder, result, "应返回一个新的 Builder 实例");
        assertInstanceOf(SimpleBeanDefinition.class, result.getBeanDefinition(), "内部的 BeanDefinition 应为 SimpleBeanDefinition");
    }

    @Test
    void genericBeanDefinition_withClass() {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(null);
        BeanDefinitionBuilder result = builder.genericBeanDefinition(String.class);
        assertEquals(String.class, result.getBeanDefinition().getBeanClass(),
                "应将 beanClass 设置为 String.class");
    }

    @Test
    void genericBeanDefinition_withClassAndName() {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(null);
        BeanDefinitionBuilder result = builder.genericBeanDefinition(Integer.class, "myInt");
        assertEquals(Integer.class, result.getBeanDefinition().getBeanClass(),
                "应将 beanClass 设置为 Integer.class");
        assertEquals("myInt", result.getBeanDefinition().getBeanClassName(),
                "应将 beanName 设置为 \"myInt\"");
    }

    @Test
    void setBeanClass_shouldCreateNewBuilderAndSetClass() {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(null);
        BeanDefinitionBuilder result = builder.setBeanClass(Double.class);
        assertEquals(Double.class, result.getBeanDefinition().getBeanClass(),
                "setBeanClass() 应设置 beanClass");
    }

    @Test
    void setBeanClassName_shouldCreateNewBuilderAndSetName() {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(null);
        BeanDefinitionBuilder result = builder.setBeanClassName("com.example.Bean");
        assertEquals("com.example.Bean", result.getBeanDefinition().getBeanClassName(),
                "setBeanClassName() 应设置 beanName");
    }

    @Test
    void setScope_singleton() {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(null);
        BeanDefinitionBuilder result = builder.setScope(ScopeName.SINGLETON);
        var bd = result.getBeanDefinition();
        assertEquals("singleton", bd.getScope(), "scope 应为 \"singleton\"");
        assertTrue(bd.isSingleton(), "isSingleton() 应返回 true");
        assertFalse(bd.isPrototype(), "isPrototype() 应返回 false");
    }

    @Test
    void setScope_prototype() {
        BeanDefinitionBuilder builder = new BeanDefinitionBuilder(null);
        BeanDefinitionBuilder result = builder.setScope(ScopeName.PROTOTYPE);
        var bd = result.getBeanDefinition();
        assertEquals("prototype", bd.getScope(), "scope 应为 \"prototype\"");
        assertFalse(bd.isSingleton(), "isSingleton() 应返回 false");
        assertTrue(bd.isPrototype(), "isPrototype() 应返回 true");
    }
}
