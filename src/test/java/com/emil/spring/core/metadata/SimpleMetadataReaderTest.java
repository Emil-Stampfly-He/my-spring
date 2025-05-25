package com.emil.spring.core.metadata;

import com.email.spring.bean.annotation.Component;
import com.email.spring.bean.annotation.ComponentScan;
import com.email.spring.bean.annotation.Controller;
import com.email.spring.core.metadata.AnnotationMetadata;
import com.email.spring.core.metadata.SimpleMetadataReader;
import org.junit.jupiter.api.Test;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleMetadataReaderTest {
    @ComponentScan(basePackages = {"com.foo", "com.bar"})
    static class ConfigWithScan {}

    @Test
    void testReadMetadataFindsAnnotationAndAttributes() throws Exception {
        SimpleMetadataReader reader = new SimpleMetadataReader();
        String path = ConfigWithScan.class.getName().replace('.', '/') + ".class";

        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            assertNotNull(is, "无法加载测试类的 .class 资源");

            AnnotationMetadata md = reader.readMetadata(is);
            String annName = ComponentScan.class.getName();

            // 识别到注解本身
            assertTrue(md.hasAnnotation(annName));

            // 读取到 basePackages 数组
            String[] pkgs = md.getStringArray(annName, "basePackages");
            assertArrayEquals(new String[]{"com.foo", "com.bar"}, pkgs);
        }
    }

    @Test
    void testReadMetadataEmptyWhenNoAnnotation() throws Exception {
        // 一个不带任何注解的本地类
        class PlainClass {}

        String path = PlainClass.class.getName().replace('.', '/') + ".class";
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            assertNotNull(is);

            AnnotationMetadata md = new SimpleMetadataReader().readMetadata(is);
            // 完全没有 @ComponentScan
            assertFalse(md.hasAnnotation(ComponentScan.class.getName()));
            // 即使调用 getStringArray 也应返回空数组
            assertArrayEquals(new String[0],
                    md.getStringArray(ComponentScan.class.getName(), "basePackages"));
        }
    }

    @Controller
    static class ControllerClass {}

    @Test
    void testHasMetaAnnotationForDerivedController() throws Exception {
        String path = ControllerClass.class.getName()
                .replace('.', '/') + ".class";
        try (InputStream is = getClass()
                .getClassLoader()
                .getResourceAsStream(path)) {
            assertNotNull(is, "无法加载 ControllerClass.class");

            AnnotationMetadata md = new SimpleMetadataReader().readMetadata(is);

            // 首先它本身得被识别为 @Controller
            assertTrue(md.hasAnnotation(Controller.class.getName()));

            // 然后使用新方法检测 @Controller 的元注解 @Component
            assertTrue(
                    md.hasMetaAnnotation(Component.class.getName()),
                    "Controller 应该被识别为带有 @Component（元注解）"
            );
        }
    }
}
