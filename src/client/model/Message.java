package client.model;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Comparable<Message>, Serializable {
    public String receiver;
    public String sender;
    private String content;
    private Date date;
    public Message(String receiver, String sender, String content, Date date){
        this.receiver = receiver;
        this.sender = sender;
        this.content = content;
        this.date = date;
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
    public String toHTML(boolean isLeft){
        if (isLeft) return "<div align=\'LEFT\'><p>" + getFormattedDate() + sender +
                ":</p><p style=\"width: 70%;border-color: black;border-radius: 1em; " +
                "background-color: aliceblue; font-size: medium; position: relative; left: 2%\" >"+
                content +"</p></div>";
        else return "<div align=\'RIGHT\'><p>" + getFormattedDate() + sender +
                ":</p><p style=\"width: 70%;border-color: black;border-radius: 1em; " +
                "background-color: aliceblue; font-size: medium; position: relative; right: 2%\" >"+
                content +"</p></div>";
    }
    @Override
    public int compareTo(Message o) {
        return date.compareTo(o.date);
    }
    public int compareTo(Date date){
        return this.date.compareTo(date);
    }

    public String getHead(){
        return getFormattedDate() + " " + sender + ":";
    }
    public String getContent(){
        return content;
    }
}
