package client.model;

import javafx.scene.control.Alert;

public class ShowDialog {
    public static void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(null);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
    public static void showMessage(String message){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("information");
        alert.setContentText(null);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
}
