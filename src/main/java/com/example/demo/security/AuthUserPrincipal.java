package com.example.demo.security;

import java.io.Serializable;

/**
 * Simple authenticated user representation stored as Security principal.
 */
public class AuthUserPrincipal implements Serializable {

    private final Long id;
    private final String email;
    private final String username;

    public AuthUserPrincipal(Long id, String email, String username) {
        this.id = id;
        this.email = email;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getUsername() {
        return username;
    }
}

