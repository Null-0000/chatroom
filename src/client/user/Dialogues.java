package client.user;

import client.tools.ResizingList;
import client.tools.SocketFunctions;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dialogues {
    private ResizingList<Dialogue> dialogues;
    private String owner;
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
    public void receiveMessage(String sender, String content){
        for (Dialogue dialogue: dialogues){
            if (dialogue.p2.equals(sender)) {
                dialogue.addMessage(sender, owner, content);
                break;
            }
        }
    }
    public void sendMessages(String receiver, String content){
        for (Dialogue dialogue: dialogues){
            if (dialogue.p2.equals(receiver)){
                dialogue.addMessage(owner, receiver, content);
                break;
            }
        }
    }

    //从远程数据库读取用户下线时接收地数据
    private void loadRemoteData() throws IOException {
        String inMessage = SocketFunctions.loadDialogueData(owner);
        Pattern p = Pattern.compile("Bsender (.*?) Esender Bcontent (.*?) Econtent");
        Matcher m = p.matcher(inMessage);
        String sender;
        String content;
        while (m.find()){
            sender = m.group(1);
            content = m.group(2);
            receiveMessage(sender, content);
        }
    }

}
