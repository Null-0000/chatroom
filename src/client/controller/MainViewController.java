package client.controller;

import client.launcher.Resource;
import client.model.Dialogue;
import client.model.User;
import client.view.StageM;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    @FXML private Label id;
    @FXML private Label name;
    @FXML private Label sig;
    @FXML private ListView<String> friList;

    @FXML public void addFriend(){
        StageM.getManager().show(Resource.AddFriendView);
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        friList.getSelectionModel().selectedItemProperty().addListener(
                (obs, ov, nv) -> {
                    Dialogue dialogue = User.getInstance().getDialogueFrom(nv);
                    dialogue.show();
                    //第二次打开ChatView的时候才生效（滚至底部）
                    dialogue.getChatView().getController().scrollToBottom();
                }
        );
    }

}
