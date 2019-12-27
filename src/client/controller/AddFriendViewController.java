package client.controller;

import kit.ShowDialog;
import client.model.User;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;

import javafx.scene.control.TextField;

import javafx.scene.layout.GridPane;

import java.io.IOException;

public class AddFriendViewController {
    @FXML private GridPane root;
    @FXML private TextField friendNameField;
    @FXML private TextField friendIDField;

    public void SearchButtonAction(ActionEvent actionEvent) {
        String friendName;
        int ID = -1;
        friendName = friendNameField.getText();
        ID = Integer.parseInt(friendIDField.getText());
        if(friendName == null && ID == -1){
            ShowDialog.showWarning("请输入好友的信息");
            return;
        }
        if(friendName.equals(User.getInstance().getName())){
            ShowDialog.showWarning("添加的好友不能为自己");
            return;
        }
        if(friendName != null && User.getInstance().getFriendList().contains(friendName)){
            ShowDialog.showMessage("你已添加" + friendName + "为好友");
            return;
        }
        try {
            if(ShowDialog.showConfirm("确认添加好友", friendName)){
                if(Connector.getInstance().makeFriendWith(friendName)){
//                    User.getInstance().addFriend(friendName);
                    ShowDialog.showMessage("添加好友成功");
                } else {
                    ShowDialog.showMessage("好友未找到，请确认好友信息");
                }
            } else {
                return;
            }

        } catch (IOException e) {
            ShowDialog.showAlert(e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void keyAction(ActionEvent actionEvent) {
        SearchButtonAction(new ActionEvent());
    }
}
