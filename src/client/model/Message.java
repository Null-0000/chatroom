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
        if (isLeft) return "<div class=\'lt_para\' align=\'LEFT\'><p>" + getFormattedDate() + "&nbsp;" + sender +
                ":</p><p class=\'content\'>"+ content +"</p></div>";
        else return "<div class=\'rt_para\' align=\'RIGHT\'><p class=\'head\'>" + getFormattedDate() + "&nbsp;" + sender +
                ":</p><p class=\'content\'>"+ content +"</p></div>";
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
