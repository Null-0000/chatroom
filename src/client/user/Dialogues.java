package client.user;

import client.tools.ResizingList;
import client.tools.SocketFunctions;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dialogues {
    public ResizingList<Dialogue> getDialogues() {
        return dialogues;
    }

    private ResizingList<Dialogue> dialogues;
    private String owner;

    public ResizingList<String> getFriends() {
        return friends;
    }

    private ResizingList<String> friends;
    public Dialogues(String name, ResizingList<String> friends) throws IOException {
        this.dialogues = new ResizingList<Dialogue>();
        this.owner = name;
        this.friends = friends;
        for (String friend: friends)
            dialogues.add(new Dialogue(owner, friend));
        //loadLocalData();
        loadRemoteData();

    }
    public void receiveMessage(String sender, String content, String date){
        for (Dialogue dialogue: dialogues){
            if (dialogue.friend.equals(sender)) {
                dialogue.addMessage(sender, owner, content, date);
                break;
            }
        }
    }

    //从远程数据库读取用户下线时接收地数据
    private void loadRemoteData() throws IOException {
        String inMessage = SocketFunctions.loadDialogueData(owner);
        Pattern p = Pattern.compile("Bsender (.*?) Esender Bcontent (.*?) Econtent Bdate (.*?) Edate");
        Matcher m = p.matcher(inMessage);
        String sender;
        String content;
        String date;
        while (m.find()){
            sender = m.group(1);
            content = m.group(2);
            date = m.group(3);
            receiveMessage(sender, content, date);
        }
    }

}
