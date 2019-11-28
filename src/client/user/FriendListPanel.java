package client.user;

import client.tools.ResizingList;
import client.frames.UserFrame;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

public class FriendListPanel extends JPanel {
    static final int W = UserFrame.W;
    static final int H = UserFrame.H - UserCard.H;
    private List friendList;
    private JLabel title;

    FriendListPanel(ArrayList<String> friends){
        setBackground(Color.white);
        setPreferredSize(new Dimension(W, H));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        title = new JLabel("Friends");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(new Font(Font.DIALOG, Font.BOLD, 30));

        friendList = new List();
        if (friends != null) {
            for (int i = 0; i < friends.size(); i++) {
                friendList.add(friends.get(i));
            }
        }
        //未完成弹出聊天窗口机制

        add(title);
        add(friendList);
    }
    public void addMember(String friend){
        friendList.add(friend);
        repaint();
    }

}
