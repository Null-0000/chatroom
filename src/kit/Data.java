package kit;

import java.io.Serializable;
import java.util.ArrayList;

public class Data implements Serializable {
    public static final String LOAD_USER_INFO = "loadUserInfo";
    public static final String REGISTER = "register";
    public static final String ADD_FRIEND = "makeFriendWith";
    public static final String CONNECT = "connect";
    public static final String LOAD_DIALOGUE = "loadDialogueData";
    public static final String EXIT = "exit";
    public static final String CREATE_GROUP = "createNewGroup";
    public static final String JOIN_GROUP = "joinInAGroup";
    public static final String GET_GROUP_MEM = "getGroupMemberList";

    private String operateType;

    public int ID;
    public String name;
    public String signature;
    public String password;
    public ArrayList<UserInfo> friendList;
    public byte[] myIconBytes;
    public ArrayList<Message> messages;
    public Message message;
    public String operator;
    public UserInfo oprInfo;

    public Data(){}
    public Data(int ID, String name, String signature, ArrayList<UserInfo> friendList, byte[] myIconBytes) {//load user info
        this.ID = ID;
        this.name = name;
        this.signature = signature;
        this.friendList = friendList;
        this.myIconBytes = myIconBytes;
    }

    public Data(int ID, String password) {//log in data package
        this.ID = ID;
        this.password = password;
    }
    public Data(String name, int ID){
        this.ID = ID;
        this.name  = name;
    }
    public Data(UserInfo info){
        this.ID = info.getID();
        this.name = info.getName();
        this.signature = info.getSig();
        this.myIconBytes = info.getIcon();
    }
    public Data(int ID){//get ID
        this.ID = ID;
    }

    public Data(String name){//search friend
        this.name = name;
    }

    public Data(ArrayList<Message> messages){
        this.messages = messages;
    }
    public Data(Message message){
        this.message = message;
    }

    public Data(String name, String password, String signature, byte[] myIconBytes) {//register
        this.name = name;
        this.password = password;
        this.signature = signature;
        this.myIconBytes = myIconBytes;
    }

    public void setOperateType(String operateType){
        this.operateType = operateType;
    }
    public boolean isOperate(String operateType){
        return this.operateType.equals(operateType);
    }

}
