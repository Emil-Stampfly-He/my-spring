package com.emil.spring.core;

import com.email.spring.core.ClassPathResource;
import com.email.spring.core.DefaultResourceLoader;
import com.email.spring.core.Resource;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class DefaultResourceLoaderTest {
    private final DefaultResourceLoader loader = new DefaultResourceLoader();

    @Test
    void testEmptyPath() {
        // Empty path should throw NullPointerException as per implementation
        assertThrows(NullPointerException.class, () -> loader.getResource("") );
    }

    @Test
    void testInvalidPath() {
        // Path without leading slash or classpath: should throw IllegalArgumentException
        assertThrows(IllegalArgumentException.class, () -> loader.getResource("invalid/path/resource.txt") );
    }

    @Test
    void testLeadingSlashResource() {
        String path = "/java/lang/String.class";
        Resource resource = loader.getResource(path);
        assertTrue(resource instanceof ClassPathResource, "Expected a ClassPathResource");
        ClassPathResource cpRes = (ClassPathResource) resource;
        // path() accessor from record
        assertEquals(path, cpRes.path(), "Resource path should be unchanged");
        // Leading slash means classLoader.getResource("/...") returns null, so exists() false
        assertFalse(cpRes.exists(), "Resource.exists() should be false for leading slash");
    }

    @Test
    void testClasspathPrefixResource() throws IOException {
        String path = "classpath:java/lang/String.class";
        Resource resource = loader.getResource(path);
        assertInstanceOf(ClassPathResource.class, resource, "Expected a ClassPathResource");
        ClassPathResource cpRes = (ClassPathResource) resource;
        // Should strip prefix
        assertEquals("java/lang/String.class", cpRes.path(), "path() should strip 'classpath:' prefix");
        // Resource should exist for a core class
        assertTrue(cpRes.exists(), "Resource.exists() should be true for existing classpath resource");

        // getURL() should return non-null URL ending with the class path
        URL url = cpRes.getURL();
        assertNotNull(url, "URL should not be null");
        String urlPath = url.getPath().replace("%20", " ");
        assertTrue(urlPath.endsWith("java/lang/String.class"), "URL path should end with the resource path");

        // getInputStream() should return a valid stream
        try (InputStream is = cpRes.getInputStream()) {
            assertNotNull(is, "InputStream should not be null");
            assertTrue(is.read() != -1, "InputStream should contain data");
        }
    }

    @Test
    void testGetClassLoader() {
        // Should return the context class loader
        assertSame(Thread.currentThread().getContextClassLoader(), loader.getClassLoader());
    }
}
