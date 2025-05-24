package com.emil.spring.core;

import com.email.spring.core.PatternMatchingResourcePatternResolver;
import com.email.spring.core.Resource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class PatternMatchingResourcePatternResolverTest {
    @Test
    void testGetResourcesFallbackClasspathPrefix() throws IOException {
        PatternMatchingResourcePatternResolver resolver = new PatternMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(
                "classpath:com/email/spring/core/PatternMatchingResourcePatternResolver.class"
        );
        assertEquals(1, resources.length, "应当只返回一个资源");
        Resource r = resources[0];
        assertTrue(r.exists(), "资源应该存在");
        URL url = r.getURL();
        assertTrue(url.toString().endsWith("PatternMatchingResourcePatternResolver.class"),
                "URL 应以类名结尾");
    }

    @Test
    void testGetResourcesInvalidPath() {
        PatternMatchingResourcePatternResolver resolver = new PatternMatchingResourcePatternResolver();
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class,
                () -> resolver.getResources("file:some/unsupported/path"));
        assertTrue(ex.getMessage().contains("Invalid path"),
                "不支持的前缀应当抛出 IllegalArgumentException");
    }

    @Test
    void testScanClassPathPattern() throws IOException {
        PatternMatchingResourcePatternResolver resolver = new PatternMatchingResourcePatternResolver();
        Resource[] resources = resolver.getResources(
                "classpath*:com/email/spring/**/*.class"
        );
        // 至少能扫描到自己这个类
        assertTrue(resources.length > 0, "应当扫描到至少一个 .class 文件");
        boolean found = Arrays.stream(resources)
                .anyMatch(r -> {
                    try {
                        return r.getURL().toString()
                                .endsWith("PatternMatchingResourcePatternResolver.class");
                    } catch (IOException e) {
                        return false;
                    }
                });
        assertTrue(found, "扫描结果应包含 PatternMatchingResourcePatternResolver.class");
    }
}
