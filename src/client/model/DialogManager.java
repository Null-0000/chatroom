package client.model;

import javafx.beans.property.MapProperty;
import kit.UserInfo;

import java.io.*;

public class DialogManager {
    private String userName;
    private String mDirPath;
    private File mDir;

    public DialogManager(String userName) {
        this.userName = userName;
        mDirPath = "out/production/chatroom/client/data/" + userName + "/";
        mDir = new File(mDirPath);
        if (!mDir.exists()) mDir.mkdir();
        //ShowDialog.showMessage("正在构造DialoguesManager\n" + file.exists());

        //写入User自己的图片文件
        File userIcon = new File("out/production/chatroom/client/data/" + userName + "/icon.png");
        storeIcon(userIcon, User.getInstance().getMyIconBytes());

    }

    /**
     * initialise the user's dialogues from the data file
     * if the data file goes wrong ,then pop a alert message dialogue
     *
     * @author Furyton
     * @since 11.27
     */
    public void initMyDialogues(MapProperty<String, Friend> friends) throws IOException {
        for (Friend friend : friends.values()) {
            String friendName = friend.getFriendName();
            File friendDir = new File(mDirPath + friendName);
            if (!friendDir.exists()) friendDir.mkdir();
            File friendDialogFile = new File(
                    mDirPath + friendName + "/dialog.dat");

            //写入朋友头像
            storeIcon(friend.getUserInfo());

            //初始化UserCard
            friend.getUserInfo().prepareUserCard();

            //读入或初始化dialogue
            FriDialog friDialog = null;
            if (!friendDialogFile.exists()) {
                friendDialogFile.createNewFile();
                friDialog = new FriDialog(friendName, userName);
            } else {
                InputStream is = new FileInputStream(friendDialogFile);
                ObjectInputStream ois = new ObjectInputStream(is);
                try {
                    friDialog = (FriDialog) ois.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    ois.close();
                    is.close();
                }
            }
            friend.init(friDialog);
        }

    }

    public void updateMyDialogues(MapProperty<String, Friend> friends) throws IOException {
        for (Friend friend : friends.values()) {
            File friendDialogFile = new File(mDirPath + friend.getFriendName() + "/dialog.dat");
            OutputStream os = new FileOutputStream(friendDialogFile);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(friend.getFriDialog());
            oos.close();
            os.close();
        }
    }

    public void storeIcon(UserInfo friendInfo) {
        UserInfo info = friendInfo;
        File friendDir = new File(mDirPath + info.getName());
        if (!friendDir.exists()) friendDir.mkdir();
        File file = new File(mDirPath + info.getName() + "/icon.jpg");
        friendInfo.setIconPath("file:" + mDirPath + info.getName() + "/icon.jpg");
        storeIcon(file, friendInfo.getIcon());
    }

    public void storeIcon(File file, byte[] bytes) {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bytes);
            fos.flush();
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getMDirPath() {
        return mDirPath;
    }

}