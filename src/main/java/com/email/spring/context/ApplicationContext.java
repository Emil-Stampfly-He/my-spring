package com.email.spring.context;

import com.email.spring.bean.factory.BeanFactory;
import com.email.spring.core.ResourcePatternResolver;

public interface ApplicationContext extends ResourcePatternResolver, BeanFactory {
    String getId();
}
