package client.user;

import client.exceptions.PasswordException;
import client.frames.UserFrame;
import client.tools.SocketFunctions;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Date;

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

    public DialoguesManager manager;

    public boolean comparePassword(String password){
        return info.getPassword().equals(password);
    }
    public User(int ID, String password) throws PasswordException, IOException {
        info = loadUserData(ID, password);
        if (info == null)
            throw new PasswordException();
        manager = new DialoguesManager(info.name);
        if (!manager.fileExist()) {
            dialogues = new Dialogues(info.name, info.friends);
        }
        else {
            dialogues = manager.initMyDialogues();
        }
        dialogues.loadRemoteData();

        this.mySocket = SocketFunctions.connectToRemote(info.name);
        this.inputStream = mySocket.getInputStream();
        this.outputStream = mySocket.getOutputStream();

        this.card = new UserCard(info.name, info.sig, info.ID);
        this.friendListPanel = new FriendListPanel(dialogues);
        this.frame = new UserFrame(card, friendListPanel);

        receiveMessages();

    }
    public void sendMessage(String receiver, String content, long datetime) throws IOException {
        String outMessage = "BHEAD send message EHEAD Bsender " + info.name + " Esender Breceiver " + receiver +
                " Ereceiver Bcontent " + content + " Econtent Bdatetime " + datetime + " Edatetime";
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
        friendListPanel.addMember();
    }
    public void setFrameActive(){
        frame.setVisible(true);
    }
    public String toString(){
        return info.name;
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

                        Message message = new Message(info.name, sender, content, date);
                        dialogues.updateDialogue(message, sender);
                        //notice(sender);
                    }
                } catch (IOException e){
                    e.printStackTrace();
                }
            }
        }
    };

}
