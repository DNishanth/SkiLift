package edu.neu.madcourse.skilift.models;

public class Message {
    private String sender; // Username of message sender
    private String message; // File name of sticker
    private long timestamp;

    public Message(String sender, String message) {
        this.sender = sender;
        this.message = message;
        this.timestamp = System.currentTimeMillis();
    }

    // Used by Firebase to reconstruct Messages
    @SuppressWarnings("unused")
    public Message() {}

    public String getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }
}