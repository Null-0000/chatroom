//package client.model;
//
//import javafx.scene.control.Alert;
//import kit.ShowDialog;
//
//import java.io.*;
//import java.util.HashMap;
//import java.util.Map;
//
//public class DialoguesManager {
//    private String userName;
//    private String fileName;
//    private String directory = "src/client/data";
//    private File file;
//    public DialoguesManager(String userName) {
//        this.userName = userName;
//        fileName = this.userName + ".dat";
//        file = new File(directory + "/" + fileName);
//
//        //ShowDialog.showMessage("正在构造DialoguesManager\n" + file.exists());
//
//        if(!file.exists()){
//            (new File(directory)).mkdir();
//            try {
//                file.createNewFile();
//            } catch (IOException e) {
//                e.printStackTrace();
//                ShowDialog.showAlert("创建用户数据文件错误");
//            }
//        }
//    }
//    /**
//     * initialise the user's dialogues from the data file
//     * if the data file goes wrong ,then pop a alert message dialogue
//     * @author Furyton
//     * @since 11.27
//     */
//    public Map<String, Dialogue> initMyDialogues() throws IOException {
//        FileInputStream fileInputStream = new FileInputStream(file);
//        ObjectInputStream input;
//
//        Map<String, Dialogue> myDialogues = null;
//
//        if(fileInputStream.available() != 0){
//            try {
//                input = new ObjectInputStream(fileInputStream);
//
//                myDialogues = (Map<String, Dialogue>)input.readObject();
//            } catch (Exception e){
//                Alert alert = new Alert(Alert.AlertType.WARNING);
//                alert.setTitle("Error");
//                alert.setHeaderText("warning: loading file error, deleting.....");
//                alert.showAndWait();
//                (new FileOutputStream(file)).close();
//            }
////            ShowDialog.showMessage("本地文件不为空");
//        }
//        fileInputStream.close();
//
//        if(myDialogues == null) myDialogues = getNewMap();
//        for(String friend: User.getInstance().getFriendList()){
//            Dialogue d = myDialogues.get(friend);
//            if(d == null) {
//                d = new Dialogue(friend, User.getInstance().getName());
//                myDialogues.put(friend, d);
//            }
//            d.setChatView();
//        }
////        for(String friend: User.getInstance().getFriendList()){
////            Dialogue d = myDialogues.get(friend);
////            if(d != null){
////                d.loadLocalDialogues();
////            }
////        }
//
//        return myDialogues;
//    }
//    private Map<String, Dialogue> getNewMap() throws IOException {
//        Map<String, Dialogue> myDialogues = new HashMap<>();
//        for (String friend: User.getInstance().getFriendList()){
//            myDialogues.put(friend, new Dialogue(friend, User.getInstance().getName()));
//        }
//
//        return myDialogues;
//    }
//    public void updateMyDialogues(Map<String, Dialogue> dialogues) throws IOException {
//        OutputStream outputStream = new FileOutputStream(file);
//        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
//        objectOutputStream.writeObject(dialogues);
//        outputStream.close();
//        objectOutputStream.close();
//    }
//}
package client.model;

import javafx.scene.control.Alert;
import kit.ShowDialog;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class DialoguesManager {
    private String userName;
    private String fileName;
    private String directory;
    private File file;
    public DialoguesManager(String userName) {
        this.userName = userName;
        directory = "out/production/chatroom/client/data/" + userName;
        fileName = this.userName + ".dat";
        file = new File(directory + "/" + fileName);

        //ShowDialog.showMessage("正在构造DialoguesManager\n" + file.exists());

        if(!file.exists()){
            (new File(directory)).mkdir();
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                ShowDialog.showAlert("创建用户数据文件错误");
            }
        }
    }
    /**
     * initialise the user's dialogues from the data file
     * if the data file goes wrong ,then pop a alert message dialogue
     * @author Furyton
     * @since 11.27
     */
    public Map<String, Dialogue> initMyDialogues() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(file);
        ObjectInputStream input;

        Map<String, Dialogue> myDialogues = null;

        if(fileInputStream.available() != 0){
            input = new ObjectInputStream(fileInputStream);
            try {
                myDialogues = (Map<String, Dialogue>)input.readObject();
            } catch (ClassCastException | InvalidClassException | ClassNotFoundException e){
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Error");
                alert.setHeaderText("warning: loading file error, deleting.....");
                alert.showAndWait();
                (new FileOutputStream(file)).close();
            }
//            ShowDialog.showMessage("本地文件不为空");
        }
        fileInputStream.close();

        if(myDialogues == null) myDialogues = getNewMap();
        for(String friend: User.getInstance().getFriendList()){
            Dialogue d = myDialogues.get(friend);
            if(d == null) {
                d = new Dialogue(friend, User.getInstance().getName());
                myDialogues.put(friend, d);
            }
            d.setChatView();
        }
//        for(String friend: User.getInstance().getFriendList()){
//            Dialogue d = myDialogues.get(friend);
//            if(d != null){
//                d.loadLocalDialogues();
//            }
//        }

        return myDialogues;
    }
    private Map<String, Dialogue> getNewMap() throws IOException {
        Map<String, Dialogue> myDialogues = new HashMap<>();
        for (String friend: User.getInstance().getFriendList()){
            new File(directory + "/" + friend).mkdir();
            new File(directory + "/" + friend + "/" + "images").mkdir();
            new File(directory + "/" + friend + "/" + "audios").mkdir();
            myDialogues.put(friend, new Dialogue(friend, User.getInstance().getName()));
        }

        return myDialogues;
    }
    public void updateMyDialogues(Map<String, Dialogue> dialogues) throws IOException {
        OutputStream outputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);
        objectOutputStream.writeObject(dialogues);
        outputStream.close();
        objectOutputStream.close();
    }
}