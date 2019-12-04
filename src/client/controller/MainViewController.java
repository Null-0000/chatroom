package client.controller;

import client.model.Dialogue;
import client.model.User;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML private Label id;
    @FXML private Label name;
    @FXML private Label sig;
    @FXML private ListView<String> friList;

    @FXML public void addFriend(){

    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        friList.getSelectionModel().selectedItemProperty().addListener(
                (obs, ov, nv) -> {
                    Dialogue dialogue = User.getInstance().getDialogueFrom(nv);
                    try {
                        dialogue.show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

}
