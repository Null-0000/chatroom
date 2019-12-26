package server;

import kit.Message;
import kit.DataPackage;
import kit.UserInfo;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

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
    public int register(DataPackage dataPackage) throws SQLException {
        addCurrentUsersAmount();
        int ID = getCurrentID();
        byte[] icon = dataPackage.myIconBytes;
        PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO users_info(ID, name, signature, password, icon) VALUES(?, ?, ?, ?, ?)");
        pstmt.setInt(1, ID);
        pstmt.setString(2, dataPackage.name);
        pstmt.setString(3, dataPackage.signature);
        pstmt.setString(4, dataPackage.password);
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
    public DataPackage selectByIDAndPassword(int id, String password) throws SQLException {
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
        try {
            rs2 = stmt.executeQuery("select friend_name from friend_map where name=\'" + name + "\'");
            while (rs2.next()){
                String friendName = rs2.getString(1);
                Statement stmt2 = conn.createStatement();
                rs3 = stmt2.executeQuery("select * from users_info where name=\'" + friendName + "\'" );
                if (rs3.next()) {
                    Blob blob = rs3.getBlob(5);
                    friendList.add(new UserInfo(rs3.getInt(1), friendName, rs3.getString(3),
                            blob.getBinaryStream().readAllBytes()));
                }
            }
        } catch (SQLException | IOException e) {
            e.printStackTrace();
        }
        stmt.close();
        return new DataPackage(id, name, sig, friendList, icon);
    }
    public DataPackage makeFriend(String info, String byName) throws SQLException, IOException {
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
                return new DataPackage(-1);
            }
        }

        stmt.executeUpdate("INSERT INTO friend_map(name,friend_name) " +
                "VALUES(\'" + name + "\',\'" + byName + "\')");
        stmt.executeUpdate("INSERT INTO friend_map(name,friend_name) " +
                "VALUES(\'" + byName + "\',\'" + name + "\')");

        stmt.close();
        return new DataPackage(userInfo);
    }
    public void storeMessage(Message message) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO messages(receiver, sender, ctype, content, datetime) VALUES(?, ?, ?, ?, ?)");
        pstmt.setString(1, message.receiver);
        pstmt.setString(2, message.sender);
        pstmt.setString(3, message.ctype);
        pstmt.setBlob(4, new ByteArrayInputStream(message.content));
        pstmt.setTimestamp(5, new Timestamp(message.date.getTime()));
        pstmt.execute();
    }
    public DataPackage loadDialogues(String name) throws SQLException {
        PreparedStatement pstmt = conn.prepareStatement("SELECT  * FROM messages WHERE receiver=?");
        pstmt.setString(1, name);
        ResultSet rs = pstmt.executeQuery();
        ArrayList<Message> dialogues = new ArrayList<>();
        while (rs.next()){
            String sender = rs.getString(2);
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
            dialogues.add(new Message(name, sender, ctype, content, date));
        }
        Statement stmt = conn.createStatement();
        stmt.executeUpdate("DELETE FROM messages WHERE receiver=\'" + name + "\'");
        stmt.close();
        return new DataPackage(dialogues);
    }

}

