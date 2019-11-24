package server;

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
    private InputStream inputStream;
    private OutputStream outputStream;
    private UserDataBaseManager manager;
    private final Pattern headre = Pattern.compile("^BHEAD (.*) EHEAD");
    ServerThread(Socket socket, UserDataBaseManager manager){
        this.socket = socket;
        this.manager = manager;
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
            Matcher m = headre.matcher(inMessage);
            String head;
            if (m.find()) {
                head = m.group(1);
            }
            else {
                return;
            }
            if (head.equals("load user info")){
                System.out.println("-------正在加载用户信息------");
                sendUserInfo(inMessage);
                System.out.println("加载完毕");
            }
            else if (head.equals("register")){
                System.out.println("-------正在注册中-------");
                register(inMessage);
                System.out.println("注册完毕");
            }

            socket.close();
            inputStream.close();
            outputStream.close();
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }


    private void sendUserInfo(String message) throws IOException {
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
            outputStream.write("error".getBytes(StandardCharsets.UTF_8));
            return;
        }
        outMessage = "Bname " + selected[0] + " Ename Bsig " + selected[1] + " Esig Bfriends ";
        if (selected.length == 2) outMessage+="null ";
        for (int i=2; i<selected.length; i++){
            outMessage += selected[i] + " ";
        }
        outMessage += "Efriends";
        outputStream.write(outMessage.getBytes(StandardCharsets.UTF_8));
    }

    private void register(String message) throws SQLException {
        Pattern p = Pattern.compile("Bname (.*) Ename Bpassword (.*) Epassword Bsig (.*) Esig");
        Matcher m = p.matcher(message);
        if (m.find()) {
            int ID = manager.register(m.group(1), m.group(2), m.group(3));
            try {
                outputStream.write(ID);
            }catch (IOException e){
                e.printStackTrace();
            }
        }
    }

}
