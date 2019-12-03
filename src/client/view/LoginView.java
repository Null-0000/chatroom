package client.view;

import client.launcher.Resource;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * 登陆窗口
 */

public class LoginView extends Stage {

    public LoginView() throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource(Resource.LoginViewResource));

        /*
        @TODO 为什么加载出来是一个空的面板？
         */

        Scene scene = new Scene(root, 500, 500);
        this.setScene(scene);
    }
}
