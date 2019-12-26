package kit;

import java.io.Serializable;
import java.util.ArrayList;

public class DataPackage implements Serializable {
    public String operateType;
    /*
    * load user info
    * register
    *
     */
    public int ID;
    public String name;
    public String signature;
    public String password;
    public ArrayList<UserInfo> friendList;
    public byte[] myIconBytes;
    public ArrayList<Message> messages;
    public Message message;
    public String operator;

    public DataPackage(){}
    public DataPackage(int ID, String name, String signature, ArrayList<UserInfo> friendList, byte[] myIconBytes) {//load user info
        this.ID = ID;
        this.name = name;
        this.signature = signature;
        this.friendList = friendList;
        this.myIconBytes = myIconBytes;
    }

    public DataPackage(int ID, String password) {//log in data package
        this.ID = ID;
        this.password = password;
    }
    public DataPackage(String name, int ID){
        this.ID = ID;
        this.name  = name;
    }
    public DataPackage(UserInfo info){
        this.ID = info.getID();
        this.name = info.getName();
        this.signature = info.getSig();
        this.myIconBytes = info.getIcon();
    }
    public DataPackage(int ID){//get ID
        this.ID = ID;
    }

    public DataPackage(String name){//search friend
        this.name = name;
    }

    public DataPackage(ArrayList<Message> messages){
        this.messages = messages;
    }
    public DataPackage(Message message){
        this.message = message;
    }

    public DataPackage(String name, String password, String signature, byte[] myIconBytes) {//register
        this.name = name;
        this.password = password;
        this.signature = signature;
        this.myIconBytes = myIconBytes;
    }

    public void setOperateType(String operateType){
        this.operateType = operateType;
    }
}
