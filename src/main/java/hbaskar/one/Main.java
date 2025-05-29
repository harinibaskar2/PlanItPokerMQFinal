package hbaskar.one;

import javax.swing.*;
import hbaskar.three.*;

/**
 * Main class to create a JFrame and display the login panel.
 *
 * @author javiergs
 */
public class Main extends JFrame {
	
	public Main() {
		LoginNanny loginNanny = new LoginNanny(this);
		LoginPanel loginPanel = new LoginPanel(loginNanny);
		add(loginPanel);
		StoriesNanny storiesNanny = new StoriesNanny(this);
		StoriesPanel storiesPanel = new StoriesPanel(storiesNanny);
	}
	
	public static void main(String[] args) {
		Main main = new Main();
		main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		main.setSize(400, 400);
		main.setLocationRelativeTo(null);
		main.setVisible(true);
	}
	
}

