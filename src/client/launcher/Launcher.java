package client.launcher;

import client.view.LoginView;
import client.view.MainView;
import client.view.RegisterView;
import client.view.StageM;
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
        RegisterView registerView = new RegisterView();

        loginView.isShowing();
        StageM.getManager().addStage(Resource.LoginViewID, loginView);
        StageM.getManager().addStage(Resource.RegisterID, registerView);

        StageM.getManager().show(Resource.LoginViewID);
    }
}
