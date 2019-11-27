package client.user;

import client.frames.ChattingFrame;
import client.tools.ResizingList;

public class Dialogue {
    private ResizingList<Message> messageList;
    protected String p1;
    protected String p2;
    private ChattingFrame chattingFrame;

    public Dialogue(String p1, String p2){
        this.p1 = p1;
        this.p2 = p2;
        messageList = new ResizingList<Message>();
    }
    public void addMessage(String sender, String receiver, String content){
        messageList.add(new Message(sender, receiver, content));
      //  chattingFrame.updateMessages();
    }
}
