package client.controller;

/**
 * 用于与服务器通信
 * sendMessage,loadDialogues,loadUserInfo,receiveMessage
 */

public class Connector {
    private final String HOST = "127.0.0.1";
    private final int PORT = 5432;

    private Connector instance = new Connector();
    public Connector getInstance() {
        return instance;
    }

    
}
