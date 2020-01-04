package client.controller;

import client.model.MFileChooser;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import kit.Message;
import client.model.User;

import javafx.application.Platform;
import javafx.beans.property.ListProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;

import javafx.scene.layout.TilePane;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import uk.ac.ed.ph.snuggletex.SnuggleEngine;
import uk.ac.ed.ph.snuggletex.SnuggleInput;
import uk.ac.ed.ph.snuggletex.SnuggleSession;

import javax.imageio.ImageIO;
import javax.imageio.stream.FileImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatViewController implements Initializable {
    private final Font EMOJI_FONT = Font.font("Segoe UI Emoji", 20);
    @FXML
    private GridPane root;
    @FXML
    private Label chatToLabel;
    @FXML
    private WebView dialogView;
    @FXML
    private WebView show;
    @FXML
    private TextArea typeArea;
    @FXML
    private TilePane emojiView;
    @FXML
    private ToggleButton emojiControl;
    @FXML
    private ToggleButton fmlControl;

    private ListProperty<Message> messageList;
    public boolean isGroup;
    private WebEngine webEngine1;
    private WebEngine webEngine2;
    private Document document;
    private Node body;


    private SnuggleEngine engine = new SnuggleEngine();
    private SnuggleSession session = engine.createSession();
    private SnuggleInput input;
    private int chatTo_id;

    public ChatViewController(int chatTo_id, ListProperty<Message> messageList) {
        this.chatTo_id = chatTo_id;
        this.messageList = messageList;
    }

    @FXML
    private void sendMessage() throws Exception {
        String content = typeArea.getText();
        typeArea.setText("");
        if (content.equals("")) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setContentText("warning: can not send an empty message!");
            alert.show();
            return;
        }
        synchronized (dialogView) {
            Date now = new Date();
            byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
            Message message = new Message(chatTo_id, User.getInstance().getID(), contentBytes, now, isGroup);
            User.getInstance().sendMessage(message);
        }
    }

    @FXML
    private void imageSelect() throws Exception {
        File file = MFileChooser.showFileChooser("image", "jpg", "png", "jpeg", "bmp");
        if (file == null) return;
        String ctype = Files.probeContentType(Paths.get(file.getPath()));
        FileImageInputStream fiis = new FileImageInputStream(file);
        byte[] content = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = fiis.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        content = baos.toByteArray();
        Date date = new Date();
        Message message = new Message(chatTo_id, User.getInstance().getID(), ctype, content, date, isGroup);
        //在html中连接文件时只能从当前目录出发,绝对路径和project下路径都没有效果
        User.getInstance().sendMessage(message);
        baos.close();
        fiis.close();
    }

    @FXML
    private void audioSelect() throws Exception {
        File file = MFileChooser.showFileChooser("audio", "mp3", "mpeg", "wma", "aac");
        if (file == null) return;
        String ctype = Files.probeContentType(Paths.get(file.getPath()));
        FileImageInputStream fiis = new FileImageInputStream(file);
        byte[] content = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        while ((len = fiis.read(buf)) != -1) {
            baos.write(buf, 0, len);
        }
        content = baos.toByteArray();
        Date date = new Date();
        Message message = new Message(chatTo_id, User.getInstance().getID(), ctype, content, date, isGroup);
        //在html中连接文件时只能从当前目录出发,绝对路径和project下路径都没有效果
        User.getInstance().sendMessage(message);
        baos.close();
        fiis.close();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        typeArea.setWrapText(true);
        //隐藏TextArea的滚动条
        webEngine1 = dialogView.getEngine();
        webEngine1.setUserAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:71.0) Gecko/20100101 Firefox/71.0");
        webEngine1.getLoadWorker().stateProperty().addListener((obs, ov, nv) -> {
            if (nv == Worker.State.SUCCEEDED) {
                document = webEngine1.getDocument();
                body = document.getElementsByTagName("body").item(0);
                for (Message message : messageList) {
                    showMessage(message);
                }
            }
        });

        webEngine1.load(getClass().getResource("Dialog.html").toExternalForm());
        //webEngine1.load("src/client/controller/Dialog.html");

        webEngine2 = show.getEngine();

        typeArea.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (show.isVisible()) {
                    try {
                        webEngine2.loadContent(translate());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        typeArea.setFont(EMOJI_FONT);

        for (int i = 0x1F600; i < 0x1F644; i++) {
            Label label = new Label(Character.toString(i));
            label.setFont(EMOJI_FONT);
            label.setOnMouseClicked((e) -> {
                typeArea.setText(typeArea.getText() + label.getText());
            });
            emojiView.getChildren().add(label);
            //将unicode编码为0x1F600到0x1F644的所有emoji写到一个个的Label上
        }

        ToggleGroup group = new ToggleGroup();
        emojiControl.setToggleGroup(group);
        fmlControl.setToggleGroup(group);
        group.selectedToggleProperty().addListener((obs, ov, nv) -> {
            if (nv == null) {
                emojiView.setVisible(false);
                emojiView.setManaged(false);
                show.setVisible(false);
                show.setManaged(false);
            } else if (nv == emojiControl) {
                show.setVisible(false);
                show.setManaged(false);
                emojiView.setVisible(true);
                emojiView.setManaged(true);
            } else if (nv == fmlControl) {
                emojiView.setVisible(false);
                emojiView.setManaged(false);
                show.setVisible(true);
                show.setManaged(true);
            }
        });

    }

    public void synchroniseMessages(ListProperty<Message> messageList) {
        messageList.addListener((obs, ov, nv) -> {
            Message newMessage = nv.get(nv.size() - 1);

            showMessage(newMessage);
        });
    }

    public void scrollToBottom() {
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
        while (m.find()) {
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
        while (m.find()) {
            rt = m.start();
            result += text.substring(lt, rt);
            lt = m.end();
            result += translateOne(text.substring(rt, lt));
        }
        result += text.substring(lt);
        return result;
    }

    public void showMessage(Message message) {
        Platform.runLater(() -> {
            //不加这玩意的话，程序不会报错，但是debug时却发现Element div显示的是java.lang.illegalStateError，发消息时还没事
            //接受的消息没有成功被指定的css渲染？？？？？？加了这玩意后时灵时不灵
            Element div = document.createElement("div");
            int user_id = User.getInstance().getID();
            div.setAttribute("class", (message.sender == user_id) ? "rt_div" : "lt_div");
            div.setAttribute("align", (message.sender == user_id) ? "RIGHT" : "LEFT");
            Element pHead = document.createElement("p");
            pHead.setTextContent(message.getHead());
            div.appendChild(pHead);
            Element pContent;
            switch (message.ctype.replaceAll("/.*", "")) {
                case "text":
                    pContent = document.createElement("p");
                    pContent.setAttribute("class", "content");
                    try {
                        translateAll(new String(message.getContent()), pContent);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    NodeList mathNodeList = pContent.getElementsByTagName("math");
                    for (int i = 0; i < mathNodeList.getLength(); i++) {
                        mathNodeList.item(i).getAttributes().getNamedItem("display").setTextContent("inline");
                    }
                    break;
                case "image":
                    //发现渲染
                    int width = 100;
                    int height = 100;

                    File file = new File(message.getUrl().replaceAll(
                            "\\.\\.", "out/production/chatroom/client"));
                    try {
                        BufferedImage bufferedImage = ImageIO.read(file);
                        height = (bufferedImage.getHeight() / bufferedImage.getWidth()) * 100;
                        //等比例放缩图片
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    System.out.println(file.exists());
                    pContent = document.createElement("img");
                    pContent.setAttribute("width", "" + width);
                    pContent.setAttribute("height", "" + height);
                    pContent.setAttribute("class", "content");
                    pContent.setAttribute("src", message.getUrl());
                    break;
                case "audio":
                    pContent = document.createElement("audio");
                    pContent.setAttribute("class", "content");
                    pContent.setAttribute("controls", "controls");
                    Element source = document.createElement("source");
                    source.setAttribute("src", message.getUrl());
                    source.setAttribute("type", message.ctype);
                    pContent.appendChild(source);
                    break;
                default:
                    pContent = null;
                    break;
            }
            div.appendChild(pContent);
            body.appendChild(div);

            scrollToBottom();
        });
    }
}