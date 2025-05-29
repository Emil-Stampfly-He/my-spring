package com.email.spring.bean.factory.config;

import com.email.spring.bean.BeanDefinition;
import com.email.spring.bean.BeanDefinitionBuilder;
import com.email.spring.bean.ScopeName;
import com.email.spring.bean.annotation.Bean;
import com.email.spring.bean.annotation.Scope;
import com.email.spring.bean.factory.BeanDefinitionRegistry;
import com.email.spring.core.Resource;
import com.email.spring.core.metadata.AnnotationMetadata;
import com.email.spring.core.metadata.CachingMetadataReader;
import com.email.spring.core.metadata.SimpleCachingMetadataReader;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.Map;

public class ConfigurationClassPostProcessor implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanFactory) throws BeansException {
        try {
            CachingMetadataReader reader = new SimpleCachingMetadataReader();
            Resource[] allClassResources = resourceResolver.getResources(DEFAULT_PATH);
            for (Resource resource : allClassResources) {
                URL url = resource.getURL();
                AnnotationMetadata metadata = reader.getMetadata(url);

                if (metadata.hasAnnotation(ANNOTATION_COMPONENT_SCAN)) {
                    // com.emil.spring
                    String[] basePackages = metadata.getStringArray(ANNOTATION_COMPONENT_SCAN, "basePackages");
                    for (String basePackage : basePackages) {
                        // classpath:/com/emil/spring/**/*.class
                        String path = CLASSPATH_URL_PREFIX +
                                basePackage.replace(".", "/") +
                                ALL_CLASS_RESOURCES_SUFFIX;
                        Resource[] resources = resourceResolver.getResources(path);

                        for (Resource rs : resources) {
                            AnnotationMetadata mtd = reader.getMetadata(rs.getURL());
                            Class<?> clazz = reader.getClass(rs.getURL());
                            if (clazz.isAnnotation()) continue;
                            Map<String, Object> configurationAttributes = mtd.getAttributes(ANNOTATION_CONFIGURATION);

                            // TODO: 未来应当遍历Map中的所有属性，为所有属性创建单独的逻辑，以下只关注了@Configuration的name属性
                            String beanNameConfigurationPart = (String) configurationAttributes.get("name");

                            boolean hasConfiguration = mtd.hasAnnotation(ANNOTATION_CONFIGURATION);
                            if (hasConfiguration) {
                                Method[] allMethods = clazz.getDeclaredMethods(); // @Configuration类下所有的方法
                                for (Method method : allMethods) {
                                    Bean atBean = method.getAnnotation(Bean.class);
                                    if (atBean != null) {
                                        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition();

                                        Scope scope = method.getAnnotation(Scope.class);
                                        if (scope != null) builder.setScope(scope.value());
                                        else builder.setScope(ScopeName.SINGLETON); // 默认设置为单例

                                        String beanName = generateBeanNameWithConfiguration(method, beanNameConfigurationPart, atBean);
                                        Class<?> beanType = method.getReturnType();
                                        String initMethod = atBean.initMethod();
                                        String destroyMethod = atBean.destroyMethod();

                                        BeanDefinition beanDefinition = builder.setBeanClassName(beanName)
                                                .setBeanClass(beanType)
                                                .setInitMethod(initMethod)
                                                .setDestroyMethod(destroyMethod)
                                                .beanDefinition();
                                        beanFactory.registerBeanDefinition(beanName, beanDefinition);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String generateBeanNameWithConfiguration(Method method,
                                                            String beanNameConfigurationPart,
                                                            Bean atBean) {
        String beanName;
        if (beanNameConfigurationPart != null && !beanNameConfigurationPart.isEmpty()) {
            if (!atBean.value().isEmpty()) {
                beanName = beanNameConfigurationPart + "$" + atBean.value();
            } else {
                beanName = beanNameConfigurationPart + "$" + method.getName();
            }
        } else {
            if (!atBean.value().isEmpty()) {
                beanName = atBean.value();
            } else {
                beanName = method.getName();
            }
        }
        return beanName;
    }
}
