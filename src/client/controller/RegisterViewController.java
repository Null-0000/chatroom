package client.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterViewController {
    @FXML
    private GridPane root;
    @FXML
    private TextField userName;
    @FXML
    private TextField userPassword;
    @FXML
    private TextField userPasswordConfirm;
    @FXML
    private TextField userSignature;

    @FXML
    protected void submitButton(ActionEvent event){
        if(userName.getText().isEmpty()) {
            showAlert("昵称不能为空");
            return;
        }
        if(userPassword.getText().isEmpty()) {
            showAlert("请设置密码");
            return;
        }
        if(userPasswordConfirm.getText().isEmpty()){
            showAlert("请再次输入密码");
            return;
        }
        String name = userName.getText();
        String password = userPassword.getText();
        String passwordConfirm = userPasswordConfirm.getText();
        String signature = userSignature.getText();

        if(!password.equals(passwordConfirm)){
            showAlert("两次输入的密码不同,请重新输入");
            userPasswordConfirm.setText("");
            return;
        }
        try {
            int ID = Integer.parseInt(Connector.getInstance().register(name, password, signature));
        } catch (NumberFormatException e){
            e.printStackTrace();
            showAlert("服务器分配ID错误");
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Unknown Error");
        }
    }
    private void showAlert(String message){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setContentText(null);
        alert.setHeaderText(message);
        alert.show();
    }

    @FXML
    public void paneKeyAction(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            submitButton(new ActionEvent());
        }
    }
}
