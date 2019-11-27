package client.user;

import client.CurrentUser;
import client.tools.ResizingList;
import client.frames.UserFrame;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;

public class FriendListPanel extends JPanel {
    static final int W = UserFrame.W;
    static final int H = UserFrame.H - UserCard.H;
    private ResizingList<String> friends;
    private Dialogues dialogues;
    private JList friendList;
    private JLabel title;


    public FriendListPanel(Dialogues dialogues){
        this.dialogues = dialogues;
        this.friends = dialogues.getFriends();

        setBackground(Color.white);
        setPreferredSize(new Dimension(W, H));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        title = new JLabel("Friends");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(new Font(Font.DIALOG, Font.BOLD, 35));

        ListModel listModel = new DefaultComboBoxModel(friends.items);
        friendList = new JList(listModel);
        friendList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        friendList.setSize(new Dimension(W-20, H-20));

        for (Dialogue dialogue: dialogues.getDialogues()){
            friendList.add(new JLabel(dialogue.friend));
        }

        friendList.addListSelectionListener(new FriendListSelectionListener());
        //未完成弹出聊天窗口机制

        add(title);
        add(Box.createVerticalStrut(10));
        add(friendList);
    }
    public void addMember(String friend){
        friendList.add(new JLabel(friend));
        repaint();
    }

    private class FriendListSelectionListener implements javax.swing.event.ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            int index = e.getFirstIndex();
            dialogues.getDialogues().getItem(index).setChattingFrameVisible(true);
        }
    }
}
