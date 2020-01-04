package server;

import javafx.application.Platform;
import kit.*;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.Map;

public class ServerThread extends Thread {
    private Socket socket;
    private Map<String, Socket> socketMap;
    private UserDataBaseManager manager;
    public ServerThread(Socket socket, UserDataBaseManager manager, Map<String, Socket> socketMap){
        this.socket = socket;
        this.manager = manager;
        this.socketMap = socketMap;
    }
    @Override
    public void run() {
        try {
            Data receive = IODealer.receive(socket, false);

            //注意：在这里使用ClassConverter，那么client所发的所有内容必须都要经过ClassConverter才能识别

            if(receive == null){
                System.out.println("an unknown exception occurs or someone logs out");
                socket.close();
                return;
            }
            Data sends = disposeInMessage(receive);

            if(socket == null) return;
            if(sends == null){
                socket.close();
            }

            IODealer.send(socket, sends, true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Data disposeInMessage(Data inMessage) throws Exception {
        Data output = new Data();
        if (inMessage.isOperate(Data.LOAD_USER_INFO)){
            updateLog(null, "-------正在加载用户信息------");
            output = sendUserInfo(inMessage);
            updateLog(null, "加载完毕");
        }else if (inMessage.isOperate(Data.REGISTER)){
            updateLog(null, "-------正在注册中-------");
            output = register(inMessage);//userInfo
            updateLog(null, "-------注册完毕-------");
        }
        else if (inMessage.isOperate(Data.ADD_FRIEND)){
            updateLog(inMessage.operator, "-------正在查找用户的信息-------");
            output = makeFriend(inMessage);//userInfo
            updateLog(inMessage.operator, "-------添加朋友完毕-------");
        }
        else if (inMessage.isOperate(Data.CONNECT)){
            updateLog(inMessage.operator, "-------与用户开始进行通讯-------");
            output = connectToClient(inMessage);//Message
        }
        else if (inMessage.isOperate(Data.LOAD_DIALOGUE)){
            updateLog(inMessage.operator, "-------正在查找用户的对话信息-------");
            output = loadDialogues(inMessage);//ArrayList<Message>
            System.out.println("user's operator " + inMessage.operator);
            updateLog(inMessage.operator, "-------查找用户的对话信息完毕-------\n");
        }
        /*
        else if(inMessage.isOperate(Data.CREATE_GROUP)){
            updateLog(inMessage.operator, "--------正在创建新的群---------");
            output = createGroup(inMessage);
            updateLog(inMessage.operator, "--------创建新的群完毕---------");
        }
        else if(inMessage.isOperate(Data.JOIN_GROUP)){
            updateLog(inMessage.operator, "--------正在加入群" + inMessage.ID + "----------");
        }

         */

        return output;
    }
    private Data loadDialogues(Data inMessage) throws SQLException {
        return manager.loadDialogues(inMessage.name);
    }
    private Data connectToClient(Data inMessage) throws Exception {
        String name = inMessage.name;

        socketMap.put(name, socket);

        Data data;
        while (true){
            data = IODealer.receive(socket, false);

            System.out.println();
            updateLog(name, "服务器收到一个DataPackage");

            if(data == null) break;
            if(data.isOperate(Data.EXIT)) break;

            Message message = data.message;

            System.out.println(message);

            Socket toSocket = socketMap.get(message.receiver);

            if(toSocket != null){
                IODealer.send(toSocket, data, false);
                updateLog(name, "找到receiver，服务器执行发送语句");
            } else {
                manager.storeMessage(message);
            }
        }

        socketMap.remove(name);

        return null;
    }
    private Data makeFriend(Data message) throws SQLException, IOException {
        Data data = manager.makeFriend(message.name, message.operator);
        Socket socket = socketMap.get(data.name);
        if (data.ID != -1 && socket != null){
            Data data1 = new Data(message.oprInfo);
            data1.setOperateType(Data.ADD_FRIEND);
            IODealer.send(socket, data1, false);
        }
        return data;
    }
    private Data sendUserInfo(Data userInfo) throws SQLException {
        int ID = userInfo.ID;
        updateLog(null, "接受到ID=" + ID);

        String password = userInfo.password;
        System.out.println();
        updateLog(null, "接受到密码=" + password);

        Data proceeded = manager.selectByIDAndPassword(ID, password);
        Data tmp = new Data(-1);
        if(proceeded == null){
            System.out.println();
            updateLog(null, "ID与密码不匹配");
            return tmp;
        }
        addUser(proceeded.name);
        return proceeded;
    }
    private Data register(Data info) throws SQLException {
        int ID = manager.register(info);
        return new Data(ID);
    }
    private Data createGroup(Data info) {
        if(manager.createGroup(info)){
            return new Data(1);
        } else {
            return new Data(-1);
        }
    }
    private Data joinGroup(Data info){
        if(manager.joinGroup(info)){
            return new Data(1);
        } else {
            return new Data(-1);
        }
    }

    private void updateLog(String user, String log){
        Platform.runLater(() -> {
            if(user != null)
                ServerLauncher.update(user, log);
            else
                ServerLauncher.update(ServerLauncher.MAIN, log);
        });
    }
    private void addUser(String user){
        Platform.runLater(() -> {
            ServerLauncher.addUser(user);
        });
    }
}
