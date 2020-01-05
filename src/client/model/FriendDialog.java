package client.model;

import client.view.FriendChatView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import kit.Info;
import kit.UserInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * dialogue between user and one friend
 */

public class FriendDialog extends Dialog implements Serializable {
    private transient Info userB;

    public FriendDialog(Info userB, int userAID) throws IOException {
        super(userAID);
        this.userB = userB;
    }

    public void setChatView() throws IOException {
        chatView = new FriendChatView((UserInfo) userB, messageList);
        //chatView不能被序列化，故每次读取本地文件后需要重新new哟个chatView
    }



    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(messageList.toArray());
        oos.writeInt(userAID);
        oos.writeBoolean(hasNewMessage.get());
        oos.writeObject(userB);
    }
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ArrayList list = new ArrayList(Arrays.asList((Object[]) ois.readObject()));
        messageList = new SimpleListProperty<>(FXCollections.observableArrayList(list));
        userAID = ois.readInt();
        hasNewMessage = new SimpleBooleanProperty(ois.readBoolean());
        userB = (UserInfo) ois.readObject();
    }
}