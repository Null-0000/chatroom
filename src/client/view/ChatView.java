package client.view;

import client.controller.ChatViewController;
import client.launcher.Resource;
import client.model.Message;
import javafx.beans.property.ListProperty;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import java.io.IOException;

public class ChatView extends Stage {
    /*private Document document;
    private WebEngine webEngine;

     */
    public ChatView(String chatTo, ListProperty<Message> messageList) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        ChatViewController controller = new ChatViewController(chatTo);
        controller.synchroniseMessages(messageList);
        loader.setController(controller);
        loader.setLocation(this.getClass().getResource(Resource.ChatViewResource));
        setTitle("chatting chamber");

        AnchorPane root = loader.load();

        VBox vBox = (VBox) root.getChildren().get(0);
        ((Label) vBox.getChildren().get(0)).setText(chatTo);
        /*
        webEngine = ((WebView) vBox.getChildren().get(1)).getEngine();
        webEngine.getLoadWorker().stateProperty().addListener((
                (obs, ov, nv)->{
                    System.out.println(nv);
                    if (nv == Worker.State.SUCCEEDED)
                        document = webEngine.getDocument();
                }
                ));
        webEngine.load(getClass().getResource("fxml/WebView.html").toExternalForm());

         */
        Scene scene = new Scene(root);
        this.setScene(scene);
    }
    /*public void updateWebView(Message message, boolean left){
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
        webEngine.reload();
    }

     */
}
