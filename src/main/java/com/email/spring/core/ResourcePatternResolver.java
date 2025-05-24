package com.email.spring.core;

import java.io.IOException;

public interface ResourcePatternResolver extends ResourceLoader {
    Resource[] getResources(String locationPattern) throws IOException;
}
