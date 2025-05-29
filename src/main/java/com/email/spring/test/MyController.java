package com.email.spring.test;

import com.email.spring.bean.annotation.Autowired;
import com.email.spring.bean.annotation.Controller;

@Controller
public class MyController {
    @Autowired
    private Bean1 bean1;

    @Autowired
    private Bean2 bean2;

    @Autowired(required = false)
    private Bean4 bean4;

    public Bean1 getBean1() {return this.bean1;}
    public Bean2 getBean2() {return this.bean2;}
    public Bean4 getBean4() {return this.bean4;}
}
