package hbaskar.one;

import javax.swing.JFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Main class to create a JFrame and display the login panel.
 *
 * @author javiergs
 */
public class Main extends JFrame {
    private static final Logger logger = LoggerFactory.getLogger(Main.class);
	public Main() {
    logger.trace("Main constructor called");
    T1LoginNanny loginNanny = new T1LoginNanny(this);
    T1LoginPanel loginPanel = new T1LoginPanel(loginNanny);
    add(loginPanel);
}

public static void main(String[] args) {
    logger.trace("Starting Main...");
    Main main = new Main();
    main.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    main.setSize(400, 400);
    main.setLocationRelativeTo(null);
    main.setVisible(true);
}
} 

