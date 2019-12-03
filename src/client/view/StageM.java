package client.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

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

    public void addStage(String viewID, Stage stage){
        stageMap.put(viewID, stage);
    }

    public Stage getStage(String viewID){
        return stageMap.get(viewID);
    }

    public void show(String viewID){
        getStage(viewID).show();
    }
    public void close(String viewID){
        getStage(viewID).close();
    }
    public void shift(String viewIDA, String viewIDB){
        close(viewIDA); show(viewIDB);
    }
}