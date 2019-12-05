package client.view;

import client.launcher.Resource;
import client.model.User;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableArray;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.beans.PropertyChangeSupport;
import java.io.IOException;
import java.util.ArrayList;

public class MainView extends Stage {

    public MainView(User user) throws IOException {
        int id = user.getID();
        String name = user.getName();
        String signature = user.getSignature();
        ObservableList<String> friends = user.getFriendList();
        AnchorPane anchorPane = FXMLLoader.load(getClass().getResource(Resource.MainViewResource));
        VBox vBox = (VBox) anchorPane.getChildren().get(0);
        GridPane gridPane = (GridPane) vBox.getChildren().get(0);
        ((Label)gridPane.getChildren().get(0)).setText(""+id);
        ((Label)gridPane.getChildren().get(1)).setText(""+name);
        ((Label)gridPane.getChildren().get(2)).setText(""+signature);

        ListView<String> listView = (ListView<String>) vBox.getChildren().get(1);
        listView.setItems(friends);

        setScene(new Scene(anchorPane));

        setOnCloseRequest(event -> {
            try {
                User.getInstance().exit();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert("退出时错误");
            }
            System.exit(0);
            Platform.exit();
        });
    }
    private void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(null);
        alert.setHeaderText(message);
        alert.show();
    }

}
