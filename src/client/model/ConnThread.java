package client.model;

import javafx.application.Platform;
import javafx.beans.property.MapProperty;
import kit.Data;
import kit.IODealer;
import kit.Message;
import kit.UserInfo;

import java.io.IOException;
import java.net.Socket;

public class ConnThread extends Thread {
    private MapProperty<Integer, Friend> friends;
    private MapProperty<Integer, Group> groups;
    private Socket mySocket;

    public ConnThread(MapProperty<Integer, Friend> friends, MapProperty<Integer, Group> groups, Socket mySocket) {
        this.friends = friends;
        this.groups = groups;
        this.mySocket = mySocket;
    }

    @Override
    public void run() {
        System.out.println("开始接收信息");
        for (Friend friend : friends.values()) {
            friend.getFriendDialog().synchronizeMessage();
        }
        for (Group group : groups.values()) {
            group.getGroupDialog().synchronizeMessage();
        }

        while (true) {
            Data receive = IODealer.receive(mySocket, false);
            if (receive.isOperate(Data.ADD_FRIEND)) {
                Platform.runLater(() ->
                {
                    try {
                        UserInfo userInfo = new UserInfo(receive.ID, receive.name,
                                receive.signature, receive.iconBytes);
                        Friend friend = new Friend(userInfo);
                        User.getInstance().addFriend(friend);
                        FriendDialog dialog = new FriendDialog(
                                friend.getUserInfo().getID(), User.getInstance().getID());
                        friend.init(dialog);
                        dialog.synchronizeMessage();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            else if (receive.isOperate(Data.JOIN_GROUP)) {
                String groupName = receive.name;//从Data中取得组名
                UserInfo userInfo = receive.operatorInfo;//从Data中获取新成员的信息
                groups.get(groupName).getGroupInfo().getMembers().add(userInfo);
            }

            else {
                Message message = receive.message;
                if (message.isMass)//判断消息是否是群发的
                    groups.get(message.receiver).getGroupDialog().updateMessage(message);
                else
                    friends.get(message.sender).getFriendDialog().updateMessage(message);
            }

        }
    }
}
