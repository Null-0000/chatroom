package client.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Comparable<Message>, Serializable {
    public String receiver;
    public String sender;
    public String ctype;
    public byte[] content;
    public Date date;
    public Message(String receiver, String sender, byte[] content, Date date){
        this.receiver = receiver;
        this.sender = sender;
        this.ctype = "text";
        this.content = content;
        this.date = date;
    }
    public Message(String receiver, String sender, String ctype, byte[] content, Date date){
        this(receiver, sender, content, date);
        this.ctype = ctype;
    }
    public Date getDate(){
        return date;
    }
    private String getFormattedDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm:ss");
        return simpleDateFormat.format(date);
    }
    @Override
    public String toString() {
        return String.format("{receiver:%s,sender:%s,content:%s,date:%s}",receiver,
                sender,content,date);
    }
    @Override
    public int compareTo(Message o) {
        return date.compareTo(o.date);
    }
    public int compareTo(Date date){
        return this.date.compareTo(date);
    }

    public String getHead(){
        return getFormattedDate() + " " + sender + ":\n";
    }
    public byte[] getContent(){
        return content;
    }
}
