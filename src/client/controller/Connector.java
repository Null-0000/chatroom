package client.controller;

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

    public boolean loadUserInfo(int ID, String password) throws IOException {
        String inMessage, outMessage;
        ArrayList<String> friends = new ArrayList<>();
        Socket socket = new Socket(HOST, PORT);

        OutputStream outputStream = socket.getOutputStream();
        outMessage = "BHEAD load user info EHEAD BID " + ID + " EID Bpassword " + password + " Epassword ";
        outputStream.write(outMessage.getBytes(StandardCharsets.UTF_8));
        socket.shutdownOutput();

        byte[] bytes = new byte[1024];
        InputStream inputStream = socket.getInputStream();
        int len = inputStream.read(bytes);
        inMessage = new String(bytes, 0, len);
        if (inMessage.equals("error")) return false;
        Pattern p = Pattern.compile("Bname (.*) Ename Bsig (.*) Esig Bfriends (.*) Efriends");
        Matcher m = p.matcher(inMessage);
        if (m.find()) {
            String name = m.group(1);
            String sig = (m.group(2).equals("null")) ? null : m.group(2);
            if (!m.group(3).equals("null")) {
                Scanner scan = new Scanner(m.group(3));
                while (scan.hasNext()) friends.add(scan.next());
            }
            socket.close();
            outputStream.close();
            inputStream.close();
            User.getInstance().setField(ID, name, sig, friends);
            return true;
        }
        return false;
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