package com.example.goodgameproject.Items;

public class FindFriendChatItem {
    private String targetUser;
    private int image;
    private String targetId;

    public FindFriendChatItem(String targetUser, int image, String targetId) {
        this.targetUser = targetUser;
        this.image = image;
        this.targetId = targetId;
    }

    public String getTargetUser() {
        return targetUser;
    }

    public FindFriendChatItem setTargetUser(String targetUser) {
        this.targetUser = targetUser;
        return this;
    }

    public int getImage() {
        return image;
    }

    public FindFriendChatItem setImage(int image) {
        this.image = image;
        return this;
    }

    public String getTargetId() {
        return targetId;
    }

    public FindFriendChatItem setTargetId(String targetId) {
        this.targetId = targetId;
        return this;
    }
}
