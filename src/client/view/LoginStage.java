package client.view;

import client.launcher.Recourse;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * 登陆窗口
 */

public class LoginStage extends Stage {

    public LoginStage() throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(Recourse.LoginStage));

        Scene scene = new Scene(root, 500, 500);

    }
}
