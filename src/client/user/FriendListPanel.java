package client.user;

import client.tools.ResizingList;
import client.frames.UserFrame;
import client.frames.AddFriendFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class FriendListPanel extends JPanel {
    static final int W = UserFrame.W;
    static final int H = UserFrame.H - UserCard.H;
    private List friendList;
    private JLabel title;
    private JButton addFriendButton;
    FriendListPanel(ResizingList<String> friends){
        setBackground(Color.white);
        setPreferredSize(new Dimension(W, H));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        title = new JLabel("Friends");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(new Font(Font.DIALOG, Font.BOLD, 30));

        friendList = new List();
        if (friends != null) {
            for (int i = 0; i < friends.count(); i++) {
                friendList.add(friends.getItem(i));
            }
        }
        //未完成弹出聊天窗口机制

        addFriendButton = new JButton("add");
        addFriendButton.setAlignmentX(LEFT_ALIGNMENT);
        addFriendButton.addActionListener(new addFriendButtonListener());

        add(title);
        add(friendList);
        add(addFriendButton);
    }

    private class addFriendButtonListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            AddFriendFrame addFriendFrame = new AddFriendFrame();
            addFriendFrame.setVisible(true);
        }
    }
}
