package hbaskar.two;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Creating a new room.
 *
 * @author javiergs
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