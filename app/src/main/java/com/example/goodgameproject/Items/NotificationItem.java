package com.example.goodgameproject.Items;

public class NotificationItem {

    private String targetUser;
    private String targetId;

    public NotificationItem(String targetUser, String targetId) {
        this.targetUser = targetUser;
        this.targetId=targetId;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public NotificationItem setTargetUser(String targetUser) {
        this.targetUser = targetUser;
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
