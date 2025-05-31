package hbaskar.two;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import hbaskar.one.PlanItPokerRepository;
import hbaskar.one.PlanItPokerRepository.Room;

public class T1ScheduleRoomPanel extends JPanel {
    private T1CreateRoomNanny createRoomNanny;

    public T1ScheduleRoomPanel(T1CreateRoomNanny createRoomNanny) {
        this.createRoomNanny = createRoomNanny;
        setLayout(new GridLayout(5, 1));

        // Title
        JLabel title = new JLabel("Schedule Room");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(title);

        // Time selection
        JLabel timeLabel = new JLabel("Select Time Slot:");
        String[] timeSlots = {
            "9:00 AM - 10:00 AM", 
            "10:00 AM - 11:00 AM", 
            "1:00 PM - 2:00 PM",
            "2:00 PM - 3:00 PM"
        };
        JComboBox<String> timeCombo = new JComboBox<>(timeSlots);

        JPanel timePanel = new JPanel(new GridLayout(1, 2));
        timePanel.add(timeLabel);
        timePanel.add(timeCombo);
        add(timePanel);

        // Confirm button
        JButton confirmButton = new JButton("Confirm Booking");
        confirmButton.addActionListener(e -> {
            String time = (String) timeCombo.getSelectedItem();

            // Use repository instead of Blackboard
            PlanItPokerRepository repo = PlanItPokerRepository.getInstance();
            String currentRoomCode = repo.getCurrentRoomCode();
            Room room = repo.getRoom(currentRoomCode);
            if (room != null) {
                room.setScheduledTime(time);
                System.out.println("Scheduled time for room " + currentRoomCode + ": " + time);
            } else {
                System.err.println("No room found to schedule time.");
            }

            createRoomNanny.switchToStoriesPanel();
        });
        add(confirmButton);
    }
}
