package client.user;

import java.util.Date;

public class Message {
    private String sender;
    private String receiver;
    private String content;
    private Date date;
    public Message(String sender, String receiver, String content, Date date){
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.date = date;
    }

}
