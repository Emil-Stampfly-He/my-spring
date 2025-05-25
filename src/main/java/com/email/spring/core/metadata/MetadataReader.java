package com.email.spring.core.metadata;

import java.io.IOException;
import java.io.InputStream;

public interface MetadataReader {
    AnnotationMetadata readMetadata(InputStream classInput) throws IOException;
}
