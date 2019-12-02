package client.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * 管理主要的stage，loginStage,registerStage,addFriendStage,mainStage
 * 聊天界面不在这里管理
 * 可以通过这个类统一实现stage的关闭和显示，用于切换stage
 * 用法 StageM.getManager().close(XXX), StageM.getManager().show(XXX)
 */

public class StageM {
    private static StageM stageM = new StageM();
    public static StageM getManager(){
        return stageM;
    }

    private static Map<String, Stage> stageMap = new HashMap<>();

    public void addStage(String stageName){
        try {
            Parent root = FXMLLoader.load(getClass().getResource(stageName));
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stageMap.put(stageName, stage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Stage getStage(String stageName){
        return stageMap.get(stageName);
    }

    public void show(String stageName){
        getStage(stageName).show();
    }
    public void close(String stageName){
        getStage(stageName).close();
    }
}