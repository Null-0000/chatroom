package client.user;

import client.*;
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
    private InputStream inputStream;
    private OutputStream outputStream;
    private Runnable receiveMessageThread;
    private Dialogues dialogues;

    public Dialogues manager;

    public User(int ID, String password) throws PasswordException, IOException {
        info = loadUserData(ID, password);
        if (info == null)
            throw new PasswordException();

        initMyDialogue(ID);

        this.mySocket = SocketFunctions.connectToRemote(info.name);
        this.inputStream = mySocket.getInputStream();
        this.outputStream = mySocket.getOutputStream();

        this.card = new UserCard(info.name, info.sig, info.ID);
        this.friendListPanel = new FriendListPanel(info.friends);
        this.frame = new UserFrame(card, friendListPanel);

        receiveMessages();
    }

    /**
     * initialise the user's dialogues from the data file
     * if the data file goes wrong ,then pop a alert message dialogue
     * @author Furyton
     * @param userID
     * @since 11.27
     */

    File file;
    private void initMyDialogue(int userID) throws IOException {
        file = new File("data/" + Integer.toString(userID) + ".dat");

        if (!file.exists()) {
            file.createNewFile();
        }
        manager = new Dialogues(info.name);

        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream input = null;

        if(fileInputStream.available() != 0){
            input = new ObjectInputStream(fileInputStream);

            try {
                manager = (Dialogues) input.readObject();
            }catch (ClassCastException |  InvalidClassException | ClassNotFoundException e){
                JOptionPane.showMessageDialog(null, "loading local data error, we need to clear the local data...", "alert", JOptionPane.ERROR_MESSAGE);
                new FileOutputStream(file);
            }
        }

        manager.setName(info.name);
    }
    public void writeDialogueManager() throws IOException {
        ObjectOutputStream output = new ObjectOutputStream(new FileOutputStream(file));
        output.writeObject(manager);
        output.close();
    }

    public void sendMessage(String receiver, String content) throws IOException {
        String outMessage = "BHEAD send message EHEAD Bsender " + info.name + " Esender Breceiver " + receiver +
                " Ereceiver Bcontent " + content + " Econtent";
        outputStream.write(outMessage.getBytes(StandardCharsets.UTF_8));
    }
    private void receiveMessages() {
        receiveMessageThread = new Runnable() {
            @Override
            public void run() {
                System.out.println("开始接收信息");
                int len;
                byte[] bytes = new byte[1024];
                while (CurrentUser.active) {
                    try {
                        len = inputStream.read(bytes);
                        if (len != -1) {
                            String inMessage = new String(bytes, 0, len);
                            String sender = selectBy(inMessage, "Bsender (.*?) Esender");
                            String content = selectBy(inMessage, "Bcontent (.*?) Econtent");
                            String date = selectBy(inMessage, "Bdate (.*) Edate");

                            Date date1 = new Date();

                            synchronized (manager){
                                Message message = new Message(info.name, sender, content, date1);
                                Dialogues.updateDialogue(message, sender);
                                //CurrentUser.user.notice(sender);
                            }
                        }
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                }
                System.out.println("接收了一条信息");
            }
        };
        receiveMessageThread.run();
    }

    private UserInfo loadUserData(int ID, String password) throws IOException {
        //链接服务器，获取用户图片，昵称，签名, 朋友
        UserInfo info = SocketFunctions.loadUserInfo(ID, password);
        return info;
    }
    //local message, send message, friend pop menu, icon image
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


}
