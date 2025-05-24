package com.emil.spring.core;

import com.email.spring.core.ClassPathResource;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;

public class ClassPathResourceTest {
    private static ClassLoader classLoader;

    @BeforeAll
    static void setup() {
        classLoader = Thread.currentThread().getContextClassLoader();
    }

    @Test
    void testExistingResource() throws IOException {
        // Use a core JDK class file as a guaranteed existing resource
        String path = "java/lang/String.class";
        ClassPathResource resource = new ClassPathResource(path, classLoader);

        // exists() should return true
        assertTrue(resource.exists(), "Resource should exist on the classpath");

        // getDescription()
        assertEquals("classpath:" + path, resource.getDescription());

        // getURL() should return a non-null URL that ends with the path
        URL url = resource.getURL();
        assertNotNull(url, "URL should not be null");
        String urlPath = url.getPath().replace("%20", " ");
        assertTrue(urlPath.endsWith(path), "URL path should end with the resource path");

        // getInputStream() should return a non-null stream and be able to read
        try (InputStream is = resource.getInputStream()) {
            assertNotNull(is, "InputStream should not be null");
            // Read first byte to ensure stream is valid
            int first = is.read();
            assertTrue(first != -1, "InputStream should have data");
        }
    }

    @Test
    void testNonExistingResource() {
        String path = "non/existent/Resource.txt";
        ClassPathResource resource = new ClassPathResource(path, classLoader);

        // exists() should return false
        assertFalse(resource.exists(), "Resource should not exist on the classpath");

        // getURL() should throw FileNotFoundException
        assertThrows(IOException.class, resource::getURL,
                "getURL should throw IOException for non-existing resource");

        // getInputStream() should throw FileNotFoundException
        assertThrows(IOException.class, resource::getInputStream,
                "getInputStream should throw IOException for non-existing resource");
    }

    @Test
    void testNullOrEmptyPath() {
        // Null path
        assertThrows(IllegalArgumentException.class,
                () -> new ClassPathResource(null, classLoader),
                "Constructor should throw IllegalArgumentException for null path");

        // Empty path
        assertThrows(IllegalArgumentException.class,
                () -> new ClassPathResource("  ", classLoader),
                "Constructor should throw IllegalArgumentException for empty path");
    }
}
