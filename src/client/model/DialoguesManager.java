package client.model;

import javafx.beans.property.ListProperty;
import javafx.scene.control.Alert;
import kit.ShowDialog;
import kit.UserInfo;

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

        File userIcon = new File("out/production/chatroom/client/data/" + userName + "/icon.png");
        if (!userIcon.exists()) {
            try {
                userIcon.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(userIcon);
            fos.write(User.getInstance().getMyIconBytes());
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
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
        for(UserInfo friendInfo: User.getInstance().getFriendList()){
            Dialogue d = myDialogues.get(friendInfo.getName());
            if(d == null) {
                d = new Dialogue(friendInfo.getName(), User.getInstance().getName());
                myDialogues.put(friendInfo.getName(), d);
            }
            d.setChatView();


            File iconFile = new File(directory + "/" + friendInfo.getName() + "/icon.jpg");
            friendInfo.setIconPath("file:" + directory + "/" + friendInfo.getName() + "/icon.jpg");
            if (!iconFile.exists()) iconFile.createNewFile();
            FileOutputStream fos = new FileOutputStream(iconFile);
            fos.write(friendInfo.getIcon());
            fos.flush();
            fos.close();

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
        for (UserInfo friendInfo: User.getInstance().getFriendList()){
            new File(directory + "/" + friendInfo.getName()).mkdir();
            new File(directory + "/" + friendInfo.getName() + "/" + "images").mkdir();
            new File(directory + "/" + friendInfo.getName() + "/" + "audios").mkdir();
            myDialogues.put(friendInfo.getName(), new Dialogue(friendInfo.getName(), User.getInstance().getName()));
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