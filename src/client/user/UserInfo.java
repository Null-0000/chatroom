package client.user;

import client.tools.ResizingList;

import java.util.ArrayList;

public class UserInfo {
    public int ID;
    public String name;
    public String sig;
    public ArrayList<String> friends;

    public UserInfo(int ID, String nikeName,
                    String sig, ArrayList<String> friends) {
        this.ID = ID;
        this.name = nikeName;
        this.sig = sig;
        this.friends = friends;
    }
}
