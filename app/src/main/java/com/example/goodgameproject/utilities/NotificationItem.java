package com.example.goodgameproject.utilities;

public class NotificationItem {

    private String targetUser;
    private int image;
    private String targetId;

    public NotificationItem(String targetUser, int image,String targetId) {
        this.targetUser = targetUser;
        this.image = image;
        this.targetId=targetId;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public NotificationItem setTargetUser(String targetUser) {
        this.targetUser = targetUser;
        return this;
    }

    public int getImage() {
        return image;
    }

    public NotificationItem setImage(int image) {
        this.image = image;
        return this;
    }

    public String getTargetId() {
        return targetId;
    }

    public NotificationItem setTargetId(String targetId) {
        this.targetId = targetId;
        return this;
    }
}
