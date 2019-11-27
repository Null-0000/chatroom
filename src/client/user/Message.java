package client.user;

import java.util.Date;

public class Message {
    private String sender;
    private String receiver;
    private String content;
    private String date;
    public Message(String sender, String receiver, String content, String date){
        this.sender = sender;
        this.receiver = receiver;
        this.content = content;
        this.date = date;
    }
    public String toString(){
        return ("\n" + date + " " + sender + ":\n\t" + content) + "\n";
    }

}
