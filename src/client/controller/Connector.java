package client.controller;

import kit.IODealer;
import kit.Message;
import kit.ClassConverter;
import kit.DataPackage;
import client.model.User;

import java.io.IOException;
import java.io.InputStream;
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

        DataPackage dataPackage = new DataPackage(ID, password);
        dataPackage.setOperateType("loadUserInfo");

        IODealer.send(socket, dataPackage, false);

        DataPackage receive = IODealer.receive(socket, false);

        if(receive.ID == -1) return false;
        else{
            User.getInstance().setField(receive);
            return true;
        }
    }
    public int register(DataPackage dataPackage) throws IOException {
        Socket socket = new Socket(HOST, PORT);

        dataPackage.setOperateType("register");

        IODealer.send(socket, dataPackage, false);

        DataPackage receiveData = IODealer.receive(socket, false);

        return receiveData.ID;
    }
    public boolean makeFriendWith(String info) throws Exception {
        Socket socket = new Socket(HOST, PORT);
        InputStream inputStream = socket.getInputStream();

        DataPackage dataPackage = new DataPackage(info);
        dataPackage.setOperateType("makeFriendWith");
        dataPackage.operator = User.getInstance().getName();

        IODealer.send(socket, dataPackage, false);

        DataPackage receive = IODealer.receive(socket, false);

        User.getInstance().addFriend(receive.name);

        if(receive.ID == -1) return false;
        else return true;
    }

    public Socket connectToRemote() throws Exception {
        Socket socket = new Socket(HOST, PORT);
        DataPackage dataPackage = new DataPackage(User.getInstance().getName(), User.getInstance().getID());
        dataPackage.setOperateType("connect");

        IODealer.send(socket, dataPackage, false);

        return socket;
    }
    public ArrayList<Message> loadDialogueData() throws Exception {
        Socket socket = new Socket(HOST, PORT);

        DataPackage dataPackage = new DataPackage(User.getInstance().getName(), User.getInstance().getID());
        dataPackage.setOperateType("loadDialogueData");

        IODealer.send(socket, dataPackage, false);

        DataPackage receive = IODealer.receive(socket, false);

        return receive.messages;
    }
}