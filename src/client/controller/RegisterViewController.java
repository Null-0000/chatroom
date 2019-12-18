package client.controller;

import client.model.ShowDialog;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import kit.DataPackage;

import java.io.*;

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
    private ImageView selectedIcon;

    private String IconUrl = "src/client/view/images/defaultUserIcon.jpeg";

    @FXML
    protected void submitButton(ActionEvent event){
        if(userName.getText().isEmpty()) {
            ShowDialog.showAlert("昵称不能为空");
            return;
        }
        if(userPassword.getText().isEmpty()) {
            ShowDialog.showAlert("请设置密码");
            return;
        }
        if(userPasswordConfirm.getText().isEmpty()){
            ShowDialog.showAlert("请再次输入密码");
            return;
        }
        String name = userName.getText();
        String password = userPassword.getText();
        String passwordConfirm = userPasswordConfirm.getText();
        String signature = userSignature.getText();

        if(!password.equals(passwordConfirm)){
            ShowDialog.showAlert("两次输入的密码不同,请重新输入");
            userPasswordConfirm.setText("");
            return;
        }
        try {
//            int ID = Integer.parseInt(Connector.getInstance().register(name, password, signature));

            ShowDialog.showMessage("your image url is: " + IconUrl);
            File file= new File(IconUrl);
            FileInputStream fileInputStream = new FileInputStream(file);
//            selectedIcon.setImage(new Image(fileInputStream));
            byte[] iconByte = new byte[(int) (file.length() + 1)];
            fileInputStream.read(iconByte);
            int ID = Connector.getInstance().register(new DataPackage(name, password, signature, iconByte));
            ShowDialog.showMessage("你获得的ID为" + ID);
        } catch (Exception e){
            e.printStackTrace();
            ShowDialog.showAlert("服务器分配ID错误");
        }
    }

    @FXML
    public void paneKeyAction(KeyEvent keyEvent) {
        if(keyEvent.getCode() == KeyCode.ENTER){
            submitButton(new ActionEvent());
        }
    }

    private FileChooser fileChooser = new FileChooser();

    public void chooseFile(ActionEvent actionEvent) throws IOException {
        configureFileChooser(fileChooser);
        File file = fileChooser.showOpenDialog(null);
        if(file != null) {
            IconUrl = file.getPath();
            FileInputStream fileInputStream = new FileInputStream(file);
            selectedIcon.setImage(new Image(fileInputStream));
            fileInputStream.close();
        }
    }

    private static void configureFileChooser(final FileChooser fileChooser) {
        fileChooser.setTitle("View Pictures");
        fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("All Images", "*.*"),
                new FileChooser.ExtensionFilter("JPG", "*.jpg"),
                new FileChooser.ExtensionFilter("PNG", "*.png"),
                new FileChooser.ExtensionFilter("JPEG","*.jpeg")
        );
    }
}
