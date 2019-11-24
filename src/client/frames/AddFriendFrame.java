package client.frames;

import client.CurrentUser;
import client.SocketFunctions;
import client.exceptions.ServerNotFoundException;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class AddFriendFrame extends JFrame {
    static final int W = 600;
    static final int H = 150;
    private JPanel mainPanel;
    private JLabel title;
    private JTextField queryField;
    private JButton submitButton;
    public AddFriendFrame(){
        setMinimumSize(new Dimension(W, H));

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        mainPanel.setPreferredSize(new Dimension(W, H));
        mainPanel.setBackground(Color.white);
        mainPanel.setBorder(BorderFactory.createEtchedBorder());

        title = new JLabel("输入对象的ID或昵称进行查找");
        title.setFont(new Font(Font.DIALOG, Font.BOLD, 20));
        mainPanel.add(title);

        queryField = new JTextField();
        mainPanel.add(queryField);

        submitButton = new JButton("submit");
        submitButton.addActionListener(new submitButtonActionListener());
        submitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        mainPanel.add(submitButton);

        add(mainPanel);
    }

    private class submitButtonActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String info = queryField.getText();
            try {
                String name = SocketFunctions.makeFriendWith(info, CurrentUser.user);
                JOptionPane.showMessageDialog(mainPanel, (name==null)? "您查找的用户不存在！":
                        "成功添加" + name + "为您的好友");
                CurrentUser.user.makeFriend(name);
            } catch (IOException ex) {
                ex.printStackTrace();
            } catch (ServerNotFoundException ex) {
                ex.printStackTrace();
            }
        }
    }
}
