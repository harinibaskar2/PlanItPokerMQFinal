package hbaskar.two;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import hbaskar.one.T1PlanItPokerRepository;
import hbaskar.one.T1PlanItPokerRepository.Room;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides a panel UI for scheduling a time slot for a created room
 * in the PlanItPoker application. It allows users to select from predefined time
 * slots and confirm the booking for the currently active room.
 *
 *
 * This panel is meant to be shown after a room has been successfully created,
 * as part of the room setup workflow.
 *
 * @author hbaskar
 * @version 1.0
 */


public class T1ScheduleRoomPanel extends JPanel {
    private T1CreateRoomNanny createRoomNanny;
    private static final Logger logger = LoggerFactory.getLogger(T1ScheduleRoomPanel.class);

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
            T1PlanItPokerRepository repo = T1PlanItPokerRepository.getInstance();
            String currentRoomCode = repo.getCurrentRoomCode();
            Room room = repo.getRoom(currentRoomCode);
            if (room != null) {
                room.setScheduledTime(time);
                logger.info("Scheduled time for room " + currentRoomCode + ": " + time);
            } else {
                logger.error("No room found to schedule time.");
            }

            createRoomNanny.switchToStoriesPanel();
        });
        add(confirmButton);
    }
}

