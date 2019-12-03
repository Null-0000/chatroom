package client.view;

import client.launcher.Resource;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

import java.io.IOException;

public class MainView extends Stage {
    public MainView() throws IOException {
        Pane root = FXMLLoader.load(getClass().getResource(Resource.MainViewResource));

        this.setScene(new Scene(root, 500, 600));
        this.setTitle("MainView");
    }
}
