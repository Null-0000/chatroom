package client.model;

import client.view.ChatView;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;

import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;

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
