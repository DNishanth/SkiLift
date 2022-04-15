package edu.neu.madcourse.skilift.models;

import java.sql.Timestamp;

public class Message {
    private final String sender; // Username of message sender
    private final String message; // File name of sticker
    private final String timestamp;

    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.timestamp = new Timestamp(System.currentTimeMillis()).toString();
    }

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public String getTimestamp() {
        return timestamp;
    }
}