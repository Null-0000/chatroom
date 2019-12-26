package client.model;


import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;


public class UserCard extends HBox {
    private String name;
    private String sig;
    private String imgUrl;
    private ImageView icon;
    private VBox vBox;
    private Circle circle;
    //private Dialogue dialogue;
    public UserCard(String name, String sig, String imgUrl){
        this.name = name;
        this.sig = sig;
        this.imgUrl = imgUrl;
      //  this.dialogue = User.getInstance().getDialogueFrom(name);

        icon = new ImageView(new Image(imgUrl, 40, 40, false, false));
/*
        ToggleButton openBut = new ToggleButton();
        openBut.selectedProperty().addListener((obs, ov, nv)->{
            if (nv == Boolean.TRUE){
                dialogue.show();
            }
            else {
                dialogue.hide();
            }
        });

 */
        vBox = new VBox();
        vBox.setSpacing(3);
        vBox.getChildren().addAll(new Label(name), new Label(sig));

        circle = new Circle();
        circle.setRadius(3);
        circle.setFill(Color.RED);
        circle.setVisible(false);

        setAlignment(Pos.CENTER_LEFT);
        setSpacing(5);
        getChildren().addAll(icon, vBox, circle);
    }

    public String getName(){
        return this.name;
    }
    public void showCircle(){
        circle.setVisible(true);
    }
    public void hideCircle(){
        circle.setVisible(false);
    }
}
