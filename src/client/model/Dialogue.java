package client.model;

import client.view.ChatView;
import client.view.StageM;
import javafx.beans.property.ListProperty;
import javafx.beans.property.ListPropertyBase;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import old_version.views.MyStage;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * dialogue between user and one friend
 */

public class Dialogue implements Serializable {
    private ListProperty<Message> messageList;
    private String friendName;

    private ChatView chatView;


    public Dialogue(String friendName, String userName) throws IOException {
        this.friendName = friendName;
        ObservableList<Message> observableList = FXCollections.observableArrayList();
        this.messageList = new SimpleListProperty<>(observableList);
        this.chatView = new ChatView(friendName);
        //StageM.getManager().addStage(userName + " to " + friendName, chatView);
        /*
        messageList.addListener((obs, ov, nv)->{
            Message newMessage =  nv.get(nv.size() - 1);
            if (newMessage.sender.equals(friendName))
                chatView.updateWebView(newMessage, false);
            else {
                chatView.updateWebView(newMessage, true);
            }
        });

         */
    }
    public void updateMessage(Message message) {
        messageList.add(message);

//        chattingFrame.updateDialogField(message);
        /*if(message.sender.equals(friendName)){
            if(isGoingOn()){
                chattingFrame.updateDialogField(message);
                }
           }
         */
//        messageArrayList.sort(Message::compareTo);
    }
//    private boolean isGoingOn(){
//        return chattingFrame.isVisible();
//    }
    public ListProperty<Message> getMessageList() {
        return messageList;
    }
    public ListProperty<Message> getPeriodMessage(Date date) {
        if (messageList.isEmpty()) return null;
        for (int i = messageList.size() - 1; i >= 0; i--) {
            if (messageList.get(i).compareTo(date) < 0) {
                return (ListProperty<Message>) messageList.subList(i, messageList.size() - 1);
            }
        }
        return null;
    }
    public void show(){
        chatView.show();
    }
//    public void setChattingFrameVisible() {
//        chattingFrame.setVisible(true);
//    }
}
