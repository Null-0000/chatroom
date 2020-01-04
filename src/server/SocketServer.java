package server;

import javafx.application.Platform;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

class SocketServer {
    private final int PORT = 5432;
    private ServerSocket server;
    private UserDataBaseManager manager;
    private Map<Integer, Socket> socketMap;
    public SocketServer() throws IOException, SQLException, ClassNotFoundException {
        server = new ServerSocket(PORT);
        manager = new UserDataBaseManager();
        socketMap = new HashMap<>();
        int count = 0;
        Platform.runLater(() -> {
            ServerLauncher.update(ServerLauncher.MAIN, "-------Server Running--------");
        });
        System.out.println("");
        while (true){
            Socket socket = server.accept();
            ServerThread thread = new ServerThread(socket, manager, socketMap);
            System.out.println("-----收到请求，线程" + (++count) + "正在运行-----");
            count ++;
            int finalCount = count;
            Platform.runLater(() -> {
                ServerLauncher.update(ServerLauncher.MAIN, "-----收到请求，线程" + finalCount + "正在运行-----");
            });
            thread.start();
            Platform.runLater(() -> {
                ServerLauncher.update(ServerLauncher.MAIN, "有一个线程运行结束");
            });
        }
    }
}