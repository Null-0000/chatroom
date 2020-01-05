package client.view;

import client.launcher.Resource;
import client.model.User;

import kit.UserCard;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class MainView extends Stage {
    private static ListView<UserCard> friendListView;
    private static ListView<UserCard> groupListView;

    public MainView(User user) throws IOException {

        SplitPane root= FXMLLoader.load(this.getClass().getResource(Resource.MainViewResource));

        ObservableList items = root.getItems();
        GridPane userInfoGridPane = (GridPane) items.get(0);
        TabPane tabPane = (TabPane) items.get(1);
        ObservableList tabs = tabPane.getTabs();
        ((Tab)tabs.get(0)).getContent().lookup("#friendListView");

        ((Label)userInfoGridPane.getChildren().get(5)).setText(String.valueOf(user.getID()));
        ((Label)userInfoGridPane.getChildren().get(5)).setTooltip(new Tooltip(String.valueOf(user.getID())));

        ((Label)userInfoGridPane.getChildren().get(4)).setText(user.getName());
        ((Label)userInfoGridPane.getChildren().get(4)).setTooltip(new Tooltip(user.getName()));

        ((Label)userInfoGridPane.getChildren().get(7)).setText(user.getSignature());
        ((Label)userInfoGridPane.getChildren().get(7)).setTooltip(new Tooltip(user.getSignature()));

        File userIcon = new File("out/production/chatroom/client/data/" + user.getName() + "/icon.png");

        ((ImageView)userInfoGridPane.getChildren().get(0)).setImage(new Image(new FileInputStream(userIcon)));
        friendListView = (ListView<UserCard>) ((Tab)tabs.get(0)).getContent().lookup("#friendListView");
        groupListView = (ListView<UserCard>) ((Tab)tabs.get(1)).getContent().lookup("#groupListView");

        setScene(new Scene(root));

        setOnCloseRequest((e)->{
            try {
                User.getInstance().exit();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            Platform.exit();
            System.exit(0);
        });
        setOnShowing((e) -> {
            this.requestFocus();
        });
        setTitle("Chatting Room");
        getIcons().add(new Image(String.valueOf(this.getClass().getResource("images/AppIcon.png"))));
    }

    public static void clearFriendListSelection(){
        friendListView.getSelectionModel().clearSelection();
    }

    public static void clearGroupListSelection() {
        groupListView.getSelectionModel().clearSelection();
    }
}
