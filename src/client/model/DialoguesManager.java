package client.model;

import javafx.beans.property.MapProperty;
import javafx.scene.control.Alert;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class DialoguesManager {
    private String userName;
    private File file;
    public DialoguesManager(String userName) throws IOException {
        this.userName = userName;
        file = new File("src/client/data/" + userName);
    }
    public boolean fileExist(){
        return file.exists();
    }
    /**
     * initialise the user's dialogues from the data file
     * if the data file goes wrong ,then pop a alert message dialogue
     * @author Furyton
     * @since 11.27
     */
    public Map<String, Dialogue> initMyDialogues() throws IOException {
        Map<String, Dialogue> dialogueMap = new HashMap<>();
        for (String friendFileName: file.list()){
            String friend = friendFileName.substring(0, friendFileName.length() - 4);
            InputStream is = new FileInputStream(file.getPath() + "/" + friendFileName);
            ObjectInputStream ois = new ObjectInputStream(is);
            try {
                Dialogue dialogue = (Dialogue) ois.readObject();
                dialogue.setChatView();
                dialogueMap.put(friend, dialogue);
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            ois.close();
            is.close();
        }
        return dialogueMap;
    }
    public void updateMyDialogues(Map<String, Dialogue> dialogueMap) throws IOException {
        if (!fileExist()){
            file.mkdir();
        }
        for (String friend: dialogueMap.keySet()) {
            File friendFile = new File(file.getPath() + "/" + friend + ".dat");
            OutputStream outputStream = new FileOutputStream(friendFile);
            ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
            objectOutputStream.writeObject(dialogueMap.get(friend));
            outputStream.close();
            objectOutputStream.close();
        }
    }
}
