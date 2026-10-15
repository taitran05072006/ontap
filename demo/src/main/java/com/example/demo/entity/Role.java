package com.example.demo.entity;

import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;

public enum Role {
    USER,
    ADMIN;

    public String authorize() {
        return "ROLE_" + name();
    }

    public List<SimpleGrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority(authorize()));
    }
}
