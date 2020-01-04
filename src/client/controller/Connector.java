package client.controller;

import client.model.FriDialog;
import client.model.Friend;
import kit.*;
import client.model.User;

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

        if(receive.ID == -1) return false;
        else{
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
        data.oprInfo = User.getInstance().getUserInfo();

        IODealer.send(socket, data, false);

        Data receive = IODealer.receive(socket, false);

        if(receive.ID == -1) return false;

        UserInfo info1 = new UserInfo(receive.ID, receive.name, receive.signature, receive.myIconBytes);
        Friend friend = new Friend(info1);
        User.getInstance().addFriend(friend);
        FriDialog dialog = new FriDialog(friend.getFriendName(), User.getInstance().getName());
        friend.init(dialog);
        dialog.synchronizeMessage();
        return true;
    }

    public Socket connectToRemote() throws Exception {
        Socket socket = new Socket(HOST, PORT);
        Data data = new Data(User.getInstance().getName(), User.getInstance().getID());
        data.setOperateType("connect");

        IODealer.send(socket, data, false);

        return socket;
    }
    public ArrayList<Message> loadDialogueData() throws Exception {
        Socket socket = new Socket(HOST, PORT);

        Data data = new Data(User.getInstance().getName(), User.getInstance().getID());
        data.setOperateType("loadDialogueData");

        IODealer.send(socket, data, false);

        Data receive = IODealer.receive(socket, false);

        return receive.messages;
    }
}