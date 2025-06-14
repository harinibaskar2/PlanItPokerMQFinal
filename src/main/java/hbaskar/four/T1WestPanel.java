package hbaskar.four;

import java.awt.Color;
import java.awt.Font;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;

import hbaskar.T1Card;
import hbaskar.one.T1PlanItPokerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Represents the left-side control panel of the PlanItPoker dashboard UI.
 * This panel allows users to manage rooms, invite teammates, and create stories.
 * It includes controls for:

 *   Selecting and creating rooms
 *   Viewing and refreshing the list of players
 *   Inviting teammates to a selected room
 *   Creating stories within the current room
 *   Displaying stories chart with averages

 *
 * @author hbaskar
 * @version 1.1
 */
public class T1WestPanel extends JPanel {
    private static final Logger logger = LoggerFactory.getLogger(T1WestPanel.class);

    private JComboBox<String> roomSelector;
    private JComboBox<String> inviteRoomSelector; // Changed from JTextField to JComboBox
    private JPanel playersPanel;
    private T1PlanItPokerRepository repository = T1PlanItPokerRepository.getInstance();

    private JTextField inviteNameField;
    private JButton inviteButton;

    private JTextField newRoomCodeField;
    private JButton createRoomButton;

    private T1DashboardNanny dashboardNanny;
    private String username;

    // Only storyTitleField now
    private JTextField storyTitleField;
    private JButton createStoryButton;
    
    // Chart button
    private JButton showChartButton;

    public T1WestPanel(T1DashboardNanny dashboardNanny, String username) {
        this.dashboardNanny = dashboardNanny;
        this.username = username;

        setBackground(new Color(255, 204, 204));
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));

        JLabel usernameLabel = new JLabel(username);
        usernameLabel.setFont(new Font("Arial", Font.BOLD, 14));
        add(usernameLabel);

        // Room selector combo box
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

        // Create new room UI
        add(new JLabel("Create a new room:"));
        newRoomCodeField = new JTextField();
        add(newRoomCodeField);
        createRoomButton = new JButton("Create Room");
        add(createRoomButton);

        // Invite teammate UI
        add(new JLabel("Invite a teammate:"));
        inviteNameField = new JTextField();
        inviteNameField.setToolTipText("Enter teammate name");
        add(inviteNameField);

        // Invite room selector dropdown instead of text input
        inviteRoomSelector = new JComboBox<>(rooms.toArray(new String[0]));
        inviteRoomSelector.setToolTipText("Select room code to join");
        add(inviteRoomSelector);

        inviteButton = new JButton("Invite");
        add(inviteButton);

        add(new JTextField("https://app.planitpoker.com"));
        add(new JButton("Copy URL"));

        // --- Story creation UI ---
        add(new JLabel(" ")); // spacer
        add(new JLabel("Create a new story:"));

        storyTitleField = new JTextField();
        storyTitleField.setToolTipText("Enter story title");
        add(storyTitleField);

        createStoryButton = new JButton("Create Story");
        add(createStoryButton);

        // --- Stories Chart UI ---
        add(new JLabel(" ")); // spacer
        add(new JLabel("Story Analytics:"));
        
        showChartButton = new JButton("Show Stories Chart");
        showChartButton.setToolTipText("Display chart of all stories with their average scores");
        add(showChartButton);

        // Listeners

        roomSelector.addActionListener(e -> {
            String selectedRoom = (String) roomSelector.getSelectedItem();
            repository.setCurrentRoomCode(selectedRoom);
            dashboardNanny.onRoomSelected(selectedRoom);
            refreshPlayerList();
        });

        createRoomButton.addActionListener(e -> {
            String newRoomName = newRoomCodeField.getText().trim();
            if (newRoomName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a room name.");
                return;
            }
            String createdRoomCode = repository.createRoom(newRoomName, username);
            if (createdRoomCode != null && !createdRoomCode.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Room '" + createdRoomCode + "' created successfully!");
                updateRoomSelector(createdRoomCode);
                newRoomCodeField.setText("");
            } else {
                JOptionPane.showMessageDialog(this, "Failed to create room '" + newRoomName + "'.");
            }
        });

        inviteButton.addActionListener(e -> {
            String teammateName = inviteNameField.getText().trim();
            String roomCodeToJoin = (String) inviteRoomSelector.getSelectedItem();

            if (teammateName.isEmpty() || roomCodeToJoin == null || roomCodeToJoin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a teammate name and select a room.");
                return;
            }

            boolean added = repository.joinRoom(roomCodeToJoin, teammateName);
            if (added) {
                JOptionPane.showMessageDialog(this, teammateName + " invited successfully to room " + roomCodeToJoin + "!");
                inviteNameField.setText("");
                if (roomCodeToJoin.equals(repository.getCurrentRoomCode())) {
                    refreshPlayerList();
                }
            } else {
                JOptionPane.showMessageDialog(this, "Failed to invite " + teammateName + ". They may already be in the room or room does not exist.");
            }
        });

        createStoryButton.addActionListener(e -> {
            String title = storyTitleField.getText().trim();
            String currentRoom = repository.getCurrentRoomCode();

            if (currentRoom == null || currentRoom.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select or create a room first.");
                return;
            }
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a story title.");
                return;
            }

            // Assuming createStory now only takes room and title (without description)
        });

        // Chart button listener
        showChartButton.addActionListener(e -> showStoriesChart());
    }

    private void showStoriesChart() {
        String currentRoom = repository.getCurrentRoomCode();
        if (currentRoom == null || currentRoom.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please select a room first.", "No Room Selected", JOptionPane.WARNING_MESSAGE);
            return;
        }

        T1PlanItPokerRepository.Room room = repository.getRoom(currentRoom);
        if (room == null) {
            JOptionPane.showMessageDialog(this, "Room not found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        List<T1Card> stories = room.getAllStories();
        if (stories.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No stories found in this room.", "No Stories", JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // Calculate averages for all stories
        for (T1Card story : stories) {
            story.calculateAverageScore();
        }

        // Show the chart dialog
        T1StoriesChartDialog chartDialog = new T1StoriesChartDialog((JFrame) SwingUtilities.getWindowAncestor(this), stories);
        chartDialog.setVisible(true);
    }

    private void updateRoomSelector(String newRoom) {
        roomSelector.addItem(newRoom);
        inviteRoomSelector.addItem(newRoom);
        roomSelector.setSelectedItem(newRoom);
        repository.setCurrentRoomCode(newRoom);
        dashboardNanny.onRoomSelected(newRoom);
        refreshPlayerList();
    }

    private void refreshPlayerList() {
        playersPanel.removeAll();

        String currentRoom = repository.getCurrentRoomCode();
        if (currentRoom != null) {
            List<String> names = repository.getRoom(currentRoom).getPlayers();
            logger.info("Players in room " + currentRoom + ": " + names);
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