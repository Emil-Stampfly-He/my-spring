package com.emil.spring.bean.factory;

import com.email.spring.bean.BeanDefinition;
import com.email.spring.bean.ScopeName;
import com.email.spring.bean.SimpleBeanDefinition;
import com.email.spring.bean.factory.DefaultBeanFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultBeanFactoryTest {
    private DefaultBeanFactory factory;
    private Field beanDefMapField;
    private Field singletonMapField;

    @BeforeEach
    void setUp() throws Exception {
        factory = new DefaultBeanFactory();

        // 反射拿到私有字段
        beanDefMapField = DefaultBeanFactory.class.getDeclaredField("beanDefinitionMap");
        beanDefMapField.setAccessible(true);

        singletonMapField = DefaultBeanFactory.class.getDeclaredField("singletonBeanMap");
        singletonMapField.setAccessible(true);
    }

    @SuppressWarnings("unchecked")
    private Map<String, BeanDefinition> getBeanDefMap() throws Exception {
        return (Map<String, BeanDefinition>) beanDefMapField.get(factory);
    }

    @SuppressWarnings("unchecked")
    private Map<String, Class<?>> getSingletonMap() throws Exception {
        return (Map<String, Class<?>>) singletonMapField.get(factory);
    }

    @Test
    void registerSingletonBean_firstTime() throws Exception {
        SimpleBeanDefinition bd = new SimpleBeanDefinition();
        bd.setBeanClassName("myBean");
        bd.setBeanClass(String.class);
        bd.setScope(ScopeName.SINGLETON);

        factory.registerBeanDefinition("bean1", bd);

        Map<String, BeanDefinition> beanDefs = getBeanDefMap();
        Map<String, Class<?>> singletons = getSingletonMap();

        assertTrue(beanDefs.containsKey("bean1"), "应在 beanDefinitionMap 中注册 bean1");
        assertSame(bd, beanDefs.get("bean1"), "注册的 BeanDefinition 应与传入的一致");

        assertTrue(singletons.containsKey("bean1"), "singletonBeanMap 应包含 bean1");
        assertEquals(String.class, singletons.get("bean1"),
                "singletonBeanMap 中 bean1 的类型应为 String.class");
    }

    @Test
    void registerPrototypeBean_firstTime() throws Exception {
        SimpleBeanDefinition bd = new SimpleBeanDefinition();
        bd.setBeanClassName("protoBean");
        bd.setBeanClass(Integer.class);
        bd.setScope(ScopeName.PROTOTYPE);

        factory.registerBeanDefinition("bean2", bd);

        Map<String, BeanDefinition> beanDefs = getBeanDefMap();
        Map<String, Class<?>> singletons = getSingletonMap();

        assertTrue(beanDefs.containsKey("bean2"), "应在 beanDefinitionMap 中注册 bean2");
        assertFalse(singletons.containsKey("bean2"),
                "prototype Bean 不应出现在 singletonBeanMap 中");
    }

    @Test
    void overrideSingletonWithPrototype() throws Exception {
        // 先注册 singleton
        SimpleBeanDefinition bd1 = new SimpleBeanDefinition();
        bd1.setBeanClassName("overrideBean");
        bd1.setBeanClass(Double.class);
        bd1.setScope(ScopeName.SINGLETON);
        factory.registerBeanDefinition("bean3", bd1);

        // 再用 prototype 覆盖
        SimpleBeanDefinition bd2 = new SimpleBeanDefinition();
        bd2.setBeanClassName("overrideBean");
        bd2.setBeanClass(Long.class);
        bd2.setScope(ScopeName.PROTOTYPE);
        factory.registerBeanDefinition("bean3", bd2);

        Map<String, BeanDefinition> beanDefs = getBeanDefMap();
        Map<String, Class<?>> singletons = getSingletonMap();

        assertSame(bd2, beanDefs.get("bean3"),
                "覆盖后，beanDefinitionMap 中应保存最后一次注册的 BeanDefinition");
        assertFalse(singletons.containsKey("bean3"),
                "当用 prototype 覆盖 singleton 时，不应在 singletonBeanMap 中保留 bean3");
    }

    @Test
    void overridePrototypeWithSingleton() throws Exception {
        // 先注册 prototype
        SimpleBeanDefinition bd1 = new SimpleBeanDefinition();
        bd1.setBeanClassName("overrideBean2");
        bd1.setBeanClass(Character.class);
        bd1.setScope(ScopeName.PROTOTYPE);
        factory.registerBeanDefinition("bean4", bd1);

        // 再用 singleton 覆盖
        SimpleBeanDefinition bd2 = new SimpleBeanDefinition();
        bd2.setBeanClassName("overrideBean2");
        bd2.setBeanClass(Short.class);
        bd2.setScope(ScopeName.SINGLETON);
        factory.registerBeanDefinition("bean4", bd2);

        Map<String, BeanDefinition> beanDefs = getBeanDefMap();
        Map<String, Class<?>> singletons = getSingletonMap();

        assertSame(bd2, beanDefs.get("bean4"),
                "覆盖后，beanDefinitionMap 中应保存最新的 BeanDefinition");
        assertTrue(singletons.containsKey("bean4"),
                "当用 singleton 覆盖 prototype 时，应在 singletonBeanMap 中注册 bean4");
        assertEquals(Short.class, singletons.get("bean4"),
                "singletonBeanMap 中 bean4 的类型应更新为 Short.class");
    }

    @Test
    void overrideSingletonWithSingleton_updatesClass() throws Exception {
        // 初次注册 singleton
        SimpleBeanDefinition bd1 = new SimpleBeanDefinition();
        bd1.setBeanClassName("bean5");
        bd1.setBeanClass(Boolean.class);
        bd1.setScope(ScopeName.SINGLETON);
        factory.registerBeanDefinition("bean5", bd1);

        // 再用另一个 singleton 注册
        SimpleBeanDefinition bd2 = new SimpleBeanDefinition();
        bd2.setBeanClassName("bean5");
        bd2.setBeanClass(Byte.class);
        bd2.setScope(ScopeName.SINGLETON);
        factory.registerBeanDefinition("bean5", bd2);

        Map<String, BeanDefinition> beanDefs = getBeanDefMap();
        Map<String, Class<?>> singletons = getSingletonMap();

        assertSame(bd2, beanDefs.get("bean5"),
                "覆盖后，beanDefinitionMap 中应保存新的 BeanDefinition");
        assertEquals(Byte.class, singletons.get("bean5"),
                "singletonBeanMap 中 bean5 的类型应更新为 Byte.class");
    }

    @Test
    void getBeanDefinition_returnsRegisteredDefinition() {
        SimpleBeanDefinition bd = new SimpleBeanDefinition();
        bd.setBeanClassName("beanA");
        bd.setBeanClass(String.class);
        bd.setScope(ScopeName.SINGLETON);

        factory.registerBeanDefinition("beanA", bd);

        BeanDefinition retrieved = factory.getBeanDefinition("beanA");
        assertSame(bd, retrieved, "getBeanDefinition 应返回注册时的 BeanDefinition 实例");
    }

    @Test
    void removeBeanDefinition_removesDefinitionAndAffectsContainsAndCount() {
        SimpleBeanDefinition bd = new SimpleBeanDefinition();
        bd.setBeanClassName("beanB");
        bd.setBeanClass(Integer.class);
        bd.setScope(ScopeName.PROTOTYPE);

        factory.registerBeanDefinition("beanB", bd);
        assertTrue(factory.containsBeanDefinition("beanB"));
        assertEquals(1, factory.getBeanDefinitionCount());

        factory.removeBeanDefinition("beanB");
        assertFalse(factory.containsBeanDefinition("beanB"), "removeBeanDefinition 后 containsBeanDefinition 应为 false");
        assertEquals(0, factory.getBeanDefinitionCount(), "removeBeanDefinition 后 count 应减为 0");
    }

    @Test
    void containsBeanDefinition_behavesCorrectly() {
        assertFalse(factory.containsBeanDefinition("noBean"));
        SimpleBeanDefinition bd = new SimpleBeanDefinition();
        bd.setBeanClassName("beanC");
        bd.setBeanClass(Double.class);
        bd.setScope(ScopeName.SINGLETON);
        factory.registerBeanDefinition("beanC", bd);
        assertTrue(factory.containsBeanDefinition("beanC"));
    }

    @Test
    void getBeanDefinitionNames_and_getBeanDefinitionCount() {
        SimpleBeanDefinition bd1 = new SimpleBeanDefinition();
        bd1.setBeanClassName("bean1");
        bd1.setBeanClass(String.class);
        bd1.setScope(ScopeName.SINGLETON);
        SimpleBeanDefinition bd2 = new SimpleBeanDefinition();
        bd2.setBeanClassName("bean2");
        bd2.setBeanClass(Integer.class);
        bd2.setScope(ScopeName.PROTOTYPE);

        factory.registerBeanDefinition("bean1", bd1);
        factory.registerBeanDefinition("bean2", bd2);

        String[] names = factory.getBeanDefinitionNames();
        assertEquals(2, names.length, "应返回 2 个名称");
        assertTrue(
                (names[0].equals("bean1") && names[1].equals("bean2")) ||
                        (names[0].equals("bean2") && names[1].equals("bean1")),
                "返回的名称数组应包含 bean1 和 bean2"
        );
        assertEquals(2, factory.getBeanDefinitionCount(), "getBeanDefinitionCount 应返回 2");
    }

    @Test
    void getBean_byName_returnsBeanClassObject() {
        SimpleBeanDefinition bd = new SimpleBeanDefinition();
        bd.setBeanClassName("beanD");
        bd.setBeanClass(Character.class);
        bd.setScope(ScopeName.SINGLETON);
        factory.registerBeanDefinition("beanD", bd);

        Object bean = factory.getBean("beanD");
        assertTrue(bean instanceof Class<?>, "getBean 返回应为 Class 对象");
        assertEquals(Character.class, bean, "getBean 应返回注册时的 beanClass");
    }

    @Test
    void containsBean_sameAsContainsBeanDefinition() {
        assertFalse(factory.containsBean("xBean"));
        SimpleBeanDefinition bd = new SimpleBeanDefinition();
        bd.setBeanClassName("xBean");
        bd.setBeanClass(String.class);
        bd.setScope(ScopeName.SINGLETON);
        factory.registerBeanDefinition("xBean", bd);
        assertTrue(factory.containsBean("xBean"));
    }

    @Test
    void isSingleton_and_isPrototype_basedOnScope() {
        SimpleBeanDefinition singletonBd = new SimpleBeanDefinition();
        singletonBd.setBeanClassName("sBean");
        singletonBd.setBeanClass(String.class);
        singletonBd.setScope(ScopeName.SINGLETON);

        SimpleBeanDefinition protoBd = new SimpleBeanDefinition();
        protoBd.setBeanClassName("pBean");
        protoBd.setBeanClass(String.class);
        protoBd.setScope(ScopeName.PROTOTYPE);

        factory.registerBeanDefinition("sBean", singletonBd);
        factory.registerBeanDefinition("pBean", protoBd);

        assertTrue(factory.isSingleton("sBean"), "sBean 应被视为 singleton");
        assertFalse(factory.isPrototype("sBean"), "sBean 不应被视为 prototype");

        assertTrue(factory.isPrototype("pBean"), "pBean 应被视为 prototype");
        assertFalse(factory.isSingleton("pBean"), "pBean 不应被视为 singleton");
    }
}
