package client.controller;

import client.launcher.Resource;
import client.view.StageM;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;

public class LoginViewController {
    @FXML
    private GridPane root;
    @FXML
    private TextField IDField;
    @FXML
    private PasswordField passwordField;
    @FXML
    protected void loginButtonAction(ActionEvent event) throws IOException {
        if(IDField.getText().isEmpty()){
            showAlert("请输入你的ID");
            return;
        }
        if(passwordField.getText().isEmpty()){
            showAlert("请输入密码");
            return;
        }

        int ID; String password = passwordField.getText();
        if (NumberUtils.isDigits(IDField.getText())) ID = Integer.parseInt(IDField.getText());
        else {
            showAlert("您输入的ID不合法，请重新输入");
            return;
        }
        boolean isAccessible = Connector.getInstance().loadUserInfo(ID, password);
        if(isAccessible) showAlert("将要跳转到主界面");
        else showAlert("ID不存在或密码错误");
    }
    @FXML
    protected void registerButtonAction(){
        StageM.getManager().shift(Resource.LoginViewID, Resource.RegisterID);
    }

    private void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(null);
        alert.setHeaderText(message);
        alert.show();
    }

    public void paneKeyAction(KeyEvent keyEvent) throws IOException {
        if(keyEvent.getCode() == KeyCode.ENTER){
            loginButtonAction(new ActionEvent());
        }
    }
}