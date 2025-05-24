package com.emil.spring.bean;

import com.email.spring.bean.ScopeName;
import com.email.spring.bean.SimpleBeanDefinition;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleBeanDefinitionTest {
    @Test
    void beanClassName_setAndGet() {
        SimpleBeanDefinition bd = new SimpleBeanDefinition();
        bd.setBeanClassName("com.example.MyBean");
        assertEquals("com.example.MyBean", bd.getBeanClassName(),
                "getBeanClassName() 应返回刚刚设置的 beanName");
    }

    @Test
    void beanClass_setAndGet() {
        SimpleBeanDefinition bd = new SimpleBeanDefinition();
        bd.setBeanClass(Integer.class);
        assertEquals(Integer.class, bd.getBeanClass(),
                "getBeanClass() 应返回刚刚设置的 beanClass");
    }

    @Test
    void scope_singleton_behavior() {
        SimpleBeanDefinition bd = new SimpleBeanDefinition();
        bd.setScope(ScopeName.SINGLETON);
        assertEquals("singleton", bd.getScope(), "getScope() 应返回 \"singleton\"");
        assertTrue(bd.isSingleton(), "isSingleton() 应返回 true");
        assertFalse(bd.isPrototype(), "isPrototype() 应返回 false");
    }

    @Test
    void scope_prototype_behavior() {
        SimpleBeanDefinition bd = new SimpleBeanDefinition();
        bd.setScope(ScopeName.PROTOTYPE);
        assertEquals("prototype", bd.getScope(), "getScope() 应返回 \"prototype\"");
        assertFalse(bd.isSingleton(), "isSingleton() 应返回 false");
        assertTrue(bd.isPrototype(), "isPrototype() 应返回 true");
    }
}
