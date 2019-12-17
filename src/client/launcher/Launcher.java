package client.launcher;

import client.view.*;
import javafx.application.Application;
import javafx.stage.Stage;

import java.io.IOException;


public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        LoginView loginView = new LoginView();

        StageM.getManager().addStage(Resource.LoginViewID, loginView);

        StageM.getManager().show(Resource.LoginViewID);
    }
}
