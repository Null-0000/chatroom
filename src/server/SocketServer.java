package server;

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
    private Map<String, Socket> socketMap;
    public SocketServer() throws IOException, SQLException, ClassNotFoundException {
        server = new ServerSocket(PORT);
        manager = new UserDataBaseManager();
        socketMap = new HashMap<>();
        int count = 0;
        System.out.println("-------Server Running--------");
        new exitThread().start();
        while (true){
            Socket socket = server.accept();
            ServerThread thread = new ServerThread(socket, manager, socketMap);
            System.out.println("-----收到请求，线程" + (++count) + "正在运行-----");
            thread.start();
            System.out.println("有一个线程运行结束");
        }
    }
    private class exitThread extends Thread {
        Scanner scan = new Scanner(System.in);

        @Override
        public void run() {
            while(true){
                if(scan.nextLine().equals("exit".toLowerCase())){
                    System.exit(0);
                }
            }
        }
    }
}