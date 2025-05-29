package hbaskar.four;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import hbaskar.one.Blackboard;

/**
 * Panel that contains the left side of the dashboard.
 * It contains the username, start button, players, timer, invite a teammate, copy URL, and room selector.
 */
public class WestPanel extends JPanel {

    private JComboBox<String> roomSelector;
    private JPanel playersPanel;

    public WestPanel(DashboardNanny dashboardNanny, String username) {
        setBackground(new Color(255, 204, 204));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        // ✅ Use dynamic username
        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(usernameLabel);

        // Load rooms dynamically from Blackboard
        List<String> rooms = Blackboard.getRooms();
        roomSelector = new JComboBox<>(rooms.toArray(new String[0]));
        add(roomSelector);

        add(new JButton("Start"));
        add(new JLabel("Players:"));

        playersPanel = new JPanel();
        playersPanel.setLayout(new BoxLayout(playersPanel, BoxLayout.Y_AXIS));
        playersPanel.setBackground(new Color(255, 204, 204));
        refreshPlayerList();
        add(playersPanel);

        add(new JLabel("00:00:00"));
        add(new JLabel("Invite a teammate"));
        add(new JTextField("https://app.planitpoker.com"));
        add(new JButton("Copy URL"));

        roomSelector.addActionListener(e -> {
            String selectedRoom = (String) roomSelector.getSelectedItem();
            dashboardNanny.onRoomSelected(selectedRoom);
        });
    }

    private void refreshPlayerList() {
        playersPanel.removeAll();
        List<String> names = Blackboard.getNames();
        for (String name : names) {
            JLabel nameLabel = new JLabel(name + " just entered the room");
            nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
            playersPanel.add(nameLabel);
        }
        playersPanel.revalidate();
        playersPanel.repaint();
    }

    public void updatePlayers() {
        refreshPlayerList();
    }
}
