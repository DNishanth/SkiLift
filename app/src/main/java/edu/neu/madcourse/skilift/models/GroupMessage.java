package edu.neu.madcourse.skilift.models;

public class GroupMessage {
    private final String groupID;
    private final String groupMembers;

    public GroupMessage(String groupID, String groupMembers) {
        this.groupID = groupID;
        this.groupMembers = groupMembers;
    }
    
    public String getGroupID() {
        return groupID;
    }

    public String getGroupMembers() {
        return groupMembers;
    }
}
