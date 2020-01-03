package kit;

import client.model.UserCard;

import java.io.Serializable;

public class UserInfo implements Serializable {
    private int ID;
    private String name;
    private String sig;
    private byte[] icon;
    private String iconPath;
    private UserCard userCard;
    public UserInfo(int ID, String name, String sig, byte[] icon){
        this.ID = ID;
        this.name = name;
        this.sig = sig;
        this.icon = icon;
    }

    public UserCard getUserCard() {
        return userCard;
    }
    public void prepareUserCard() {
        UserCard card = new UserCard(name, sig, iconPath);
        userCard = card;
    }


    public int getID() {
        return ID;
    }
    public void setID(int ID) {
        this.ID = ID;
    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getSig() {
        return sig;
    }
    public void setSig(String sig) {
        this.sig = sig;
    }
    public byte[] getIcon() {
        return icon;
    }
    public void setIcon(byte[] icon) {
        this.icon = icon;
    }
    public String getIconPath() {
        return iconPath;
    }
    public void setIconPath(String iconPath) {
        this.iconPath = iconPath;
    }

    public String toString() {
        return String.format("{ID: %d, name: %s}", ID, name);
    }

}
