package client.model;

import client.view.GroupChatView;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import kit.GroupInfo;
import kit.UserInfo;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GroupDialog extends Dialog implements Serializable {
    private transient int groupID;
    private transient List<UserInfo> members;

    public GroupDialog(int userAID, int groupID, List<UserInfo> members){
        super(userAID);
        this.groupID = groupID;
        this.members = members;
    }

    public void setChatView() throws IOException{
        GroupInfo info = User.getInstance().getGroups().get(groupID).getGroupInfo();
        chatView = new GroupChatView(info, messageList);
    }

    private void writeObject(ObjectOutputStream oos) throws IOException {
        oos.defaultWriteObject();
        oos.writeObject(messageList.toArray());
        oos.writeInt(userAID);
        oos.writeBoolean(hasNewMessage.get());
        oos.writeInt(groupID);
        oos.writeObject(members);
    }
    private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        ArrayList list = new ArrayList(Arrays.asList((Object[]) ois.readObject()));
        messageList = new SimpleListProperty<>(FXCollections.observableArrayList(list));
        userAID = ois.readInt();
        hasNewMessage = new SimpleBooleanProperty(ois.readBoolean());
        groupID = ois.readInt();
        members = (List<UserInfo>) ois.readObject();
    }
}
