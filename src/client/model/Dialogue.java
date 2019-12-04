package client.model;

import client.view.ChatView;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * dialogue between user and one friend
 */

public class Dialogue implements Serializable {
    private List<Message> messageList = new ArrayList<>();
    private String friendName;

    transient private ChatView chatView;

    public Dialogue(String friendName, String userName) throws IOException {
        this.friendName = friendName;
        chatView = new ChatView(friendName, messageList);
    }

    public void updateMessage(Message message) {
        messageList.add(message);
    }
    public List<Message> getMessageList() {
        return messageList;
    }
//    public ListProperty<Message> getPeriodMessage(Date date) {
//        if (messageList.isEmpty()) return null;
//        for (int i = messageList.size() - 1; i >= 0; i--) {
//            if (messageList.get(i).compareTo(date) < 0) {
//                return (ListProperty<Message>) messageList.subList(i, messageList.size() - 1);
//            }
//        }
//        return null;
//    }
    public void show() throws IOException {
        if(chatView == null) chatView = new ChatView(friendName, messageList);
        chatView.show();
    }
}
