package edu.neu.madcourse.skilift.models;

import java.util.ArrayList;

public class GroupMessage {
    private final String groupID;
    private final ArrayList<String> groupMembers;

    public GroupMessage(String groupID, ArrayList<String> groupMembers) {
        this.groupID = groupID;
        this.groupMembers = groupMembers;
    }
    
    public String getGroupID() {
        return groupID;
    }

    public ArrayList<String> getGroupMembers() {
        return groupMembers;
    }
}
