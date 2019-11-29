package client.tools;

import client.CurrentUser;
import client.exceptions.PasswordException;
import client.exceptions.ServerNotFoundException;
import client.user.Message;
import client.user.User;
import client.user.UserInfo;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SocketFunctions {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5432;
    public static UserInfo loadUserInfo(int ID, String password) throws IOException {
        String outMessage;
        Object inMessage;
        Socket socket = new Socket(HOST, PORT);

        outMessage = "BHEAD load user info EHEAD BID " + ID + " EID Bpassword " + password + " Epassword ";

        User.objectOutputStream.writeObject(outMessage);
        User.objectOutputStream.flush();

        try {
            inMessage = User.objectInputStream.readObject();
            if(inMessage instanceof String) return null;
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "载入用户信息时错误", "ALERT", JOptionPane.ERROR_MESSAGE);
            return null;
        }
        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();

        return (UserInfo)inMessage;
    }
    //register and get ID
    public static String register(String name, String password, String sig) throws Exception {
        Socket socket = new Socket(HOST, PORT);

        String message = "BHEAD register EHEAD Bname " + name + " Ename Bpassword " +
                password + " Epassword Bsig " + sig + " Esig";
//        outputStream.write(message.getBytes(StandardCharsets.UTF_8));
        User.objectOutputStream.writeObject(message);
        User.objectOutputStream.flush();

        String ID = (String)User.objectInputStream.readObject();

        socket.shutdownInput();
        socket.shutdownOutput();
        socket.close();
        return ID;
    }
    public static String makeFriendWith(String info, User user) throws Exception {
        Socket socket = new Socket(HOST, PORT);

        String message = "BHEAD make friend EHEAD Binfo " + info + " Einfo Bname " +
                user.toString() + " Ename";
        User.objectOutputStream.writeObject(message);
        User.objectOutputStream.flush();
        socket.shutdownOutput();

        if(User.objectInputStream.available() != 0) return (String) User.objectInputStream.readObject();
        else throw new ServerNotFoundException();
    }
    public static void login(int ID, String password) throws PasswordException, IOException {
        User user = new User(ID, password);

        CurrentUser.user = user;
        CurrentUser.active = true;
        user.setFrameActive();
    }
    public static Socket connectToRemote(String name) throws IOException {
        Socket socket = new Socket(HOST, PORT);
        String outMessage = "BHEAD connect EHEAD Bname " + name + " Ename";

        User.objectOutputStream.writeObject(outMessage);
        User.objectOutputStream.flush();
        return socket;
    }
    public static ArrayList<Message> loadDialogueData(String name) throws IOException {
        Socket socket = new Socket(HOST, PORT);

        String outMessage = "BHEAD load dialogues EHEAD Bname " + name + " Ename";
        User.objectOutputStream.writeObject(outMessage);
        User.objectOutputStream.flush();

        ArrayList<Message> messages = new ArrayList<>();
        try {
            messages = (ArrayList<Message>) User.objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "远程读入未读消息错误", "ALERT", JOptionPane.ERROR_MESSAGE);
        }
        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();

        return messages;
    }
}
