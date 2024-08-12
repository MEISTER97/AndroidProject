package com.example.goodgameproject;

public class MessageGroup {
    private String text;
    private String senderId;
    private String senderName;
    private long timestamp;

    public MessageGroup() {
    }

    public MessageGroup(String text, String senderId, long timestamp,String senderName) {
        this.text = text;
        this.senderId = senderId;
        this.timestamp = timestamp;
        this.senderName=senderName;
    }

    public String getText() {
        return text;
    }

    public MessageGroup setText(String text) {
        this.text = text;
        return this;
    }

    public String getSenderId() {
        return senderId;
    }

    public MessageGroup setSenderId(String senderId) {
        this.senderId = senderId;
        return this;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public MessageGroup setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public String getSenderName() {
        return senderName;
    }

    public MessageGroup setSenderName(String senderName) {
        this.senderName = senderName;
        return this;
    }
}
