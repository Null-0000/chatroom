package client.controller;

import client.model.Message;
import client.model.User;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;

import javafx.scene.control.TextArea;

import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.ac.ed.ph.snuggletex.DOMOutputOptions;
import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleSession;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatViewController implements Initializable {
    private final Font EMOJI_FONT = Font.font("Segoe UI Emoji", 20);
    @FXML private Label chatToLabel;
    @FXML private WebView dialogView;
    @FXML private WebView show;
    @FXML private TextArea typeArea;
    @FXML private TilePane emojiView;
    private ListProperty<Message> messageList;

    private WebEngine webEngine1;
    private WebEngine webEngine2;
    private Document document;
    private Node body;


    private SnuggleEngine engine = new SnuggleEngine();
    private SnuggleSession session = engine.createSession();
    private SnuggleInput input;
    private String chatTo;
    public ChatViewController(String chatTo, ListProperty<Message> messageList){
        this.chatTo = chatTo;
        this.messageList = messageList;
    }
    @FXML private void sendMessage() throws Exception {
        String content = typeArea.getText();
        typeArea.setText("");
        if(content.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("warning: can not send an empty message!");
            alert.show();
            return;
        }
        synchronized (dialogView){
            Date now = new Date();
            Message message = new Message(chatTo, User.getInstance().getName(), content, now);
            User.getInstance().sendMessage(message);
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeArea.setWrapText(true);
        //隐藏TextArea的滚动条
        webEngine1 = dialogView.getEngine();
        webEngine1.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:71.0) Gecko/20100101 Firefox/71.0");
        webEngine1.getLoadWorker().stateProperty().addListener((obs, ov, nv)->{
            System.out.println(nv + chatTo);
            if (nv == Worker.State.SUCCEEDED){
                document = webEngine1.getDocument();
                body = document.getElementsByTagName("body").item(0);
                for (Message message: messageList) {
                    renderMessage(message);
                }
            }
        });

        webEngine1.load(getClass().getResource("Dialog.html").toExternalForm());

        webEngine2 = show.getEngine();

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
        typeArea.setFont(EMOJI_FONT);

        for (int i=0x1F600; i<0x1F644; i++){
            Label label = new Label(Character.toString(i));
            label.setFont(EMOJI_FONT);
            label.setOnMouseClicked((e)->{
                typeArea.setText(typeArea.getText() + label.getText());
            });
            emojiView.getChildren().add(label);
            //将unicode编码为0x1F600到0x1F644的所有emoji写到一个个的Label上
        }
    }
    public void synchroniseMessages(ListProperty<Message> messageList){
        messageList.addListener((obs, ov, nv) -> {
            Message newMessage = nv.get(nv.size() - 1);
            renderMessage(newMessage);
        });
    }
    public void scrollToBottom(){
        webEngine1.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        //第一次打开的时候仍然不会执行
    }
    private String translateOne(String s) throws IOException {
        input = new SnuggleInput(s);
        session.parseInput(input);
        String out = session.buildXMLString();
        out = out.replaceAll(" display=\"block\"", "");
        input = null;
        session.reset();
        return out;
    }
    public void translate(String s, Element element) throws IOException {
        input = new SnuggleInput(s);
        session.parseInput(input);

        session.buildDOMSubtree(element);
        input = null;
        session.reset();
    }
    private void translateAll(String text, Element element) throws IOException {
        Pattern p = Pattern.compile("\\$\\$.*?\\$\\$");
        Matcher m = p.matcher(text);
        int lt = 0, rt = 0;
        Element appendElement;
        while (m.find()){
            rt = m.start();
            appendElement = document.createElement("a");
            appendElement.setTextContent(text.substring(lt, rt));
            element.appendChild(appendElement);
            lt = m.end();
            translate(text.substring(rt, lt), element);
        }
        appendElement = document.createElement("a");
        appendElement.setTextContent(text.substring(lt));
        element.appendChild(appendElement);
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
            result += translateOne(text.substring(rt, lt));
        }
        result += text.substring(lt);
        return result;
    }
    public void renderMessage(Message message){
        Platform.runLater(()-> {
            //不加这玩意的话，程序不会报错，但是debug时却发现Element div显示的是java.lang.illegalStateError，发消息时还没事
            //接受的消息没有成功被指定的css渲染？？？？？？加了这玩意后时灵时不灵
            Element div = document.createElement("div");
            div.setAttribute("class", (message.sender.equals(chatTo)) ? "lt_div" : "rt_div");
            div.setAttribute("align", (message.sender.equals(chatTo)) ? "LEFT" : "RIGHT");
            Element pHead = document.createElement("p");
            pHead.setTextContent(message.getHead());
            div.appendChild(pHead);
            Element pContent = document.createElement("p");
            pContent.setAttribute("class", "content");
            try {
                translateAll(message.getContent(), pContent);
            } catch (IOException e) {
                e.printStackTrace();
            }
            NodeList mathNodeList = pContent.getElementsByTagName("math");
            for (int i=0; i<mathNodeList.getLength(); i++){
                mathNodeList.item(i).getAttributes().getNamedItem("display").setTextContent("inline");
            }

            div.appendChild(pContent);
            body.appendChild(div);

            scrollToBottom();
        });
    }
}