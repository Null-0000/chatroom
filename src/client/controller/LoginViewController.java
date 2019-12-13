package client.controller;

import client.launcher.Resource;
import client.model.ShowDialog;
import client.model.User;
import client.view.MainView;
import client.view.StageM;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.util.logging.SocketHandler;

public class LoginViewController {
    @FXML
    private GridPane root;
    @FXML
    private TextField IDField;
    @FXML
    private PasswordField passwordField;
    @FXML
    protected void loginButtonAction(ActionEvent event){
        if(IDField.getText().isEmpty()){
            ShowDialog.showAlert("请输入你的ID");
            return;
        }
        if(passwordField.getText().isEmpty()){
            ShowDialog.showAlert("请输入密码");
            return;
        }

        int ID; String password = passwordField.getText();
        if (NumberUtils.isDigits(IDField.getText())) ID = Integer.parseInt(IDField.getText());
        else {
            ShowDialog.showAlert("您输入的ID不合法，请重新输入");
            return;
        }
        boolean isAccessible = false;
        try {
            isAccessible = Connector.getInstance().loadUserInfo(ID, password);
        } catch (IOException e) {
            e.printStackTrace();
            ShowDialog.showAlert("服务器连接错误");
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(isAccessible){
            try {
                User.getInstance().initialise();
                StageM.getManager().addStage(Resource.MainViewID, new MainView(User.getInstance()));
                StageM.getManager().shift(Resource.LoginViewID, Resource.MainViewID);
            } catch (IOException e) {
                e.printStackTrace();
                ShowDialog.showAlert("载入服务端用户信息错误");
            }

        }
        else ShowDialog.showAlert("ID不存在或密码错误");
    }
    @FXML
    protected void registerButtonAction(){
        StageM.getManager().shift(Resource.LoginViewID, Resource.RegisterID);
    }

    public void paneKeyAction(KeyEvent keyEvent) throws IOException {
        if(keyEvent.getCode() == KeyCode.ENTER){
            loginButtonAction(new ActionEvent());
        }
    }
}