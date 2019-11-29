package client.user;

import client.CurrentUser;
import client.frames.ChattingFrame;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * dialogue between user and one friend
 */

public class Dialogue implements Serializable {
    private ArrayList<Message> messageArrayList;
    private String friendName;
    private ChattingFrame chattingFrame;

    public Dialogue(String friendName, String userName) {
        this.friendName = friendName;
        this.messageArrayList = new ArrayList<Message>();
        this.chattingFrame = new ChattingFrame(friendName, userName);
    }
    public void updateMessage(Message message) {
        messageArrayList.add(message);
        chattingFrame.updateDialogField(message);
        /*if(message.sender.equals(friendName)){
            if(isGoingOn()){
                chattingFrame.updateDialogField(message);
            }
        }

         */
//        messageArrayList.sort(Message::compareTo);
    }
    private boolean isGoingOn(){
        return chattingFrame.isVisible();
    }
    public ArrayList<Message> getMessageArrayList() {
        return messageArrayList;
    }
    public ArrayList<Message> getPeriodMessage(Date date) {
        if (messageArrayList.isEmpty()) return null;
        for (int i = messageArrayList.size() - 1; i >= 0; i--) {
            if (messageArrayList.get(i).compareTo(date) < 0) {
                return (ArrayList<Message>) messageArrayList.subList(i, messageArrayList.size() - 1);
            }
        }
        return null;
    }

    public void setChattingFrameVisible() {
        chattingFrame.setVisible(true);
    }
}
