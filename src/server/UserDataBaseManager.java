package server;

import javafx.application.Platform;
import kit.*;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class UserDataBaseManager {
    private final String driver = "com.mysql.cj.jdbc.Driver";
    private final String url = "jdbc:mysql://localhost:3306/chat_room?serverTimezone=Asia/Shanghai";
    private final String user = "henry";
    private final String pass = "mxylfbcz4321";
    private Connection conn;

    public UserDataBaseManager() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        conn = DriverManager.getConnection(url, user, pass);
    }

    public int register(Data data) throws SQLException {
        addCurrentUsersAmount();
        int ID = getCurrentID();
        byte[] icon = data.iconBytes;
        PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO users_info(ID, name, signature, password, icon) VALUES(?, ?, ?, ?, ?)");
        pstmt.setInt(1, ID);
        pstmt.setString(2, data.name);
        pstmt.setString(3, data.signature);
        pstmt.setString(4, data.password);
        pstmt.setBlob(5, new ByteArrayInputStream(icon));
        pstmt.executeUpdate();
        pstmt.close();
        return ID;
    }

    private int getCurrentID() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT current_ID FROM global_info");
        int ID = -1;
        if (rs.next())
            ID = rs.getInt(1);
        stmt.executeUpdate("UPDATE global_info SET current_ID=" + (ID + 1) + " WHERE current_ID=" + ID);
        stmt.close();
        return ID;
    }

    private void addCurrentUsersAmount() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT users FROM global_info");
        if (rs.next())
            stmt.executeUpdate("UPDATE global_info SET users=" + (rs.getInt(1) + 1) + " WHERE users=" + rs.getInt(1));
        stmt.close();
    }

    public Data selectByIDAndPassword(int id, String password) throws SQLException {
        if (id < 0) return null;
        Statement stmt = conn.createStatement();
        ResultSet rs1;
        String name;
        String sig;
        byte[] icon;
        try {
            PreparedStatement pstmt = conn.prepareStatement("select * from users_info where id=" + id);
            rs1 = pstmt.executeQuery();
            if(!rs1.next()) return null;
            if (!rs1.getString(4).equals(password))
                return null;
            name = rs1.getString(2);
            sig = rs1.getString(3);
            Blob iconBlob = rs1.getBlob(5);
            icon = iconBlob.getBinaryStream().readAllBytes();
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return null;
        }
        ResultSet rs2;
        ResultSet rs3;
        ArrayList<UserInfo> friendList = new ArrayList<>();
        ArrayList<GroupInfo> groupList = new ArrayList<>();

        try {
            //find friends
            rs2 = stmt.executeQuery("select friend_id from friend_map where id=" + id);
            while (rs2.next()) {
                int friend_id = rs2.getInt(1);
                Statement stmt2 = conn.createStatement();
                rs3 = stmt2.executeQuery("select * from users_info where ID = " + friend_id);
                if (rs3.next()) {
                    Blob blob = rs3.getBlob(5);
                    friendList.add(new UserInfo(friend_id, rs3.getString(2), rs3.getString(3),
                            blob.getBinaryStream().readAllBytes()));
                }
            }

            //find group
            rs2 = stmt.executeQuery("select group_id from users_group where id=" + id);
            while (rs2.next()) {
                int groupID = rs2.getInt(1);
                Statement stmt2 = conn.createStatement();
                rs3 = stmt2.executeQuery("select * from group_info where group_id=" + groupID);
                if (rs3.next()) {
                    Blob blob = rs3.getBlob(6);
                    ArrayList<UserInfo> list = (ArrayList<UserInfo>) getMembers(groupID, false);
                    groupList.add(new GroupInfo(groupID, rs3.getString(2),
                            blob.getBinaryStream().readAllBytes(), list, id));
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        stmt.close();
        Data data = new Data();
        data.ID = id;
        data.name = name;
        data.signature = sig;
        data.listA = friendList;
        data.listB = groupList;
        data.iconBytes = icon;
        return data;
    }


    public Data makeFriend(String name, int ID) throws SQLException, IOException {
        Statement stmt = conn.createStatement();

        int friend_id;
        UserInfo userInfo;
        ResultSet rs = stmt.executeQuery("SELECT * FROM users_info WHERE name= \'" + name + "\'");

        if (rs.next()) {
            friend_id = rs.getInt(1);
            Blob blob = rs.getBlob(5);
            userInfo = new UserInfo(friend_id, name,
                    rs.getString(3), blob.getBinaryStream().readAllBytes());
        } else {
            return new Data(-1);
        }

        stmt.executeUpdate("INSERT INTO friend_map(id,friend_id) " +
                "VALUES(" + ID + "," + friend_id + ")");
        stmt.executeUpdate("INSERT INTO friend_map(id,friend_id) " +
                "VALUES(" + friend_id + "," + ID + ")");

        stmt.close();
        return new Data(userInfo);
    }

    public void storeMessage(Message message) throws Exception {
        if (!message.isMass) storeMsg(message, -1);
        else {
            ArrayList<Info> list = (ArrayList<Info>) getMembers(message.receiver.getID(), false);

            for (Info recID : list) {
                if(recID.getID() == message.sender.getID()) continue;
                Message msg = new Message(recID, message.sender, message.ctype, message.content, message.date, true);
                storeMsg(msg, message.receiver.getID());
            }
        }
    }

    private void storeMsg(Message message, int fromGroup) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO messages(receiver, sender, ctype, content, datetime, fromgrp) VALUES(?, ?, ?, ?, ?, ?)");
        pstmt.setInt(1, message.receiver.getID());
        pstmt.setInt(2, message.sender.getID());
        pstmt.setString(3, message.ctype);
        pstmt.setBlob(4, new ByteArrayInputStream(message.content));
        pstmt.setTimestamp(5, new Timestamp(message.date.getTime()));
        pstmt.setInt(6, fromGroup);
        pstmt.execute();
    }

    public Data loadDialogues(int ID) throws Exception {
        PreparedStatement pstmt = conn.prepareStatement("SELECT  * FROM messages WHERE receiver=?");
        pstmt.setInt(1, ID);
        ResultSet rs = pstmt.executeQuery();
        ArrayList<Message> dialogues = new ArrayList<>();
        while (rs.next()) {
            int sender = rs.getInt("sender");
            String ctype = rs.getString("ctype");
            Blob contentBlob = rs.getBlob("content");
            byte[] content = new byte[(int) contentBlob.length()];
            InputStream inputStream = contentBlob.getBinaryStream();
            try {
                inputStream.read(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Date date = new Date(rs.getTimestamp("datetime").getTime());
//            boolean isMass = rs.getBoolean(6);
            int fromGroup = rs.getInt("fromgrp");
            Message newMsg;

//            System.out.println("sender :" + sender + " , fromgrp :" + fromGroup + " , receiver: " + ID);

            if (fromGroup == -1) newMsg = new Message(getInfo(ID, false), getInfo(sender, false), ctype, content, date, false);
            else newMsg = new Message(getInfo(fromGroup, true), getInfo(sender, false), ctype, content, date, true);
//            System.out.println(newMsg);
            dialogues.add(newMsg);
        }
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM messages WHERE receiver=" + ID + "");
        stmt.close();

//        updateLog(null, dialogues + "");

        return new Data(dialogues);
    }

    private Info getInfo(int id, boolean isGroup) throws Exception {
        int ID = id;
        String name = null;
        byte[] icon = new byte[0];

        Statement stmt = conn.createStatement();
        ResultSet resultSet;

        if (isGroup) {
            resultSet = stmt.executeQuery("SELECT * FROM group_info WHERE group_id = " + id);

            if(resultSet.next()){
                name = resultSet.getString("group_name");
                icon = resultSet.getBlob("icon").getBinaryStream().readAllBytes();
            } else {
                return null;
            }
        } else {
            resultSet = stmt.executeQuery("SELECT * FROM users_info WHERE ID = " + id);

            if(resultSet.next()){
                name = resultSet.getString("name");
                icon = resultSet.getBlob("icon").getBinaryStream().readAllBytes();

            } else {
                return null;
            }
        }

        return new Info(ID, name, icon);
    }

    /**
     * @author Furyton
     * @since 1.4 16:00
     */
    private void addCurrentGroupAmount() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT grps FROM global_info");
        if (rs.next()) {
            stmt.executeUpdate("UPDATE global_info SET grps=" + (rs.getInt(1) + 1) + " WHERE grps=" + rs.getInt(1));
        }
        stmt.close();
    }

    /**
     * @author Furyton
     * @since 1.4 16:00
     */
    private int getCurrentGroupID() throws SQLException {
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT current_group_ID FROM global_info");
        int ID = -1;
        if (rs.next())
            ID = rs.getInt(1);
        stmt.executeUpdate("UPDATE global_info SET current_group_ID=" + (ID + 1) + " WHERE current_group_ID=" + ID);
        return ID;
    }

    /**
     * @author Furyton
     * @since 1.4 16:00
     */
    public int createGroup(Data data) {
        int ID;
        try {
            addCurrentGroupAmount();
            ID = getCurrentGroupID();
            PreparedStatement pstmt = conn.prepareStatement("INSERT INTO group_info" +
                    "(group_id, group_name, builder, member_count, members, icon) VALUES(?, ?, ?, ?, ?, ?)");
            pstmt.setInt(1, ID);
            pstmt.setString(2, data.name);
            pstmt.setString(3, data.operatorInfo.getName());
            pstmt.setInt(4, 1);
            pstmt.setString(5, "" + data.operatorInfo.getID());
            pstmt.setBlob(6, new ByteArrayInputStream(data.iconBytes));
            pstmt.executeUpdate();

            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO users_group(id, group_id) " + "VALUES(" + data.operatorInfo.getID() + "," + ID + ")");
        } catch (Exception e) {
            e.printStackTrace();
            updateLog(data.operatorInfo.getName(), "创建群聊过程中，数据库操作出现错误");
            return -1;
        }

        return ID;
    }

    /**
     * @author Furyton
     * @since 1.4 16:00
     */
    public Data joinGroup(Data info) {
        //name, groupID
        //更新group members，count
        //更新用户的group info
        Data data = new Data();
        try {
//            String name = info.operatorInfo.getName();
            int user_id = info.operatorInfo.getID();
            int group_id = -1;
            boolean found = false;
            if (info.name != null) {
                PreparedStatement pstmt = conn.prepareStatement("select * from group_info where group_name=?");
                pstmt.setString(1, info.name);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    group_id = rs.getInt(1);
                    String groupName = rs.getString(2);
                    String builder = rs.getString(3);
                    String memberStr = rs.getString(5);
                    Scanner scan = new Scanner(memberStr);
                    scan.useDelimiter(",");
                    List members = getMembers(group_id, false);
                    Blob blob = rs.getBlob(6);
                    byte[] bytes = blob.getBinaryStream().readAllBytes();

                    data.ID = group_id;
                    data.name = groupName;
                    data.builder = builder;
                    data.listA = members;
                    data.iconBytes = bytes;

                    found = true;
                }
            }
            if (!found && info.ID != -1) {
                PreparedStatement pstmt = conn.prepareStatement("select group_id from group_info where group_id=?");
                pstmt.setInt(1, info.ID);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next()) {
                    group_id = rs.getInt(1);
                    String groupName = rs.getString(2);
                    String builder = rs.getString(3);
                    String memberStr = rs.getString(5);
                    Scanner scan = new Scanner(memberStr);
                    scan.useDelimiter(",");
                    List members = getMembers(group_id, false);
                    Blob blob = rs.getBlob(6);
                    byte[] bytes = blob.getBinaryStream().readAllBytes();

                    data.ID = group_id;
                    data.name = groupName;
                    data.builder = builder;
                    data.listA = members;
                    data.iconBytes = bytes;

                    found = true;
                }
            }
            if (found) {

                Statement stmt = conn.createStatement();

                stmt.executeUpdate("INSERT INTO users_group(id,group_id) " +
                        "VALUES(" + user_id + "," + group_id + ")");

                PreparedStatement pstmt = conn.prepareStatement("select * from group_info where group_id=" + group_id);
                ResultSet rs = pstmt.executeQuery();
                rs.next();

                int memCount = rs.getInt(4);
                String members = rs.getString(5);

                memCount++;
                members = members + "," + info.operatorInfo.getID();

                stmt.executeUpdate("update group_info set member_count=" + memCount + " where group_id=" + group_id);
                stmt.executeUpdate("update group_info set members='" + members + "' where group_id=" + group_id);

                stmt.close();

                data.listA.add(info.operatorInfo);
            } else data.ID = -1;
        } catch (Exception e) {
            e.printStackTrace();
            updateLog(info.operatorInfo.getName(), "加入群聊过程中，数据库操作出现错误");
            data.ID = -1;
        }
        return data;
    }

    /**
     * @author Furyton
     * @since 1.4 16:00
     */
    public List getMembers(int groupID, boolean isOnlyId) throws Exception {
        PreparedStatement pstmt = conn.prepareStatement("select members from group_info where group_id=" + groupID);
        ResultSet rs = pstmt.executeQuery();
        rs.next();

        String[] IDs = rs.getString(1).split(",");

        Statement stmt = conn.createStatement();

        ArrayList<UserInfo> members = new ArrayList<>();
        ArrayList<Integer> memberIDs = new ArrayList<>();

        for (String id : IDs) {
            int ID = Integer.parseInt(id);
            memberIDs.add(ID);
            if (isOnlyId) continue;

            rs = stmt.executeQuery("select * from users_info where ID=" + ID + "");

            if (rs.next()) {
                Blob blob = rs.getBlob(5);
                members.add(new UserInfo(ID, rs.getString(2), rs.getString(3),
                        blob.getBinaryStream().readAllBytes()));
            }
        }
        if (isOnlyId) return memberIDs;
        else return members;
    }

    public void modify(Data data, int id) throws SQLException {
        String name = data.name;
        String sig = data.signature;
        byte[] icon = data.iconBytes;

        PreparedStatement pstmt = conn.prepareStatement("update users_info set name = ? where ID=" + id);
        pstmt.setString(1, name);
        pstmt.executeUpdate();

        pstmt = conn.prepareStatement("update users_info set signature = ? where ID=" + id);
        pstmt.setString(1, sig);
        pstmt.executeUpdate();

        pstmt = conn.prepareStatement("update users_info set icon = ? where ID=" + id);
        pstmt.setBlob(1, new ByteArrayInputStream(icon));
        pstmt.executeUpdate();
    }


    /**
     * @author Furyton
     * @since 1.4 16:00
     */
    private void updateLog(String user, String log) {
        Platform.runLater(() -> {
            if (user != null)
                ServerLauncher.update(user, log);
            else
                ServerLauncher.update(ServerLauncher.MAIN, log);
        });
    }
}