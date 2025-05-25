package com.emil.spring.core.metadata;

import com.email.spring.bean.annotation.Component;
import com.email.spring.core.metadata.AnnotationMetadata;
import com.email.spring.core.metadata.CachingMetadataReader;
import com.email.spring.core.metadata.SimpleCachingMetadataReader;
import org.junit.jupiter.api.Test;

import java.io.UncheckedIOException;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class SimpleCachingMetadataReaderTest {
    @Component
    static class DummyClass {}

    @Test
    void testGetMetadataReturnsAnnotationMetadata() {
        CachingMetadataReader factory = new SimpleCachingMetadataReader();
        String path = DummyClass.class.getName().replace('.', '/') + ".class";
        URL url = getClass().getClassLoader().getResource(path);
        assertNotNull(url, "无法解析 DummyClass 的资源 URL");

        AnnotationMetadata md = factory.getMetadata(url);
        assertTrue(md.hasAnnotation(Component.class.getName()));
    }

    @Test
    void testCachingMechanismReturnsSameInstance() {
        CachingMetadataReader reader = new SimpleCachingMetadataReader();
        String path = DummyClass.class.getName().replace('.', '/') + ".class";
        URL url = getClass().getClassLoader().getResource(path);
        assertNotNull(url);

        AnnotationMetadata first = reader.getMetadata(url);
        AnnotationMetadata second = reader.getMetadata(url);
        assertSame(first, second, "相同 URL 应返回缓存中的同一实例");
    }

    @Test
    void testGetMetadataWithInvalidUrlThrowsUncheckedIOException() throws Exception {
        CachingMetadataReader reader = new SimpleCachingMetadataReader();
        URL bad = new URL("file:///non-existent-file.class");

        assertThrows(UncheckedIOException.class, () -> reader.getMetadata(bad));
    }

    @Test
    void testGetClassNameReturnsCorrectQualifiedName() {
        // 准备
        SimpleCachingMetadataReader reader = new SimpleCachingMetadataReader();
        String expectedName = SimpleCachingMetadataReader.class.getName();
        String resourcePath = expectedName.replace('.', '/') + ".class";
        URL url = getClass().getClassLoader().getResource(resourcePath);

        // 前置断言：资源必须存在
        assertNotNull(url, "无法加载 SimpleCachingMetadataReader.class 对应的资源");

        // 执行
        String actualName = reader.getClassName(url);

        // 验证
        assertEquals(expectedName, actualName,
                "getClassName 应该返回正确的全限定类名");
    }

    @Test
    void testGetClassNameThrowsUncheckedIOExceptionForNonexistentUrl() throws Exception {
        // 准备一个指向不存在文件的 URL
        SimpleCachingMetadataReader reader = new SimpleCachingMetadataReader();
        URL badUrl = new URL("file:///non-existent-file.class");

        // 执行 & 验证：应该抛出 UncheckedIOException
        assertThrows(UncheckedIOException.class,
                () -> reader.getClassName(badUrl),
                "对于不存在的 URL，应抛出 UncheckedIOException");
    }

    @Test
    void testGetClassReturnsCorrectClass() {
        SimpleCachingMetadataReader reader = new SimpleCachingMetadataReader();

        // 准备：拿到 SimpleCachingMetadataReader 自身的 .class 资源 URL
        String resourcePath = SimpleCachingMetadataReader.class.getName()
                .replace('.', '/') + ".class";
        URL url = getClass().getClassLoader().getResource(resourcePath);
        assertNotNull(url, "无法加载 SimpleCachingMetadataReader.class 对应的资源");

        // 执行
        Class<?> loaded = reader.getClass(url);

        // 验证
        assertSame(SimpleCachingMetadataReader.class, loaded,
                "getClass 应返回与原类相同的 Class<?> 对象");
    }

    @Test
    void testGetClassThrowsRuntimeExceptionForUnknownClass() {
        // 使用匿名子类覆盖 getClassName，使其返回一个不存在的类名
        SimpleCachingMetadataReader reader = new SimpleCachingMetadataReader() {
            @Override
            public String getClassName(URL url) {
                return "com.nonexistent.FooBar";
            }
        };

        // URL 可以是任意存在的资源
        String resourcePath = SimpleCachingMetadataReader.class.getName()
                .replace('.', '/') + ".class";
        URL url = getClass().getClassLoader().getResource(resourcePath);
        assertNotNull(url);

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> reader.getClass(url),
                "对于不存在的类，应抛出 RuntimeException");
        assertTrue(ex.getMessage().contains("Failed to load class: com.nonexistent.FooBar"));
        assertInstanceOf(ClassNotFoundException.class, ex.getCause());
    }

    @Test
    void testGetClassThrowsUncheckedIOExceptionForInvalidUrl() throws Exception {
        SimpleCachingMetadataReader reader = new SimpleCachingMetadataReader();
        URL badUrl = new URL("file:///non-existent-file.class");

        // 打开流失败时，getClassName 会抛出 UncheckedIOException
        assertThrows(UncheckedIOException.class,
                () -> reader.getClass(badUrl),
                "对于不存在的 URL，应抛出 UncheckedIOException");
    }
}
