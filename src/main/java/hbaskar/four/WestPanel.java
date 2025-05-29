
package hbaskar.four;
import java.awt.*;
import javax.swing.*;


/**
 * Panel that contains the left side of the dashboard.
 * It contains the username, start button, players, timer, invite a teammate, copy URL, and room selector.
 */
public class WestPanel extends JPanel {

	private JComboBox<String> roomSelector;

	public WestPanel(DashboardNanny dashboardNanny) {
		setBackground(new Color(255, 204, 204));
		setLayout(new GridLayout(8, 1));

		add(new JLabel("Javier"));

		// Room selector dropdown
		roomSelector = new JComboBox<>(new String[] { "Room 1", "Room 2", "Room 3" });
		add(roomSelector);

		add(new JButton("Start"));
		add(new JLabel("Players:"));
		add(new JLabel("00:00:00"));
		add(new JLabel("Invite a teammate"));
		add(new JTextField("https://app.planitpoker.com"));
		add(new JButton("Copy URL"));

		// Add event listener for room selection
		roomSelector.addActionListener(e -> {
			String selectedRoom = (String) roomSelector.getSelectedItem();
			dashboardNanny.onRoomSelected(selectedRoom);
		});
	}
}
