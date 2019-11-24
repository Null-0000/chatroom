package client.user;

import client.tools.ResizingList;

public class UserInfo {
    public int ID;
    private String password;
    public String name;
    public String sig;
    public ResizingList<String> friends;

    public UserInfo(int ID, String password, String nikeName,
                    String sig, ResizingList<String> friends) {
        this.ID = ID;
        this.password = password;
        this.name = nikeName;
        this.sig = sig;
        this.friends = friends;
    }

    protected String getPassword() {
        return password;
    }
}
