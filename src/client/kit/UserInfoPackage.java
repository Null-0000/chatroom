package client.kit;

import java.io.Serializable;
import java.util.ArrayList;

public class UserInfoPackage implements Serializable {
    public int ID;
    public String name;
    public String signature;
    public String password;
    public ArrayList<String> friendList;
    public byte[] myIconBytes;

    public UserInfoPackage(int ID, String name, String signature, ArrayList<String> friendList, byte[] myIconBytes) {
        this.ID = ID;
        this.name = name;
        this.signature = signature;
        this.friendList = friendList;
        this.myIconBytes = myIconBytes;
    }

    public UserInfoPackage(int ID, String password) {
        this.ID = ID;
        this.password = password;
    }
}
