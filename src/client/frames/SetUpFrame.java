package client.frames;

import client.FontClass;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SetUpFrame extends JFrame {
    public static int W = 600;
    public static int H = 180;
    private JLabel title;
    private JPanel mainPanel;
    private JPanel buttonPanel;
    private JLabel label;
    private JButton registerButton, loginButton;
    public SetUpFrame(){
        FontClass.loadIndyFont();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(W, H));

        mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());
        mainPanel.setBackground(Color.white);

        title = new JLabel("                         Chat Room");
        title.setFont(new Font(Font.DIALOG, Font.BOLD, 30));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        label = new JLabel("           Welcome to this new world!");

        registerButton = new JButton("Register");
        registerButton.addActionListener(new RegisterButtonListener());
        loginButton = new JButton("Log In");
        loginButton.addActionListener(new LoginButtonListener());

        buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
        buttonPanel.setBackground(Color.white);
        buttonPanel.add(Box.createHorizontalStrut(140));
        buttonPanel.add(loginButton);
        buttonPanel.add(Box.createHorizontalStrut(30));
        buttonPanel.add(registerButton);

        mainPanel.add(title, BorderLayout.NORTH);
        mainPanel.add(label, BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        add(mainPanel);
        setVisible(true);
    }

    private class RegisterButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            RegisterFrame frame = new RegisterFrame();
            frame.setVisible(true);
        }
    }

    private class LoginButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            LoginFrame frame = new LoginFrame();
            frame.setVisible(true);
        }
    }
}
