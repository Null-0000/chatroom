package client.view;

import client.launcher.Resource;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterView extends Stage {
    public RegisterView() throws IOException {
        GridPane root = FXMLLoader.load(this.getClass().getResource(Resource.RegisterViewResource));

        this.setTitle("Register");
        Text text = new Text();

        root.setOnMouseClicked(event -> {
            root.requestFocus();
        });

        this.setScene(new Scene(root, 700, 400));
        this.setOnCloseRequest(e -> StageM.getManager().show(Resource.LoginViewID));
        setResizable(false);
        getIcons().add(new Image(String.valueOf(this.getClass().getResource("images/AppIcon.png"))));
    }
}
