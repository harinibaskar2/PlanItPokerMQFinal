package hbaskar.two;

import java.awt.Font;
import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import hbaskar.one.Blackboard;

public class ScheduleRoomPanel extends JPanel {
    private CreateRoomNanny createRoomNanny;

    public ScheduleRoomPanel(CreateRoomNanny createRoomNanny) {
        this.createRoomNanny = createRoomNanny;
        setLayout(new GridLayout(5, 1)); // increased rows to 5



        // 2. Title
        JLabel title = new JLabel("Schedule Room");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        add(title);

        // 3. Time selection
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

        // 4. Confirm button
        JButton confirmButton = new JButton("Confirm Booking");
        confirmButton.addActionListener(e -> {
            String time = (String) timeCombo.getSelectedItem();
            System.out.println("Scheduled time: " + time);
            Blackboard.setRoomTime(time);
            createRoomNanny.switchToStoriesPanel();
        });
        add(confirmButton);
    }
}
