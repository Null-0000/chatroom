package client.controller;

import client.kit.ClassConverter;
import client.kit.UserInfoPackage;
import client.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 用于与服务器通信
 * sendMessage,loadDialogues,loadUserInfo,receiveMessage
 * Connector.getInstance().sendMessage(XXX).
 */

public class Connector {
    private final String HOST = "127.0.0.1";
    private final int PORT = 5432;
//    Socket

    private static Connector instance = new Connector();
    public static Connector getInstance() {
        return instance;
    }

    public boolean loadUserInfo(int ID, String password) throws Exception {
        Socket socket = new Socket(HOST, PORT);

        OutputStream outputStream = socket.getOutputStream();

        outputStream.write(ClassConverter.getBytesFromObject(new UserInfoPackage(ID, password)));

        socket.shutdownOutput();

        byte[] bytes = new byte[1024];
        InputStream inputStream = socket.getInputStream();
        int len = inputStream.read(bytes);

        UserInfoPackage userInfoPackage = (UserInfoPackage)ClassConverter.getObjectFromBytes(bytes);

        if(userInfoPackage.ID == -1) return false;
        else{
            User.getInstance().setField(userInfoPackage);
            return true;
        }
    }
    public String register(String name, String password, String sig) throws IOException {
        Socket socket = new Socket(HOST, PORT);

        OutputStream outputStream = socket.getOutputStream();
        String message = "BHEAD register EHEAD Bname " + name + " Ename Bpassword " +
                password + " Epassword Bsig " + sig + " Esig";
        outputStream.write(message.getBytes(StandardCharsets.UTF_8));

        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[1024];
        int len = inputStream.read(bytes);
        String ID = new String(bytes, 0, len);
        socket.close();
        outputStream.close();
        inputStream.close();
        return ID;
    }
    public boolean makeFriendWith(String info) throws IOException {
        Socket socket = new Socket(HOST, PORT);
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        String message = "BHEAD make friend EHEAD Binfo " + info + " Einfo Bname " +
                User.getInstance().getName() + " Ename";
        outputStream.write(message.getBytes(StandardCharsets.UTF_8));
        socket.shutdownOutput();
        byte[] bytes = new byte[1024];
        int len = inputStream.read(bytes);
        if (len == -1) throw new IOException();
        String result = new String(bytes, 0, len);

        if(result.equals("not found")) return false;
        else if(result.equals("added")){
//            User.getInstance().addFriend(info);
            return true;
        }
        return false;
    }

    public Socket connectToRemote(String name) throws IOException {
        Socket socket = new Socket(HOST, PORT);
        OutputStream outputStream = socket.getOutputStream();
        String outMessage = "BHEAD connect EHEAD Bname " + name + " Ename";
        outputStream.write(outMessage.getBytes());
        return socket;
    }
    public String loadDialogueData() throws IOException {
        Socket socket = new Socket(HOST, PORT);
        OutputStream outputStream = socket.getOutputStream();
        String outMessage = "BHEAD load dialogues EHEAD Bname " + User.getInstance().getName() + " Ename";
        outputStream.write(outMessage.getBytes(StandardCharsets.UTF_8));

        InputStream inputStream = socket.getInputStream();
        int len;
        byte[] bytes = new byte[1024];
        String inMessage = "";
        while ((len = inputStream.read(bytes)) != -1) {
            inMessage += new String(bytes, 0, len);
        }
        return inMessage;
    }
}