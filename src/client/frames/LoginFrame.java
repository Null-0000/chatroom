package client.frames;

import client.exceptions.PasswordException;
import client.user.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginFrame extends JFrame {
    static final int W = 600;
    static final int H = 200;
    private JPanel panel;
    private JTextField IDInput, passwordInput;
    private JButton submitButton;
    private JButton registerButton;
    public LoginFrame(){
        setTitle("login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(W, H));

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(W, H));

        IDInput = new JTextField();
        IDInput.setBorder(BorderFactory.createTitledBorder("ID"));
        panel.add(IDInput);

        passwordInput = new JTextField();
        passwordInput.setBorder(BorderFactory.createTitledBorder("password"));
        panel.add(passwordInput);

        submitButton = new JButton("submit");
        submitButton.addActionListener(new LoginListener());
        panel.add(submitButton);

        registerButton = new JButton("register");
        registerButton.addActionListener(new RegisterListener());
        panel.add(registerButton);

        add(panel);
        setVisible(true);
    }

    private class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                User user = new User(Integer.parseInt(IDInput.getText()), passwordInput.getText());
                user.setFrameActive();
                setVisible(false);
            } catch (PasswordException exception) {
                JOptionPane.showMessageDialog(panel, "ID与密码不匹配，请重新输入。");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    private class RegisterListener implements ActionListener {

        @Override
        public void actionPerformed(ActionEvent e) {
            RegisterFrame frame = new RegisterFrame();
            frame.setVisible(true);
        }
    }
}
