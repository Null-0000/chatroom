package client.frames;

import client.FontClass;
import client.tools.SocketFunctions;
import client.exceptions.PasswordException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class RegisterFrame extends JFrame {
    static final int W = 500;
    static final int H = 375;
    private JPanel mainPanel;
    private JLabel titleLabel;
    private JTextField nameField;
    private JPasswordField pwField;
    private JPasswordField rpwField;
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
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titleLabel.setFont(new Font(Font.DIALOG, Font.BOLD, 30));

        nameField = new JTextField();
        nameField.setBorder(BorderFactory.createTitledBorder("name"));

        pwField = new JPasswordField();
        pwField.setBorder(BorderFactory.createTitledBorder("password"));

        rpwField = new JPasswordField();
        rpwField.setBorder(BorderFactory.createTitledBorder("rewrite your password"));

        sigField = new JTextField();
        sigField.setBorder(BorderFactory.createTitledBorder("signature"));

        submitButton = new JButton("submit");
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        submitButton.addActionListener(new submitButtonListener());

        mainPanel.add(titleLabel);
        mainPanel.add(nameField);
        mainPanel.add(pwField);
        mainPanel.add(rpwField);
        mainPanel.add(sigField);
        mainPanel.add(submitButton);
        add(mainPanel);
    }

    private class submitButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String name = nameField.getText();
            String pw = new String(pwField.getPassword());
            String rpw = new String(rpwField.getPassword());
            String sig = sigField.getText();
            if (!pw.equals(rpw)){
                JOptionPane.showMessageDialog(mainPanel,"两次输入的密码不一致，请重新输入！");
                return;
            }
            if (checkHasNull(name, pw)) return;
            String ID = "";
            try {
                ID = SocketFunctions.register(name, pw, sig);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(mainPanel, "error");
                return;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            JOptionPane.showMessageDialog(mainPanel, "Your ID is " + ID);
            try {
                SocketFunctions.login(Integer.parseInt(ID), pw);
            } catch (PasswordException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
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
