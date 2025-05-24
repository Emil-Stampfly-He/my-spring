package com.email.spring.core;

import java.io.*;
import java.net.URL;

public class FileSystemResource implements Resource {

    private final File file;

    public FileSystemResource(String path) {
        this.file = new File(path);
    }

    @Override
    public boolean exists() {
        return file.exists();
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!exists()) throw new FileNotFoundException(getDescription());
        return new FileInputStream(file);
    }

    @Override
    public URL getURL() throws IOException {
        return file.toURI().toURL();
    }

    @Override
    public String getDescription() {
        return "file:" + file.getAbsolutePath();
    }
}
