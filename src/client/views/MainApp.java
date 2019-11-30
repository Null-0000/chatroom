package client.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainApp extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        Parent root = FXMLLoader.load(MainApp.class.getResource("login.fxml"));
        Scene scene1 = new Scene(root);
        //scene1.getStylesheets().add(MainApp.class.getResource("Login.css").toExternalForm());

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene1);
        primaryStage.show();

    }
}
