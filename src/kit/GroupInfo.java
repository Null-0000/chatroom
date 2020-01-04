package kit;

import client.model.UserCard;

import java.io.Serializable;
import java.util.List;

public class GroupInfo extends Info implements Serializable {
    private UserCard groupCard;
    private List<UserInfo> members;
    private int groupOwner;
    public GroupInfo(int groupID, String groupName, byte[] groupIcon, List<UserInfo> members, int groupOwner){
        super(groupID, groupName, groupIcon);
        this.members = members;
        this.groupOwner = groupOwner;
    }


    public void prepareGroupCard() {
        groupCard = new UserCard(ID, name, "group", iconPath);
    }

    public UserCard getGroupCard() {
        return groupCard;
    }

    public void setGroupCard(UserCard groupCard) {
        this.groupCard = groupCard;
    }

    public List<UserInfo> getMembers() {
        return members;
    }

    public void setMembers(List<UserInfo> members) {
        this.members = members;
    }
}
