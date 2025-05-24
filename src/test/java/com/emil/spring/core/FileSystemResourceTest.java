package com.emil.spring.core;

import com.email.spring.core.FileSystemResource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.*;
import java.net.URL;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class FileSystemResourceTest {
    @TempDir
    static Path tempDir;

    @Test
    void testExistingFile() throws IOException {
        // Create a temporary file with known content
        File tempFile = tempDir.resolve("test.txt").toFile();
        String content = "Hello, MiniSpring!";
        try (FileOutputStream fos = new FileOutputStream(tempFile)) {
            fos.write(content.getBytes());
        }

        FileSystemResource resource = new FileSystemResource(tempFile.getAbsolutePath());

        // exists() should return true
        assertTrue(resource.exists(), "Resource should exist for a valid file path");

        // getDescription() should start with file: and contain the file path
        String desc = resource.getDescription();
        assertTrue(desc.startsWith("file:"), "Description should start with 'file:'");
        assertTrue(desc.contains(tempFile.getAbsolutePath()), "Description should include the file path");

        // getURL() should return a URL that ends with the file name
        URL url = resource.getURL();
        assertNotNull(url, "URL should not be null for existing file");
        String urlPath = url.getPath();
        assertTrue(urlPath.endsWith(tempFile.getName()), "URL path should end with the file name");

        // getInputStream() should return a valid stream with expected content
        try (InputStream is = resource.getInputStream()) {
            byte[] data = new byte[content.length()];
            int read = is.read(data);
            assertEquals(content.length(), read, "Should read the full content length");
            assertEquals(content, new String(data), "Stream content should match written content");
        }
    }

    @Test
    void testNonExistingFile() {
        // Construct a path that does not exist
        File nonExistent = tempDir.resolve("nonexistent.txt").toFile();
        FileSystemResource resource = new FileSystemResource(nonExistent.getAbsolutePath());

        // exists() should return false
        assertFalse(resource.exists(), "Resource should not exist for a non-existent file path");

        // getDescription() should still return a file description
        String desc = resource.getDescription();
        assertTrue(desc.startsWith("file:"), "Description should start with 'file:'");
        assertTrue(desc.contains(nonExistent.getAbsolutePath()), "Description should include the file path");

        // getInputStream() should throw FileNotFoundException
        assertThrows(FileNotFoundException.class, resource::getInputStream,
                "getInputStream() should throw FileNotFoundException for non-existent file");

        // getURL() should still return a URL, even if file doesn't exist
        try {
            URL url = resource.getURL();
            assertNotNull(url, "URL should not be null even for non-existent file");
            String urlPath = url.getPath();
            // URL path should end with the file name
            assertTrue(urlPath.endsWith(nonExistent.getName()), "URL path should end with the file name");
        } catch (IOException e) {
            fail("getURL() should not throw IOException for non-existent file");
        }
    }
}
