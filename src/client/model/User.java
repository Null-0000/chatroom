package client.model;

import client.controller.Connector;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;
import kit.*;

import java.io.FileReader;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

public class User {
    private UserInfo userInfo;
    private MapProperty<String, Friend> friends;

    private DialoguesManager manager;
    private Socket mySocket;
    private InputStream inputStream;
    private OutputStream outputStream;

    private static User instance = new User();

    public static User getInstance() {
        return instance;
    }

    public void setField(DataPackage u) {
        this.userInfo = new UserInfo(u.ID, u.name, u.signature, u.myIconBytes);
        //this.friends = new SimpleListProperty<>(observableList);
        ObservableMap<String, Friend> obsMap = FXCollections.observableHashMap();
        for (UserInfo userInfo : u.friendList) {
            Friend friend = new Friend();
            friend.setUserInfo(userInfo);
            obsMap.put(userInfo.getName(), friend);
        }
        this.friends = new SimpleMapProperty<>(obsMap);
        manager = new DialoguesManager(userInfo.getName());
    }

    public void initialise() throws Exception {
        manager.initMyDialogues(friends);

        loadRemoteData();

        this.mySocket = Connector.getInstance().connectToRemote();
        this.inputStream = mySocket.getInputStream();

        receiveMessages();
    }

    public void loadRemoteData() throws Exception {
        ArrayList<Message> messages = Connector.getInstance().loadDialogueData();
        for (Message message : messages) {
            friends.get(message.sender).getDialogue().updateMessage(message);
        }
    }

    public void addFriend(UserInfo info) {
        Friend friend = new Friend();
        friend.setUserInfo(info);
        friends.put(info.getName(), friend);
        //此处为主界面更新好友列表
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

    public Dialogue getDialogueFrom(String friendName) {
        return friends.get(friendName).getDialogue();
    }

    public Collection<Friend> getFriendList() {
        return friends.values();
    }

    public Collection<String> getFriendNames() {
        return friends.keySet();
    }

    public void sendMessage(Message message) throws Exception {
        String receiver = message.receiver;
        friends.get(receiver).getDialogue().updateMessage(message);

        DataPackage dataPackage = new DataPackage(message);
        dataPackage.setOperateType("sendMessage");
        IODealer.send(mySocket, dataPackage, false);
    }

    private void receiveMessages() {
        ReceiveMessageThread receiveMessageThread = new ReceiveMessageThread(friends, mySocket);
        receiveMessageThread.start();
    }

    public void exit() throws Exception {
        DataPackage dataPackage = new DataPackage();
        dataPackage.setOperateType("exit");

        IODealer.send(mySocket, dataPackage, false);

        /**登出时储存文件*/
        manager.updateMyDialogues(friends);
    }

}