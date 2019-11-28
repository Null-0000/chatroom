package client.user;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Dialogues {
    private static Map<String, Dialogue> dialogueMap;//a dialogue with receiver
    private static String userName;

    public Dialogues(String userName) {
        dialogueMap = new HashMap<>();
        this.userName = userName;

    }
    public void setName(String userName){
        this.userName = userName;
    }

    public static void updateDialogue(Message message, String friendName){
        Dialogue dialogue = dialogueMap.get(friendName);

        if(dialogue == null){
            dialogue = new Dialogue(friendName);
            dialogue.updateMessage(message);
            dialogueMap.put(friendName, dialogue);
        } else {
            dialogue.updateMessage(message);
        }
    }
    public static ArrayList<Message> getAllDialogue(String friendName){
        return dialogueMap.get(friendName).getMessageArrayList();
    }
    public static ArrayList<Message> getPeriodDialogue(String friendName, Date date){
        Dialogue dialogue = dialogueMap.get(friendName);
        if(dialogue == null) return null;
        return dialogue.getPeriodMessage(date);
    }
}
