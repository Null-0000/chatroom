package client.model;

import client.view.ChatView;

import javafx.beans.property.ListProperty;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kit.Message;


import javax.imageio.stream.FileImageOutputStream;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

/**
 * dialogue between user and one friend
 */

public class Dialogue implements Serializable {
    private transient ListProperty<Message> messageList;
    private transient String friendName;

    private transient ChatView chatView;

    public Dialogue(String friendName, String userName) throws IOException {
        this.friendName = friendName;
        ObservableList<Message> observableList = FXCollections.observableArrayList();
        this.messageList = new SimpleListProperty<>(observableList);
        setChatView();
    }
    public void synchronizeMessage(){
        chatView.synchronizeMessage(messageList);
    }

    public void setChatView() throws IOException {
        chatView = new ChatView(friendName, messageList);
        //chatView不能被序列化，故每次读取本地文件后需要重新new哟个chatView
    }
    public void updateMessage(Message message) {
        String user = User.getInstance().getName();
        String to = (message.sender.equals(user))? message.receiver: message.sender;
        switch (message.ctype.replaceAll("/.*", "")){
            case "image":
                File imgDir = new File("src/client/data/" + User.getInstance().getName() +
                        "/" + to + "/images");
                Date imgDate = new Date();
                String imgSuffix = "." + message.ctype.replaceAll(".*/", "");
                File imgFile = new File(imgDir.getPath() + "/img" + imgDate.getTime() + imgSuffix);
                message.setUrl(".." + imgFile.getPath().replaceAll(".*?client", "").replaceAll("\\\\", "/"));
                try {
                    imgFile.createNewFile();
                    FileImageOutputStream fios = new FileImageOutputStream(imgFile);
                    fios.write(message.content);
                    fios.flush();
                    fios.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "audio":
                File audDir = new File("src/client/data/" + User.getInstance().getName() +
                        "/" + to + "/audios");
                Date audDate = new Date();
                String audSuffix = "." + message.ctype.replaceAll(".*/", "");
                File audFile = new File(audDir.getPath() + "/aud" + audDate.getTime() + audSuffix);
                message.setUrl(".." + audFile.getPath().replaceAll(".*?client", "").replaceAll("\\\\", "/"));
                try {
                    audFile.createNewFile();
                    FileImageOutputStream fios = new FileImageOutputStream(audFile);
                    fios.write(message.content);
                    fios.flush();
                    fios.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        messageList.add(message);
    }
    public ListProperty<Message> getMessageList() {
        return messageList;
    }

    public void show(){
        chatView.show();
    }
    public ChatView getChatView(){
        return chatView;
    }
    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(messageList.toArray());
        oos.writeUTF(friendName);
    }
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ArrayList list = new ArrayList(Arrays.asList((Object[]) ois.readObject()));
        messageList = new SimpleListProperty<>(FXCollections.observableArrayList(list));
        friendName = ois.readUTF();
    }
}