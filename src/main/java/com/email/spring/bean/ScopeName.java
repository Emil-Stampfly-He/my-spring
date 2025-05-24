package com.email.spring.bean;

public enum ScopeName {
    SINGLETON("singleton"), PROTOTYPE("prototype");

    private final String name;
    ScopeName(String name) { this.name = name; }
    public String getString() { return name; }
}
