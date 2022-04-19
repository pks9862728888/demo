package com.example.demo.artifactsmanager.enums;

public enum Role {
    EDIT("EDIT"),
    VIEW("VIEW");

    private final String role;

    Role(String role) {
        this.role = role;
    }

    public String getRole() {
        return role;
    }
}
