package client.model;

import client.controller.Connector;
import javafx.beans.property.ListProperty;
import javafx.beans.property.MapProperty;
import javafx.beans.property.SimpleMapProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static client.model.RegexFunctions.selectBy;

public class User {
    private String name;
    private String signature;
    private int ID;
    private ArrayList<String> friendList;
    private DialoguesManager manager;
    private MapProperty<String, Dialogue> dialogueMap;
    private Socket mySocket;

    private static User instance = new User();
    private InputStream inputStream;
    private OutputStream outputStream;

    public static User getInstance(){
        return instance;
    }

    public void setField(int ID, String name, String signature, ArrayList<String> friendList){
        this.ID = ID;
        this.name = name;
        this.signature = signature;
        this.friendList = friendList;
    }
    public void initialise() throws IOException {
        manager = new DialoguesManager(name);
        Dialogue dialogue;
        if (!manager.fileExist()) {
            ObservableMap<String, Dialogue> observableMap = FXCollections.observableHashMap();
            for (String friend: friendList){
                dialogue = new Dialogue(friend, name);
                observableMap.put(friend, dialogue);
            }
            dialogueMap = new SimpleMapProperty<>(observableMap);
            dialogueMap.setValue(observableMap);
        }
        else {
            /**从本地读取信息*/
            dialogueMap = manager.initMyDialogues();
        }
        loadRemoteData();

        this.mySocket = Connector.getInstance().connectToRemote(name);
        this.inputStream = mySocket.getInputStream();
        this.outputStream = mySocket.getOutputStream();

        receiveMessages();
    }
    public void loadRemoteData() throws IOException {
        String inMessage = Connector.getInstance().loadDialogueData();
        Pattern p = Pattern.compile("Bsender (.*?) Esender Bcontent (.*?) Econtent Bdatetime (.*?) Edatetime");
        Matcher m = p.matcher(inMessage);
        String sender;
        String content;
        Date date;
        while (m.find()){
            sender = m.group(1);
            content = m.group(2);
            date = new Date(Long.parseLong(m.group(3)));
            Message message = new Message(name, sender, content, date);
            dialogueMap.get(sender).updateMessage(message);
        }
    }

    public void addFriend(String friendName){
        friendList.add(friendName);
        //此处为主界面更新好友列表
    }
    public String getName(){
        return name;
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
    public MapProperty<String, Dialogue> dialogueMapProperty(){
        return dialogueMap;
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }
    public void sendMessage(Message message) throws IOException {
        //dialogues.updateDialogue(message);
        String receiver = message.receiver;
        dialogueMap.get(receiver).updateMessage(message);
        String content = message.getContent();
        long datetime = message.getDate().getTime();
        String outMessage = "BHEAD send message EHEAD Bsender " + name + " Esender Breceiver " + receiver +
                " Ereceiver Bcontent " + content + " Econtent Bdatetime " + datetime + " Edatetime";
        outputStream.write(outMessage.getBytes(StandardCharsets.UTF_8));
    }
    private void receiveMessages() {
        ReceiveMessageThread receiveMessageThread = new ReceiveMessageThread();
        receiveMessageThread.start();
    }

    public void exit() throws IOException {
        outputStream.write("exit".getBytes(StandardCharsets.UTF_8));
        /**登出时储存文件*/
        manager.updateMyDialogues(dialogueMap);
        inputStream.close();
        outputStream.close();
        mySocket.close();
    }

    class ReceiveMessageThread extends Thread{
        @Override
        public void run() {
            System.out.println("开始接收信息");
            int len;
            byte[] bytes = new byte[1024];
            while (true) {
                try {
                    len = inputStream.read(bytes);
                    if (len != -1) {
                        String inMessage = new String(bytes, 0, len);
                        String sender = selectBy(inMessage, "Bsender (.*?) Esender");
                        String content = selectBy(inMessage, "Bcontent (.*?) Econtent");
                        long datetime = Long.parseLong(selectBy(inMessage, "Bdatetime (.*?) Edatetime"));
                        Date date = new Date(datetime);

                        Message message = new Message(name, sender, content, date);
                        dialogueMap.get(sender).updateMessage(message);
                        //notice(sender);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}