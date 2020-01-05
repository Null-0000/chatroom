package client.model;

import client.view.FriendChatView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import kit.UserInfo;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * dialogue between user and one friend
 */

public class FriendDialog extends Dialog implements Serializable {
    private transient int userB_id;

    public FriendDialog(int userB_id, int userAID) throws IOException {
        super(userAID);
        this.userB_id = userB_id;
    }

    public void setChatView() throws IOException {
        UserInfo info = User.getInstance().getFriends().get(userB_id).getUserInfo();
        chatView = new FriendChatView(info, messageList);
        //chatView不能被序列化，故每次读取本地文件后需要重新new哟个chatView
    }



    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(messageList.toArray());
        oos.writeInt(userAID);
        oos.writeBoolean(hasNewMessage.get());
        oos.writeInt(userB_id);
    }
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ArrayList list = new ArrayList(Arrays.asList((Object[]) ois.readObject()));
        messageList = new SimpleListProperty<>(FXCollections.observableArrayList(list));
        userAID = ois.readInt();
        hasNewMessage = new SimpleBooleanProperty(ois.readBoolean());
        userB_id = ois.readInt();
    }
}