package client.model;

import client.controller.Connector;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kit.ClassConverter;
import kit.DataPackage;
import kit.Message;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Map;

public class User {
    private String name;
    private String signature;
    private int ID;
    private byte[] myIconBytes;
    private ListProperty<String> friendList;
    private DialoguesManager manager;
    private Map<String, Dialogue> dialogueMap;
    private Socket mySocket;

    private static User instance = new User();
    private InputStream inputStream;
    private OutputStream outputStream;

    public static User getInstance(){
        return instance;
    }

    public void setField(DataPackage u){
        this.ID = u.ID;
        this.name = u.name;
        this.signature = u.signature;
        ObservableList<String> observableList = FXCollections.observableArrayList(u.friendList);
        this.friendList = new SimpleListProperty<>(observableList);
        this.myIconBytes = u.myIconBytes;
    }
    public void initialise() throws Exception {
        manager = new DialoguesManager(name);

        dialogueMap = manager.initMyDialogues();

        loadRemoteData();

        this.mySocket = Connector.getInstance().connectToRemote();
        this.inputStream = mySocket.getInputStream();
        this.outputStream = mySocket.getOutputStream();

        receiveMessages();
    }
    public void loadRemoteData() throws Exception {
        ArrayList<Message> messages = Connector.getInstance().loadDialogueData();
        for(Message message : messages){
            dialogueMap.get(message.sender).updateMessage(message);
        }
    }

    public void addFriend(String friendName){
        friendList.add(friendName);
        //此处为主界面更新好友列表
    }
    public String getName(){
        return name;
    }
    public byte[] getMyIconBytes(){
        return myIconBytes;
    }

    public String getSignature() {
        return signature;
    }

    public int getID() {
        return ID;
    }

    public Dialogue getDialogueFrom(String friendName){
        return dialogueMap.get(friendName);
    }

    public ListProperty<String> getFriendList() {
        return friendList;
    }
    public void sendMessage(Message message) throws Exception {
        String receiver = message.receiver;
        dialogueMap.get(receiver).updateMessage(message);

        DataPackage dataPackage = new DataPackage(message);
        dataPackage.setOperateType("sendMessage");
        outputStream.write(ClassConverter.getBytesFromObject(dataPackage));
    }
    private void receiveMessages() {
        ReceiveMessageThread receiveMessageThread = new ReceiveMessageThread();
        receiveMessageThread.start();
    }

    public void exit() throws Exception {
        DataPackage dataPackage = new DataPackage();
        dataPackage.setOperateType("exit");
        outputStream.write(ClassConverter.getBytesFromObject(dataPackage));
        outputStream.flush();
        mySocket.shutdownOutput();
        /**登出时储存文件*/
        manager.updateMyDialogues(dialogueMap);
    }

    class ReceiveMessageThread extends Thread{
        @Override
        public void run() {
            System.out.println("开始接收信息");
            for (Dialogue dialogue: dialogueMap.values()){
                dialogue.synchronizeMessage();
            }
            int len;
            byte[] bytes = new byte[1024 * 32];
            while (true) {
                try {
                    len = inputStream.read(bytes);
                    if (len != -1) {
                        DataPackage receive = (DataPackage) ClassConverter.getObjectFromBytes(bytes);
                        Message message = receive.message;
                        dialogueMap.get(message.sender).updateMessage(message);
                    }
                } catch (SocketException s) {
                    System.out.println("Receiving Tread Ends.");
                    return;
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}