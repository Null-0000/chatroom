package client.controller;

import client.model.Message;
import client.model.User;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.web.HTMLEditor;


import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;

public class ChatViewController implements Initializable {
    @FXML private HTMLEditor htmlEditor;
    @FXML private TextArea textArea;
    @FXML private Label chatToLabel;
    private final String HTMLHEAD = "<html><head><style>p{font-size:25px;}</style></head><body>";
    private final String HTMLTAIL = "</body></html>";
    private String htmlText = "";
    private String chatTo;
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
        synchronized (htmlEditor){
            Date now = new Date();
            Message message = new Message(chatTo, User.getInstance().getName(), content, now);
            User.getInstance().sendMessage(message);
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        htmlEditor.setVisible(false);
        Platform.runLater(()->{
            Node[] nodes = htmlEditor.lookupAll(".tool-bar").toArray(new Node[0]);
            for (Node node: nodes){
                node.setVisible(false);
                node.setManaged(false);
            }
            htmlEditor.setVisible(true);
        });
        htmlEditor.setDisable(true);
    }
    public void synchroniseMessages(ListProperty<Message> messageList){
        messageList.addListener((obs, ov, nv) ->{
            Message newMessage = nv.get(nv.size() - 1);
            if (newMessage.sender.equals(chatTo)){
                htmlText += newMessage.toHTML(true);
            } else {
                htmlText += newMessage.toHTML(false);
            }
            Platform.runLater(()->htmlEditor.setHtmlText(HTMLHEAD + htmlText + HTMLTAIL));
            /**while you are updating the component out of FX application thread, you will get an
             * IllegalStateException and then use PlatForm.runLater to solve it.*/
        });
    }
    public void loadMessages(ListProperty<Message> messageList){
        for (Message message: messageList){
            boolean isLeft = message.sender.equals(chatTo);
            htmlText += message.toHTML(isLeft);
        }
        htmlEditor.setHtmlText(HTMLHEAD + htmlText + HTMLTAIL);
    }
}
