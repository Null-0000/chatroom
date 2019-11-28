package client.user;

import client.*;
import client.exceptions.PasswordException;
import client.frames.UserFrame;
import client.tools.SocketFunctions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import static client.tools.RegexFunctions.selectBy;

public class User {
    private UserInfo info;
    private UserCard card;
    private FriendListPanel friendListPanel;
    private UserFrame frame;
    private Socket mySocket;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Dialogues dialogues;

    public boolean comparePassword(String password){
        return info.getPassword().equals(password);
    }
    public User(int ID, String password) throws PasswordException, IOException {
        info = loadUserData(ID, password);
        if (info == null)
            throw new PasswordException();
        this.dialogues = new Dialogues(info.name, info.friends);

        this.mySocket = SocketFunctions.connectToRemote(info.name);
        this.inputStream = mySocket.getInputStream();
        this.outputStream = mySocket.getOutputStream();

        this.card = new UserCard(info.name, info.sig, info.ID);
        this.friendListPanel = new FriendListPanel(dialogues);
        this.frame = new UserFrame(card, friendListPanel);

        receiveMessages();

    }

    public void sendMessage(String receiver, String content, String date) throws IOException {
        String outMessage = "BHEAD send message EHEAD Bsender " + info.name + " Esender Breceiver " + receiver +
                " Ereceiver Bcontent " + content + " Econtent Bdate " + date + " Edate";
        outputStream.write(outMessage.getBytes(StandardCharsets.UTF_8));
    }
    private void receiveMessages() {
        ReceiveMessageThread receiveMessageThread = new ReceiveMessageThread();
        receiveMessageThread.start();
    }


    private UserInfo loadUserData(int ID, String password) throws IOException {
        //链接服务器，获取用户图片，昵称，签名, 朋友
        UserInfo info = SocketFunctions.loadUserInfo(ID, password);
        return info;
    }

    public void makeFriend(String friend){
        info.friends.add(friend);
        friendListPanel.addMember(friend);
    }
    public void setFrameActive(){
        frame.setVisible(true);
    }

    public String toString(){
        return info.name;
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
                        String date = selectBy(inMessage, "Bdate (.*) Edate");
                        dialogues.receiveMessage(sender, content, date);
                        //notice(sender);
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    };
}


