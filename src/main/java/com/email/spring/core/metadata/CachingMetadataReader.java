package com.email.spring.core.metadata;

import java.net.URL;

public interface CachingMetadataReader {
    AnnotationMetadata getMetadata(URL url);
    String getClassName(URL url);
    Class<?> getClass(URL url);
}
