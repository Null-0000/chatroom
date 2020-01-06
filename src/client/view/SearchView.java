package client.view;

import client.launcher.Resource;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class SearchView extends Stage {
    public SearchView() throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource(Resource.AddFriendViewResource));

        this.setTitle("Add New Friend");

        root.setOnMouseClicked(event -> {
            root.requestFocus();
        });

        this.setScene(new Scene(root));
        setResizable(false);
        getIcons().add(new Image(String.valueOf(this.getClass().getResource("images/AppIcon.png"))));
    }
}
