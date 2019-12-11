package client.controller;

import client.model.Message;
import client.model.User;
import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;

import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import javafx.scene.control.TextArea;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleSession;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatViewController implements Initializable {
    @FXML private Label chatToLabel;
    @FXML private WebView dialogView;
    @FXML private WebView show;
    @FXML private TextArea typeArea;
    private WebEngine webEngine1;
    private WebEngine webEngine2;
    private final String HTMLHEAD = "<html><head><link rel=\'stylesheet\' " +
            "href=\'" + getClass().getResource("ChatView.css") + "\'></head>" +
            "<body>";
    private final String HTMLTAIL = "</body></html>";
    private SnuggleEngine engine = new SnuggleEngine();
    private SnuggleSession session = engine.createSession();
    private SnuggleInput input;
    private String htmlText = "";
    private String chatTo;
    public ChatViewController(String chatTo){
        this.chatTo = chatTo;
    }
    @FXML private void sendMessage() throws IOException {
        String content = translate();
        typeArea.setText("");
        if(content.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("warning: can not send an empty message!");
            return;
        }
        synchronized (dialogView){
            Date now = new Date();
            Message message = new Message(chatTo, User.getInstance().getName(), content, now);
            User.getInstance().sendMessage(message);
        }
    }
    @FXML private void editFormula(KeyEvent e) throws IOException {
        if (e.isAltDown() && e.getCode()== KeyCode.EQUALS){
            System.out.println(translate());
        }
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeArea.setWrapText(true);
        //隐藏TextArea的滚动条
        webEngine1 = dialogView.getEngine();
        webEngine2 = show.getEngine();
        webEngine1.getLoadWorker().stateProperty().addListener((obs, ov, nv)->{
            if (nv == Worker.State.SUCCEEDED){
                webEngine1.executeScript("window.scrollTo(0, document.body.scrollHeight);");
            }
        });

        typeArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                try {
                    webEngine2.loadContent(translate());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

        show.setVisible(false);
        Platform.runLater(()->{
            Node[] nodess = show.lookupAll(".tool-bar").toArray(new Node[0]);
            for (Node node: nodess){
                node.setVisible(false);
                node.setManaged(false);
            }
            show.setVisible(true);
        });
        show.setDisable(true);
    }
    public void synchroniseMessages(ListProperty<Message> messageList){
        messageList.addListener((obs, ov, nv) ->{
            Message newMessage = nv.get(nv.size() - 1);
            if (newMessage.sender.equals(chatTo)){
                htmlText += newMessage.toHTML(true);
            } else {
                htmlText += newMessage.toHTML(false);
            }
            Platform.runLater(()-> webEngine1.loadContent(HTMLHEAD + htmlText + HTMLTAIL));
            /**while you are updating the component out of FX application thread, you will get an
             * IllegalStateException and then use PlatForm.runLater to solve it.*/
        });
    }
    public void loadMessages(ListProperty<Message> messageList){
        for (Message message: messageList){
            boolean isLeft = message.sender.equals(chatTo);
            htmlText += message.toHTML(isLeft);
        }
        webEngine1.loadContent(HTMLHEAD + htmlText + HTMLTAIL);
        System.out.println(htmlText);
    }
    private String translate(String s) throws IOException {
        input = new SnuggleInput(s);
        session.parseInput(input);
        String out = session.buildXMLString();
        out = out.replaceAll(" display=\"block\"", "");
        input = null;
        session.reset();
        return out;
    }
    private String translate() throws IOException {
        String text = typeArea.getText();
        Pattern p = Pattern.compile("\\$\\$.*?\\$\\$");
        Matcher m = p.matcher(text);
        String result = "";
        int lt = 0, rt = 0;
        while (m.find()){
            rt = m.start();
            result += text.substring(lt, rt);
            lt = m.end();
            result += translate(text.substring(rt, lt));
        }
        result += text.substring(lt);
        return result;
    }
}
