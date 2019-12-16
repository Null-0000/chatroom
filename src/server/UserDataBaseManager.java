package server;

import client.model.Message;
import kit.DataPackage;

import javax.xml.crypto.Data;
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
        stmt.executeUpdate("INSERT INTO messages(sender,receiver,content,datetime) VALUES(\'" + message.sender + "\',\'" +
                message.receiver + "\',\'" + message.content + "\',\'" + new Timestamp(message.date.getTime()) + "\')");
    }
    public DataPackage loadDialogues(String name) throws SQLException {
        ResultSet rs = stmt.executeQuery("SELECT * FROM messages WHERE receiver=\'" + name + "\'");
        ArrayList<Message> dialogues = new ArrayList<>();
        while (rs.next()){
            dialogues.add(new Message(name, rs.getString(1), rs.getString(3), new Date(rs.getTimestamp(4).getTime())));
        }
        stmt.executeUpdate("DELETE FROM messages WHERE receiver=\'" + name + "\'");
        return new DataPackage(dialogues);
    }







}

