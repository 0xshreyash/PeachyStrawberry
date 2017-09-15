package com.comp30022.helium.strawberry.chat;

/**
 * Created by shreyashpatodia on 15/09/17.
 */

public class User {

    private String username;
    private long userId;

    public User(String username, long userId) {
        this.username = new String(username);
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getUserId() {
        return userId;
    }

    public void setUserId(long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        if (userId != user.userId) return false;
        return username != null ? username.equals(user.username) : user.username == null;

    }

    @Override
    public int hashCode() {
        int result = username != null ? username.hashCode() : 0;
        result = 31 * result + (int) (userId ^ (userId >>> 32));
        return result;
    }
}
