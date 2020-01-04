package client.model;


import javafx.animation.Animation;
import javafx.animation.FadeTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.util.Duration;


public class UserCard extends BorderPane {
    private int ID;
    private String name;
    private String sig;
    private String imgUrl;
    private ImageView icon;
    private VBox vBox;
    private Circle circle;
    private FadeTransition fadeTransition;
    //private Dialogue dialogue;
    public UserCard(int ID, String name, String sig, String imgUrl){
        this.ID = ID;
        this.name = name;
        this.sig = sig;
        this.imgUrl = imgUrl;
      //  this.dialogue = User.getInstance().getDialogueFrom(name);

        icon = new ImageView(new Image(imgUrl, 40, 40, false, false));
        vBox = new VBox();
        vBox.setSpacing(1);
        vBox.setPadding(new Insets(5, 10, 5, 10));
        //top right bottom left
        Label nameLabel = new Label(name);
        Label sigLabel = new Label(sig);
        sigLabel.setStyle("-fx-text-fill: gray;" +
                "-fx-font-family: 'Courier New';" +
                "-fx-font-weight: lighter;" +
                "-fx-font-style: italic;" +
                "-fx-font-size: 10px");

        vBox.getChildren().addAll(nameLabel, sigLabel);

        circle = new Circle();
        circle.setRadius(3);
        circle.setFill(Color.RED);
        circle.setVisible(false);

        fadeTransition = new FadeTransition(Duration.millis(400), circle);
        fadeTransition.setFromValue(0.8);
        fadeTransition.setToValue(0.1);
        fadeTransition.setCycleCount(Animation.INDEFINITE);
        fadeTransition.setAutoReverse(true);

        setLeft(icon);
        setCenter(vBox);
        setRight(circle);
        setAlignment(circle, Pos.CENTER);
    }

    public String getName(){
        return this.name;
    }
    public void showCircle(){
        circle.setVisible(true);
        fadeTransition.play();
    }
    public void hideCircle(){
        circle.setVisible(false);
        fadeTransition.stop();
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public int getID() {
        return this.ID;
    }
}
