package client.model;

import javafx.beans.property.MapProperty;
import javafx.scene.control.Alert;

import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

public class DialoguesManager {
    private String userName;
    private File file;
    public DialoguesManager(String userName) throws IOException {
        this.userName = userName;
        file = new File("src/client/data/" + userName + ".dat");
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
    public MapProperty<String, Dialogue> initMyDialogues() throws IOException {
        MapProperty<String, Dialogue> dialogueMap = null;

        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream input;

        if(fileInputStream.available() != 0){
            input = new ObjectInputStream(fileInputStream);

            try {
                dialogueMap = (MapProperty<String, Dialogue>) input.readObject();
            } catch (ClassCastException | InvalidClassException | ClassNotFoundException e){
                JOptionPane.showMessageDialog(null, "warning: loading file error, deleting.....",
                        "alert", JOptionPane.ERROR_MESSAGE);

                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("warning: loading file error, deleting.....");

                file.delete();
            }
        }
        fileInputStream.close();

        return dialogueMap;
    }
    public void updateMyDialogues(MapProperty<String, Dialogue> dialogueMap) throws IOException {
        if (fileExist())
            file.delete();
        file.createNewFile();
        OutputStream outputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(dialogueMap);
        outputStream.close();
        objectOutputStream.close();
    }
}
