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
        RegisterView registerView = new RegisterView();
        AddFriendView addFriendView = new AddFriendView();

        StageM.getManager().addStage(Resource.LoginViewID, loginView);
        StageM.getManager().addStage(Resource.RegisterID, registerView);
        StageM.getManager().addStage(Resource.AddFriendView, addFriendView);

        StageM.getManager().show(Resource.LoginViewID);
    }
}
