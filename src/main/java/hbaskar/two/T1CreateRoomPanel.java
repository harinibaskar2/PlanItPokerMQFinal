package hbaskar.two;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * This class defines the user interface panel for creating a new room
 * in the PlanItPoker application. It provides input fields for the user 
 * to enter a room name and select a mode from predefined options.
 * 
 * 
 * When the "Create" button is clicked, it delegates the room creation logic 
 * to the {@code T1CreateRoomNanny}, which handles backend repository calls 
 * and UI transitions.
 * 
 * This panel uses a {@code GridLayout} to organize its components vertically 
 * and ensure a simple, user-friendly layout.
 * 
 * @author hbaskar
 * @version 1.0
 */

public class T1CreateRoomPanel extends JPanel {
	
	public T1CreateRoomPanel(T1CreateRoomNanny createRoomNanny) {
		setLayout(new GridLayout(4, 1));
		JLabel title = new JLabel("Create new Room");
		title.setHorizontalAlignment(SwingConstants.CENTER);
		add(title);
		JPanel box1 = new JPanel();
		box1.setLayout(new GridLayout(1, 2));
		JLabel nameLabel = new JLabel("Name:");
		JTextField nameField = new JTextField("CSC307");
		box1.add(nameLabel);
		box1.add(nameField);
		add(box1);
		JPanel box2 = new JPanel();
		box2.setLayout(new GridLayout(1, 2));
		JLabel modeLabel = new JLabel("Mode:");
		box2.add(modeLabel);
		String[] options = {"Scrum", "Fibonacci", "Sequential", "Hours", "T-shirt", "Custom deck"};
		JComboBox<String> comboBox = new JComboBox<>(options);
		box2.add(comboBox);
		add(box2);
		JPanel box3 = new JPanel();
		JButton createButton = new JButton("Create");
		box3.add(createButton);
		add(box3);
		createButton.addActionListener(e ->
			createRoomNanny.createRoom(nameField.getText(), (String) comboBox.getSelectedItem())
		);
	}
	
}