package client.exceptions;

public class ServerNotFoundException extends Exception{
    public ServerNotFoundException(){
        super("ServerNotFound");
    }
}
