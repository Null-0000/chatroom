package client.user;

import javax.swing.*;
import java.io.*;

public class LocalDialoguesManager {
    private static String userName;
    private static File file;

    public static void setUserFile(String _userName) throws IOException {
        userName = _userName;
        file = new File("src/client/data/" + userName + ".dat");
    }
    public static boolean fileExist(){
        return file.exists();
    }
    /**
     * initialise the user's dialogues from the data file
     * if the data file goes wrong ,then pop a alert message dialogue
     * @author Furyton
     * @since 11.27
     */
    public static Dialogues initMyDialogues() throws IOException {
        Dialogues dialogues = null;

        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream input = null;

        if(fileInputStream.available() != 0){
            input = new ObjectInputStream(fileInputStream);

            try {
                dialogues = (Dialogues) input.readObject();
            } catch (ClassCastException | InvalidClassException | ClassNotFoundException e){
                JOptionPane.showMessageDialog(null, "warning: loading file error, deleting.....",
                        "alert", JOptionPane.ERROR_MESSAGE);

                file.delete();
            }
        }
        fileInputStream.close();

        return dialogues;
    }
    public static void updateMyDialogues(Dialogues dialogues) throws IOException {
        if (fileExist())
            file.delete();
        file.createNewFile();
        OutputStream outputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(dialogues);
        outputStream.close();
        objectOutputStream.close();
    }
}
