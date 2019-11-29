package client.user;

import client.frames.UserFrame;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import java.awt.*;

import java.util.Vector;

public class FriendListPanel extends JPanel {
    static final int W = UserFrame.W;
    static final int H = UserFrame.H - UserCard.H;
    private Vector<String> friends;
    private Dialogues dialogues;
    private JScrollPane scrollPane;
    private JList friendList;
    private JLabel title;
    private ListModel listModel;

    public FriendListPanel(Dialogues dialogues){
        this.dialogues = dialogues;
        this.friends = new Vector<String>();
        this.friends.addAll(dialogues.getAllFriendName());

        setBackground(Color.white);
        setPreferredSize(new Dimension(W, H));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        title = new JLabel("Friends");
        title.setAlignmentX(CENTER_ALIGNMENT);
        title.setFont(new Font(Font.DIALOG, Font.BOLD, 35));

        listModel = new DefaultComboBoxModel(friends);
        friendList = new JList(listModel);
        friendList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        friendList.setSize(new Dimension(W-20, H-20));

        friendList.addListSelectionListener(new FriendListSelectionListener());

        add(title);
        add(Box.createVerticalStrut(10));
        scrollPane = new JScrollPane(friendList);
        add(scrollPane);
    }
    public void addMember(){
        ListModel listModel = new DefaultComboBoxModel(friends);
        friendList.setModel(listModel);
        friendList.repaint();
    }

    private class FriendListSelectionListener implements javax.swing.event.ListSelectionListener {
        @Override
        public void valueChanged(ListSelectionEvent e) {
            int index = e.getFirstIndex();
            dialogues.setChattingFrameVisible(friends.elementAt(index));
        }
    }
}
