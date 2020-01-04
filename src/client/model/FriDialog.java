package client.model;

import client.view.ChatView;


import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kit.Message;


import java.io.*;

/**
 * dialogue between user and one friend
 */

public class FriDialog extends Dialog implements Serializable {
    private transient String userB;

    public FriDialog(String userB, String userA) throws IOException {
        super(userA);
        this.userB = userB;
    }

    public void setChatView() throws IOException {
        chatView = new ChatView(userB, messageList);
        //chatView不能被序列化，故每次读取本地文件后需要重新new哟个chatView
    }



    protected void writeObject(ObjectOutputStream oos) throws IOException {
        super.writeObject(oos);
        oos.writeUTF(userB);
    }
    protected void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        super.readObject(ois);
        userB = ois.readUTF();
    }
}