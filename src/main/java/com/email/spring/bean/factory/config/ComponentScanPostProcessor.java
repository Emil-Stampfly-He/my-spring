package com.email.spring.bean.factory.config;

import com.email.spring.bean.BeanDefinition;
import com.email.spring.bean.BeanDefinitionBuilder;
import com.email.spring.bean.ScopeName;
import com.email.spring.bean.factory.BeanDefinitionRegistry;
import com.email.spring.core.PatternMatchingResourcePatternResolver;
import com.email.spring.core.Resource;
import com.email.spring.core.metadata.AnnotationMetadata;
import com.email.spring.core.metadata.CachingMetadataReader;
import com.email.spring.core.metadata.SimpleCachingMetadataReader;

import java.io.IOException;
import java.net.URL;

public class ComponentScanPostProcessor implements BeanFactoryPostProcessor {

    private static final String DEFAULT_PATH = "classpath*:com/email/spring/**/*.class";
    private static final String CLASSPATH_URL_PREFIX = "classpath*:";
    private static final String ALL_CLASS_RESOURCES_SUFFIX = "/**/*.class";
    private static final String ANNOTATION_COMPONENT_SCAN = "com.email.spring.bean.annotation.ComponentScan";
    private static final String ANNOTATION_COMPONENT = "com.email.spring.bean.annotation.Component";

    private final PatternMatchingResourcePatternResolver resourceResolver = new PatternMatchingResourcePatternResolver();

    @Override
    public void postProcessBeanDefinitionRegistry(BeanDefinitionRegistry beanFactory) throws BeansException {
        try {
            CachingMetadataReader reader = new SimpleCachingMetadataReader();
            Resource[] allClassResources = this.resourceResolver.getResources(DEFAULT_PATH);
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
                        Resource[] resources = this.resourceResolver.getResources(path);

                        for (Resource rs : resources) {
                            AnnotationMetadata mtd = reader.getMetadata(rs.getURL());

                            String className = reader.getClassName(rs.getURL());
                            Class<?> clazz = reader.getClass(rs.getURL());
                            if (clazz.isAnnotation()) continue;

                            boolean hasComponent = mtd.hasAnnotation(ANNOTATION_COMPONENT);
                            boolean hasComponentDerivative = mtd.hasMetaAnnotation(ANNOTATION_COMPONENT);

                            if (hasComponent || hasComponentDerivative) {
                                BeanDefinition beanDefinition = BeanDefinitionBuilder
                                        .genericBeanDefinition(clazz, className)
                                        .setScope(ScopeName.SINGLETON) // 使用@Component及其衍生注解都是单例的
                                        .beanDefinition();
                                String beanName = generateBeanName(beanDefinition, beanFactory);
                                beanFactory.registerBeanDefinition(beanName, beanDefinition);
                            }
                        }
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static String generateBeanName(BeanDefinition beanDefinition, BeanDefinitionRegistry beanFactory) {
        String[] split = beanDefinition.getBeanClassName().split("\\.");
        String trueBeanName = firstLetterToLowerCase(split[split.length - 1]);
        String[] beanFactoryStringSplit = beanFactory.toString().split("\\.");
        return trueBeanName + "$" + beanFactoryStringSplit[beanFactoryStringSplit.length - 1];
    }

    private static String firstLetterToLowerCase(String str) {
        char[] charArray = str.toCharArray();
        charArray[0] = Character.toLowerCase(charArray[0]);
        return new String(charArray);
    }
}
