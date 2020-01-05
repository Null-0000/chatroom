package client.view;

import client.launcher.Resource;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class SetView extends Stage {
    private String userName;
    private String userSig;
    public SetView() throws IOException {
        Parent root = FXMLLoader.load(this.getClass().getResource(Resource.SetViewResource));
        this.setTitle("Settings");
        this.setScene(new Scene(root));
    }
}
