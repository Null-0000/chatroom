package client.user;

import java.io.Serializable;
import java.util.ArrayList;

public class UserInfo implements Serializable {
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
