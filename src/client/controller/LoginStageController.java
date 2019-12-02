package client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.math.NumberUtils;

public class LoginStageController {
    @FXML
    private GridPane root;
    @FXML
    private TextField IDField;
    @FXML
    private PasswordField passwordField;
    @FXML
    protected void loginButtonAction(ActionEvent event){
        int ID;
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(null);
        if (NumberUtils.isDigits(IDField.getText()))
            ID = Integer.parseInt(IDField.getText());
        else {
            //JOptionPane.showMessageDialog(panel, "您输入的ID不合法，请重新输入。");
            alert.setHeaderText("您输入的ID不合法，请重新输入！");
            alert.show();
            return;
        }
/*      try {
            SocketFunctions.login(ID, passwordField.getText());
        } catch (PasswordException exception) {
            //JOptionPane.showMessageDialog(panel, "ID与密码不匹配，请重新输入。");
            alert.setHeaderText("ID与密码不匹配，请重新输入！");
            alert.show();
        } catch (IOException ex) {
            ex.printStackTrace();
        }*/
    }
}