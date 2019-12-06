package client.view;

import client.controller.ChatViewController;
import client.launcher.Resource;
import client.model.Message;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;

import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;

import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


import java.io.IOException;

public class ChatView extends Stage {
    private ChatViewController controller;
    public ChatView(String chatTo, ListProperty<Message> messageList) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        controller = new ChatViewController(chatTo);
        controller.synchroniseMessages(messageList);
        loader.setController(controller);
        loader.setLocation(this.getClass().getResource(Resource.ChatViewResource));
        setTitle("chatting chamber");

        BorderPane root = loader.load();

        ((Label)(root.getChildren().get(0))).setText(chatTo);

        Scene scene = new Scene(root);
        this.setScene(scene);
    }

    public void loadLocalMessages(ListProperty<Message> messageList) {
        controller.loadMessages(messageList);
    }

}
