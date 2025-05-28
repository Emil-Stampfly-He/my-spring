package com.email.spring.test;

import com.email.spring.bean.annotation.Autowired;
import com.email.spring.bean.annotation.Controller;

@Controller
public class MyController {
    @Autowired
    private Bean1 bean1;

    public Bean1 getBean1() {return this.bean1;}
}
