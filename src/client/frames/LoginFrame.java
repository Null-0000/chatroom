package client.frames;

import client.CurrentUser;
import client.FontClass;
import client.exceptions.PasswordException;
import client.user.User;
import org.apache.commons.lang3.math.NumberUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class LoginFrame extends JFrame {
    static final int W = 800;
    static final int H = 225;
    private JPanel panel;
    private JTextField IDInput, passwordInput;
    private JButton submitButton;

    public LoginFrame(){
        FontClass.loadIndyFont();

        setTitle("login");
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(W, H));
        setMaximumSize(new Dimension(W, H));

        panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setPreferredSize(new Dimension(W, H));

        IDInput = new JTextField();
        IDInput.setBorder(BorderFactory.createTitledBorder("ID"));

        passwordInput = new JTextField();
        passwordInput.setBorder(BorderFactory.createTitledBorder("password"));

        submitButton = new JButton("submit");
        submitButton.addActionListener(new LoginListener());
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(IDInput);
        panel.add(passwordInput);
        panel.add(submitButton);
        add(panel);

    }

    private class LoginListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            int ID;
            if (NumberUtils.isDigits(IDInput.getText()))
                ID = Integer.parseInt(IDInput.getText());
            else {
                JOptionPane.showMessageDialog(panel, "您输入的ID不合法，请重新输入。");
                return;
            }
            try {
                User user = new User(Integer.parseInt(IDInput.getText()), passwordInput.getText());
                CurrentUser.user = user;
                user.setFrameActive();
                setVisible(false);
            } catch (PasswordException exception) {
                JOptionPane.showMessageDialog(panel, "ID与密码不匹配，请重新输入。");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

}
