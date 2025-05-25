package com.emil.spring.bean;

import com.email.spring.bean.BeanDefinition;
import com.email.spring.bean.BeanDefinitionBuilder;
import com.email.spring.bean.ScopeName;
import com.email.spring.bean.SimpleBeanDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class BeanDefinitionBuilderTest {
    @Test
    void genericBeanDefinition_noArgs() {
        BeanDefinitionBuilder b1 = BeanDefinitionBuilder.genericBeanDefinition();
        BeanDefinitionBuilder b2 = BeanDefinitionBuilder.genericBeanDefinition();
        assertNotNull(b1, "genericBeanDefinition() 不应返回 null");
        assertNotSame(b1, b2, "每次调用 genericBeanDefinition() 应返回新的 Builder 实例");
        assertInstanceOf(SimpleBeanDefinition.class, b1.beanDefinition(), "内部的 BeanDefinition 应为 SimpleBeanDefinition");
    }

    @Test
    void genericBeanDefinition_withClass() {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(String.class);
        assertEquals(String.class, builder.beanDefinition().getBeanClass(),
                "应将 beanClass 设置为 String.class");
    }

    @Test
    void genericBeanDefinition_withClassAndName() {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(Integer.class, "myInt");
        assertEquals(Integer.class, builder.beanDefinition().getBeanClass(),
                "应将 beanClass 设置为 Integer.class");
        assertEquals("myInt", builder.beanDefinition().getBeanClassName(),
                "应将 beanName 设置为 \"myInt\"");
    }

    @Test
    void setBeanClass_returnsSameBuilderAndSetsClass() {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        BeanDefinitionBuilder result = builder.setBeanClass(Double.class);
        assertSame(builder, result, "setBeanClass() 应返回同一 Builder 实例");
        assertEquals(Double.class, builder.beanDefinition().getBeanClass(),
                "setBeanClass() 应设置 beanClass");
    }

    @Test
    void setBeanClassName_returnsSameBuilderAndSetsName() {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        BeanDefinitionBuilder result = builder.setBeanClassName("com.example.Bean");
        assertSame(builder, result, "setBeanClassName() 应返回同一 Builder 实例");
        assertEquals("com.example.Bean", builder.beanDefinition().getBeanClassName(),
                "setBeanClassName() 应设置 beanName");
    }

    @Test
    void setScope_singleton() {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        BeanDefinitionBuilder result = builder.setScope(ScopeName.SINGLETON);
        assertSame(builder, result, "setScope() 应返回同一 Builder 实例");
        var bd = builder.beanDefinition();
        assertEquals("singleton", bd.getScope(), "scope 应为 \"singleton\"");
        assertTrue(bd.isSingleton(), "isSingleton() 应返回 true");
        assertFalse(bd.isPrototype(), "isPrototype() 应返回 false");
    }

    @Test
    void setScope_prototype() {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        BeanDefinitionBuilder result = builder.setScope(ScopeName.PROTOTYPE);
        assertSame(builder, result, "setScope() 应返回同一 Builder 实例");
        var bd = builder.beanDefinition();
        assertEquals("prototype", bd.getScope(), "scope 应为 \"prototype\"");
        assertFalse(bd.isSingleton(), "isSingleton() 应返回 false");
        assertTrue(bd.isPrototype(), "isPrototype() 应返回 true");
    }

    @Test
    void getBeanDefinition_returnsSameInstanceEachCall() {
        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();
        BeanDefinition first = builder.beanDefinition();
        BeanDefinition second = builder.beanDefinition();
        assertSame(first, second, "多次调用 getBeanDefinition() 应返回相同实例");
    }
}
