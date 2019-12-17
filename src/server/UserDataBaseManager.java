package server;

import client.model.Message;
import kit.DataPackage;

import javax.xml.crypto.Data;
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
    private Statement stmt;

    public UserDataBaseManager() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        conn = DriverManager.getConnection(url, user, pass);
        stmt = conn.createStatement();
    }
    public int register(DataPackage dataPackage) throws SQLException {
        addCurrentUsersAmount();
        int ID = getCurrentID();
        String cmd = String.format("insert into users_info(ID, name, signature, password) values(%d,\'%s\',\'%s\',\'%s\')", ID, dataPackage.name, dataPackage.signature, dataPackage.password);
        stmt.executeUpdate(cmd);
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
    public String[] selectByIDAndPassword(int id, String password) {
        if (id < 0) return null;
        String[] result;
        ResultSet rs1;
        String name;
        String sig;
        try {
            rs1 = stmt.executeQuery("select * from users_info where id=" + id);
            rs1.next();
            if (!rs1.getString(4).equals(password))
                return null;
            name = rs1.getString(2);
            sig = rs1.getString(3);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
        ResultSet rs2;
        try {
            rs2 = stmt.executeQuery("select friend_name from friend_map where name=\'" + name + "\'");
            rs2.last();
            result = new String[rs2.getRow() + 2];
            int cnt = 2;
            do {
                result[cnt++] = rs2.getString(1);
            } while (rs2.previous());
        } catch (SQLException e) {
            result = new String[2];
        }
        result[0] = name;
        result[1] = sig;
        return result;
    }
    public DataPackage makeFriend(String info, String byName) throws SQLException {
        String name;
        ResultSet rs = stmt.executeQuery("SELECT name FROM users_info WHERE name=\'" + info + "\'");
        if (rs.next()) {
            name = info;
        } else {
            rs = stmt.executeQuery("SELECT name FROM users_info WHERE ID=" + info);
            if (rs.next()) {
                name = rs.getString(1);
            } else {
                return new DataPackage(-1);
            }
        }

        stmt.executeUpdate("INSERT INTO friend_map(name,friend_name) " +
                "VALUES(\'" + name + "\',\'" + byName + "\')");
        stmt.executeUpdate("INSERT INTO friend_map(name,friend_name) " +
                "VALUES(\'" + byName + "\',\'" + name + "\')");

        int ID = 0;

        return new DataPackage(name, ID);
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
        stmt.executeUpdate("DELETE FROM messages WHERE receiver=\'" + name + "\'");
        return new DataPackage(dialogues);
    }

}

