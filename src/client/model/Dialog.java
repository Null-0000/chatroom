package client.model;

import client.view.ChatView;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import kit.Message;

import javax.imageio.stream.FileImageOutputStream;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public abstract class Dialog implements Serializable {
    protected transient ListProperty<Message> messageList;
    protected transient String userA;

    protected transient ChatView chatView;
    protected transient BooleanProperty hasNewMessage;

    public Dialog() {
    }

    public Dialog(String userA) {
        this.userA = userA;
        ObservableList<Message> observableList = FXCollections.observableArrayList();
        this.messageList = new SimpleListProperty<>(observableList);
        this.hasNewMessage = new SimpleBooleanProperty(false);
    }

    public abstract void setChatView() throws IOException;

    public void synchronizeMessage() {
        chatView.synchronizeMessage(messageList);
    }

    /*    public void setChatView() throws IOException {
            chatView = new ChatView(userB, messageList);
            //chatView不能被序列化，故每次读取本地文件后需要重新new哟个chatView
        }*/
    public void updateMessage(Message message) {
        int user_id = User.getInstance().getID();
        int to_id;
        if (message.isMass)
            to_id = message.receiver;
        else
            to_id = (message.sender == user_id)? message.receiver: message.sender;

        switch (message.ctype.replaceAll("/.*", "")) {
            case "image":
                File imgDir = new File("out/production/chatroom/client/data/" + User.getInstance().getName() +
                        "/" + to_id + "/images");
                Date imgDate = new Date();
                String imgSuffix = "." + message.ctype.replaceAll(".*/", "");
                File imgFile = new File(imgDir.getPath() + "/img" + imgDate.getTime() + imgSuffix);
                message.setUrl(".." + imgFile.getPath().replaceAll(".*?client", "").replaceAll("\\\\", "/"));
                try {
                    imgFile.createNewFile();
                    FileImageOutputStream fios = new FileImageOutputStream(imgFile);
                    fios.write(message.content);
                    fios.flush();
                    fios.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            case "audio":
                File audDir = new File("out/production/chatroom/client/data/" + User.getInstance().getName() +
                        "/" + to_id + "/audios");
                Date audDate = new Date();
                String audSuffix = "." + message.ctype.replaceAll(".*/", "");
                File audFile = new File(audDir.getPath() + "/aud" + audDate.getTime() + audSuffix);
                message.setUrl(".." + audFile.getPath().replaceAll(".*?client", "").replaceAll("\\\\", "/"));
                try {
                    audFile.createNewFile();
                    FileImageOutputStream fios = new FileImageOutputStream(audFile);
                    fios.write(message.content);
                    fios.flush();
                    fios.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        if (!chatView.isShowing()) hasNewMessage.set(true);
        messageList.add(message);
    }

    public ListProperty<Message> getMessageList() {
        return messageList;
    }

    public void show() {
        chatView.show();
    }

    public void hide() {
        chatView.hide();
    }

    public ChatView getChatView() {
        return chatView;
    }

    public BooleanProperty getHasNewMessage() {
        return hasNewMessage;
    }

}

