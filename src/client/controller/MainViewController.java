package client.controller;

import client.launcher.Resource;
import client.model.Dialogue;
import client.model.User;
import client.view.StageM;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML private ListView<String> friendListView;
    @FXML private Button addFriendButton;

    @FXML public void addFriend(){
        StageM.getManager().show(Resource.AddFriendView);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<String> friendList = FXCollections.observableArrayList(User.getInstance().getFriendList());
        friendListView.setItems(friendList);

        /*friendListView.getSelectionModel().selectedItemProperty().addListener(
                (obs, ov, nv) -> {
                    Dialogue dialogue = User.getInstance().getDialogueFrom(nv);
                    dialogue.show();
                }
        );
        friendListView.setOnMouseClicked(mouseEvent -> {

        });*/
        friendListView.getSelectionModel().selectedItemProperty().addListener(new NoticeListItemChangeListener());
        addFriendButton.setTooltip(new Tooltip("添加好友"));
        addFriendButton.setOnAction(actionEvent -> addFriend());
    }
    private class NoticeListItemChangeListener implements ChangeListener<Object> {

        @Override
        public void changed(ObservableValue<?> observableValue, Object o, Object t1) {
            Dialogue dialogue = User.getInstance().getDialogueFrom((String) t1);
            dialogue.show();
        }
    }

}
