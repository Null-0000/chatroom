package server;

import java.sql.*;

public class UserDataBaseManager {
    private final String driver = "com.mysql.cj.jdbc.Driver";
    private final String url = "jdbc:mysql://localhost:3306/chat_room";
    private final String user = "henry";
    private final String pass = "mxylfbcz4321";
    private Connection conn;
    private Statement stmt;

    public UserDataBaseManager() throws ClassNotFoundException, SQLException {
        Class.forName(driver);
        conn = DriverManager.getConnection(url, user, pass);
        stmt = conn.createStatement();
    }

    public int register(String name, String password, String sig) throws SQLException {
        addCurrentUsersAmount();
        int ID = getCurrentID();
        String cmd = String.format("insert into users_info(ID, name, signature, password) values(%d,\'%s\',\'%s\',\'%s\')", ID, name, sig, password);
        stmt.executeUpdate(cmd);
        return ID;
    }

    private int getCurrentID() throws SQLException {
        int ID = stmt.executeQuery("SELECT current_ID FROM global_info").getInt(1);
        stmt.executeUpdate("UPDATE global_info SET current_ID=" + (ID+1) + " WHERE current_ID=" + ID);
        return ID;
    }

    private void addCurrentUsersAmount() throws SQLException {
        int i = stmt.executeQuery("SELECT users FROM global_info").getInt(1);
        stmt.executeUpdate("UPDATE global_info SET users=" + (i+1) + " WHERE users=" + i);
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
            rs2 = stmt.executeQuery("select name2 from relationship where name1=" + name);
            rs2.last();
            result = new String[rs2.getRow() + 2];
            int cnt = 2;
            while (rs2.previous()){
                result[cnt++] = rs2.getString(1);
            }
        } catch (SQLException e) {
            result = new String[2];
        }
        result[0] = name;
        result[1] = sig;
        return result;
    }
}
