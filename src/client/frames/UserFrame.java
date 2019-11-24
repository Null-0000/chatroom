package client.frames;

import client.user.FriendListPanel;
import client.user.UserCard;

import javax.swing.*;
import java.awt.*;

public class UserFrame extends JFrame {
    public static final int W = 450;
    public static final int H = 800;
    private JPanel userPanel;
    public UserFrame(UserCard card, FriendListPanel friendListPanel){
        setName("User");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(W, H));

        userPanel = new JPanel();
        userPanel.setLayout(new BoxLayout(userPanel, BoxLayout.Y_AXIS));
        userPanel.setPreferredSize(new Dimension(W, H));
        userPanel.setBackground(Color.cyan);

        userPanel.add(card);
        userPanel.add(Box.createVerticalStrut(20));
        userPanel.add(friendListPanel);
        this.getContentPane().add(userPanel);
    }
}
