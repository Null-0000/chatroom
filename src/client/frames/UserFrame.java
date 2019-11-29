package client.frames;

import client.CurrentUser;
import client.FontClass;
import client.tools.SocketFunctions;
import client.user.FriendListPanel;
import client.user.UserCard;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class UserFrame extends JFrame {
    public static final int W = 400;
    public static final int H = 800;
    private JPanel userPanel;
    private JButton addFriendButton;
    public UserFrame(UserCard card, FriendListPanel friendListPanel){
        FontClass.loadIndyFont();

        setName("User");

        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                int exit = JOptionPane.showConfirmDialog(null,
                        "do you want to exit?");
                if (exit == 0) {
                    try {
                        CurrentUser.user.exit();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                    System.exit(0);
                }
            }
        });
        setMinimumSize(new Dimension(W, H));

        userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setPreferredSize(new Dimension(W, H));
        userPanel.setBackground(Color.white);

        addFriendButton = new JButton("add");
        addFriendButton.setAlignmentX(LEFT_ALIGNMENT);
        addFriendButton.addActionListener(new addFriendButtonListener());

        userPanel.add(card);
        userPanel.add(Box.createVerticalStrut(20));
        userPanel.add(friendListPanel);
        userPanel.add(addFriendButton);

        this.getContentPane().add(userPanel);
    }
    private class addFriendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AddFriendFrame addFriendFrame = new AddFriendFrame();
            addFriendFrame.setVisible(true);
        }
    }

}

