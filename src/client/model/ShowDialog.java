package client.model;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;

import java.util.Optional;

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
    public static void showWarning(String message){
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("Warning");
        alert.setContentText(null);
        alert.setHeaderText(message);
        alert.showAndWait();
    }
    public static boolean showConfirm(String header,String message){
//        按钮部分可以使用预设的也可以像这样自己 new 一个
        Alert _alert = new Alert(Alert.AlertType.CONFIRMATION, message, new ButtonType("No", ButtonBar.ButtonData.NO),
                new ButtonType("Yes", ButtonBar.ButtonData.YES));
//        设置窗口的标题
        _alert.setTitle("确认");
        _alert.setHeaderText(header);
//        设置对话框的 icon 图标，参数是主窗口的 stage
//        _alert.initOwner(d_stage);
//        showAndWait() 将在对话框消失以前不会执行之后的代码
        Optional<ButtonType> _buttonType = _alert.showAndWait();
//        根据点击结果返回
        if(_buttonType.get().getButtonData().equals(ButtonBar.ButtonData.YES)){
            return true;
        }
        else {
            return false;
        }
    }

}
