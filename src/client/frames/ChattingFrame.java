package client.frames;

import client.FontClass;
import client.user.Dialogues;
import client.user.Message;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Date;

/**
 * @author WuShiguang
 */

public class ChattingFrame extends JFrame{
    private JPanel jPanel;
    private JTextArea dialogField;
    private JTextArea typingField;
    private JButton sendButton;

    private String friendName, userName;
    private boolean shit = true;

//    private JButton messageLogButton;

    public ChattingFrame(String friendName, String userName){
        FontClass.loadIndyFont();

        setTitle("You Are Chatting With " + friendName);
        this.friendName = friendName; this.userName = userName;

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setDefaultLookAndFeelDecorated(true);

        setSize(600, 600);
        setMaximumSize(new Dimension(700,700));
        setMinimumSize(new Dimension(400, 400));
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
        dialogField.setLineWrap(true);

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
    public void updateDialogField(Message message){
        synchronized (dialogField){
            dialogField.append(message.toString());
//            dialogField.paintImmediately(dialogField.getBounds());//update view in time
            dialogField.setCaretPosition(dialogField.getText().length());
        }
    }
    private void setTypingField(){
        Panel panel = new Panel();

        typingField = new JTextArea(4, 1);
        typingField.setEditable(true);
        typingField.setLineWrap(true);

        JScrollPane jScrollPane = new JScrollPane(typingField);

//        typingField.addKeyListener();

        jPanel.add(jScrollPane, BorderLayout.SOUTH);
    }
    private void setButton(){
        Panel panel = new Panel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        sendButton = new JButton("Send it");
        //messageLogButton = new JButton("message log");
        //panel.add(messageLogButton);
        sendButton.setAlignmentX(1);
        panel.add(Box.createHorizontalGlue());
        panel.add(sendButton);
        panel.add(Box.createHorizontalStrut(20));
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
        typingField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if(shit) {
                    typingField.setText("");
                    shit = false;
                }
            }

            @Override
            public void focusLost(FocusEvent e) {
                if(typingField.getText().length() == 0){
                    typingField.setText("please input");
                    typingField.setForeground(Color.GRAY);
                    shit = true;
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
            Message message = new Message(friendName, userName, typingString, new Date());
            updateDialogField(message);
//            Dialogues.updateDialogue(message, friendName);
        }
    }

    /**
     * for test
     */

    public static void main(String[] args){
        new ChattingFrame("wkj", "wsg");
    }

}
