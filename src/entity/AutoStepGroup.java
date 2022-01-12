package entity;

import java.util.ArrayList;

public class AutoStepGroup {
    private String groupName;
    private ArrayList<AutoStep> steps = new ArrayList<>();

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }
}
