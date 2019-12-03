package client.view;

import client.launcher.Resource;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * 登陆窗口
 */

public class LoginView extends Stage {

    public LoginView() throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource(Resource.LoginViewResource));
        ((TextField) root.lookup("#IDField")).setPromptText("请输入你的ID");
        ((TextField) root.lookup("#passwordField")).setPromptText("请输入你的密码");
        root.setOnMouseClicked(event -> {
            root.requestFocus();
        });
        Scene scene = new Scene(root, 700, 400);
        this.setScene(scene);
    }
}
