package client.view;

import client.launcher.Resource;
import javafx.fxml.FXMLLoader;

import javafx.scene.Scene;
import javafx.scene.control.Label;

import javafx.scene.layout.AnchorPane;

import javafx.scene.layout.VBox;
import javafx.stage.Stage;


import java.io.IOException;

public class ChatView extends Stage {
    public ChatView(String chatTo) throws IOException {
        AnchorPane root = FXMLLoader.load(this.getClass().getResource(Resource.ChatViewResource));
        setTitle("chatting chamber");

        VBox vBox = (VBox) root.getChildren().get(0);
        ((Label) vBox.getChildren().get(0)).setText(chatTo);

        Scene scene = new Scene(root);
        this.setScene(scene);
    }
}
