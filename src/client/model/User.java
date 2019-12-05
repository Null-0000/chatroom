package client.model;

import client.controller.Connector;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static client.model.RegexFunctions.selectBy;

public class User {
    private String name;
    private String signature;
    private int ID;
    private ObservableList<String> friendList;
    private DialoguesManager manager;
    private Map<String, Dialogue> dialogues;
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
        this.friendList = FXCollections.observableArrayList(friendList);
    }
    public void initialise() throws IOException {
        manager = new DialoguesManager(name);
        dialogues = manager.initMyDialogues();

//        if(dialogues == null) showAlert("在user的initialise里dialogues为空");

        loadRemoteData();

        this.mySocket = Connector.getInstance().connectToRemote(name);
        this.inputStream = mySocket.getInputStream();
        this.outputStream = mySocket.getOutputStream();

        ReceiveMessageThread thread = new ReceiveMessageThread();
        thread.start();

//        ShowDialog.showMessage("END");
    }

    public void addFriend(String friendName){
        friendList.add(friendName);
        try {
            Dialogue d = new Dialogue(friendName, name);
            d.setChatView();
            dialogues.put(friendName, d);
        } catch (IOException e) {
            e.printStackTrace();
            ShowDialog.showMessage(e.getMessage());
        }
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
            dialogues.get(sender).updateMessage(message);
            ShowDialog.showMessage(sender + " " + content + " " + "收到离线消息");
        }
    }
    public Dialogue getDialogueFrom(String friendName){
        return dialogues.get(friendName);
    }

    public ObservableList<String> getFriendList() {
        return friendList;
    }
    public void sendMessage(Message message) throws IOException {
        String receiver = message.receiver;
        dialogues.get(receiver).updateMessage(message);
        String content = message.getContent();
        long datetime = message.getDate().getTime();
        String outMessage = "BHEAD send message EHEAD Bsender " + name + " Esender Breceiver " + receiver +
                " Ereceiver Bcontent " + content + " Econtent Bdatetime " + datetime + " Edatetime";
        outputStream.write(outMessage.getBytes(StandardCharsets.UTF_8));
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
                        dialogues.get(sender).updateMessage(message);
                        ShowDialog.showMessage("收到一条消息");
                        //notice(sender);
                    }
                } catch (SocketException e){
                    System.out.println("接受消息线程结束");
                    return;
                } catch (IOException e) {
                    e.printStackTrace();
                    return;
                }
            }
        }
    }
}