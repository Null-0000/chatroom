package client.user;

import client.frames.UserFrame;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import java.awt.*;

public class UserCard extends JPanel {
    final static int W = UserFrame.W;
    final static int H = UserFrame.H / 4;
    final static int FW = W * 3/4 - 20;
    final static int FH = H/8;
    private JPanel mainPanel;
    private JPanel left, right;
    private JTextField nameField, sigField, IDField;
    public UserCard(String nikeName, String sig, int ID){
        Font font = new Font(Font.SANS_SERIF, Font.ITALIC, 20);

        mainPanel = new JPanel();

        IDField = new JTextField("" + ID);
        IDField.setFont(font);
        IDField.setBorder(BorderFactory.createTitledBorder("ID"));
        IDField.setPreferredSize(new Dimension(FW, FH));
        IDField.setEnabled(false);

        nameField = new JTextField(nikeName);
        nameField.setFont(font);
        nameField.setBorder(BorderFactory.createTitledBorder("name"));
        nameField.setPreferredSize(new Dimension(FW, FH));
        nameField.setEnabled(false);

        sigField = new JTextField(sig);
        sigField.setFont(font);
        sigField.setBorder(BorderFactory.createTitledBorder("signature"));
        sigField.setPreferredSize(new Dimension(FW, FH));
        sigField.setEnabled(false);

        setPreferredSize(new Dimension(W, H));
        setBackground(Color.white);
        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));

        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.X_AXIS));
        mainPanel.setBorder(BorderFactory.createTitledBorder("my postcard"));

        left = new JPanel();
        left.setPreferredSize(new Dimension(W/4, H));
        left.setBorder(BorderFactory.createBevelBorder(BevelBorder.RAISED));
        left.setBackground(Color.white);
        //此处未实现添加照片的功能

        right = new JPanel();
        right.setPreferredSize(new Dimension(W * 3/4, H));
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setBackground(Color.white);
        right.add(Box.createVerticalGlue());
        right.add(IDField);
        right.add(Box.createVerticalStrut(3));
        right.add(nameField);
        right.add(Box.createVerticalStrut(3));
        right.add(sigField);

        mainPanel.add(left); mainPanel.add(right);
        add(mainPanel);
    }
}
