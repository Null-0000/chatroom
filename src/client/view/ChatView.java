package client.view;

import client.controller.ChatViewController;
import client.launcher.Resource;
import client.model.Message;
import client.model.MyList;
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


import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.List;

public class ChatView extends Stage {
    private ChatViewController controller;
    MyList<Message> messageList;

    public ChatView(String chatTo, MyList<Message> messageList) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        controller = new ChatViewController(chatTo);
        this.messageList = messageList;

        controller.synchroniseMessages(this.messageList);
        loader.setController(controller);
        loader.setLocation(this.getClass().getResource(Resource.ChatViewResource));
        setTitle("chatting chamber");

        AnchorPane root = loader.load();

        VBox vBox = (VBox) root.getChildren().get(0);
        ((Label) vBox.getChildren().get(0)).setText(chatTo);

        Scene scene = new Scene(root);
        this.setScene(scene);
    }
    public void loadLocalMessages(){
        controller.loadLocalMessages(this.messageList);
    }
}
