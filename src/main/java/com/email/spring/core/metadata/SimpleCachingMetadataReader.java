package com.email.spring.core.metadata;

import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class SimpleCachingMetadataReader implements CachingMetadataReader {

    private final SimpleMetadataReader reader = new SimpleMetadataReader();
    private final ConcurrentMap<URL, AnnotationMetadata> cache = new ConcurrentHashMap<>();

    public AnnotationMetadata getMetadata(URL url) {
        return this.cache.computeIfAbsent(url, u -> {
            try (InputStream is = u.openStream()) {
                return this.reader.readMetadata(is);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        });
    }

    public String getClassName(URL url) {
        return this.reader.getClassName(url);
    }

    public Class<?> getClass(URL url) {
        String className = getClassName(url);
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Failed to load class: " + className, e);
        }
    }
}
