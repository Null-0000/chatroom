package client.model;

import client.controller.Connector;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

import static client.model.RegexFunctions.selectBy;

public class User {
    private String name;
    private String signature;
    private int ID;
    private ArrayList<String> friendList;
    private DialoguesManager manager;
    private Dialogues dialogues;
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
        if (!manager.fileExist()) {
            dialogues = new Dialogues(name, friendList);
        }
        else {
            dialogues = manager.initMyDialogues();
        }
        dialogues.loadRemoteData();

        this.mySocket = Connector.getInstance().connectToRemote(name);
        this.inputStream = mySocket.getInputStream();
        this.outputStream = mySocket.getOutputStream();

        receiveMessages();
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
        return dialogues.getAllDialogue(friendName);
    }

    public ArrayList<String> getFriendList() {
        return friendList;
    }
    public void sendMessage(Message message) throws IOException {
        String receiver = message.receiver;
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
        manager.updateMyDialogues(dialogues);
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
                        dialogues.updateDialogue(message, sender);
                        //notice(sender);
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    }
}