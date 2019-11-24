package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;

class SocketServer {
    private final int PORT = 5432;
    private ServerSocket server;
    private UserDataBaseManager manager;
    SocketServer() throws IOException, SQLException, ClassNotFoundException {
        server = new ServerSocket(PORT);
        manager = new UserDataBaseManager();
        int count = 0;
        System.out.println("-------服务器启动--------");
        while (true){
            Socket socket = server.accept();
            ServerThread thread = new ServerThread(socket, manager);
            System.out.println("-----收到请求，线程" + (++count) + "正在运行-----");
            thread.start();
            System.out.println("有一个线程运行结束");
        }
    }
}
