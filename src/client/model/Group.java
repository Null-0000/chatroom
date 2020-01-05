package client.model;

import client.view.GroupChatView;
import kit.GroupInfo;
import kit.UserInfo;

import java.io.IOException;

public class Group {
    private GroupInfo groupInfo;
    private GroupDialog groupDialog;

    public Group(GroupInfo info){
        this.groupInfo = info;
        User.getInstance().getManager().storeIcon(groupInfo);
        groupInfo.prepareGroupCard();
    }

    public void init(GroupDialog dialog) throws IOException {
        groupDialog = dialog;
        if (groupDialog.getHasNewMessage().get()) groupInfo.getGroupCard().showCircle();
        groupDialog.setChatView();
        groupDialog.getHasNewMessage().addListener((obs, ov, nv)->{
            if (nv) groupInfo.getGroupCard().showCircle();
            else groupInfo.getGroupCard().hideCircle();
        });
    }

    public String getGroupName() {
        return groupInfo.getName();
    }

    public GroupInfo getGroupInfo() {return groupInfo;}

    public GroupDialog getGroupDialog() {return groupDialog;}

    public void addMember(UserInfo info) {
        groupInfo.getMembers().add(info);
        ((GroupChatView) groupDialog.getChatView()).getMembersView().getItems().add(info.getName());
    }
}
