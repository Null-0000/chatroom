package client.view;

import client.launcher.Resource;
import client.model.User;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import javax.swing.text.AbstractDocument;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class MainView extends Stage {
    public static ListView<String> friendListView;
    public MainView(User user) throws IOException {
        SplitPane root= FXMLLoader.load(this.getClass().getResource(Resource.MainViewResource));

        ObservableList items = root.getItems();
        GridPane userInfoGridPane = (GridPane) items.get(0);
        TabPane tabPane = (TabPane) items.get(1);
        ObservableList tabs = tabPane.getTabs();
        friendListView = (ListView<String>) ((Tab)tabs.get(0)).getContent().lookup("#friendListView");

        ((Label)userInfoGridPane.getChildren().get(5)).setText(String.valueOf(user.getID()));
        ((Label)userInfoGridPane.getChildren().get(5)).setTooltip(new Tooltip(String.valueOf(user.getID())));

        ((Label)userInfoGridPane.getChildren().get(4)).setText(user.getName());
        ((Label)userInfoGridPane.getChildren().get(4)).setTooltip(new Tooltip(user.getName()));

        ((Label)userInfoGridPane.getChildren().get(7)).setText(user.getSignature());
        ((Label)userInfoGridPane.getChildren().get(7)).setTooltip(new Tooltip(user.getSignature()));

        File userIcon = new File("src/client/data/" + user.getName() + ".png");
        FileOutputStream fileOutputStream = new FileOutputStream(userIcon);
        fileOutputStream.write(user.getMyIconBytes());

        ((ImageView)userInfoGridPane.getChildren().get(0)).setImage(new Image(new FileInputStream(userIcon)));

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
    public static void clearListSelection(){
        friendListView.getSelectionModel().clearSelection();
    }
}
