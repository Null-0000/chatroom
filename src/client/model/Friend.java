package client.model;

import kit.UserInfo;

public class Friend {
    private UserInfo userInfo;
    private Dialogue dialogue;

    public Friend(){}
    public Friend(Dialogue dialogue, UserInfo userInfo){
        this.dialogue = dialogue;
        this.userInfo = userInfo;
    }

    public void setDialogue(Dialogue dialogue) {
        this.dialogue = dialogue;
    }
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    public UserInfo getUserInfo() {return userInfo; }
    public Dialogue getDialogue() {return dialogue; }
    public String getFriendName() { return userInfo.getName(); }

}
