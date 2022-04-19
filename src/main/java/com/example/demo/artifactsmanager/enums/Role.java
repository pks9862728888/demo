package com.example.demo.artifactsmanager.enums;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

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

    public String getRoleName() {
        return "ROLE_" + role;
    }

    /**
     * Returns true if user has edit permission
     */
    public static boolean hasEditPermission(Authentication authentication) {
        return authentication.getAuthorities()
                .contains(new SimpleGrantedAuthority(EDIT.getRoleName()));
    }
}
