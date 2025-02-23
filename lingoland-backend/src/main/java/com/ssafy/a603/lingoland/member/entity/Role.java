package com.ssafy.a603.lingoland.member.entity;

import lombok.Getter;
import org.springframework.web.bind.annotation.GetMapping;

@Getter
public enum Role {
    ROLE_USER("ROLE_USER");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String value() {
        return role;
    }
}
