package server;

import java.net.Socket;

public class SocketTag {
    private String name;

    public Socket getSocket() {
        return socket;
    }

    private Socket socket;
    public SocketTag(String name, Socket socket){
        this.name = name;
        this.socket = socket;
    }
    public String toString(){
        return name;
    }
}
