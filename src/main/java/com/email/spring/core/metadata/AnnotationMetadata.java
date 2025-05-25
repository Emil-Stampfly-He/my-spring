package com.email.spring.core.metadata;

import java.lang.annotation.Annotation;
import java.util.*;

public class AnnotationMetadata {

    // 原来只存注解全类名
    private final Set<String> annotations = new HashSet<>();

    // 新增：每个注解对应一个属性名 → 属性值
    private final Map<String, Map<String, Object>> attributes = new HashMap<>();

    public void addAnnotation(String annotation) {
        annotations.add(annotation);
        attributes.putIfAbsent(annotation, new LinkedHashMap<>());
    }

    public boolean hasAnnotation(String annotation) {
        return annotations.contains(annotation);
    }

    /**
     * 判断是否存在这样一个注解 A：A 本身被 metaAnnotationName （全类名）所标注
     * （支持递归查找：比如 @MyController 上又有 @Controller，@Controller 上又有 @Component 等）
     */
    public boolean hasMetaAnnotation(String metaAnnotationName) {
        for (String annClassName : annotations) {
            try {
                Class<?> annClass = Class.forName(annClassName);
                if (isMetaAnnotated(annClass, metaAnnotationName, new HashSet<>())) {
                    return true;
                }
            } catch (ClassNotFoundException e) {
                // 类路径上不存在该注解，跳过
            }
        }
        return false;
    }

    /**
     * 递归检测 annClass（一个注解类型）是否直接或间接地被 metaAnnotationName 标注
     */
    private boolean isMetaAnnotated(Class<?> annClass,
                                    String metaAnnotationName,
                                    Set<Class<?>> visited) {
        if (!visited.add(annClass)) {
            return false;  // 防止循环
        }
        for (Annotation meta : annClass.getAnnotations()) {
            Class<? extends Annotation> metaType = meta.annotationType();
            // 直接命中
            if (metaType.getName().equals(metaAnnotationName)) {
                return true;
            }
            // 递归往上找
            if (isMetaAnnotated(metaType, metaAnnotationName, visited)) {
                return true;
            }
        }
        return false;
    }

    public Set<String> getAnnotations() {
        return Collections.unmodifiableSet(annotations);
    }

    /**
     * 保存单个属性
     * name: 注解名全类名，attrName: 注解属性名，value: 属性值
     */
    public void addAttribute(String name, String attrName, Object value) {
        attributes.computeIfAbsent(name, k -> new LinkedHashMap<>())
                .put(attrName, value);
    }

    /**
     * 取出某个注解的所有属性（可能没配置过就空 map）
     */
    public Map<String, Object> getAttributes(String name) {
        return Collections.unmodifiableMap(
                attributes.getOrDefault(name, Collections.emptyMap())
        );
    }

    /**
     * 方本质是getAttributes，但是转换为方便常用的 String[] 形式
     */
    public String[] getStringArray(String name, String attrName) {
        Object val = getAttributes(name).get(attrName);
        if (val instanceof String[]) {
            return (String[]) val;
        }
        return new String[0];
    }
}

