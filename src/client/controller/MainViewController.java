package client.controller;

import client.launcher.Resource;
import client.model.*;
import client.view.AddFriendView;
import client.view.StageM;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Optional;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {

    @FXML private ListView<UserCard> friendListView;
    @FXML private ListView<UserCard> groupListView;
    @FXML private Button addFriendButton;

    @FXML public void addFriend() throws IOException {
        StageM.getManager().resetStage(Resource.AddFriendView, new AddFriendView());
        StageM.getManager().show(Resource.AddFriendView);
    }
    @FXML public void createGroup() throws IOException {
        TextInputDialog tid = new TextInputDialog();
        Optional<String> rs = tid.showAndWait();
        if (!rs.isPresent()) return;
        File iconFile = MFileChooser.showFileChooser("group icon",
                "jpg", "jpeg", "bmp", "png");
        if (iconFile == null) return;
        FileInputStream fis = new FileInputStream(iconFile);
        byte[] bytes = fis.readAllBytes();
        if (Connector.getInstance().createGroup(rs.get(), bytes)){
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setHeaderText(null);
            alert.setHeaderText("create group successful");
            alert.show();
        }
        else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText(null);
            alert.setContentText("create group error");
            alert.show();
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        for (Friend friend: User.getInstance().getFriendList()){
            UserCard card = friend.getUserInfo().getUserCard();
            friendListView.getItems().add(card);
        }

        for (Group group: User.getInstance().getGroups().values()) {
            UserCard card = group.getGroupInfo().getGroupCard();
            groupListView.getItems().add(card);
        }

        User.getInstance().getFriends().addListener((obs, ov, nv)->{
            friendListView.getItems().clear();
            for (Friend friend: nv.values()){
                if (friend.getUserInfo().getUserCard() == null)
                    friend.getUserInfo().prepareUserCard();
                friendListView.getItems().add(friend.getUserInfo().getUserCard());
            }
        });

        User.getInstance().getGroups().addListener((obs, ov, nv) -> {
            groupListView.getItems().clear();
            for (Group group: nv.values()){
                if (group.getGroupInfo().getGroupCard() == null)
                    group.getGroupInfo().prepareGroupCard();
                groupListView.getItems().add(group.getGroupInfo().getGroupCard());
            }
        });

        friendListView.getSelectionModel().selectedItemProperty().
                addListener(new NoticeFriendListItemChangeListener());
        groupListView.getSelectionModel().selectedItemProperty().
                addListener(new NoticeGroupListItemChangeListener());

        addFriendButton.setTooltip(new Tooltip("添加好友"));
        addFriendButton.setOnAction(actionEvent -> {
            try {
                addFriend();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    private class NoticeFriendListItemChangeListener implements ChangeListener<Object> {
        @Override
        public void changed(ObservableValue<?> observableValue, Object o, Object t1) {
            UserCard userCard = (UserCard) t1;
            if (userCard == null) return;
            FriendDialog friendDialog = User.getInstance().getFriends
                    ().get(userCard.getID()).getFriendDialog();
            friendDialog.show();
            friendDialog.getHasNewMessage().set(false);
        }
    }

    private class NoticeGroupListItemChangeListener implements ChangeListener<Object> {
        @Override
        public void changed(ObservableValue<?> observableValue, Object o, Object t1) {
            UserCard userCard = (UserCard) t1;
            if (userCard == null) return;
            GroupDialog groupDialog = User.getInstance().getGroups().get(
                    userCard.getID()).getGroupDialog();
            groupDialog.show();
            groupDialog.getHasNewMessage().set(false);
        }
    }

}
