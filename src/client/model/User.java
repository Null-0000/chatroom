package client.model;

import client.controller.Connector;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import kit.*;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class User {
    private UserInfo userInfo;
    private SimpleMapProperty<Integer, Friend> friends;
    private SimpleMapProperty<Integer, Group> groups;

    private DialogManager manager;
    private Socket mySocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private static User instance = new User();

    public static User getInstance() {
        return instance;
    }

    public void setField(Data u) {
        this.userInfo = new UserInfo(u.ID, u.name, u.signature, u.iconBytes);
        manager = new DialogManager(u.ID);
        this.friends = new SimpleMapProperty<>(FXCollections.observableHashMap());

        System.out.println("This is " + u.name);

        for (UserInfo userInfo : (List<UserInfo>)u.listA) {
            Friend friend = new Friend(userInfo);
            friends.putIfAbsent(userInfo.getID(), friend);
        }
        this.groups = new SimpleMapProperty<>(FXCollections.observableHashMap());
        for (GroupInfo groupInfo : (List<GroupInfo>)u.listB){
            Group group = new Group(groupInfo);
            groups.putIfAbsent(groupInfo.getID(), group);
        }

    }

    public void initialise() throws Exception {
        manager.initFriendsDialog(friends);
        manager.initGroupsDialog(groups);

        loadRemoteData();

        this.mySocket = Connector.getInstance().connectToRemote();
        this.inputStream = mySocket.getInputStream();

        receiveMessages();
    }

    public void loadRemoteData() throws Exception {
        ArrayList<Message> messages =
                Connector.getInstance().loadMessage(Data.LOAD_MESSAGE);
        for (Message message : messages) {
            if (message.isMass)
                groups.get(message.receiver.getID()).getGroupDialog().updateMessage(message);
            else
                friends.get(message.sender.getID()).getFriendDialog().updateMessage(message);
        }

    }

    public void addFriend(Friend friend) {
        friends.put(friend.getUserInfo().getID(), friend);

        //此处为主界面更新好友列表
    }

    public void addGroup(Group group) {
        groups.put(group.getGroupInfo().getID(), group);
    }

    public DialogManager getManager() {
        return manager;
    }

    public String getName() {
        return userInfo.getName();
    }

    public byte[] getMyIconBytes() {
        return userInfo.getIcon();
    }

    public String getSignature() {
        return userInfo.getSig();
    }

    public int getID() {
        return userInfo.getID();
    }

    public FriendDialog getDialogueFrom(int friendID) {
        return friends.get(friendID).getFriendDialog();
    }

    public Collection<Friend> getFriendList() {
        return friends.values();
    }

    public Collection<Integer> getFriendIDs() {
        return friends.keySet();
    }

    public SimpleMapProperty<Integer, Friend> getFriends() {
        return friends;
    }

    public SimpleMapProperty<Integer, Group> getGroups() {return groups;}

    public void sendMessage(Message message) throws Exception {
        Info receiver = message.receiver;
        Data data = new Data(message);
        data.setOperateType(Data.SEND_MESSAGE);
        if (message.isMass) {
            groups.get(receiver.getID()).getGroupDialog().updateMessage(message);
        }
        else {
            friends.get(receiver.getID()).getFriendDialog().updateMessage(message);
        }
        IODealer.send(mySocket, data, false);
    }

    private void receiveMessages() {
        ConnThread connThread = new ConnThread(friends, groups, mySocket);
        connThread.start();
    }

    public void exit() throws Exception {
        Data data = new Data();
        data.setOperateType("exit");

        IODealer.send(mySocket, data, false);

        /**登出时储存文件*/
        manager.updateMyDialogues(friends, groups);
    }

    public UserInfo getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserInfo userInfo) { this.userInfo = userInfo;}
}