package client.tools;

import client.CurrentUser;
import client.exceptions.PasswordException;
import client.exceptions.ServerNotFoundException;
import client.user.Message;
import client.user.User;
import client.user.UserInfo;
import com.sun.xml.internal.bind.api.impl.NameConverter;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocketFunctions {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5432;
    public static UserInfo loadUserInfo(int ID, String password) throws IOException {
        String outMessage;
        Object inMessage;
        Socket socket = new Socket(HOST, PORT);

        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        outMessage = "BHEAD load user info EHEAD BID " + ID + " EID Bpassword " + password + " Epassword ";
        outputStream.write(outMessage.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();

        try {
            inMessage = objectInputStream.readObject();
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
    public static String register(String name, String password, String sig) throws IOException {
        Socket socket = new Socket(HOST, PORT);

        OutputStream outputStream = socket.getOutputStream();
        String message = "BHEAD register EHEAD Bname " + name + " Ename Bpassword " +
                password + " Epassword Bsig " + sig + " Esig";
        outputStream.write(message.getBytes(StandardCharsets.UTF_8));

        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[1024];
        int len = inputStream.read(bytes);
        String ID = new String(bytes, 0, len);

        socket.shutdownInput();
        socket.shutdownOutput();
        socket.close();
        return ID;
    }
    public static String makeFriendWith(String info, User user) throws IOException, ServerNotFoundException {
        Socket socket = new Socket(HOST, PORT);
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        String message = "BHEAD make friend EHEAD Binfo " + info + " Einfo Bname " +
                user.toString() + " Ename";
        outputStream.write(message.getBytes(StandardCharsets.UTF_8));
        byte[] bytes = new byte[1024];
        int len = inputStream.read(bytes);
        if (len == -1) throw new ServerNotFoundException();
        String result = new String(bytes, 0, len);

        socket.shutdownOutput();
        return result;
    }
    public static void login(int ID, String password) throws PasswordException, IOException {
        User user = new User(ID, password);

        CurrentUser.user = user;
        CurrentUser.active = true;
        user.setFrameActive();
    }
    public static Socket connectToRemote(String name) throws IOException {
        Socket socket = new Socket(HOST, PORT);
        OutputStream outputStream = socket.getOutputStream();
        String outMessage = "BHEAD connect EHEAD Bname " + name + " Ename";
        outputStream.write(outMessage.getBytes());
        return socket;
    }
    public static ArrayList<Message> loadDialogueData(String name) throws IOException {
        Socket socket = new Socket(HOST, PORT);
        OutputStream outputStream = socket.getOutputStream();
        InputStream inputStream = socket.getInputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);

        String outMessage = "BHEAD load dialogues EHEAD Bname " + name + " Ename";
        outputStream.write(outMessage.getBytes(StandardCharsets.UTF_8));

        ArrayList<Message> messages = new ArrayList<>();
        try {
            messages = (ArrayList<Message>) objectInputStream.readObject();
        } catch (ClassNotFoundException e) {
            JOptionPane.showMessageDialog(null, "远程读入未读消息错误", "ALERT", JOptionPane.ERROR_MESSAGE);
        }
        socket.shutdownOutput();
        socket.shutdownInput();
        socket.close();
        objectInputStream.close();

        return messages;
    }
}
