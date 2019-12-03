package client.model;

import java.util.ArrayList;

public class UserInfo {
    private String name;
    private String signature;
    private int ID;
    private ArrayList<String> friendList;

    private static UserInfo instance = new UserInfo();
    public static UserInfo getInstance(){
        return instance;
    }

    public void setField(int ID, String name, String signature, ArrayList<String> friendList){
        this.ID = ID;
        this.name = name;
        this.signature = signature;
        this.friendList = friendList;
    }
}