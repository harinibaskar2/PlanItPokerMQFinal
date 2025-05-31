package hbaskar.four;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;

import hbaskar.one.PlanItPokerRepository;

public class WestPanel extends JPanel {

    private JComboBox<String> roomSelector;
    private JPanel playersPanel;
    private PlanItPokerRepository repository = PlanItPokerRepository.getInstance();

    // Invite teammate UI components
    private JTextField teammateNameField;
    private JTextField roomCodeField;
    private JButton inviteButton;

    public WestPanel(DashboardNanny dashboardNanny, String username) {
        setBackground(new Color(255, 204, 204));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(usernameLabel);

        List<String> rooms = repository.getAvailableRoomCodes();
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

        // Invite teammate UI
        add(new JLabel("Invite a teammate:"));

        teammateNameField = new JTextField();
        teammateNameField.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 30));
        teammateNameField.setToolTipText("Enter teammate name");
        add(teammateNameField);

        add(new JLabel("Room code for teammate to join:"));

        roomCodeField = new JTextField();
        roomCodeField.setMaximumSize(new java.awt.Dimension(Integer.MAX_VALUE, 30));
        roomCodeField.setToolTipText("Enter room code");
        add(roomCodeField);

        inviteButton = new JButton("Invite");
        add(inviteButton);

        inviteButton.addActionListener(e -> {
            String teammateName = teammateNameField.getText().trim();
            String roomCode = roomCodeField.getText().trim();

            if (teammateName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a teammate name.");
                return;
            }
            if (roomCode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a room code.");
                return;
            }

            boolean added = repository.joinRoom(roomCode, teammateName);
            if (added) {
                JOptionPane.showMessageDialog(this, teammateName + " invited successfully to room " + roomCode + "!");
                teammateNameField.setText("");
                roomCodeField.setText("");
                refreshPlayerList();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to invite " + teammateName + ". They may already be in the room or the room code is invalid.");
            }
        });

        add(new JTextField("https://app.planitpoker.com"));
        add(new JButton("Copy URL"));

        // Room selector listener
        roomSelector.addActionListener(e -> {
            String selectedRoom = (String) roomSelector.getSelectedItem();
            repository.setCurrentRoomCode(selectedRoom);
            dashboardNanny.onRoomSelected(selectedRoom);
            refreshPlayerList(); // Refresh players when room changes
        });
    }

    private void refreshPlayerList() {
        playersPanel.removeAll();

        String currentRoom = repository.getCurrentRoomCode();
        if (currentRoom != null) {
            List<String> names = repository.getRoom(currentRoom).getPlayers();
            System.out.println("Players in room " + currentRoom + ": " + names);
            for (String name : names) {
                JLabel nameLabel = new JLabel(name + " just entered the room");
                nameLabel.setFont(new Font("Arial", Font.PLAIN, 12));
                playersPanel.add(nameLabel);
            }
        }

        playersPanel.revalidate();
        playersPanel.repaint();
    }

    public void updatePlayers() {
        refreshPlayerList();
    }
}
