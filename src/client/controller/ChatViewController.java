package client.controller;

import client.model.Message;
import client.model.MyList;
import client.model.ShowDialog;
import client.model.User;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class ChatViewController implements Initializable{
    @FXML private WebView webView;
    @FXML private TextArea textArea;
    @FXML private Label chatToLabel;
    private String chatTo;
    private Document document;
    public ChatViewController(String chatTo){
        this.chatTo = chatTo;
    }
    @FXML private void sendMessage() {
        String content = textArea.getText();
        textArea.setText("");
        if(content.equals("")) {
            ShowDialog.showAlert("warning: can not send an empty message!");
            return;
        }
        synchronized (webView){
            Date now = new Date();
            Message message = new Message(chatTo, User.getInstance().getName(), content, now);
//            updateWebView(message, false);
            try {
                User.getInstance().sendMessage(message);
            } catch (IOException e) {
                e.printStackTrace();
                ShowDialog.showAlert("消息发送失败");
            }
        }
    }
    public void synchroniseMessages(MyList myList){
        myList.addPropertyChangeListener(evt -> {
            List<Message> newList = (List<Message>) evt.getNewValue();
            Message newMessage = newList.get(newList.size() - 1);
            if (newMessage.sender.equals(chatTo)){
                updateWebView(newMessage, true);
            } else {
                updateWebView(newMessage, false);
            }
        });
//        pcs.addPropertyChangeListener((ListChangeListener<Message>) c ->{
//            ObservableList<Message> newList = (ObservableList<Message>) c.getList();
//            Message newMessage = newList.get(newList.size() - 1);
//
//            if (newMessage.sender.equals(chatTo)){
//                updateWebView(newMessage, false);
//            } else if (newMessage.sender.equals(User.getInstance().getName())){
//                updateWebView(newMessage, true);
//            }
//        });
    }
    public void loadLocalMessages(List<Message> messageList) {
//        ShowDialog.showMessage("你的消息记录已经加载完毕");
        for(Message message: messageList){
            boolean isLeft = message.sender.equals(chatTo);
            updateWebView(message, isLeft);
        }
    }
    private void updateWebView(Message message, boolean left) {

            Element appendMessageHead = document.createElement("p");
            Element appendMessageContent = document.createElement("p");
            appendMessageHead.setTextContent(message.getHead());
            if (left) {
                appendMessageContent.setTextContent(message.getContent());
                appendMessageHead.setAttribute("align", "LEFT");
                appendMessageContent.setAttribute("align", "LEFT");
            } else {
                appendMessageContent.setTextContent(message.getContent());
                appendMessageHead.setAttribute("align", "RIGHT");
                appendMessageContent.setAttribute("align", "RIGHT");
            }
            document.getElementsByTagName("div").item(0).appendChild(appendMessageHead);
            document.getElementsByTagName("div").item(0).appendChild(appendMessageContent);

    }

    public void keyAction(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            sendMessage();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
//        ShowDialog.showMessage("进入ChatViewController的initialize");
        WebEngine engine = webView.getEngine();
        engine.getLoadWorker().stateProperty().addListener((obs, o, n)->{
            if (n == Worker.State.SUCCEEDED){
                document = engine.getDocument();
//                ShowDialog.showMessage("document被赋值");
//                if(document != null) ShowDialog.showMessage("document非空");
            }
        });
        engine.load(getClass().getResource("../view/fxml/WebView.html").toExternalForm());
    }
}