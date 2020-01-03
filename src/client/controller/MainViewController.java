package client.controller;

import client.launcher.Resource;
import client.model.Dialogue;
import client.model.Friend;
import client.model.User;
import client.model.UserCard;
import client.view.AddFriendView;
import client.view.StageM;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import kit.UserInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML private ListView<UserCard> friendListView;
    @FXML private Button addFriendButton;

    @FXML public void addFriend() throws IOException {
        StageM.getManager().resetStage(Resource.AddFriendView, new AddFriendView());
        StageM.getManager().show(Resource.AddFriendView);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ObservableList<UserCard> friendCards = FXCollections.observableArrayList();

        for (Friend friend: User.getInstance().getFriendList()){
            UserCard card = friend.getUserInfo().getUserCard();
            friendCards.add(card);
        }

        friendListView.setItems(friendCards);
        friendListView.getSelectionModel().selectedItemProperty().addListener(new NoticeListItemChangeListener());
        addFriendButton.setTooltip(new Tooltip("添加好友"));
        addFriendButton.setOnAction(actionEvent -> {
            try {
                addFriend();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private class NoticeListItemChangeListener implements ChangeListener<Object> {
        @Override
        public void changed(ObservableValue<?> observableValue, Object o, Object t1) {
            UserCard userCard = (UserCard) t1;
            if (userCard == null) return;
            Dialogue dialogue = User.getInstance().getDialogueFrom(userCard.getName());
            dialogue.show();
            dialogue.getHasNewMessage().set(false);

        }
    }

}
