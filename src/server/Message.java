package server;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Comparable<Message>, Serializable{
    public String receiver;
    public String sender;
    public String content;
    public Date date;
    public Message(String receiver, String sender, String content, Date date){
        this.receiver = receiver;
        this.sender = sender;
        this.content = content;
        this.date = date;
    }

    private String getDate(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH-mm-ss");
        return simpleDateFormat.format(date);
    }

    @Override
    public String toString() {
        return "\n\t" + getDate() + "\n" + sender + " : " + content + "\n";
    }

    @Override
    public int compareTo(Message o) {
        return date.compareTo(o.date);
    }
    public int compareTo(Date date){
        return this.date.compareTo(date);
    }
}
