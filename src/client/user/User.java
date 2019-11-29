package client.user;

import client.exceptions.PasswordException;
import client.frames.UserFrame;
import client.tools.SocketFunctions;

import javax.swing.*;
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
    public static ObjectInputStream objectInputStream;
    public static ObjectOutputStream objectOutputStream;
    private Dialogues dialogues;

    public DialoguesManager manager;

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

        objectInputStream = new ObjectInputStream(mySocket.getInputStream());
        objectOutputStream = new ObjectOutputStream(mySocket.getOutputStream());

        this.card = new UserCard(info.name, info.sig, info.ID);
        this.friendListPanel = new FriendListPanel(dialogues);
        this.frame = new UserFrame(card, friendListPanel);

        receiveMessages();
    }
    public void sendMessage(Message message) throws IOException {
        objectOutputStream.writeObject(message);
        objectOutputStream.flush();

        mySocket.shutdownOutput();
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
        objectOutputStream.writeObject("exit");
        objectOutputStream.flush();

        manager.updateMyDialogues(dialogues);
        mySocket.shutdownOutput();
        mySocket.close();
    }
    class ReceiveMessageThread extends Thread{
        public void run() {
            System.out.println("开始接收信息");

            Message message;

            while (true) {
                try {
                    if(objectInputStream.available() != 0){
                        message = (Message)objectInputStream.readObject();
                        dialogues.updateDialogue(message, message.sender);
                    }
                } catch (IOException | ClassNotFoundException e){
                    e.printStackTrace();
                    JOptionPane.showMessageDialog(null, "神秘错误", "ALERT", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}
