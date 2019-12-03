package client.controller;

import client.launcher.Resource;
import client.view.StageM;
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


        if(IDField.getText().isEmpty()){
            showAlert("请输入你的ID");
            return;
        }
        if(passwordField.getText().isEmpty()){
            showAlert("请输入密码");
            return;
        }

        if (NumberUtils.isDigits(IDField.getText())) ID = Integer.parseInt(IDField.getText());
        else {
            showAlert("您输入的ID不合法，请重新输入");
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
    @FXML
    protected void registerButtonAction(){
        StageM.getManager().show(Resource.RegisterID);
        StageM.getManager().close(Resource.LoginViewID);
    }

    private void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(null);
        alert.setHeaderText(message);
        alert.show();
    }
}