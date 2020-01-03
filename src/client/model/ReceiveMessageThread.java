package client.model;

import javafx.beans.property.MapProperty;
import kit.Data;
import kit.IODealer;
import kit.Message;

import java.net.Socket;

public class ReceiveMessageThread extends Thread{
    private MapProperty<String, Friend> friends;
    private Socket mySocket;
    public ReceiveMessageThread(MapProperty<String, Friend> friends, Socket mySocket){
        this.friends = friends;
        this.mySocket = mySocket;
    }
    @Override
    public void run() {
        System.out.println("开始接收信息");
        for (Friend friend: friends.values()){
            friend.getFriDialog().synchronizeMessage();
        }

        while (true) {
            try {
                Data receive = IODealer.receive(mySocket, false);
                Message message = receive.message;
                friends.get(message.sender).getFriDialog().updateMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
