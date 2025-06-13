package hbaskar.one;

import javax.swing.JFrame;

import hbaskar.three.T1StoriesNanny;
import hbaskar.three.T1TaigaPanel;

/**
 * Main class to create a JFrame and display the login panel.
 *
 * @author javiergs
 */
public class Main extends JFrame {
	
	public Main() {
    System.out.println("Main constructor called");
    T1LoginNanny loginNanny = new T1LoginNanny(this);
    T1LoginPanel loginPanel = new T1LoginPanel(loginNanny);
    add(loginPanel);
}

public static void main(String[] args) {
    System.out.println("Starting Main...");
    Main main = new Main();
    main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    main.setSize(400, 400);
    main.setLocationRelativeTo(null);
    main.setVisible(true);
}
} 

