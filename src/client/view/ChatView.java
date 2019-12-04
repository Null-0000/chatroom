package client.view;

import client.controller.ChatViewController;
import client.launcher.Resource;
import client.model.Message;
import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
import java.util.List;

public class ChatView extends Stage {

    public ChatView(String chatTo, List<Message> messageList) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        ChatViewController controller = new ChatViewController(chatTo);

        ObservableList<Message> messages = FXCollections.observableArrayList(messageList);

        controller.synchroniseMessages(messages);
        loader.setController(controller);
        loader.setLocation(this.getClass().getResource(Resource.ChatViewResource));
        setTitle("chatting chamber");

        AnchorPane root = loader.load();

        VBox vBox = (VBox) root.getChildren().get(0);
        ((Label) vBox.getChildren().get(0)).setText(chatTo);

        Scene scene = new Scene(root);
        this.setScene(scene);
    }
}
