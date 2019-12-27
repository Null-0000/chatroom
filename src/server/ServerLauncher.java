package server;

import client.model.User;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.util.HashMap;

class server extends Thread{
    @Override
    public void run() {
        try {
            new SocketServer();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
public class ServerLauncher extends Application {
    int cnt = 0;
    public static void main(String[] args){
        launch(args);
    }

    Label selectedLbl = new Label("[None]");
    public static ListView<String> users;
    public static TextArea log = new TextArea();

    public static String MAIN = "Main Page";

    public static HashMap<String, String> userLog = new HashMap<>();
    public static HashMap<String, ObservableValue<Boolean>> userSelected = new HashMap<>();

    @Override
    public void start(Stage primaryStage) {
        GridPane gridPane = new GridPane();

        Label pageLbl = new Label("Select Users:");
        users = new ListView<>();
        users.setPrefSize(120, 400);

        userLog.put("Main Page", "main");

        userSelected.put("Main Page", new SimpleBooleanProperty(true));

        users.setEditable(true);

        users.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        users.getItems().addAll(userSelected.keySet());

        Callback<String, ObservableValue<Boolean>> itemToBoolean = (String item) -> userSelected.get(item);

        users.setCellFactory(CheckBoxListCell.forListView(itemToBoolean));

        users.getSelectionModel()
                .selectedItemProperty()
                .addListener(this::selectionChanged);

        Button printBtn = new Button("Print Selected Log");
        printBtn.setOnAction(e -> printSelection());

        gridPane.setHgap(10);
        gridPane.setVgap(5);

        gridPane.addColumn(0, pageLbl, users, printBtn);

        Label selectionLbl = new Label("Your selection: ");
        gridPane.add(selectionLbl, 0, 3);
        gridPane.add(selectedLbl, 1, 3, 2, 1);

        log = new TextArea();
        log.setPrefSize(400, 400);
        log.setWrapText(true);
        log.setEditable(false);

        gridPane.add(log, 1, 1);

        gridPane.setStyle("-fx-padding: 10;" +
                "-fx-border-style: solid inside;" +
                "-fx-border-width: 2;" +
                "-fx-border-insets: 5;" +
                "-fx-border-radius: 5;" +
                "-fx-border-color: blue;");
        Scene scene = new Scene(gridPane);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Server Log");
        primaryStage.setOnCloseRequest((e)->{
            Platform.exit();
            System.exit(0);
        });

        users.getSelectionModel().selectFirst();

        primaryStage.show();

        new server().start();

    }
    public void selectionChanged(ObservableValue<? extends String> observable, String oldValue, String newValue) {
        ObservableList<String> selectedItems =
                users.getSelectionModel().getSelectedItems();
        String selectedValues =
                (selectedItems.isEmpty()) ? "[None]" : selectedItems.toString();
        this.selectedLbl.setText(selectedValues);
        log.setText(userLog.get(users.getSelectionModel().getSelectedItem()));
    }
    public void printSelection() {
        System.out.println("Selected items: ");
        for(String key : userSelected.keySet()){
            ObservableValue<Boolean> value = userSelected.get(key);
            if(value.getValue()){
                System.out.println(key);
            }
        }
    }

    public static void update(String user, String s){
        if(users.getSelectionModel().getSelectedItem() != null && users.getSelectionModel().getSelectedItem().equals(user)){
            log.appendText("\n" + s);
        }
        String l = userLog.get(user);
        l = l + "\n" + s;
        userLog.put(user, l);
    }
    public static void addUser(String user){
        if(userLog.containsKey(user)) return;
        
        users.getItems().add(user);
        userLog.put(user, "");
        userSelected.put(user, new SimpleBooleanProperty(false));
    }
}
