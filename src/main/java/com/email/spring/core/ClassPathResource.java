package com.email.spring.core;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public record ClassPathResource(String path, ClassLoader classLoader) implements Resource {

    public ClassPathResource(String path, ClassLoader classLoader) {
        if (path == null || path.trim().isEmpty()) throw new IllegalArgumentException("Path must not be null or empty");
        this.path = path;
        this.classLoader = classLoader == null ? ClassLoader.getSystemClassLoader() : classLoader;
    }

    @Override
    public boolean exists() {
        return classLoader.getResource(this.path) != null;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        InputStream is = classLoader.getResourceAsStream(this.path);
        if (is == null) throw new FileNotFoundException(getDescription());
        return is;
    }

    @Override
    public URL getURL() throws IOException {
        URL url = classLoader.getResource(this.path);
        if (url == null) throw new FileNotFoundException(getDescription());
        return url;
    }

    @Override
    public String getDescription() {
        return "classpath:" + this.path;
    }
}
