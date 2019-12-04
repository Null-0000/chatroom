package client.controller;

import client.model.Dialogue;
import client.model.Message;
import client.model.User;
import javafx.beans.property.ListProperty;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;


import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class ChatViewController implements Initializable {
    @FXML private WebView webView;
    @FXML private TextArea textArea;
    @FXML private Label chatToLabel;
    private String chatTo;
    private Document document;
    private Node divItem;
    public ChatViewController(String chatTo){
        this.chatTo = chatTo;
    }
    @FXML private void sendMessage() throws IOException {
        String content = textArea.getText();
        textArea.setText("");
        if(content.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("warning: can not send an empty message!");
            return;
        }
        synchronized (webView){
            Date now = new Date();
            Message message = new Message(chatTo, User.getInstance().getName(), content, now);
            User.getInstance().sendMessage(message);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        WebEngine engine = webView.getEngine();
        engine.getLoadWorker().stateProperty().addListener((obs, o, n)->{
            if (n == Worker.State.SUCCEEDED){
                document = engine.getDocument();
                divItem = document.getElementsByTagName("div").item(0);
                System.out.println(divItem);
            }
        });
        engine.load(getClass().getResource("../view/fxml/WebView.html").toExternalForm());
    }
    public void synchroniseMessages(ListProperty<Message> messageList){
        messageList.addListener((obs, ov, nv) ->{
            Message newMessage = nv.get(nv.size() - 1);
            if (newMessage.sender.equals(chatTo)){
                updateWebView(newMessage, true);
            } else {
                updateWebView(newMessage, false);
            }
        });
    }
    private void updateWebView(Message message, boolean left){
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
        divItem.appendChild(appendMessageHead);
        divItem.appendChild(appendMessageContent);
    }
}
