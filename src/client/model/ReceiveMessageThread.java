package client.model;

import javafx.beans.property.MapProperty;
import kit.DataPackage;
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
            friend.getDialogue().synchronizeMessage();
        }

        while (true) {
            try {
                DataPackage receive = IODealer.receive(mySocket, false);
                Message message = receive.message;
                friends.get(message.sender).getDialogue().updateMessage(message);
            } catch (Exception e) {
                e.printStackTrace();
                break;
            }
        }
    }
}
