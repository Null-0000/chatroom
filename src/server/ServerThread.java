package server;

import client.tools.RegexFunctions;
import client.tools.ResizingList;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServerThread extends Thread {
    private Socket socket;
    private ResizingList<SocketTag> socketList;
    private InputStream inputStream;
    private OutputStream outputStream;
    private UserDataBaseManager manager;
    private final Pattern headre = Pattern.compile("^BHEAD (.*) EHEAD");
    public ServerThread(Socket socket, UserDataBaseManager manager, ResizingList<SocketTag> socketList){
        this.socket = socket;
        this.manager = manager;
        this.socketList = socketList;
    }
    @Override
    public void run() {
        try {
            inputStream =  socket.getInputStream();
            outputStream = socket.getOutputStream();
            int len;
            String inMessage;
            byte[] bytes = new byte[1024];
            len = inputStream.read(bytes);
            inMessage = new String(bytes, 0, len);
            String outMessage = disposeInMessage(inMessage);
            if (outMessage == null){
                System.out.println("an unknown exception occurs");
                socket.close();
                inputStream.close();
                outputStream.close();
                return;
            }
            if (outMessage.equals("connect"))
                return;
            outputStream.write(outMessage.getBytes(StandardCharsets.UTF_8));
            socket.close();
            inputStream.close();
            outputStream.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    private String disposeInMessage(String inMessage) throws IOException, SQLException {
        Matcher m = headre.matcher(inMessage);
        String head;
        if (m.find()) {
            head = m.group(1);
            inMessage = inMessage.substring(m.group(0).length() + 1);
        }
        else {
            return null;
        }

        String output = null;
        if (head.equals("load user info")){
            System.out.println("-------正在加载用户信息------");
            output = sendUserInfo(inMessage);
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
            output = connectToClient(inMessage);

        }
        else if (head.equals("load dialogues")){
            System.out.println("正在查找用户的对话信息");
            output = loadDialogues(inMessage);
            System.out.println("查找用户的对话信息完毕" );
        }

        return output;
    }

    private String loadDialogues(String inMessage) throws SQLException {
        String name = RegexFunctions.selectBy(inMessage, "Bname (.*) Ename");
        String output = manager.loadDialogues(name);
        return output;
    }

    private String connectToClient(String inMessage) throws IOException, SQLException {
        String name = RegexFunctions.selectBy(inMessage, "Bname (.*) Ename");
        socketList.add(new SocketTag(name, socket));
        int len;
        byte[] bytes = new byte[1024];
        String message;
        while (true){
            if ((len = inputStream.read(bytes)) != -1){
                message = new String(bytes, 0, len);
                String sender = RegexFunctions.selectBy(message, "Bsender (.*) Esender");
                String receiver = RegexFunctions.selectBy(message, "Breceiver (.*) Ereceiver");
                String content = RegexFunctions.selectBy(message, "Bcontent (.*) Econtent");
                String date = RegexFunctions.selectBy(message, "Bdate (.*) Edate");
                boolean active = false;
                for (SocketTag socketTag : socketList) {
                    if (socketTag.toString().equals(receiver)) {
                        String outMessage = "Bsender " + sender + " Esender Bcontent " + content +
                                " Econtent Bdate " + date + " Edate";
                        socketTag.getSocket().getOutputStream().write(outMessage.getBytes(StandardCharsets.UTF_8));
                        active = true;
                    }
                }
                if (!active)
                    manager.storeMessage(sender, receiver, content, date);
            }
        }
    }
    private String makeFriend(String message) throws SQLException {
        Pattern p = Pattern.compile("Binfo (.*) Einfo Bname (.*) Ename");
        Matcher m = p.matcher(message);
        String result = null;
        if (m.find())
            result = manager.makeFriend(m.group(1), m.group(2));
        return result;
    }
    private String sendUserInfo(String message) {
        String outMessage;
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
        outMessage = "Bname " + selected[0] + " Ename Bsig " + selected[1] + " Esig Bfriends ";
        if (selected.length == 2) outMessage+="null ";
        for (int i=2; i<selected.length; i++){
            outMessage += selected[i] + " ";
        }
        outMessage += "Efriends";
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
