package client.view;

import client.launcher.Resource;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class RegisterView extends Stage {
    public RegisterView() throws IOException {
        Pane root = FXMLLoader.load(this.getClass().getResource(Resource.RegisterViewResource));

        this.setTitle("Register");

        FileInputStream fileInputStream = new FileInputStream(new File("src/client/view/images/defaultUserIcon.jpeg"));
        ((ImageView)root.lookup("#selectedIcon")).setImage(new Image(fileInputStream));

        root.setOnMouseClicked(event -> {
            root.requestFocus();
        });

        this.setScene(new Scene(root));
        this.setOnCloseRequest(e -> StageM.getManager().show(Resource.LoginViewID));
        setResizable(false);
        getIcons().add(new Image(String.valueOf(this.getClass().getResource("images/AppIcon.png"))));
    }
}
