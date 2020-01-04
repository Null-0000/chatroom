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
    private MapProperty<String, Friend> friends;
    private Socket mySocket;

    public ConnThread(MapProperty<String, Friend> friends, Socket mySocket) {
        this.friends = friends;
        this.mySocket = mySocket;
    }

    @Override
    public void run() {
        System.out.println("开始接收信息");
        for (Friend friend : friends.values()) {
            friend.getFriDialog().synchronizeMessage();
        }

        while (true) {
            try {
                Data receive = IODealer.receive(mySocket, false);
                if (receive.isOperate(Data.ADD_FRIEND)) {
                    Platform.runLater(() ->
                    {
                        try {
                            UserInfo info = new UserInfo(receive.ID, receive.name,
                                    receive.signature, receive.myIconBytes);
                            Friend friend = new Friend(info);
                            User.getInstance().addFriend(friend);
                            FriDialog dialog = new FriDialog(friend.getFriendName(),
                                    User.getInstance().getName());
                            friend.init(dialog);
                            dialog.synchronizeMessage();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                } else {
                    Message message = receive.message;
                    friends.get(message.sender).getFriDialog().updateMessage(message);
                }
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
