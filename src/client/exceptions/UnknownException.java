package client.exceptions;

public class UnknownException extends Exception {
    public UnknownException(){
        super("an unknown exception occurs");
    }
}
