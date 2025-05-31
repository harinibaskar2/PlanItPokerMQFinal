package hbaskar.one;

import java.awt.GridLayout;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

/**
 * Create a panel for user login.
 *
 * @author javiergs
 */
public class T1LoginPanel extends JPanel {
	
	public T1LoginPanel(T1LoginNanny joinRoomNanny) {
		JLabel titleLabel = new JLabel("Let's start!");
		titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel subtitleLabel = new JLabel("Join the room:");
		subtitleLabel.setHorizontalAlignment(SwingConstants.CENTER);
		JLabel accountLabel = new JLabel("Already have an account?");
		accountLabel.setHorizontalAlignment(SwingConstants.CENTER);
		JTextField nameField = new JTextField("Enter your name");
		JButton enterButton = new JButton("Enter");
		JButton loginButton = new JButton("Login");
		
		setLayout(new GridLayout(6, 1));
		add(titleLabel);
		add(subtitleLabel);
		add(nameField);
		add(enterButton);
		add(accountLabel);
		add(loginButton);
		
		enterButton.addActionListener(e -> joinRoomNanny.enterRoom(nameField.getText()));
		loginButton.addActionListener(e -> joinRoomNanny.login(nameField.getText()));
	}
	
}