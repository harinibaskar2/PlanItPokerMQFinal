package hbaskar.two;

import java.awt.*;
import javax.swing.*;


public class ScheduleRoomPanel extends JPanel {
    private CreateRoomNanny createRoomNanny;

    public ScheduleRoomPanel(CreateRoomNanny createRoomNanny) {
        setLayout(new GridLayout(4, 1));
        
        JLabel title = new JLabel("Schedule Room");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        add(title);

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

        JButton confirmButton = new JButton("Confirm Booking");
        confirmButton.addActionListener(e -> {
            String time = (String) timeCombo.getSelectedItem();
            System.out.println("Scheduled time: " + time);
            one.Blackboard.setRoomTime(time);
            createRoomNanny.switchToStoriesPanel(); // navigate next
        });

        add(confirmButton);
    }
}
