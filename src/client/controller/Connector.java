package client.controller;

import client.model.*;
import kit.*;

import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;

public class Connector {
    private final String HOST = "127.0.0.1";
    private final int PORT = 5432;

    private static Connector instance = new Connector();

    public static Connector getInstance() {
        return instance;
    }

    public boolean loadUserInfo(int ID, String password) throws Exception {
        Socket socket = new Socket(HOST, PORT);

        Data data = new Data(ID, password);
        data.setOperateType("loadUserInfo");

        IODealer.send(socket, data, false);

        Data receive = IODealer.receive(socket, false);

        if (receive.ID == -1) return false;
        else {
            User.getInstance().setField(receive);
            return true;
        }
    }

    public int register(Data data) throws IOException {
        Socket socket = new Socket(HOST, PORT);

        data.setOperateType("register");

        IODealer.send(socket, data, false);

        Data receiveData = IODealer.receive(socket, false);

        return receiveData.ID;
    }

    public boolean makeFriendWith(String info) throws Exception {
        Socket socket = new Socket(HOST, PORT);

        Data data = new Data(info);
        data.setOperateType("makeFriendWith");
        data.operator = User.getInstance().getName();
        data.oprUserInfo = User.getInstance().getUserInfo();

        IODealer.send(socket, data, false);

        Data receive = IODealer.receive(socket, false);

        if (receive.ID == -1) return false;

        UserInfo userInfo1 = new UserInfo(receive.ID, receive.name, receive.signature, receive.iconBytes);
        Friend friend = new Friend(userInfo1);
        User.getInstance().addFriend(friend);
        FriendDialog dialog = new FriendDialog(friend.getUserInfo().getID()
                , User.getInstance().getName());
        friend.init(dialog);
        dialog.synchronizeMessage();
        return true;
    }

    public boolean createGroup(String groupName, byte[] bytes) throws IOException {
        Socket socket = new Socket(HOST, PORT);

        Data data = new Data();
        data.name = groupName;
        data.iconBytes = bytes;
        data.setOperator();
        data.setOperateType(Data.CREATE_GROUP);
        IODealer.send(socket, data, false);

        Data receiveData = IODealer.receive(socket, false);
        if (receiveData.ID == -1) return false;
        ArrayList<UserInfo> members = new ArrayList<>();
        members.add(User.getInstance().getUserInfo());
        GroupInfo groupInfo = new GroupInfo(receiveData.ID, groupName, bytes, members, 0);
        //服务器将新的group封装成一个group_info发回
        setGroup(groupInfo);
        return true;
    }

    public boolean enterGroup(String info) throws IOException {
        Socket socket = new Socket(HOST, PORT);

        //暂时只能按群名加群
        Data data = new Data();
        data.name = info;
        data.ID = -1;
        data.setOperator();
        data.setOperateType(Data.JOIN_GROUP);
        IODealer.send(socket, data, false);

        Data recvData = IODealer.receive(socket, false);
        if (recvData.ID == -1) return false;
        int groupOwner = -1;
        ArrayList<UserInfo> members = (ArrayList<UserInfo>) recvData.listA;
        String builder = recvData.builder;
        while (!members.get(++groupOwner).getName().equals(builder));
        GroupInfo groupInfo = new GroupInfo(recvData.ID, recvData.name, recvData.iconBytes, members, groupOwner);
        setGroup(groupInfo);
        return true;
    }

    private void setGroup(GroupInfo groupInfo) throws IOException {
        Group group = new Group(groupInfo);
        GroupDialog dialog = new GroupDialog(User.getInstance().getName(),
                groupInfo.getID(), groupInfo.getMembers());
        group.init(dialog);
        dialog.synchronizeMessage();
        User.getInstance().addGroup(group);
    }

    public Socket connectToRemote() throws Exception {
        Socket socket = new Socket(HOST, PORT);
        Data data = new Data(User.getInstance().getName(), User.getInstance().getID());
        data.setOperateType("connect");

        IODealer.send(socket, data, false);

        return socket;
    }

    public ArrayList<Message> loadMessage(String type) throws Exception {
        Socket socket = new Socket(HOST, PORT);

        Data data = new Data();
        data.setOperator();
        data.setOperateType(type);

        IODealer.send(socket, data, false);

        Data receive = IODealer.receive(socket, false);

        return (ArrayList<Message>)receive.listA;
    }

}