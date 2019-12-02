package client.launcher;

import client.view.StageM;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        StageM.getManager().addStage(Recourse.LoginStage);
        StageM.getManager().show(Recourse.LoginStage);
    }
}
