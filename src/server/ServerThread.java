package server;

import client.tools.RegexFunctions;
import Kits.*;
import client.user.UserInfo;

import javax.swing.*;
import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerThread extends Thread {
    private Socket socket;
    private Map<String, Socket> socketMap;
    private InputStream inputStream;
    private OutputStream outputStream;
    private UserDataBaseManager manager;
    private final Pattern headre = Pattern.compile("^BHEAD (.*) EHEAD");

    public ServerThread(Socket socket, UserDataBaseManager manager, Map<String, Socket> socketMap){
        this.socket = socket;
        this.manager = manager;
        this.socketMap = socketMap;
    }
    @Override
    public void run() {
        try {
            inputStream =  socket.getInputStream();
            outputStream = socket.getOutputStream();

            ObjectOutputStream objectOutputStream = null;
            ObjectInputStream objectInputStream = null;
            try {
                objectOutputStream = new ObjectOutputStream(outputStream);
                objectInputStream = new ObjectInputStream(inputStream);
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "与客户端连接错误", "ALERT", JOptionPane.ERROR_MESSAGE);
            }

            String inMessage = "";

            try {
                inMessage = (String)objectInputStream.readObject();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "与客户端连接错误", "ALERT", JOptionPane.ERROR_MESSAGE);
                socket.close();
                inputStream.close();
                outputStream.close();
                return;
            }

            Object outMessage = disposeInMessage(inMessage);

            if (outMessage == null){
                System.out.println("an unknown exception occurs or someone log out");
                socket.close();
                inputStream.close();
                outputStream.close();
                return;
            }
            if (outMessage.equals("connect"))
                return;

//            outputStream.write(ClassConverter.getBytesFromObject((Serializable) outMessage));
            objectOutputStream.writeObject(outMessage);
            objectOutputStream.flush();

            socket.close();
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Object disposeInMessage(String inMessage) throws Exception {
        Matcher m = headre.matcher(inMessage);
        String head;
        if (m.find()) {
            head = m.group(1);
            inMessage = inMessage.substring(m.group(0).length() + 1);
        }
        else {
            return null;
        }

        Object output = null;
        if (head.equals("load user info")){
            System.out.println("-------正在加载用户信息------");
            output = sendUserInfo(inMessage);//done
            System.out.println("加载完毕");
        }
        else if (head.equals("register")){
            System.out.println("-------正在注册中-------");
            output = register(inMessage);
            System.out.println("注册完毕");
        }
        else if (head.equals("make friend")){
            System.out.println("-------正在查找用户的信息-------");
            output = makeFriend(inMessage);
            System.out.println("添加朋友完毕");
        }
        else if (head.equals("connect")){
            System.out.println("-------与用户开始进行通讯--------");
            output = connectToClient(inMessage);//done
        }
        else if (head.equals("load dialogues")){
            System.out.println("正在查找用户的对话信息");
            output = loadDialogues(inMessage);//done
            System.out.println("查找用户的对话信息完毕" );
        }

        return output;
    }

    private ArrayList<Message> loadDialogues(String inMessage) throws SQLException {
        String name = RegexFunctions.selectBy(inMessage, "Bname (.*) Ename");
        ArrayList<Message> dialogues = manager.loadDialogues(name);
        return dialogues;
    }

    private String connectToClient(String inMessage) throws Exception {
        String name = RegexFunctions.selectBy(inMessage, "Bname (.*) Ename");

        socketMap.put(name, socket);

        byte[] bytes = new byte[1024];
        Message message;
        Object getClass;

        inputStream = socket.getInputStream();
        outputStream = socket.getOutputStream();
        ObjectInputStream objectInputStream = new ObjectInputStream(inputStream);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(outputStream);

        while (true){
            if(objectInputStream.available() > 0){
                getClass = objectInputStream.readObject();

                if(getClass instanceof String) break;
                message = (Message)getClass;

                Socket socket = socketMap.get(message.receiver);

                synchronized (socket){
                    if(socket != null) objectOutputStream.writeObject(message);
                    else manager.storeMessage(message);
                }
            }
        }
        outputStream.close();
        inputStream.close();
        socket.close();
        socketMap.remove(name);

        return null;
    }
    private String makeFriend(String message) throws SQLException {
        Pattern p = Pattern.compile("Binfo (.*) Einfo Bname (.*) Ename");
        Matcher m = p.matcher(message);
        String result = null;
        if (m.find())
            result = manager.makeFriend(m.group(1), m.group(2));
        return result;
    }
    private Object sendUserInfo(String message) {
        Object outMessage;
        Pattern p = Pattern.compile("BID (\\d*) EID Bpassword (.*) Epassword");
        Matcher m = p.matcher(message);
        int ID = -1;
        String password = null;
        if (m.find()) {
            ID = Integer.parseInt(m.group(1));
            System.out.println("接受到ID=" + ID);
            password = m.group(2);
            System.out.println("接受到密码=" + password);
        }
        String[] selected = manager.selectByIDAndPassword(ID, password);
        if (selected == null){
            System.out.println("ID与密码不匹配");
            return "error";
        }

        ArrayList<String> friendList = (ArrayList<String>) Arrays.asList(selected).subList(2, selected.length);

        outMessage = new UserInfo(ID, selected[0], selected[1], friendList);

        return outMessage;
    }
    private String register(String message) throws SQLException {

        Pattern p = Pattern.compile("Bname (.*) Ename Bpassword (.*) Epassword Bsig (.*) Esig");
        Matcher m = p.matcher(message);
        String ID = null;
        if (m.find()) ID = "" + manager.register(m.group(1), m.group(2), m.group(3));
        return ID;
    }
}
