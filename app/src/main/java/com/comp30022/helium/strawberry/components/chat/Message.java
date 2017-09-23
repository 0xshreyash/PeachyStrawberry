package com.comp30022.helium.strawberry.components.chat;

import com.comp30022.helium.strawberry.entities.User;

/**
 * Created by shreyashpatodia on 15/09/17.
 */

public class Message {

    private String message;
    private User sender;
    private long createdAt;

    public Message(String message, User sender, long createdAt) {
        this.message = message;
        this.sender = sender;
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }
}
