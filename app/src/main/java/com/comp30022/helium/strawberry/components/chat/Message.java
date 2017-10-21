package com.comp30022.helium.strawberry.components.chat;

import com.comp30022.helium.strawberry.entities.User;

/**
 * Created by shreyashpatodia on 15/09/17.
 */

/**
 * A message that the users might use to communicate with each other.
 */
public class Message {

    private String message;
    private User sender, receiver;
    private long createdAt;

    public Message(String message, User sender, User receiver, long createdAt) {
        this.message = message;
        this.sender = sender;
        this.receiver = receiver;
        this.createdAt = createdAt;
    }

    public String getMessage() {
        return message;
    }

    public User getSender() {
        return sender;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public User getReceiver() {
        return receiver;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Message) {
            Message other = (Message) obj;
            return (this.message.equals(other.message) && this.createdAt == other.createdAt &&
                    this.sender.equals(other.sender) &&
                    this.receiver.equals(other.receiver));
        }
        return false;
    }

    @Override
    public String toString() {
        return "Message{" +
                "message='" + message + '\'' +
                ", sender=" + sender +
                ", receiver=" + receiver +
                ", createdAt=" + createdAt +
                '}';
    }
}
