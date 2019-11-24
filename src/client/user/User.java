package client.user;

import client.*;
import client.exceptions.PasswordException;
import client.frames.UserFrame;

import java.io.IOException;

public class User {
    private UserInfo info;
    private UserCard card;
    private FriendListPanel friendListPanel;
    private UserFrame frame;


    public boolean comparePassword(String password){
        return info.getPassword().equals(password);
    }
    public User(int ID, String password) throws PasswordException, IOException {
        info = loadData(ID, password);
        if (info == null)
            throw new PasswordException();
        this.card = new UserCard(info.name, info.sig, info.ID);
        this.friendListPanel = new FriendListPanel(info.friends);
        this.frame = new UserFrame(card, friendListPanel);
    }
    private UserInfo loadData(int ID, String password) throws IOException {
        //链接服务器，获取用户图片，昵称，签名, 朋友
        UserInfo info = SocketFunctions.loadUserInfo(ID, password);
        return info;
    }

    public void makeFriend(String friend){
        info.friends.add(friend);
        friendListPanel.addMember(friend);
    }
    public void setFrameActive(){
        frame.setVisible(true);
    }

    public String toString(){
        return info.name;
    }


}
