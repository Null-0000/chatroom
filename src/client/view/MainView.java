package client.view;

import client.launcher.Resource;
import client.model.User;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import old_version.user.Dialogues;

import java.io.IOException;
import java.util.ArrayList;

public class MainView extends Stage {

    public MainView(User user) throws IOException {
        int id = user.getID();
        String name = user.getName();
        String signature = user.getSignature();
        ArrayList<String> friends = user.getFriendList();
        AnchorPane anchorPane = FXMLLoader.load(this.getClass().getResource(Resource.MainViewResource));
        VBox vBox = (VBox) anchorPane.getChildren().get(0);
        GridPane gridPane = (GridPane) vBox.getChildren().get(0);
        ((Label)gridPane.getChildren().get(0)).setText(""+id);
        ((Label)gridPane.getChildren().get(1)).setText(""+name);
        ((Label)gridPane.getChildren().get(2)).setText(""+signature);

        ObservableList<String> friList = FXCollections.observableArrayList(friends);
        ListView<String> listView = (ListView<String>) vBox.getChildren().get(1);
        listView.setItems(friList);

        this.setScene(new Scene(anchorPane));

    }
}
