package server;

import javafx.application.Platform;
import kit.*;

import java.io.IOException;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class ServerThread extends Thread {
    private Socket socket;
    private Map<Integer, Socket> socketMap;
    private UserDataBaseManager manager;

    public ServerThread(Socket socket, UserDataBaseManager manager, Map<Integer, Socket> socketMap) {
        this.socket = socket;
        this.manager = manager;
        this.socketMap = socketMap;
    }

    @Override
    public void run() {
        try {
            Data receive = IODealer.receive(socket, false);

            //注意：在这里使用ClassConverter，那么client所发的所有内容必须都要经过ClassConverter才能识别

            if (receive == null) {
                System.out.println("an unknown exception occurs or someone logs out");
                socket.close();
                return;
            }
            Data sends = disposeInMessage(receive);

            if (socket == null) return;

            if (sends != null) IODealer.send(socket, sends, true);
            else {
                updateLog(receive.operatorInfo.getName(), "User log out");
//                IODealer.send(socket, sends, true);
//                socket.close();
                sends = new Data();
                //sends.operator = Data.EXIT;
                sends.setOperateType(Data.EXIT);
                IODealer.send(socket, sends, true);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Data disposeInMessage(Data inMessage) throws Exception {
        Data output = new Data();
        if (inMessage.isOperate(Data.LOAD_USER_INFO)) {
            updateLog(null, "-------正在加载用户信息------");
            output = sendUserInfo(inMessage);
            updateLog(null, "加载完毕");
        } else if (inMessage.isOperate(Data.REGISTER)) {
            updateLog(null, "-------正在注册中-------");
            output = register(inMessage);//userInfo
            updateLog(null, "-------注册完毕-------");
        } else if (inMessage.isOperate(Data.ADD_FRIEND)) {
            updateLog(inMessage.operatorInfo.getName(), "-------正在查找用户的信息-------");
            output = makeFriend(inMessage);//userInfo
            updateLog(inMessage.operatorInfo.getName(), "-------添加朋友完毕-------");
        } else if (inMessage.isOperate(Data.CONNECT)) {
            updateLog(inMessage.operatorInfo.getName(), "-------与用户开始进行通讯-------");
            output = connectToClient(inMessage);//Message
        } else if (inMessage.isOperate(Data.LOAD_MESSAGE)) {
            updateLog(inMessage.operatorInfo.getName(), "-------正在查找用户的对话信息-------");
            output = loadDialogues(inMessage);//ArrayList<Message>
            System.out.println("user's operatorInfo.getName() " + inMessage.operatorInfo.getName());
            updateLog(inMessage.operatorInfo.getName(), "-------查找用户的对话信息完毕-------\n");
        }
        //
        else if (inMessage.isOperate(Data.CREATE_GROUP)) {
            updateLog(inMessage.operatorInfo.getName(), "--------正在创建新的群---------");
            output = createGroup(inMessage);
            updateLog(inMessage.operatorInfo.getName(), "--------创建新的群完毕---------");
        } else if (inMessage.isOperate(Data.JOIN_GROUP)) {
            updateLog(inMessage.operatorInfo.getName(), "--------正在加入群" + inMessage.ID + "----------");
            output = joinGroup(inMessage);
            updateLog(inMessage.operatorInfo.getName(), "--------加入群" + inMessage.ID + "完毕----------");
        } else if (inMessage.isOperate(Data.GET_GROUP_MEM)) {
            updateLog(inMessage.operatorInfo.getName(), "--------获取群" + inMessage.ID + "的群成员---------");
            output = getMembers(inMessage);
            updateLog(inMessage.operatorInfo.getName(), "--------获取群" + inMessage.ID + " 成员列表完毕---------");
        }

        return output;
    }

    private Data loadDialogues(Data inMessage) throws SQLException {
        return manager.loadDialogues(inMessage.operatorInfo.getID());
    }

    private Data connectToClient(Data inMessage) throws Exception {
        String currentUserName = inMessage.name;
        int currentID = inMessage.ID;

        socketMap.put(currentID, socket);

        Data data;
        while (true) {
            data = IODealer.receive(socket, false);

            updateLog(currentUserName, "服务器收到一个DataPackage");

            if (data == null) break;
            if (data.isOperate(Data.EXIT)) break;

            sendMessage(currentID, currentUserName, data.message);
        }

        socketMap.remove(currentID);

        return null;
    }

    private void sendMessage(int currentID, String currentUser, Message msg) throws Exception {
        if (msg.isMass) {
            updateLog(currentUser, "用户发送了一条群消息 " + msg);
            ArrayList<Integer> targetIDs = (ArrayList<Integer>) manager.getMembers(msg.receiver, true);
            for (int id : targetIDs) {
                if (id == currentID) continue;
                Socket targetSocket = socketMap.get(id);
                sendMsg(targetSocket, msg);
            }
        } else {
            updateLog(currentUser, "用户发送了一条消息 " + msg);
            sendMsg(socketMap.get(msg.receiver), msg);
        }
        updateLog(currentUser, "发送完毕");
    }

    private void sendMsg(Socket socket, Message msg) throws Exception {
        if (socket == null) {
            manager.storeMessage(msg);
        } else {
            IODealer.send(socket, new Data(msg), false);
        }
    }

    private Data makeFriend(Data message) throws SQLException, IOException {
        Data data = manager.makeFriend(message.name, message.operatorInfo.getName());
        Socket socket = socketMap.get(data.name);
        if (data.ID != -1 && socket != null) {
            Data data1 = new Data(message.operatorInfo);
            data1.setOperateType(Data.ADD_FRIEND);
            IODealer.send(socket, data1, false);
        }
        return data;
    }

    private Data sendUserInfo(Data userInfo) throws Exception {
        int ID = userInfo.ID;
        updateLog(null, "接受到ID=" + ID);

        String password = userInfo.password;
        System.out.println();
        updateLog(null, "接受到密码=" + password);

        Data proceeded = manager.selectByIDAndPassword(ID, password);
        Data tmp = new Data(-1);
        if (proceeded == null) {
            System.out.println();
            updateLog(null, "ID与密码不匹配");
            return tmp;
        }
        if (socketMap.containsKey(proceeded.ID))
            return new Data(-2);
        addUser(proceeded.name);
        return proceeded;
    }

    private Data register(Data info) throws SQLException {
        int ID = manager.register(info);
        return new Data(ID);
    }

    private Data createGroup(Data info) {
        /*
        info 数据包需要包括 name：群名， operator，operatorID
         */

        return new Data(manager.createGroup(info));
    }

    private Data joinGroup(Data info) {
        /*
        info 数据包需要包括 name：群名 或 ID：群ID， operator， operatorID
         */
        Data data = manager.joinGroup(info);
        if (data.ID != -1) {
            info.setOperateType(Data.JOIN_GROUP);
            IODealer.send(socketMap.get(info.operatorInfo.getID()), info, false);
        }
        return data;
/*
        if(manager.joinGroup(info)){
            return new Data(1);
        } else {
            return new Data(-1);
        }

 */
    }

    private Data getMembers(Data data) {
        try {
            return new Data(manager.getMembers(data.ID, false));
        } catch (Exception e) {
            updateLog(data.operatorInfo.getName(), "获得群聊成员列表错误");
            e.printStackTrace();
        }
        return new Data(new ArrayList());
    }

    private void updateLog(String user, String log) {
        Platform.runLater(() -> {
            if (user != null)
                ServerLauncher.update(user, log);
            else
                ServerLauncher.update(ServerLauncher.MAIN, log);
        });
    }

    private void addUser(String user) {
        Platform.runLater(() -> {
            ServerLauncher.addUser(user);
        });
    }
}
