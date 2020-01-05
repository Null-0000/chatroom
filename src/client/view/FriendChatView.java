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

import kit.UserInfo;

import java.io.IOException;

public class FriendChatView extends ChatView {
    public FriendChatView(UserInfo info, ListProperty<Message> messageList) throws IOException {
        super(info, messageList);

        setOnCloseRequest((e) -> {
            Platform.runLater(() -> {
                MainView.clearFriendListSelection();
            });
        });
    }

}