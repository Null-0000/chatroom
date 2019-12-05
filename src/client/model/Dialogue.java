package client.model;

import client.view.ChatView;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * dialogue between user and one friend
 */

public class Dialogue implements Serializable {
    private MyList<Message> messageList;
    private String friendName;

    transient private ChatView chatView;

    public Dialogue(String friendName, String userName) throws IOException {
        this.friendName = friendName;
//       messageList = new ArrayList<>();
        messageList = new MyList<>(new ArrayList());
    }

    public void updateMessage(Message message) {
        messageList.add(message);
    }
    public List<Message> getMessageList() {
        return messageList;
    }

    public ChatView getChatView() {
        return chatView;
    }
    public void setChatView(){
        try {
            if(messageList == null) messageList = new MyList<>(new ArrayList());
            chatView = new ChatView(friendName, messageList);
        } catch (IOException e) {
            ShowDialog.showAlert("加载" + friendName + "聊天界面错误01");
            e.printStackTrace();
        }
    }
    public void loadLocalDialogues(){
//        ShowDialog.showMessage("loadLocalDialogues");
        if(chatView == null){
            ShowDialog.showAlert("加载" + friendName + "本地消息错误");
            return;
        }
        chatView.loadLocalMessages();
    }

    transient boolean hasLoadLocalDialogues = false;
    public void show() {
        if(chatView == null){
            ShowDialog.showAlert("加载" + friendName + "聊天界面错误02");
            return;
        }
        if(!hasLoadLocalDialogues){
            loadLocalDialogues();
            hasLoadLocalDialogues = true;
        }
        chatView.show();
    }
}
