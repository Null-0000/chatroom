package client.view;

import client.controller.ChatViewController;
import client.launcher.Resource;
import javafx.application.Platform;
import kit.Info;
import kit.Message;
import javafx.beans.property.ListProperty;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.control.Label;

import javafx.scene.layout.*;

import javafx.stage.Stage;

import java.io.IOException;

public class ChatView extends Stage {
    protected ChatViewController controller;
    protected Info info;
    protected GridPane root;
    public ChatView() {}
    public ChatView(Info info, ListProperty<Message> messageList) throws IOException {
        this.info = info;
        FXMLLoader loader = new FXMLLoader();
        controller = new ChatViewController(info, messageList);
        loader.setController(controller);
        loader.setLocation(this.getClass().getResource(Resource.ChatViewResource));
        setTitle("chatting chamber");

        root = loader.load();

        ((Label)((HBox)root.getChildren().get(0)).getChildren().get(0)).setText(info.getName());

        Scene scene = new Scene(root);
        this.setScene(scene);
        setResizable(false);


    }
    public ChatViewController getController(){
        return controller;
    }
    public void synchronizeMessage(ListProperty<Message> messageList){
        controller.synchroniseMessages(messageList);
    }

}
