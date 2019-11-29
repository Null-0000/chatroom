package client.user;

import client.tools.SocketFunctions;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dialogues implements Serializable {
    private Map<String, Dialogue> dialogueMap;//a dialogue with receiver
    private String userName;

    public Dialogues(String userName, ArrayList<String> friends) {
        this.dialogueMap = new HashMap<>();
        this.userName = userName;
        for (String friend: friends){
            dialogueMap.put(friend, new Dialogue(friend, userName));
        }

    }
    public void setName(String userName){
        this.userName = userName;
    }
    public void updateDialogue(Message message, String friendName){
        Dialogue dialogue = dialogueMap.get(friendName);

        if(dialogue == null){
            dialogue = new Dialogue(friendName, userName);
            dialogue.updateMessage(message);
            dialogueMap.put(friendName, dialogue);
        } else {
            dialogue.updateMessage(message);
        }
    }
    /*public static ArrayList<Message> getAllDialogue(String friendName){
        return dialogueMap.get(friendName).getMessageArrayList();
    }
    public static ArrayList<Message> getPeriodDialogue(String friendName, Date date){
        Dialogue dialogue = dialogueMap.get(friendName);
        if(dialogue == null) return null;
        return dialogue.getPeriodMessage(date);
    }

     */
    public Set<String> getAllFriendName(){
        return dialogueMap.keySet();
    }

    public void setChattingFrameVisible(String friendName) {
        dialogueMap.get(friendName).setChattingFrameVisible();
    }
    //从远程数据库读取用户下线时接收地数据
    public void loadRemoteData() throws IOException {
        String inMessage = SocketFunctions.loadDialogueData(userName);
        Pattern p = Pattern.compile("Bsender (.*?) Esender Bcontent (.*?) Econtent Bdatetime (.*?) Edatetime");
        Matcher m = p.matcher(inMessage);
        String sender;
        String content;
        Date date;
        while (m.find()){
            sender = m.group(1);
            content = m.group(2);
            date = new Date(Long.parseLong(m.group(3)));
            Message message = new Message(userName, sender, content, date);
            updateDialogue(message, sender);
        }
    }

}
