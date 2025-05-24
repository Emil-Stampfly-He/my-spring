package com.email.spring.core;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public interface Resource {
    boolean exists();
    InputStream getInputStream() throws IOException;
    URL getURL() throws IOException;
    String getDescription();
}
