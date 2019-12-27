package kit;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Message implements Comparable<Message>, Serializable {
    public boolean isMass;
    public String receiver;
    public String sender;
    public String ctype;
    public byte[] content;
    public Date date;
    private String url;
    public Message(String receiver, String sender, byte[] content, Date date, boolean isMass){
        this.receiver = receiver;
        this.sender = sender;
        this.ctype = "text";
        this.content = content;
        this.date = date;
    }
    public Message(String receiver, String sender, String ctype, byte[] content, Date date, boolean isMass){
        this(receiver, sender, content, date, isMass);
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

    public String getHead(){
        return getFormattedDate() + " " + sender + ":\n";
    }
    public byte[] getContent(){
        return content;
    }
    public void setUrl(String url){
        this.url = url;
    }
    public String getUrl(){
        return this.url;
    }
}
