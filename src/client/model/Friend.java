package client.model;

import kit.UserInfo;

import java.io.IOException;

public class Friend {
    private UserInfo userInfo;
    private FriendDialog friendDialog;

    public Friend(){}
    public Friend(UserInfo userInfo){
        this.userInfo = userInfo;
        User.getInstance().getManager().storeIcon(userInfo);
        this.userInfo.prepareUserCard();
    }

    public void init(FriendDialog dialog) throws IOException {
        friendDialog = dialog;
        if (friendDialog.getHasNewMessage().get()) userInfo.getUserCard().showCircle();
        friendDialog.setChatView();
        friendDialog.getHasNewMessage().addListener((obs, ov, nv)->{
            if (nv) userInfo.getUserCard().showCircle();
            else userInfo.getUserCard().hideCircle();
        });
    }

    public void setFriendDialog(FriendDialog friendDialog) {
        this.friendDialog = friendDialog;
    }
    public void setUserInfo(UserInfo userInfo) {
        this.userInfo = userInfo;
    }
    public UserInfo getUserInfo() {return userInfo; }
    public FriendDialog getFriendDialog() {return friendDialog; }
    public String getFriendName() { return userInfo.getName(); }

}
