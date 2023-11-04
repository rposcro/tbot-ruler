package com.tbot.ruler.auth;

import java.util.Set;

public class User {
    
    private Long id;
    private String username;
    private Set<Role> roles;
    
    public User(Long id, String username, Set<Role> roles) {
        this.id = id;
        this.username = username;
        this.roles = roles;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Set<Role> getRoles() {
        return roles;
    }
    
}
