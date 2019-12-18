package client.controller;

import kit.Message;
import kit.ClassConverter;
import kit.DataPackage;
import client.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.ArrayList;

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
        DataPackage dataPackage = new DataPackage(ID, password);
        dataPackage.setOperateType("loadUserInfo");
        outputStream.write(ClassConverter.getBytesFromObject(dataPackage));
        outputStream.flush();
        socket.shutdownOutput();

        byte[] bytes = new byte[1024];
        InputStream inputStream = socket.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();

        while(inputStream.read(bytes) != -1){
            byteArrayOutputStream.write(bytes);
        }

        DataPackage receive = (DataPackage)ClassConverter.getObjectFromBytes(byteArrayOutputStream.toByteArray());

        if(receive.ID == -1) return false;
        else{
            User.getInstance().setField(receive);
            return true;
        }
    }
    public int register(DataPackage dataPackage) throws Exception {
        Socket socket = new Socket(HOST, PORT);

        OutputStream outputStream = socket.getOutputStream();

        dataPackage.setOperateType("register");
        outputStream.write(ClassConverter.getBytesFromObject(dataPackage));
        outputStream.flush();
        socket.shutdownOutput();

        InputStream inputStream = socket.getInputStream();
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        byte[] bytes = new byte[1024];

        while(inputStream.read(bytes) != -1){
            byteArrayOutputStream.write(bytes);
        }

        DataPackage receiveData = (DataPackage) ClassConverter.getObjectFromBytes(byteArrayOutputStream.toByteArray());
        socket.close();
        outputStream.close();
        inputStream.close();
        return receiveData.ID;
    }
    public boolean makeFriendWith(String info) throws Exception {
        Socket socket = new Socket(HOST, PORT);
        InputStream inputStream = socket.getInputStream();
        OutputStream outputStream = socket.getOutputStream();

        DataPackage dataPackage = new DataPackage(info);
        dataPackage.setOperateType("makeFriendWith");
        dataPackage.operator = User.getInstance().getName();
        outputStream.write(ClassConverter.getBytesFromObject(dataPackage));
        socket.shutdownOutput();
        byte[] bytes = new byte[1024];
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while(inputStream.read(bytes) != -1){
            byteArrayOutputStream.write(bytes);
        }
        DataPackage receive = (DataPackage)ClassConverter.getObjectFromBytes(byteArrayOutputStream.toByteArray());
        User.getInstance().addFriend(receive.name);

        if(receive.ID == -1) return false;
        else return true;
    }

    public Socket connectToRemote() throws Exception {
        Socket socket = new Socket(HOST, PORT);
        OutputStream outputStream = socket.getOutputStream();
        DataPackage dataPackage = new DataPackage(User.getInstance().getName(), User.getInstance().getID());
        dataPackage.setOperateType("connect");
        outputStream.write(ClassConverter.getBytesFromObject(dataPackage));
        outputStream.flush();
        return socket;
    }
    public ArrayList<Message> loadDialogueData() throws Exception {
        Socket socket = new Socket(HOST, PORT);
        OutputStream outputStream = socket.getOutputStream();

        DataPackage dataPackage = new DataPackage(User.getInstance().getName(), User.getInstance().getID());
        dataPackage.setOperateType("loadDialogueData");
        outputStream.write(ClassConverter.getBytesFromObject(dataPackage));
        outputStream.flush();
        socket.shutdownOutput();

        InputStream inputStream = socket.getInputStream();
        byte[] bytes = new byte[1024];

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while(inputStream.read(bytes) != -1){
            byteArrayOutputStream.write(bytes);
        }

        DataPackage receive = (DataPackage) ClassConverter.getObjectFromBytes(byteArrayOutputStream.toByteArray());

        return receive.messages;
    }
}