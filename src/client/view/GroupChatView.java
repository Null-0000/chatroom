package client.view;

import client.controller.ChatViewController;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import kit.GroupInfo;
import kit.Message;
import kit.UserInfo;

import java.io.IOException;

public class GroupChatView extends ChatView {
    private ListView<String> membersView;
    public GroupChatView(GroupInfo groupInfo, ListProperty<Message> messageList) throws IOException {
        super(groupInfo, messageList);

        VBox vBox = new VBox();
        Label label = new Label();
        label.setText("群成员");

        membersView = new ListView<>();
        for (UserInfo info: groupInfo.getMembers()){
            membersView.getItems().add(info.getName());
        }
        membersView.setVisible(false);
        membersView.setPrefWidth(400);

        vBox.getChildren().addAll(label, membersView);

        root.add(vBox, 1, 0, 1, 4);


        ToggleButton memberBut = new ToggleButton();
        memberBut.setId("memControl");
        memberBut.setPrefHeight(30);
        memberBut.setPrefWidth(30);
        ((ToolBar) root.getChildren().get(2)).getItems().add(memberBut);
        memberBut.selectedProperty().addListener((obs, ov, nv) -> {
            membersView.setVisible(nv);
            vBox.setVisible(nv);
        });

        setOnCloseRequest((e)->{
            Platform.runLater(()->{
                MainView.clearGroupListSelection();
            });
        });
    }

    public ListView<String> getMembersView() {
        return membersView;
    }

}
