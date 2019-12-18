package server;

import kit.Message;
import kit.ClassConverter;
import kit.DataPackage;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class ServerThread extends Thread {
    private Socket socket;
    private Map<String, Socket> socketMap;
    private InputStream inputStream;
    private OutputStream outputStream;
    private UserDataBaseManager manager;
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

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

            byte[] bytes = new byte[1024];

            while(inputStream.read(bytes) != -1){
                byteArrayOutputStream.write(bytes);
            }

            DataPackage receive = (DataPackage) ClassConverter.getObjectFromBytes(byteArrayOutputStream.toByteArray());
            //注意：在这里使用ClassConverter，那么client所发的所有内容必须都要经过ClassConverter才能识别

            if(receive == null){
                System.out.println("an unknown exception occurs or someone logs out");
                socket.close();
                inputStream.close();
                outputStream.close();
                return;
            }
            DataPackage sends = disposeInMessage(receive);
            outputStream.write(ClassConverter.getBytesFromObject(sends));
            outputStream.flush();
            socket.close();
            inputStream.close();
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private DataPackage disposeInMessage(DataPackage inMessage) throws Exception {
        DataPackage output = new DataPackage();
        if (inMessage.operateType.equals("loadUserInfo")){
            System.out.println("-------正在加载用户信息------");
            output = sendUserInfo(inMessage);
            System.out.println("加载完毕");
        }else if (inMessage.operateType.equals("register")){
            System.out.println("-------正在注册中-------");
            output = register(inMessage);//userInfo
            System.out.println("注册完毕");
        }
        else if (inMessage.operateType.equals("makeFriendWith")){
            System.out.println("-------正在查找用户的信息-------");
            output = makeFriend(inMessage);//userInfo
            System.out.println("添加朋友完毕");
        }
        else if (inMessage.operateType.equals("connect")){
            System.out.println("-------与用户开始进行通讯--------");
            output = connectToClient(inMessage);//Message
        }
        else if (inMessage.operateType.equals("loadDialogueData")){
            System.out.println("正在查找用户的对话信息");
            output = loadDialogues(inMessage);//ArrayList<Message>
            System.out.println("查找用户的对话信息完毕" );
        }
        else if(inMessage.operateType.equals("exit")){
            output = new DataPackage(-1);
        }

        return output;
    }
    private DataPackage loadDialogues(DataPackage inMessage) throws SQLException {
        return manager.loadDialogues(inMessage.name);
    }
    private DataPackage connectToClient(DataPackage inMessage) throws Exception {
        String name = inMessage.name;

        socketMap.put(name, socket);
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        DataPackage dataPackage;
        while (true){
            while(inputStream.read(bytes) != -1) byteArrayOutputStream.write(bytes);
            dataPackage = (DataPackage)ClassConverter.getObjectFromBytes(byteArrayOutputStream.toByteArray());
            if(dataPackage == null) break;

            Message message = dataPackage.message;

            Socket socket = socketMap.get(message.receiver);

            if(socket != null){
                socket.getOutputStream().write(ClassConverter.getBytesFromObject(dataPackage));
            } else {
                manager.storeMessage(message);
            }
        }
        outputStream.close();
        inputStream.close();
        socket.close();
        socketMap.remove(name);
        return new DataPackage(-1);
    }
    private DataPackage makeFriend(DataPackage message) throws SQLException {
        return manager.makeFriend(message.name, message.operator);
    }
    private DataPackage sendUserInfo(DataPackage userInfo) {
        int ID = userInfo.ID;
        System.out.println("接受到ID=" + ID);
        String password = userInfo.password;
        System.out.println("接受到密码=" + password);
        DataPackage proceeded = manager.selectByIDAndPassword(ID, password);
        DataPackage tmp = new DataPackage(-1);
        if(proceeded == null){
            System.out.println("ID与密码不匹配");
            return tmp;
        }
        return proceeded;
    }
    private DataPackage register(DataPackage info) throws SQLException {
        int ID = manager.register(info);
        return new DataPackage(ID);
    }
}
