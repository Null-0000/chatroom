package server;

import javafx.application.Platform;
import kit.Data;
import kit.GroupInfo;
import kit.Message;
import kit.UserInfo;

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
            rs1.next();
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
            rs2 = stmt.executeQuery("select friend_name from friend_map where name=\'" + name + "\'");
            while (rs2.next()) {
                String friendName = rs2.getString(1);
                Statement stmt2 = conn.createStatement();
                rs3 = stmt2.executeQuery("select * from users_info where name=\'" + friendName + "\'");
                if (rs3.next()) {
                    Blob blob = rs3.getBlob(5);
                    friendList.add(new UserInfo(rs3.getInt(1), friendName, rs3.getString(3),
                            blob.getBinaryStream().readAllBytes()));
                }
            }

            rs2 = stmt.executeQuery("select group_id from users_group where name=\'" + name + "\'");
            while (rs2.next()) {
                int groupID = rs2.getInt(1);
                Statement stmt2 = conn.createStatement();
                rs3 = stmt2.executeQuery("select * from group_info where group_id=" + groupID);
                if(rs3.next()) {
                    Blob blob = rs3.getBlob(6);
                    ArrayList<UserInfo> list = (ArrayList<UserInfo>) getMembers(groupID, false);
                    groupList.add(new GroupInfo(groupID, rs3.getString(2),
                            blob.getBinaryStream().readAllBytes(), list, 0));
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


    public Data makeFriend(String info, String byName) throws SQLException, IOException {
        Statement stmt = conn.createStatement();
        String name;
        UserInfo userInfo;
        ResultSet rs = stmt.executeQuery("SELECT * FROM users_info WHERE name=\'" + info + "\'");
        if (rs.next()) {
            name = rs.getString(2);
            Blob blob = rs.getBlob(5);
            userInfo = new UserInfo(rs.getInt(1), name,
                    rs.getString(3), blob.getBinaryStream().readAllBytes());
        } else {
            rs = stmt.executeQuery("SELECT * FROM users_info WHERE ID=" + info);
            if (rs.next()) {
                name = rs.getString(2);
                Blob blob = rs.getBlob(5);
                userInfo = new UserInfo(rs.getInt(1), name,
                        rs.getString(3), blob.getBinaryStream().readAllBytes());
            } else {
                stmt.close();
                return new Data(-1);
            }
        }

        stmt.executeUpdate("INSERT INTO friend_map(name,friend_name) " +
                "VALUES(\'" + name + "\',\'" + byName + "\')");
        stmt.executeUpdate("INSERT INTO friend_map(name,friend_name) " +
                "VALUES(\'" + byName + "\',\'" + name + "\')");

        stmt.close();
        return new Data(userInfo);
    }

    public void storeMessage(Message message) throws Exception {
        if (!message.isMass) storeMsg(message, -1);
        else {
            ArrayList<Integer> list = (ArrayList<Integer>) getMembers(message.receiver, true);

            for (int recID : list) {
                Message msg = new Message(recID, message.sender, message.ctype, message.content, message.date, true);
                storeMsg(msg, message.receiver);
            }

        }
    }

    private void storeMsg(Message message, int fromGroup) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO messages(receiver, sender, ctype, content, datetime, fromgrp) VALUES(?, ?, ?, ?, ?, ?)");
        pstmt.setInt(1, message.receiver);
        pstmt.setInt(2, message.sender);
        pstmt.setString(3, message.ctype);
        pstmt.setBlob(4, new ByteArrayInputStream(message.content));
        pstmt.setTimestamp(5, new Timestamp(message.date.getTime()));
        pstmt.setInt(6, fromGroup);
        pstmt.execute();
    }

    public Data loadDialogues(int ID) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT  * FROM messages WHERE receiver=?");
        pstmt.setInt(1, ID);
        ResultSet rs = pstmt.executeQuery();
        ArrayList<Message> dialogues = new ArrayList<>();
        while (rs.next()) {
            int sender = rs.getInt(2);
            String ctype = rs.getString(3);
            Blob contentBlob = rs.getBlob(4);
            byte[] content = new byte[(int) contentBlob.length()];
            InputStream inputStream = contentBlob.getBinaryStream();
            try {
                inputStream.read(content);
            } catch (IOException e) {
                e.printStackTrace();
            }
            Date date = new Date(rs.getTimestamp(5).getTime());
//            boolean isMass = rs.getBoolean(6);
            int fromGroup = rs.getInt(6);
            Message newMsg;
            if (fromGroup == -1) newMsg = new Message(ID, sender, ctype, content, date, false);
            else newMsg = new Message(fromGroup, sender, ctype, content, date, true);

            dialogues.add(newMsg);
        }
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM messages WHERE receiver=\'" + ID + "\'");
        stmt.close();
        return new Data(dialogues);
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
            pstmt.setString(3, data.operator);
            pstmt.setInt(4, 1);
            pstmt.setString(5, "" + data.operatorID);
            pstmt.setBlob(6, new ByteArrayInputStream(data.iconBytes));
            pstmt.executeUpdate();

            Statement stmt = conn.createStatement();
            stmt.executeUpdate("INSERT INTO users_group(name, group_id) " + "VALUES(\'" + data.operator + "\',\'" + ID + "\')");
        } catch (Exception e) {
            e.printStackTrace();
            updateLog(data.operator, "创建群聊过程中，数据库操作出现错误");
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
            String name = info.operator;
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

                stmt.executeUpdate("INSERT INTO users_group(name,group_id) " +
                        "VALUES(\'" + name + "\',\'" + group_id + "\')");

                PreparedStatement pstmt = conn.prepareStatement("select * from group_info where group_id=" + group_id);
                ResultSet rs = pstmt.executeQuery();
                rs.next();

                int memCount = rs.getInt(4);
                String members = rs.getString(5);

                memCount++;
                members = members + "," + info.operatorID;

                stmt.executeUpdate("update group_info set member_count=" + memCount + " where group_id=" + group_id);
                stmt.executeUpdate("update group_info set members='" + members + "' where group_id=" + group_id);

                stmt.close();
            } else data.ID = -1;
        } catch (Exception e) {
            e.printStackTrace();
            updateLog(info.operator, "加入群聊过程中，数据库操作出现错误");
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

            rs = stmt.executeQuery("select * from users_info where ID=\'" + ID + "\'");

            if (rs.next()) {
                Blob blob = rs.getBlob(5);
                members.add(new UserInfo(ID, rs.getString(2), rs.getString(3),
                        blob.getBinaryStream().readAllBytes()));
            }
        }
        if (isOnlyId) return memberIDs;
        else return members;
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