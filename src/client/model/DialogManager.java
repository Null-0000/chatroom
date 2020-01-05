package client.model;

import javafx.beans.property.MapProperty;
import kit.GroupInfo;
import kit.Info;
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
    public void initFriendsDialog(MapProperty<Integer, Friend> friends) throws IOException {
        for (Friend friend : friends.values()) {
            UserInfo info = friend.getUserInfo();
            String friendName = friend.getFriendName();
            File friendDir = new File(mDirPath + friendName);
            if (!friendDir.exists()) friendDir.mkdir();
            File friendDialogFile = new File(
                    mDirPath + friendName + "/dialog.dat");

            //读入或初始化dialogue
            FriendDialog friendDialog = null;
            if (!friendDialogFile.exists()) {
                friendDialogFile.createNewFile();
                friendDialog = new FriendDialog(info, userName);
            } else {
                FileInputStream is = new FileInputStream(friendDialogFile);
                ObjectInputStream ois = new ObjectInputStream(is);
                try {
                    friendDialog = (FriendDialog) ois.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    ois.close();
                    is.close();
                }
            }
            friend.init(friendDialog);
        }

    }

    public void initGroupsDialog(MapProperty<Integer, Group> groups) throws IOException {
        for (Group group: groups.values()){
            GroupInfo info = group.getGroupInfo();
            String groupName = info.getName();
            File groupDir = new File(mDirPath + groupName);
            if (!groupDir.exists()) groupDir.mkdir();
            File groupDialogFile = new File(
                    mDirPath + groupName + "/dialog.dat");

            //读入或初始化dialogue
            GroupDialog groupDialog = null;
            if (!groupDialogFile.exists()) {
                groupDialogFile.createNewFile();
                groupDialog = new GroupDialog(User.getInstance().getName(),
                        info, info.getMembers());
            } else {
                FileInputStream is = new FileInputStream(groupDialogFile);
                ObjectInputStream ois = new ObjectInputStream(is);
                try {
                    groupDialog = (GroupDialog) ois.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } finally {
                    ois.close();
                    is.close();
                }
            }
            group.init(groupDialog);
        }
    }

    public void updateMyDialogues(MapProperty<Integer, Friend> friends, MapProperty<Integer, Group> groups)
            throws IOException {
        for (Friend friend : friends.values()) {
            File friendDialogFile = new File(mDirPath + friend.getFriendName() + "/dialog.dat");
            OutputStream os = new FileOutputStream(friendDialogFile);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(friend.getFriendDialog());
            oos.close();
            os.close();
        }
        for (Group group : groups.values()) {
            File friendDialogFile = new File(mDirPath + group.getGroupInfo().getName() + "/dialog.dat");
            OutputStream os = new FileOutputStream(friendDialogFile);
            ObjectOutputStream oos = new ObjectOutputStream(os);
            oos.writeObject(group.getGroupDialog());
            oos.close();
            os.close();
        }
    }

    public void storeIcon(Info info) {
        File dir = new File(mDirPath + info.getName());
        if (!dir.exists()) dir.mkdir();
        File file = new File(mDirPath + info.getName() + "/icon.jpg");
        info.setIconPath("file:" + mDirPath + info.getName() + "/icon.jpg");
        storeIcon(file, info.getIcon());
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