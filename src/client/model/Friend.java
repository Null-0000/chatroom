package client.model;

import kit.UserInfo;

public class Friend {
    private UserInfo userInfo;
    private FriDialog friDialog;

    public Friend(){}
    public Friend(FriDialog friDialog, UserInfo userInfo){
        this.friDialog = friDialog;
        this.userInfo = userInfo;
    }

    public void setFriDialog(FriDialog friDialog) {
        this.friDialog = friDialog;
    }
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    public UserInfo getUserInfo() {return userInfo; }
    public FriDialog getFriDialog() {return friDialog; }
    public String getFriendName() { return userInfo.getName(); }

}
