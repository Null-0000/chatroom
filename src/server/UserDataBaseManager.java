package server;

import kit.Message;
import kit.Data;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class UserDataBaseManager {
    private final String driver = "com.mysql.cj.jdbc.Driver";
    private final String url = "jdbc:mysql://localhost:3306/chat_room?serverTimezone=Asia/Shanghai";
    private final String user = "root";
    private final String pass = "123456";
    private Connection conn;
    private Statement stmt;

    public UserDataBaseManager() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        conn = DriverManager.getConnection(url, user, pass);
        stmt = conn.createStatement();
    }
    public int register(Data data) throws SQLException {
        addCurrentUsersAmount();
        int ID = getCurrentID();
        byte[] icon = data.myIconBytes;
        PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO users_info(ID, name, signature, password, icon) VALUES(?, ?, ?, ?, ?)");
        pstmt.setInt(1, ID);
        pstmt.setString(2, data.name);
        pstmt.setString(3, data.signature);
        pstmt.setString(4, data.password);
        pstmt.setBlob(5, new ByteArrayInputStream(icon));
        pstmt.executeUpdate();
        return ID;
    }
    private int getCurrentID() throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT current_ID FROM global_info");
        int ID = -1;
        if (rs.next())
            ID = rs.getInt(1);
        stmt.executeUpdate("UPDATE global_info SET current_ID=" + (ID + 1) + " WHERE current_ID=" + ID);
        return ID;
    }
    private void addCurrentUsersAmount() throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT users FROM global_info");
        if (rs.next())
            stmt.executeUpdate("UPDATE global_info SET users=" + (rs.getInt(1) + 1) + " WHERE users=" + rs.getInt(1));
    }
    public Data selectByIDAndPassword(int id, String password) {
        if (id < 0) return null;
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
            icon = new byte[(int) iconBlob.length()];
            InputStream inputStream = iconBlob.getBinaryStream();
            inputStream.read(icon);
        } catch (SQLException | IOException e) {
            e.printStackTrace();
            return null;
        }
        ResultSet rs2;
        ArrayList<String> friendList = new ArrayList<>();
        try {
            rs2 = stmt.executeQuery("select friend_name from friend_map where name=\'" + name + "\'");
            while (rs2.next()){
                friendList.add(rs2.getString(1));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new Data(id, name, sig, friendList, icon);
    }
    public Data makeFriend(String info, String byName) throws SQLException {
        String name;
        ResultSet rs = stmt.executeQuery("SELECT name FROM users_info WHERE name=\'" + info + "\'");
        if (rs.next()) {
            name = info;
        } else {
            rs = stmt.executeQuery("SELECT name FROM users_info WHERE ID=" + info);
            if (rs.next()) {
                name = rs.getString(1);
            } else {
                return new Data(-1);
            }
        }

        stmt.executeUpdate("INSERT INTO friend_map(name,friend_name) " +
                "VALUES(\'" + name + "\',\'" + byName + "\')");
        stmt.executeUpdate("INSERT INTO friend_map(name,friend_name) " +
                "VALUES(\'" + byName + "\',\'" + name + "\')");

        int ID = 0;

        return new Data(name, ID);
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
    public Data loadDialogues(String name) throws SQLException {
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
            dialogues.add(new Message(name, sender, ctype, content, date, false));
        }
        stmt.executeUpdate("DELETE FROM messages WHERE receiver=\'" + name + "\'");
        return new Data(dialogues);
    }

    /**
     * return whether this operate goes successfully
     * @param info
     * @return
     */
    public boolean createGroup(Data info) {
        //unfinished
        return false;
    }

    /**
     * return whether this operate goes successfully
     * @param info
     * @return
     */
    public boolean joinGroup(Data info){
        //name, groupID
        //更新group members，count
        //更新用户的group info
        //unfinished
        return false;
    }


}

