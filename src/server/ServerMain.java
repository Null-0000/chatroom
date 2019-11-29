package server;

import java.io.IOException;
import java.sql.SQLException;

public class ServerMain {
    public static void main(String[] args) throws SQLException, IOException, ClassNotFoundException {
        SocketServer socketServer = new SocketServer();
    }
}
