package client.view;

import client.launcher.Resource;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterView extends Stage {
    public RegisterView() throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource(Resource.LoginViewResource));
        Stage stage = new Stage();
        stage.setScene(new Scene(root, 500, 500));
        stage.setTitle("Register");
    }
}
