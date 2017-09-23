package com.comp30022.helium.strawberry.entities;


public class User {
    private final String id, username;

    public User(String id, String username) {
        this.id = id;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    public String getId() {
        return id;
    }
}
