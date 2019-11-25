package client.frames;

import client.FontClass;
import client.SocketFunctions;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class RegisterFrame extends JFrame {
    static final int W = 400;
    static final int H = 400;
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JTextField nameField;
    private JTextField pwField;
    private JTextField rpwField;
    private JTextField sigField;
    private JButton submitButton;
    public RegisterFrame(){
        FontClass.loadIndyFont();
        setTitle("Register");
        setMinimumSize(new Dimension(W, H));

        mainPanel = new JPanel();
        mainPanel.setPreferredSize(new Dimension(W, H));
        mainPanel.setBackground(Color.white);
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));

        titleLabel = new JLabel("Register form");
        mainPanel.add(titleLabel);

        nameField = new JTextField();
        nameField.setBorder(BorderFactory.createTitledBorder("name"));
        mainPanel.add(nameField);

        pwField = new JTextField();
        pwField.setBorder(BorderFactory.createTitledBorder("password"));
        mainPanel.add(pwField);

        rpwField = new JTextField();
        rpwField.setBorder(BorderFactory.createTitledBorder("rewrite your password"));
        mainPanel.add(rpwField);

        sigField = new JTextField();
        sigField.setBorder(BorderFactory.createTitledBorder("sig"));
        mainPanel.add(sigField);

        submitButton = new JButton("submit");
        submitButton.addActionListener(new submitButtonListener());
        mainPanel.add(submitButton);

        add(mainPanel);
    }

    private class submitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String pw = pwField.getText();
            String rpw = rpwField.getText();
            String sig = sigField.getText();
            if (!pw.equals(rpw)){
                JOptionPane.showMessageDialog(mainPanel,"两次输入的密码不一致，请重新输入！");
                return;
            }
            if (checkHasNull(name, pw)) return;
            String ID;
            try {
                ID = SocketFunctions.register(name, pw, sig);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(mainPanel, "error");
                return;
            }
            JOptionPane.showMessageDialog(mainPanel, "Your ID is " + ID);
            setVisible(false);
        }

        boolean checkHasNull(String name, String password){
            if (name == null) {
                JOptionPane.showMessageDialog(mainPanel, "昵称不能为空");
                return true;
            }
            if (password == null){
                JOptionPane.showMessageDialog(mainPanel, "密码不能为空");
                return true;
            }
            return false;
        }
    }
}
