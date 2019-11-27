package client.frames;

import client.FontClass;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author WuShiguang
 */

public class ChattingFrame extends JFrame{
    private JPanel jPanel;
    private JTextArea dialogField;
    private JTextArea typingField;
    private JButton sendButton;

//    private JButton messageLogButton;

    public ChattingFrame(String chatter, String fromID, String toID){
        FontClass.loadIndyFont();

        setName("Chatting with " + toID);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);

        setSize(600, 600);
        setLocation(600, 50);

        jPanel = new JPanel();
        jPanel.setLayout(new BorderLayout());
        jPanel.setVisible(true);

        setDialogField();
        setTypingField();
        setButton();
        addActionListener();

        add(jPanel, BorderLayout.CENTER);

        setVisible(true);
    }

    /**
     * build dialog text field
     * not editable for user
     */
    public void setDialogField(){

        //lack of initial dialog
        dialogField = new JTextArea("testing dialogField");
        dialogField.setEditable(false);
        dialogField.setCaretPosition(dialogField.getText().length());

        JScrollPane jScrollPane = new JScrollPane(dialogField);
        //       jScrollPane.setVerticalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

 //       jScrollPane.setVerticalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        jPanel.add(jScrollPane, BorderLayout.CENTER);
    }

    /**
     * when messageReceiver get new message from server
     * call this function to update the dialog field
     * @param message
     * message must be suit for a certain pattern, which has not been finished
     */
    public void updateDialogField(String message){
        synchronized (dialogField){
            dialogField.append("\n" + getDate() + "\n" + message + "\n");
//            dialogField.paintImmediately(dialogField.getBounds());//update view in time
            dialogField.setCaretPosition(dialogField.getText().length());
        }
    }
    private void setTypingField(){
        Panel panel = new Panel();

        typingField = new JTextArea(4, 1);
        typingField.setEditable(true);
//        typingField.addKeyListener();

        jPanel.add(typingField, BorderLayout.SOUTH);
    }
    private void setButton(){
        Panel panel = new Panel();
        sendButton = new JButton("Send it.");
        //messageLogButton = new JButton("message log");
        //panel.add(messageLogButton);
        panel.add(sendButton);
        add(panel, BorderLayout.SOUTH);
    }
    private void addActionListener(){
        sendButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                sendMessage();
            }
        });
        typingField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if(e.getKeyCode() == KeyEvent.VK_ENTER){
                    sendMessage();
                    e.consume();
                }
            }
        });
    }

    /**
     * unfinished
     * establish a pattern for a message
     * call for a function which possess the message and send it to server
     */
    private void sendMessage(){
        String typingString = typingField.getText();
        typingField.setText("");

        //       isTurnOffKeyListener = true;

 //       isTurnOffKeyListener = true;

        if(typingString.equals("")) {
            JOptionPane.showMessageDialog(jPanel, "warning: can not send an empty message", "alert", JOptionPane.ERROR_MESSAGE);
            return;
        }
        //temporary
        JOptionPane.showMessageDialog(jPanel, "your message: " + typingString, "假装消息已发送", JOptionPane.INFORMATION_MESSAGE);

        /**
         * send(typingString, fromID, toID)
         */
        synchronized (dialogField){
            //possess the String typingString
            updateDialogField(typingString);
        }
    }

    private String getDate(){
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH-mm-ss");
        return simpleDateFormat.format(date);
    }

    /**
     * for test
     */
    /*public static void main(String[] args){
        new ChattingFrame("wsg", "wsg's ID", "wkj's ID");
    }*/
}
