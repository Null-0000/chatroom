package client.launcher;

import client.model.Dialogue;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class Test {

    public static void main(String[] args) throws IOException {
        File file = new File("test.dat");
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);

        Map<String, Dialogue> map = new HashMap<>();
        map.put("test", new Dialogue("test", "test"));

        objectOutputStream.writeObject(map);
    }

}
