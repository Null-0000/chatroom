package client.view;

import client.controller.ChatViewController;
import client.launcher.Resource;
import javafx.application.Platform;
import kit.Message;
import javafx.beans.property.ListProperty;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.control.Label;

import javafx.scene.layout.*;

import javafx.stage.Stage;

import java.io.IOException;

public class ChatView extends Stage {
    private ChatViewController controller;
    public ChatView(int id, ListProperty<Message> messageList, boolean isGroup) throws IOException {
        FXMLLoader loader = new FXMLLoader();
        controller = new ChatViewController(id, messageList);
        controller.isGroup = isGroup;
        loader.setController(controller);
        loader.setLocation(this.getClass().getResource(Resource.ChatViewResource));
        setTitle("chatting chamber");

        GridPane root = loader.load();

        ((Label)((HBox)root.getChildren().get(0)).getChildren().get(0)).setText("" + id);

        Scene scene = new Scene(root);
        this.setScene(scene);
        setResizable(false);

        setOnCloseRequest((e)->{
            Platform.runLater(()->{
                if (controller.isGroup)
                    MainView.clearGroupListSelection();
                else
                    MainView.clearFriendListSelection();
            });
        });
    }
    public ChatViewController getController(){
        return controller;
    }
    public void synchronizeMessage(ListProperty<Message> messageList){
        controller.synchroniseMessages(messageList);
    }

}