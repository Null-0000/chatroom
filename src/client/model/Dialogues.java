package client.model;

import client.controller.Connector;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Dialogues implements Serializable {
    private Map<String, Dialogue> dialogueMap;
    private String userName;

    public Dialogues(String userName, ArrayList<String> friends) throws IOException, InterruptedException {
        this.dialogueMap = new HashMap<>();
        this.userName = userName;
        for (String friend: friends){
            dialogueMap.put(friend, new Dialogue(friend, userName));
        }
    }
    public void setName(String userName){
        this.userName = userName;
    }
    public void updateDialogue(Message message) throws IOException {
        String from = message.sender;
        Dialogue dialogue = dialogueMap.get(from);
        if(dialogue == null){
            dialogue = new Dialogue(from, userName);
            dialogue.updateMessage(message);
            dialogueMap.put(from, dialogue);
        } else {
            dialogue.updateMessage(message);
        }
    }
    public Dialogue getAllDialogue(String friendName){
        return dialogueMap.get(friendName);
    }
    /*public static ArrayList<Message> getPeriodDialogue(String friendName, Date date){
        Dialogue dialogue = dialogueMap.get(friendName);
        if(dialogue == null) return null;
        return dialogue.getPeriodMessage(date);
    }

     */
    public Set<String> getAllFriendName(){
        return dialogueMap.keySet();
    }

//    public void setChattingFrameVisible(String friendName) {
//        dialogueMap.get(friendName).setChattingFrameVisible();
//    }
    //从远程数据库读取用户下线时接收地数据
    public void loadRemoteData() throws IOException, InterruptedException {
        String inMessage = Connector.getInstance().loadDialogueData();
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
            updateDialogue(message);
        }
    }
}
