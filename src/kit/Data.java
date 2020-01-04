package kit;

import client.model.User;

import java.io.Serializable;
import java.util.List;

public class Data implements Serializable {
    public static final String LOAD_USER_INFO = "loadUserInfo";
    public static final String REGISTER = "register";
    public static final String ADD_FRIEND = "makeFriendWith";
    public static final String CONNECT = "connect";
    //public static final String LOAD_DIALOGUE = "loadDialogueData";
    public static final String LOAD_MESSAGE = "loadFriendMessage";
    public static final String EXIT = "exit";
    public static final String CREATE_GROUP = "createNewGroup";
    public static final String JOIN_GROUP = "joinInAGroup";
    public static final String GET_GROUP_MEM = "getGroupMemberList";

    private String operateType;

    public int ID;
    public String name;
    public String signature;
    public String password;
    public List listA;
    public List listB;
    public byte[] iconBytes;
    public Message message;
    public String operator;
    public int operatorID;
    public String builder;


    public UserInfo oprUserInfo;

    public Data(){}
    public Data(int ID, String name, String signature, List listA, byte[] iconBytes) {//load user info
        this.ID = ID;
        this.name = name;
        this.signature = signature;
        this.listA = listA;
        this.iconBytes = iconBytes;
    }
    public void setOperator(){
        operator = User.getInstance().getName();
        oprUserInfo = User.getInstance().getUserInfo();
        operatorID = User.getInstance().getID();
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
        this.iconBytes = info.getIcon();
    }

    public Data(int ID){//get ID
        this.ID = ID;
    }

    public Data(String name){//search friend
        this.name = name;
    }

    public Data(Message message){
        this.message = message;
    }

    public Data(String name, String password, String signature, byte[] iconBytes) {//register
        this.name = name;
        this.password = password;
        this.signature = signature;
        this.iconBytes = iconBytes;
    }

    public Data(List listA){
        this.listA = listA;
    }

    public void setOperateType(String operateType){
        this.operateType = operateType;
    }
    public boolean isOperate(String operateType){
        if (this.operateType == null) return false;
        return this.operateType.equals(operateType);
    }
}
