package client.views;

import javafx.animation.*;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MyStage extends Application {

    @Override
    public void start(Stage primaryStage) throws IOException {
        GridPane root = FXMLLoader.load(MyStage.class.getResource("login.fxml"));
        Circle circle = new Circle(0, 0, 50);
        circle.setFill(Color.GRAY);
        circle.setOpacity(0.2);

        TranslateTransition translate = new TranslateTransition(Duration.millis(250));
        translate.setToX(300);
        translate.setToY(10);

        FillTransition fillTransition = new FillTransition(Duration.millis(250));
        fillTransition.setToValue(Color.DARKRED);

        ParallelTransition transition = new ParallelTransition(circle, translate, fillTransition);
        transition.setCycleCount(Timeline.INDEFINITE);
        transition.setAutoReverse(true);
        transition.play();

        Button stopTransitionButton = new Button("stop moving");
        stopTransitionButton.setOnAction(event -> transition.pause());

        Button startTransitionButton = new Button("start moving");
        startTransitionButton.setOnAction(event -> transition.play());

        root.getChildren().add(circle);
        root.add(stopTransitionButton, 0, 4);
        root.add(startTransitionButton, 1, 4);

        Scene scene1 = new Scene(root);

        primaryStage.setTitle("Login");
        primaryStage.setScene(scene1);
        primaryStage.show();
    }
}
