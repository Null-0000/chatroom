package client.model;

import client.view.ChatView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import kit.UserInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupDialog extends Dialog implements Serializable {
    private transient int group_id;
    private transient List<UserInfo> members;

    public GroupDialog(String userA, int group_id, List<UserInfo> members){
        super(userA);
        this.group_id = group_id;
        this.members = members;
    }

    public void setChatView() throws IOException{
        chatView = new ChatView(group_id, messageList, true);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(messageList.toArray());
        oos.writeUTF(userA);
        oos.writeBoolean(hasNewMessage.get());
        oos.writeInt(group_id);
        oos.writeObject(members);
    }
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ArrayList list = new ArrayList(Arrays.asList((Object[]) ois.readObject()));
        messageList = new SimpleListProperty<>(FXCollections.observableArrayList(list));
        userA = ois.readUTF();
        hasNewMessage = new SimpleBooleanProperty(ois.readBoolean());
        group_id = ois.readInt();
        members = (List<UserInfo>) ois.readObject();
    }
}
