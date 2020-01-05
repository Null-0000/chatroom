package client.model;

import client.view.ChatView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import kit.Info;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * dialogue between user and one friend
 */

public class FriendDialog extends Dialog implements Serializable {
    private transient Info userB;

    public FriendDialog(Info userB, String userA) throws IOException {
        super(userA);
        this.userB = userB;
    }

    public void setChatView() throws IOException {
        chatView = new ChatView(userB, messageList, false);
        //chatView不能被序列化，故每次读取本地文件后需要重新new哟个chatView
    }



    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(messageList.toArray());
        oos.writeUTF(userA);
        oos.writeBoolean(hasNewMessage.get());
        oos.writeObject(userB);
    }
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ArrayList list = new ArrayList(Arrays.asList((Object[]) ois.readObject()));
        messageList = new SimpleListProperty<>(FXCollections.observableArrayList(list));
        userA = ois.readUTF();
        hasNewMessage = new SimpleBooleanProperty(ois.readBoolean());
        userB = (Info) ois.readObject();
    }
}