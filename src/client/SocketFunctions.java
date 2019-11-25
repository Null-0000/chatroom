package client;

import client.exceptions.PasswordException;
import client.exceptions.ServerNotFoundException;
import client.tools.ResizingList;
import client.user.User;
import client.user.UserInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SocketFunctions {
    private static final String HOST = "127.0.0.1";
    private static final int PORT = 5432;
    public static UserInfo loadUserInfo(int ID, String password) throws IOException {
        String inMessage, outMessage;
        ResizingList<String> friends = new ResizingList<>();
        Socket socket = new Socket(HOST, PORT);

        OutputStream outputStream = socket.getOutputStream();
        outMessage = "BHEAD load user info EHEAD BID " + ID + " EID Bpassword " +password + " Epassword ";
        outputStream.write(outMessage.getBytes(StandardCharsets.UTF_8));
        socket.shutdownOutput();

        byte[] bytes = new byte[1024];
        int len;
        InputStream inputStream = socket.getInputStream();
        len = inputStream.read(bytes);
        inMessage = new String(bytes, 0, len);
        if (inMessage.equals("error")) return null;
        Pattern p = Pattern.compile("Bname (.*) Ename Bsig (.*) Esig Bfriends (.*) Efriends");
        Matcher m = p.matcher(inMessage);
        if (m.find()) {
            String name = m.group(1);
            String sig = (m.group(2).equals("null"))?null: m.group(2);
            if (!m.group(3).equals("null")){
                Scanner scan = new Scanner(m.group(3));
                while (scan.hasNext()) friends.add(scan.next());
            }
            socket.close();
            outputStream.close();
            inputStream.close();
            return new UserInfo(ID, password, name, sig, friends);
        }
        return null;
    }
    //register and get ID
    public static String register(String name, String password, String sig) throws IOException {
        Socket socket = new Socket(HOST, PORT);
        
        OutputStream outputStream = socket.getOutputStream();
        String message = "BHEAD register EHEAD Bname " + name +" Ename Bpassword " + 
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
        return result;
    }

    public static void login(int ID, String password) throws PasswordException, IOException {
        User user = new User(ID, password);
        CurrentUser.user = user;
        user.setFrameActive();
    }
}
