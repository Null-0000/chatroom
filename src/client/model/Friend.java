package client.model;

import kit.UserInfo;

import java.io.IOException;

public class Friend {
    private UserInfo userInfo;
    private FriDialog friDialog;

    public Friend(){}
    public Friend(UserInfo info){
        this.userInfo = info;
        User.getInstance().getManager().storeIcon(info);
        userInfo.prepareUserCard();
    }

    public void init(FriDialog dialog) throws IOException {
        friDialog = dialog;
        if (friDialog.getHasNewMessage().get()) getUserInfo().getUserCard().showCircle();
        friDialog.setChatView();
        friDialog.getHasNewMessage().addListener((obs, ov, nv)->{
            if (nv) userInfo.getUserCard().showCircle();
            else userInfo.getUserCard().hideCircle();
        });
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
