package hbaskar.two;

import java.awt.Color;
import java.awt.GridLayout;
import java.util.List;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import hbaskar.one.PlanItPokerRepository;

public class RoomPanel extends JPanel {

    private PlanItPokerRepository repository = PlanItPokerRepository.getInstance();

    public RoomPanel(String username, CreateRoomNanny createRoomNanny, JoinRoomNanny joinRoomNanny) {
        setLayout(new GridLayout(7, 1, 10, 10)); // rows, cols, hgap, vgap
        setBackground(new Color(255, 204, 204));

        // CREATE ROOM SECTION
        JLabel createTitle = new JLabel("Create new Room");
        createTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(createTitle);

        JPanel createBox1 = new JPanel(new GridLayout(1, 2));
        JLabel nameLabel = new JLabel("Name:");
        JTextField createNameField = new JTextField();
        createBox1.add(nameLabel);
        createBox1.add(createNameField);
        add(createBox1);

        JPanel createBox2 = new JPanel(new GridLayout(1, 2));
        JLabel modeLabel = new JLabel("Mode:");
        String[] options = { "Scrum", "Fibonacci", "Sequential", "Hours", "T-shirt", "Custom deck" };
        JComboBox<String> modeComboBox = new JComboBox<>(options);
        createBox2.add(modeLabel);
        createBox2.add(modeComboBox);
        add(createBox2);

        JButton createButton = new JButton("Create");
        add(createButton);

        // JOIN ROOM SECTION
        JLabel joinTitle = new JLabel("Join a Room");
        joinTitle.setHorizontalAlignment(SwingConstants.CENTER);
        add(joinTitle);

        JPanel joinBox = new JPanel(new GridLayout(1, 2));
        JLabel joinLabel = new JLabel("Select or type room:");
        JComboBox<String> roomDropdown = new JComboBox<>();
        roomDropdown.setEditable(false);
        joinBox.add(joinLabel);
        joinBox.add(roomDropdown);
        add(joinBox);

        JTextField joinRoomField = new JTextField();
        add(joinRoomField);

        JButton joinButton = new JButton("Join");
        add(joinButton);

        // Populate rooms in dropdown
        refreshRoomDropdown(roomDropdown);

        // Create room action
        createButton.addActionListener(e -> {
            String roomName = createNameField.getText().trim();
            String mode = (String) modeComboBox.getSelectedItem();

            if (roomName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a room name to create.");
                return;
            }

            createRoomNanny.createRoom(roomName, mode);

            // Update dropdown after create
            refreshRoomDropdown(roomDropdown);
        });

        // Join room action
        joinButton.addActionListener(e -> {
            String selectedRoom = (String) roomDropdown.getSelectedItem();
            String typedRoom = joinRoomField.getText().trim();

            String roomToJoin = typedRoom.isEmpty() ? selectedRoom : typedRoom;

            if (roomToJoin == null || roomToJoin.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select or enter a room name to join.");
                return;
            }

        boolean success = joinRoomNanny.joinRoom(roomToJoin);
    if (success) {
        JOptionPane.showMessageDialog(this, "Joined room: " + roomToJoin);
        // TODO: proceed to next step after joining
    } else {
        JOptionPane.showMessageDialog(this, "Failed to join room: " + roomToJoin);
    }
        });
    }

    private void refreshRoomDropdown(JComboBox<String> dropdown) {
        dropdown.removeAllItems();
        List<String> rooms = repository.getAvailableRoomCodes();
        for (String room : rooms) {
            dropdown.addItem(room);
        }
    }
}
