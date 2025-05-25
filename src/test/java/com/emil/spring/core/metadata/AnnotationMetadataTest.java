package com.emil.spring.core.metadata;

import com.email.spring.core.metadata.AnnotationMetadata;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class AnnotationMetadataTest {
    @Test
    void testAddAndHasAnnotation() {
        AnnotationMetadata metadata = new AnnotationMetadata();
        assertFalse(metadata.hasAnnotation("foo.Bar"));

        metadata.addAnnotation("com.emil.spring.bean.annotation.Component");
        assertTrue(metadata.hasAnnotation("com.emil.spring.bean.annotation.Component"));

        // 原来的注解集合行为不变
        assertEquals(1, metadata.getAnnotations().size());
    }

    @Test
    void testAttributesStorageAndRetrieval() {
        AnnotationMetadata metadata = new AnnotationMetadata();
        String ann = "com.emil.spring.bean.annotation.ComponentScan";

        // 先注册注解
        metadata.addAnnotation(ann);

        // 存单值属性
        metadata.addAttribute(ann, "value", "abc");
        Map<String, Object> attrs = metadata.getAttributes(ann);
        assertEquals("abc", attrs.get("value"));

        // 存数组属性
        String[] arr = new String[]{"com.foo", "com.bar"};
        metadata.addAttribute(ann, "basePackages", arr);
        // 直接拿到 Object 数组
        assertArrayEquals(arr, (String[]) metadata.getAttributes(ann).get("basePackages"));
        // 也可以用 helper 方法拿 String[]
        assertArrayEquals(arr, metadata.getStringArray(ann, "basePackages"));

        // 对不存在的属性名，getStringArray 应返回空数组
        assertArrayEquals(new String[0], metadata.getStringArray(ann, "missingProp"));

        // attributes map 应该是不可修改的
        Map<String, Object> unmod = metadata.getAttributes(ann);
        assertThrows(UnsupportedOperationException.class, () -> unmod.put("x", "y"));
    }

    @Test
    void testGetAttributesForUnknownAnnotation() {
        AnnotationMetadata metadata = new AnnotationMetadata();
        // 未 addAnnotation 时，拿到的属性 map 也是空且不可改
        Map<String, Object> attrs = metadata.getAttributes("no.such.Annotation");
        assertTrue(attrs.isEmpty());
        assertThrows(UnsupportedOperationException.class, () -> attrs.put("a", 1));
        // helper 方法也要返回空
        assertArrayEquals(new String[0], metadata.getStringArray("no.such.Annotation", "any"));
    }
}
