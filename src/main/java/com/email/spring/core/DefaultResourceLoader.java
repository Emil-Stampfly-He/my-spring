package com.email.spring.core;

public class DefaultResourceLoader implements ResourceLoader {

    private final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

    @Override
    public Resource getResource(String path) {
        if (path.isEmpty()) throw new NullPointerException("Location must not be null");

        // 1. path以 / 开头：返回ClassPathResource按照路径查找资源
        // 2. path以 classpath: 开头，返回ClassPathResource查找资源
        // 3. 都不是，抛出异常
        if (path.startsWith("/")) {
            return new ClassPathResource(path, classLoader);
        } else if (path.startsWith(ResourceLoader.CLASSPATH_URL_PREFIX)) {
            return new ClassPathResource(path.substring(ResourceLoader.CLASSPATH_URL_PREFIX.length()), classLoader);
        } else {
            throw new IllegalArgumentException("Invalid path: " + path);
        }
    }

    @Override
    public ClassLoader getClassLoader() {
        return this.classLoader;
    }
}
