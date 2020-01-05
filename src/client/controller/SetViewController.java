package client.controller;

import client.model.MFileChooser;
import client.model.User;
import client.view.MainView;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import kit.Data;
import kit.UserInfo;

import java.io.*;
import java.net.URL;
import java.util.ResourceBundle;

public class SetViewController implements Initializable {
    @FXML
    private ImageView icon;
    @FXML
    private Button alterIcon;
    @FXML
    private Label userID;
    @FXML
    private TextField userName;
    @FXML
    private TextField userSig;
    @FXML
    private Button submit;

    private File imageFile;


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserInfo userInfo = User.getInstance().getUserInfo();
        try {
            icon.setImage(new Image(new FileInputStream(new File(userInfo.getIconPath().substring(5)))));
            imageFile = new File(userInfo.getIconPath().substring(5));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        userID.setText("" + userInfo.getID());
        userName.setText(userInfo.getName());
        userSig.setText(userInfo.getSig());
    }

    @FXML
    private void selectImage() throws FileNotFoundException {
        File file = MFileChooser.showFileChooser("Select your icon", "jpeg",
                "jpg", "png", "bmp");
        if (file == null) return;
        imageFile = file;
        icon.setImage(new Image(new FileInputStream(file)));
    }

    @FXML
    private void submitUserInfo() throws IOException {
        byte[] iconByte = (new FileInputStream(imageFile)).readAllBytes();
        UserInfo info = new UserInfo(Integer.parseInt(userID.getText()),
                userName.getText(), userSig.getText(), iconByte);
        User.getInstance().setUserInfo(info);
        User.getInstance().getManager().storeMyIcon();

        Data data = new Data(info);
        data.setOperatorInfo();
        data.setOperateType(Data.MODIFY);

        Connector.getInstance().modifyInfo(data);

        MainView.reloadInfo();
    }
}