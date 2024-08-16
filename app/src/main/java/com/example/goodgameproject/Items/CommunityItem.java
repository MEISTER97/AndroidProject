package com.example.goodgameproject.Items;

public class CommunityItem {
    private String groupName;
    private String userId;
    private final String groupId;


    public CommunityItem(String groupName, String userId,String groupId) {
        this.groupName = groupName;
        this.userId = userId;
        this.groupId= groupId;

    }

    public String getGroupName() {
        return groupName;
    }

    public CommunityItem setGroupName(String groupName) {
        this.groupName = groupName;
        return this;
    }

    public String getHostId() {
        return userId;
    }

    public CommunityItem setHostId(String hostId) {
        this.userId = hostId;
        return this;
    }

    public String getGroupId() {
        return groupId;
    }

}
