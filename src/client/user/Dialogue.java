package client.user;

import client.frames.ChattingFrame;
import client.tools.ResizingList;

public class Dialogue {
    private ResizingList<Message> messageList;
    public String owner;
    public String friend;
    private ChattingFrame chattingFrame;

    public Dialogue(String owner, String friend){
        this.owner = owner;
        this.friend = friend;
        this.chattingFrame = new ChattingFrame(owner, friend);
        messageList = new ResizingList<Message>();

    }
    public void addMessage(String sender, String receiver, String content, String date){
        Message message = new Message(sender, receiver, content, date);
        messageList.add(message);
        chattingFrame.updateDialogField(message);
    }
    public void setChattingFrameVisible(boolean b){
        chattingFrame.setVisible(b);
    }
}
